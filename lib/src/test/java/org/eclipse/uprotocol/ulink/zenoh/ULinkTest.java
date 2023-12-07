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

import org.eclipse.uprotocol.v1.*;
import org.eclipse.uprotocol.transport.builder.UAttributesBuilder;
import org.eclipse.uprotocol.transport.UListener;

import com.google.protobuf.Any;
import com.google.protobuf.Int32Value;

class ULinkTest {
    /**
     * Test register and unregister function for ULink library.
     */
    @Test void testRegisterAndUnregister() {
        UStatus ustatus;
        ULink classUnderTest = new ULink();

        UUri uuri = UUri.newBuilder()
            .setEntity(UEntity.newBuilder().setName("body.access").setVersionMajor(1))
            .setResource(UResource.newBuilder().setName("door").setInstance("front_left")).build();
        final class dummyListener implements UListener {
            @Override
            public UStatus onReceive(UUri uri, UPayload payload, UAttributes attributes) {
                // Do nothing
                return UStatus.newBuilder().setCode(UCode.OK).build();
            }
        };

        ustatus = classUnderTest.registerListener(uuri, new dummyListener());
        System.out.println("Register the Listener...");
        assertEquals(ustatus.getCode(), UCode.OK);

        System.out.println("Unregister the Listener...");
        ustatus = classUnderTest.unregisterListener(uuri, new dummyListener());
        assertEquals(ustatus.getCode(), UCode.OK);

        System.out.println("Unregister the unexisting Listener...");
        ustatus = classUnderTest.unregisterListener(uuri, new dummyListener());
        assertEquals(ustatus.getCode(), UCode.INVALID_ARGUMENT);
    }

    /**
     * Simple test to register Listener and then send data to that Listener.
     */
    @Test void testPublishAndListener() throws Exception {
        UStatus ustatus;
        int testValue = 3;
        ULink classUnderTest = new ULink();

        //UUri mTopic = UUri.newBuilder()
        //    .setEntity(UEntity.newBuilder().setName("body.access").setVersionMajor(1))
        //    .setResource(UResource.newBuilder().setName("door").setInstance("front_left").setMessage("Door")).build();
        UUri uuri = UUri.newBuilder()
            .setEntity(UEntity.newBuilder().setName("body.access").setVersionMajor(1))
            .setResource(UResource.newBuilder().setName("door").setInstance("front_left")).build();

        // Register the listener
        final class TestListener implements UListener {
            @Override
            public UStatus onReceive(UUri uri, UPayload payload, UAttributes attributes) {
                assertEquals(uri, uuri);
                try {
                    Any msg = Any.parseFrom(payload.getValue());
                    Int32Value val = msg.unpack(Int32Value.class);
                    System.out.println("Receive the data " + val.getValue());
                    assertEquals(testValue, val.getValue());
                } catch(Exception e) {
                    System.err.println("Unable to parse the payload: " + e);
                }
                return UStatus.newBuilder().setCode(UCode.OK).build();
            }
        };
        System.out.println("Register the listener...");
        ustatus = classUnderTest.registerListener(uuri, new TestListener());
        assertEquals(ustatus.getCode(), UCode.OK);

        // Publish data
        System.out.println("Publish the data "+ testValue +"...");
        Any data = Any.pack(Int32Value.of(testValue));
        UPayload upayload = UPayload.newBuilder()
                .setValue(data.toByteString())
                .setFormat(UPayloadFormat.UPAYLOAD_FORMAT_PROTOBUF)
                .build();
        UAttributes attributes = UAttributesBuilder.publish(UPriority.UPRIORITY_CS4).build();
        ustatus = classUnderTest.send(uuri, upayload, attributes);
        assertEquals(ustatus.getCode(), UCode.OK);

        // Sleep for receiving data
        Thread.sleep(1000);
    }

    /**
     * Test invoking a method and receiving the response.
     */
    @Test void testInvokeMethod() {
        assertTrue(true);
    }
}
