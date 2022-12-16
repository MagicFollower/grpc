package com.abc.springboot.grpc.client;

import com.abc.springboot.grpc.exception.HelloGrpcServerConnectedException;
import com.abc.springboot.grpc.proto.HelloReply;
import com.abc.springboot.grpc.proto.HelloRequest;
import com.abc.springboot.grpc.service.HelloServiceGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;

@Service
@Slf4j
public class HelloWorldClient {

    final int PORT = 50051;
    int RETRY_COUNT = 5;

    @Autowired
    public HelloWorldClient() {

        ScheduledExecutorService timer = Executors.newScheduledThreadPool(1);
        String target = "localhost:" + PORT;
        ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
                .usePlaintext()
                .build();
        log.info("connected to gRPC-Server on {}.", target);

        // 接受服务器端推送的消息
        final boolean[] firstConnectedFlag = {false};
        final CountDownLatch firstConnectedLatch = new CountDownLatch(1);
        HelloServiceGrpc.newStub(channel).sayHello(new StreamObserver<>() {

            @Override
            public void onNext(HelloReply value) {
                // 启动成功，连接已建立（之后服务器断开，客户端需要一直重试，直到服务器重启成功）
                firstConnectedFlag[0] = true;
                firstConnectedLatch.countDown();
                RETRY_COUNT = Integer.MAX_VALUE;
                System.out.println("[收到服务端发来] : " + value.getMessage());
            }

            @Override
            public void onError(Throwable t) {
                firstConnectedFlag[0] = retry(timer, channel);
                firstConnectedLatch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println("completed");
            }
        }).onNext(HelloRequest.newBuilder().setName("Client-" + ThreadLocalRandom.current().nextInt(10, 20)).build());

        try {
            firstConnectedLatch.await();
        } catch (InterruptedException ignored) {
        }

        if (!firstConnectedFlag[0]) {
            throw new HelloGrpcServerConnectedException();
        }
    }

    /**
     * 重连方法
     *
     * @param timer   单任务定时器
     * @param channel gRPC Channel
     * @return 是否重连成功，返回值仅在第一次连接使用
     */
    boolean retry(ScheduledExecutorService timer, Channel channel) {
        final boolean[] stopRetryTag = {false};
        CountDownLatch latch = new CountDownLatch(RETRY_COUNT);
        for (int i = 0; i < RETRY_COUNT; i++) {
            int finalI = i;
            timer.schedule(() -> {
                System.out.println(Thread.currentThread().getName());
                System.out.printf("第%d次重连中...%n", finalI + 1);
                // 发送重试连接请求
                HelloServiceGrpc.newStub(channel).sayHello(new StreamObserver<>() {
                    @Override
                    public void onNext(HelloReply value) {
                        System.out.println(Thread.currentThread().getName());
                        System.out.println("[收到服务端发来] : " + value.getMessage());
                        stopRetryTag[0] = true;
                    }

                    @Override
                    public void onError(Throwable t) {
                        if (stopRetryTag[0])
                            retry(timer, channel);
                    }

                    @Override
                    public void onCompleted() {
                    }
                }).onNext(HelloRequest.newBuilder().setName("Client-" + ThreadLocalRandom.current().nextInt(0, 10)).build());
                latch.countDown();
            }, 0, TimeUnit.SECONDS);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (stopRetryTag[0]) return true;
        }
        try {
            latch.await();
        } catch (InterruptedException ignored) {
        }
        return false;
    }

}