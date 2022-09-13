package calculator.client;

import com.proto.calculator.*;
import com.proto.greeting.GreetingRequest;
import com.proto.greeting.GreetingResponse;
import com.proto.greeting.GreetingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CalculatorClient {

    public static void main(String[] args) throws InterruptedException {
        if (args.length == 0) {
            System.out.println("Need One argument to work ");
            return;
        }

        ManagedChannel managedChannel = ManagedChannelBuilder
                .forAddress("localhost", 50002)
                .usePlaintext()
                .build();

        switch (args[0]) {
            case "sum":
                doSum(managedChannel);
                break;
            case "primes":
                doPrimes(managedChannel);
                break;
            case "avg":
                doAvg(managedChannel);
                break;
            case "max":
                doMax(managedChannel);
                break;
            case "sqrt":
                doSqrt(managedChannel);
                break;
            default:
                System.out.println("Keyword Invalid :" + args[0]);
        }

        System.out.println("Shutting Down");
        managedChannel.shutdown();
    }

    private static void doSqrt(ManagedChannel managedChannel) {
        System.out.println("Enter Sqrt");
        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(managedChannel);

        SqrtResponse response = stub.sqrt(SqrtRequest.newBuilder().setNumber(25).build());
        System.out.println("Sqrt for 25 = " + response.getResult());

        try {
            response = stub.sqrt(SqrtRequest.newBuilder().setNumber(-1).build());
            System.out.println("Sqrt for -1 = " + response.getResult());
        }catch (RuntimeException e){
            System.out.println("Got an error for Sqrt");
            e.printStackTrace();
        }
    }

    private static void doMax(ManagedChannel channel) throws InterruptedException {
        System.out.println("Enter doMax");
        CalculatorServiceGrpc.CalculatorServiceStub stub = CalculatorServiceGrpc.newStub(channel);
        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<MaxRequest> stream = stub.max(new StreamObserver<MaxResponse>() {
            @Override
            public void onNext(MaxResponse response) {
                System.out.println("Max = " + response.getMax());
            }

            @Override
            public void onError(Throwable t) {
            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });

        Arrays.asList(1, 5, 3, 6, 2, 20).forEach(number ->
                stream.onNext(MaxRequest.newBuilder().setNumber(number).build())
        );
        stream.onCompleted();

        //noinspection ResultOfMethodCallIgnored
        latch.await(3, TimeUnit.SECONDS);
    }

    private static void doAvg(ManagedChannel managedChannel) throws InterruptedException {
        System.out.println("Enter doAvg");
        CalculatorServiceGrpc.CalculatorServiceStub stub = CalculatorServiceGrpc.newStub(managedChannel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<AvgRequest> stream = stub.avg(new StreamObserver<AvgResponse>() {
            @Override
            public void onNext(AvgResponse avgResponse) {
                System.out.println("Avg = " + avgResponse.getResult());
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });

        Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).forEach(number -> {
            stream.onNext(AvgRequest.newBuilder().setNumber(number).build());
        });

        stream.onCompleted();
        latch.await(3, TimeUnit.SECONDS);
    }

    private static void doPrimes(ManagedChannel managedChannel) {
        System.out.println("Enter doPrimes");
        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(managedChannel);
        stub.primes(PrimeRequest.newBuilder().setNumber(567890).build())
                .forEachRemaining(primeResponse -> {
                    System.out.println(primeResponse.getPrimeFactor());
                });
    }

    private static void doSum(ManagedChannel managedChannel) {
        System.out.println("Enter doGreet");

        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(managedChannel);
        SumResponse response = stub.sum(SumRequest.newBuilder().setFirstNumber(3).setSecondNumber(3).build());

        System.out.println("Sum = " + response.getResult());
    }
}