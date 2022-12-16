package com.abc.grpc.hello;

import com.abc.grpc.hello.proto.HelloReply;
import com.abc.grpc.hello.proto.HelloRequest;
import com.abc.grpc.hello.service.HelloServiceGrpc;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HelloWorldServer {

    private static final List<StreamObserver<HelloReply>> clients = new LinkedList<>();

    private static Server server;

    /**
     * Main launches the server from the command line.
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        final HelloWorldServer server = new HelloWorldServer();
        server.start();
        server.blockUntilShutdown();
    }

    private void start() throws IOException {
        /* The port on which the server should run */
        int port = 50051;
        server = ServerBuilder.forPort(port)
                .addService(new GreeterImpl())
                .build()
                .start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // Use stderr here since the logger may have been reset by its JVM shutdown hook.
            System.err.println("*** shutting down gRPC server since JVM is shutting down");
            try {
                HelloWorldServer.this.stop();
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
            System.err.println("*** server shut down");
        }));
        System.out.println("Server Start Successfully!");
    }

    private void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    static class GreeterImpl extends HelloServiceGrpc.HelloServiceImplBase {
        /* 用于建立连接 */

        @Override
        public StreamObserver<HelloRequest> sayHello(StreamObserver<HelloReply> responseObserver) {
            return new StreamObserver<>() {
                @Override
                public void onNext(HelloRequest requestMessage) {
                    System.out.println("[收到客户端消息]: " + requestMessage.getName());
                    responseObserver.onNext(HelloReply.newBuilder().setMessage("hello client ,I'm Java grpc Server,your message '" + requestMessage.getName() + "'").build());
                    // 添加在线客户端
                    System.out.println("客户端+1");
                    clients.add(responseObserver);
                    // 开启定时器，向客户端发送消息
                    ScheduledExecutorService timer = Executors.newScheduledThreadPool(1);
                    timer.scheduleWithFixedDelay(() -> {
                        try {
                            responseObserver.onNext(HelloReply.newBuilder().setMessage(System.nanoTime() + " → hello, client! i am server!").build());
                        } catch (Exception e) {
                            // 移除客户端
                            System.out.println(clients.size());
                            clients.remove(responseObserver);
                            System.out.println(clients.size());
                            timer.shutdownNow();
                        }
                    }, 0, 3, TimeUnit.SECONDS);
                }

                @Override
                public void onError(Throwable t) {
                    t.fillInStackTrace();
                }

                @Override
                public void onCompleted() {
                    responseObserver.onCompleted();
                }
            };
        }

        @Override
        public StreamObserver<HelloRequest> sayHelloAgain(StreamObserver<HelloReply> responseObserver) {
            return new StreamObserver<>() {
                @Override
                public void onNext(HelloRequest requestMessage) {
                    System.out.println("[收到客户端消息]: " + requestMessage.getName());
                    responseObserver.onNext(HelloReply.newBuilder().setMessage("hello client Again,I'm Java grpc Server,your message '" + requestMessage.getName() + "'").build());
                }

                @Override
                public void onError(Throwable t) {
                    t.fillInStackTrace();
                }

                @Override
                public void onCompleted() {
                    responseObserver.onCompleted();
                }
            };
        }
    }
}