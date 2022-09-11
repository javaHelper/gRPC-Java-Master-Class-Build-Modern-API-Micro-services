package greeting.client;

import com.proto.greeting.GreetingRequest;
import com.proto.greeting.GreetingResponse;
import com.proto.greeting.GreetingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GreetingClient {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Need One argument to work ");
            return;
        }

        ManagedChannel managedChannel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        switch (args[0]) {
            case "greet": doGreet(managedChannel); break;
            case "greet_many_times": doGreetManyTimes(managedChannel); break;
            default:
                System.out.println("Keyword Invalid :" + args[0]);
        }

        System.out.println("Shutting Down");
        managedChannel.shutdown();
    }

    private static void doGreetManyTimes(ManagedChannel managedChannel) {
        System.out.println("Enter in doGreetManyTimes");
        GreetingServiceGrpc.GreetingServiceBlockingStub stub = GreetingServiceGrpc.newBlockingStub(managedChannel);
        stub.greetManyTimes(GreetingRequest.newBuilder().setFirstName("Prateek").build())
                .forEachRemaining(greetingResponse -> {
                    System.out.println(greetingResponse.getResult());
                });

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