package com.abc.springboot.grpc.config;

import com.abc.springboot.grpc.exception.HelloGrpcServerConnectedException;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

public class IllegalArgumentFailureAnalyzer extends AbstractFailureAnalyzer<HelloGrpcServerConnectedException> {

    @Override
    protected FailureAnalysis analyze(Throwable rootFailure, HelloGrpcServerConnectedException cause) {
        return new FailureAnalysis("\uD83D\uDEAB 无法连接gRPC Server，服务启动失败！\uD83D\uDEAB",
                "gRPC Server没用启动? 还是你的连接参数配置错误?", cause);
    }
}