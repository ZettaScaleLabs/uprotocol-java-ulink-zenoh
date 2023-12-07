package org.eclipse.uprotocol.zenoh;

// uprotocol
import org.eclipse.uprotocol.v1.*;
import org.eclipse.uprotocol.uri.serializer.LongUriSerializer;
import org.eclipse.uprotocol.transport.UListener;
// uprotocol ulink-zenoh
import org.eclipse.uprotocol.ulink.zenoh.ULink;

// Google protobuf
import com.google.protobuf.Any;
import com.google.protobuf.Int32Value;

public class Subscriber {
    public static void main(String[] args) throws Exception {
        System.out.println("uProtocol Subscriber example");
        ULink subscriber = new ULink();

        // create uuri
        UUri uuri = UUri.newBuilder()
            .setEntity(UEntity.newBuilder().setName("body.access").setVersionMajor(1))
            .setResource(UResource.newBuilder().setName("door").setInstance("front_left").setMessage("Door")).build();
        String serialized_uuri = LongUriSerializer.instance().serialize(uuri);
        
        // Register the listener
        final class MyListener implements UListener {
            @Override
            public UStatus onReceive(UUri uri, UPayload payload, UAttributes attributes) {
                try {
                    String serialized_uri = LongUriSerializer.instance().serialize(uri);
                    Any msg = Any.parseFrom(payload.getValue());
                    Int32Value val = msg.unpack(Int32Value.class);
                    System.out.println("Receive the data " + val.getValue() + " from " + serialized_uri);
                } catch(Exception e) {
                    System.err.println("Unable to parse the payload: " + e);
                }
                return UStatus.newBuilder().setCode(UCode.OK).build();
            }
        };
        System.out.println("Register the listener...");
        subscriber.registerListener(uuri, new MyListener());

        // Waiting for receiving data
        while (true) {
            Thread.sleep(1);
        }
    }    
}
