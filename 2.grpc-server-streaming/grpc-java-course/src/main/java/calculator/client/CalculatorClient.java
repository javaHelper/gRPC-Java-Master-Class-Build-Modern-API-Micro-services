package calculator.client;

import com.proto.calculator.CalculatorServiceGrpc;
import com.proto.calculator.PrimeRequest;
import com.proto.calculator.SumRequest;
import com.proto.calculator.SumResponse;
import com.proto.greeting.GreetingRequest;
import com.proto.greeting.GreetingResponse;
import com.proto.greeting.GreetingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class CalculatorClient {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Need One argument to work ");
            return;
        }

        ManagedChannel managedChannel = ManagedChannelBuilder
                .forAddress("localhost", 50002)
                .usePlaintext()
                .build();

        switch (args[0]) {
            case "sum": doSum(managedChannel); break;
            case "primes": doPrimes(managedChannel); break;
            default:
                System.out.println("Keyword Invalid :" + args[0]);
        }

        System.out.println("Shutting Down");
        managedChannel.shutdown();
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

        System.out.println("Sum = "+ response.getResult());
    }
}