package greeting.client;

import com.proto.greeting.GreetingRequest;
import com.proto.greeting.GreetingResponse;
import com.proto.greeting.GreetingServiceGrpc;
import io.grpc.*;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetingClient {

    public static void main(String[] args) throws InterruptedException {
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
            case "long_greet": doLongGreet(managedChannel); break;
            case "greet_everyone": doGreetEveryone(managedChannel); break;
            case "greet_with_deadline": doGreetWithDeadline(managedChannel); break;
            default:
                System.out.println("Keyword Invalid :" + args[0]);
        }

        System.out.println("Shutting Down");
        managedChannel.shutdown();
    }

    private static void doGreetWithDeadline(ManagedChannel channel) {
        System.out.println("Enter doGreetWithDeadline");
        GreetingServiceGrpc.GreetingServiceBlockingStub stub = GreetingServiceGrpc.newBlockingStub(channel);
        GreetingRequest request = GreetingRequest.newBuilder().setFirstName("Clement").build();
        GreetingResponse response = stub.withDeadline(Deadline.after(3, TimeUnit.SECONDS))
                .greetWithDeadline(request);

        System.out.println("Greeting within deadline: " + response.getResult());

        try {
            response = stub.withDeadline(Deadline.after(100, TimeUnit.MILLISECONDS))
                    .greetWithDeadline(request);

            System.out.println("Greeting deadline exceeded: " + response.getResult());
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.DEADLINE_EXCEEDED) {
                System.out.println("Deadline has been exceeded");
            } else {
                System.out.println("Got an exception in greetWithDeadline");
                e.printStackTrace();
            }
        }
    }

    private static void doGreetEveryone(ManagedChannel managedChannel) throws InterruptedException {
        System.out.println("Enter doGreetEveryone");
        GreetingServiceGrpc.GreetingServiceStub stub = GreetingServiceGrpc.newStub(managedChannel);

        CountDownLatch latch = new CountDownLatch(1);
        StreamObserver<GreetingRequest> stream = stub.greetEveryone(new StreamObserver<GreetingResponse>() {
            @Override
            public void onNext(GreetingResponse greetingResponse) {
                System.out.println(greetingResponse.getResult());
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });

        Arrays.asList("Prateek", "Shrutika", "Laxmi", "Aravind").forEach(name ->
                stream.onNext(GreetingRequest.newBuilder().setFirstName(name).build()));

        stream.onCompleted();
        latch.await(3, TimeUnit.SECONDS);
    }

    private static void doLongGreet(ManagedChannel managedChannel) throws InterruptedException {
        System.out.println("Enter doLongGreet");
        GreetingServiceGrpc.GreetingServiceStub stub = GreetingServiceGrpc.newStub(managedChannel);

        List<String> names = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);

        Collections.addAll(names, "Prateek", "Ankita", "Deepak", "Varsha");
        StreamObserver<GreetingRequest> stream = stub.longGreet(new StreamObserver<GreetingResponse>() {
            @Override
            public void onNext(GreetingResponse greetingResponse) {
                System.out.println(greetingResponse.getResult());
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });

        for (String name: names) {
            stream.onNext(GreetingRequest.newBuilder().setFirstName(name).build());
        }

        stream.onCompleted();
        latch.await(3, TimeUnit.SECONDS);
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