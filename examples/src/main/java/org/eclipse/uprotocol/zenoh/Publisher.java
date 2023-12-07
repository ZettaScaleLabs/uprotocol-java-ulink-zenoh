package org.eclipse.uprotocol.zenoh;

// uprotocol
import org.eclipse.uprotocol.v1.*;
import org.eclipse.uprotocol.uri.serializer.LongUriSerializer;
import org.eclipse.uprotocol.transport.builder.UAttributesBuilder;
// uprotocol ulink-zenoh
import org.eclipse.uprotocol.ulink.zenoh.ULink;

// Google protobuf
import com.google.protobuf.Any;
import com.google.protobuf.Int32Value;

public class Publisher {
    public static void main(String[] args) throws Exception {
        System.out.println("uProtocol Publisher example");
        ULink publisher = new ULink();

        // create uuri
        UUri uuri = UUri.newBuilder()
            .setEntity(UEntity.newBuilder().setName("body.access").setVersionMajor(1))
            .setResource(UResource.newBuilder().setName("door").setInstance("front_left")).build();
        String serialized_uuri = LongUriSerializer.instance().serialize(uuri);
        
        // create uattributes
        UAttributes attributes = UAttributesBuilder.publish(UPriority.UPRIORITY_CS4).build();

        // Publish data
        int cnt = 0;
        while (true) {
            // create upayload
            Any data = Any.pack(Int32Value.of(cnt));
            UPayload payload = UPayload.newBuilder()
                    .setValue(data.toByteString())
                    .setFormat(UPayloadFormat.UPAYLOAD_FORMAT_PROTOBUF)
                    .build();
        
            Thread.sleep(1000);
            System.out.println("Sending data " + cnt + " to " + serialized_uuri + "...");
            publisher.send(uuri, payload, attributes);
            cnt++;
        }
    }
}
