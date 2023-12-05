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

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.eclipse.uprotocol.v1.UUri;
import org.eclipse.uprotocol.transport.builder.UAttributesBuilder;
import org.eclipse.uprotocol.v1.*;
import org.eclipse.uprotocol.transport.UListener;

import com.google.protobuf.Any;
import com.google.protobuf.Int32Value;

class ULinkTest {
    @Test void uLinkTest() {
        System.out.println("----- This is uLinkTest -----");
        int testValue = 3;
        ULink classUnderTest = new ULink();

        //UUri mTopic = UUri.newBuilder()
        //    .setEntity(UEntity.newBuilder().setName("body.access").setVersionMajor(1))
        //    .setResource(UResource.newBuilder().setName("door").setInstance("front_left").setMessage("Door")).build();
        UUri mTopic = UUri.newBuilder()
            .setEntity(UEntity.newBuilder().setName("body.access").setVersionMajor(1))
            .setResource(UResource.newBuilder().setName("door").setInstance("front_left")).build();

        // Register the listener
        final class TestListener implements UListener {
            @Override
            public UStatus onReceive(UUri uri, UPayload payload, UAttributes attributes) {
                assertEquals(uri, mTopic);
                try {
                    Any msg = Any.parseFrom(payload.getValue());
                    Int32Value val = msg.unpack(Int32Value.class);
                    System.out.println("The value is " + val.getValue());
                    assertEquals(testValue, val.getValue());
                } catch(Exception e) {
                    System.err.println("Unable to parse the payload: " + e);
                }
                return UStatus.newBuilder().setCode(UCode.OK).build();
            }
        };

        classUnderTest.registerListener(mTopic, new TestListener());

        // Publish data
        Any mData = Any.pack(Int32Value.of(testValue));
        UPayload mPayload = UPayload.newBuilder()
                .setValue(mData.toByteString())
                .setFormat(UPayloadFormat.UPAYLOAD_FORMAT_PROTOBUF)
                .build();
        UAttributes attributes = UAttributesBuilder.publish(UPriority.UPRIORITY_CS4).build();
        classUnderTest.send(mTopic, mPayload, attributes);

        // Sleep for receiving data
        try {
            Thread.sleep(1000);
        } catch(Exception e) {
            System.err.println("Sleeping error" + e);
        }

        assertTrue(true);
    }
}
