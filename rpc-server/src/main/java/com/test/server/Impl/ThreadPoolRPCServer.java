package com.test.server.Impl;

import com.test.server.RPCServer;
import com.test.server.ServiceProvider;
import com.test.server.WorkThread;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolRPCServer implements RPCServer {

    private final ThreadPoolExecutor threadPool;
    private ServiceProvider serviceProvide;

    public ThreadPoolRPCServer(ServiceProvider serviceProvide) {
        // java.lang.Runtime.availableProcessors()方法: 返回可用处理器的Java虚拟机的数量。
        threadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
                1000, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100));
        this.serviceProvide = serviceProvide;
    }

    public ThreadPoolRPCServer(ServiceProvider serviceProvide,
                               int corePoolSize, // 线程池中核心线程数的最大值
                               int maximumPoolSize, // 线程池中能拥有最多线程数
                               long keepAliveTime, // 线程空闲时间
                               TimeUnit unit, // 时间单位
                               BlockingQueue<Runnable> workQueue) { // 用于缓存任务的阻塞队列

        threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        this.serviceProvide = serviceProvide;
    }

    @Override
    public void start() {
        int port = serviceProvide.getPort();
        System.out.println("服务端启动了");
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                Socket socket = serverSocket.accept();
                threadPool.execute(new WorkThread(socket, serviceProvide));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
    }
}