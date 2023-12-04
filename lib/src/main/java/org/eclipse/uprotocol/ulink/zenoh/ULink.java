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

import io.zenoh.exceptions.ZenohException;
import io.zenoh.keyexpr.KeyExpr;
import io.zenoh.publication.Publisher;
import io.zenoh.Session;

import org.eclipse.uprotocol.transport.UTransport;
import org.eclipse.uprotocol.transport.UListener;
import org.eclipse.uprotocol.uri.validator.UriValidator;
import org.eclipse.uprotocol.v1.*;
import org.eclipse.uprotocol.validation.ValidationResult;
import org.eclipse.uprotocol.rpc.RpcClient;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ULink implements UTransport, RpcClient
{
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
        return new CompletableFuture<UPayload>();
    }

    /**
     * Validate the calling uE's identity
     * 
     * @param entity - the entity to authenticate
     */
    @Override
    public UStatus authenticate(UEntity entity) {
        return UStatus.newBuilder().setCode(UCode.OK).build();
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
        return UStatus.newBuilder().setCode(UCode.OK).build();
    }

    /**
     * Send a message to the uri
     */
    @Override
    public UStatus send(UUri uri, UPayload payload, UAttributes attributes) {
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
        return UStatus.newBuilder().setCode(UCode.OK).build();
    }
}
