//
// Copyright (c) 2023 ZettaScale Technology
//
// This program and the accompanying materials are made available under the
// terms of the Eclipse Public License 2.0 which is available at
// http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
// which is available at https://www.apache.org/licenses/LICENSE-2.0.
//
// SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
//
// Contributors:
//   ZettaScale Zenoh Team, <zenoh@zettascale.tech>
//
package org.eclipse.uprotocol.ulink.zenoh;

import io.zenoh.keyexpr.KeyExpr;
import io.zenoh.Session;
import io.zenoh.prelude.SampleKind;
import io.zenoh.publication.CongestionControl;
import io.zenoh.publication.Priority;
import io.zenoh.sample.Sample;
import io.zenoh.value.Value;
import io.zenoh.prelude.Encoding;
import io.zenoh.prelude.KnownEncoding;

import org.eclipse.uprotocol.transport.UTransport;
import org.eclipse.uprotocol.transport.UListener;
import org.eclipse.uprotocol.uri.validator.UriValidator;
import org.eclipse.uprotocol.v1.*;
import org.eclipse.uprotocol.validation.ValidationResult;
import org.eclipse.uprotocol.rpc.RpcClient;
import org.eclipse.uprotocol.uri.serializer.LongUriSerializer;

import com.google.protobuf.ByteString;

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class ULink implements UTransport, RpcClient
{
    private Session m_session;
    // TODO: Check whether we can have multiple UUri mapping to multiple UListener
    private final Map<UUri, UListener> mListeners = new HashMap<>();

    /*
     * uLink constructor
     * 
     * @param executor - the executor to run the listener callbacks on
     */
    public ULink() {
        try {
            m_session = Session.open();
        } catch(Exception e) {
            System.err.println("Failed to init the Zenoh session");
        }
    }

    /*
     * Send request and get completable future
     * 
     * @param uri - the uri of the request
     * @param payload - the payload of the request
     * @param attributes - the attributes of the request
     * @return CompletableFuture<UPayload> - the completed future with the response
     */
    @Override
    public CompletableFuture<UPayload> invokeMethod(UUri uri, UPayload payload, UAttributes attributes) {
        // TODO: Send Zenoh's query
        return new CompletableFuture<UPayload>();
    }

    /**
     * Validate the calling uE's identity
     * 
     * @param entity - the entity to authenticate
     */
    @Override
    public UStatus authenticate(UEntity entity) {
        // TODO: Need to check the authenticate mechanism
        // https://github.com/eclipse-uprotocol/uprotocol-spec/blob/main/up-l1/README.adoc#21-authenticate
        return UStatus.newBuilder().setCode(UCode.OK).build();
    }

    private void listener(UUri uri, Sample sample) {
        for (Map.Entry<UUri, UListener> entry : mListeners.entrySet()) {
            if (entry.getKey().equals(uri)) {
                ByteString mData = ByteString.copyFrom(sample.getValue().getPayload());
                UPayload mPayload = UPayload.newBuilder()
                        .setValue(mData)
                        .setFormat(UPayloadFormat.UPAYLOAD_FORMAT_PROTOBUF)
                        .build();
                // TODO: Get UAttributes with user attachment
                entry.getValue().onReceive(uri, mPayload, null);
            }
        }
    }

    /**
     * Register a listener that will be called when am event is received.
     * 
     * @param uri - the uri to register the listener for
     * @param listener - the listener to register
     * @return UStatus - the status of the registration
     */
    @Override
    public UStatus registerListener(UUri uri, UListener listener) {
        ValidationResult result = UriValidator.validate(uri);
        if (result.isFailure()) {
            return UStatus.newBuilder()
                    .setCode(UCode.INVALID_ARGUMENT)
                    .setMessage(result.getMessage())
                    .build();
        }
        // TODO: Define a way to transform Uuri to Zenoh's key
        String zenoh_key = "zenoh" + LongUriSerializer.instance().serialize(uri);
        try {
            KeyExpr keyExpr = KeyExpr.tryFrom(zenoh_key);
            m_session.declareSubscriber(keyExpr).with(sample -> listener(uri, sample)).res();
        } catch (Exception e) {
            System.err.println("Error while creating subscriber" + e.toString());
            return UStatus.newBuilder().setCode(UCode.INTERNAL).build();
        }

        // Register the UListener
        if (mListeners.containsKey(uri)) {
            return UStatus.newBuilder()
                    .setCode(UCode.ALREADY_EXISTS)
                    .setMessage("Listener already registered for " + uri)
                    .build();
        }
        mListeners.put(uri, listener);

        return UStatus.newBuilder().setCode(UCode.OK).build();
    }

    /**
     * Send a message to the uri
     */
    @Override
    public UStatus send(UUri uri, UPayload payload, UAttributes attributes) {
        ValidationResult result = UriValidator.validate(uri);
        if (result.isFailure()) {
            return UStatus.newBuilder()
                    .setCode(UCode.INVALID_ARGUMENT)
                    .setMessage(result.getMessage())
                    .build();
        }

        // TODO: Define a way to transform Uuri to Zenoh's key
        String zenoh_key = "zenoh" + LongUriSerializer.instance().serialize(uri);
        try {
            KeyExpr keyExpr = KeyExpr.tryFrom(zenoh_key);
            // TODO: Need a way to send UAttributes (with user attachment)
            String value = payload.getValue().toStringUtf8();
            m_session.put(keyExpr, new Value(payload.getValue().toByteArray(), new Encoding(KnownEncoding.APP_OCTET_STREAM)))
                .congestionControl(CongestionControl.BLOCK)
                .priority(Priority.REALTIME)
                .kind(SampleKind.PUT)
                .res();
        } catch (Exception e) {
            System.err.println("Error while publishing data" + e.toString());
            return UStatus.newBuilder().setCode(UCode.INTERNAL).build();
        }

        return UStatus.newBuilder().setCode(UCode.OK).build();
    }

    /**
     * Unregister the listener for the uri
     * 
     * @param uri - the uri to unregister the listener for
     * @param listener - the listener to unregister
     * @return UStatus - the status of the unregistration
     */
    @Override
    public UStatus unregisterListener(UUri uri, UListener listener) {
        // TODO: Why do we need the argument listener in unregister?
        ValidationResult result = UriValidator.validate(uri);
        if (result.isFailure()) {
            return UStatus.newBuilder()
                    .setCode(UCode.INVALID_ARGUMENT)
                    .setMessage(result.getMessage())
                    .build();
        }

        // Unregister the UListener
        if (!mListeners.containsKey(uri)) {
            return UStatus.newBuilder()
                    .setCode(UCode.INVALID_ARGUMENT)
                    .setMessage("No listener found for " + uri)
                    .build();
        }
        mListeners.remove(uri);

        return UStatus.newBuilder().setCode(UCode.OK).build();
    }
}
