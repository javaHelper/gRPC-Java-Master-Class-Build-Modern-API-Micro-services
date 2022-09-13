package greeting.client;

import com.proto.greeting.GreetingRequest;
import com.proto.greeting.GreetingResponse;
import com.proto.greeting.GreetingServiceGrpc;
import io.grpc.*;
import io.grpc.stub.StreamObserver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetingClientTls {

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Need One argument to work ");
            return;
        }

        ChannelCredentials creds = TlsChannelCredentials.newBuilder()
                .trustManager(new File("ssl/ca.crt"))
                .build();

        ManagedChannel managedChannel = Grpc.newChannelBuilderForAddress("localhost", 50051, creds).build();

        switch (args[0]) {
            case "greet":
                doGreet(managedChannel);
                break;
            default:
                System.out.println("Keyword Invalid :" + args[0]);
        }

        System.out.println("Shutting Down");
        managedChannel.shutdown();
    }

    private static void doGreet(ManagedChannel managedChannel) {
        System.out.println("Enter doGreet");
        GreetingServiceGrpc.GreetingServiceBlockingStub stub = GreetingServiceGrpc.newBlockingStub(managedChannel);
        GreetingResponse response = stub.greet(GreetingRequest
                .newBuilder()
                .setFirstName("Prateek")
                .build());

        System.out.println("Greeting : " + response.getResult());
    }
}