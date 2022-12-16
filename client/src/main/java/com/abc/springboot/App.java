package com.abc.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        System.out.println("启动中...");
        System.out.println("当前可用处理器数："+Runtime.getRuntime().availableProcessors());
        System.out.println("JVM最小可用内存-Xms："+Runtime.getRuntime().freeMemory()/1024/1024+"MB");
        System.out.println("JVM最大可用内存-Xmx："+Runtime.getRuntime().maxMemory()/1024/1024+"MB");
        SpringApplication.run(App.class, args);
    }
}
