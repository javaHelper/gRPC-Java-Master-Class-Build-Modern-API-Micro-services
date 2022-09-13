package blog.server;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class BlogServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 50002;

        MongoClient client = MongoClients.create("mongodb://localhost:27017/");

        Server server = ServerBuilder.forPort(port)
                .addService(new BlogServiceImpl(client))
                .build();
        server.start();
        System.out.println("Server Started");
        System.out.println("Listing on port : "+port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Received Shutdown Request");
            server.shutdown();
            System.out.println("Server Stopped");
        }));

        server.awaitTermination();
    }
}