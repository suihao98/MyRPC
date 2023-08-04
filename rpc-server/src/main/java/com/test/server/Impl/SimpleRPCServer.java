package com.test.server.Impl;

import com.test.server.RPCServer;
import com.test.server.ServiceProvider;
import com.test.server.WorkThread;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 这个实现类代表着java原始的BIO监听模式，来一个任务，就new一个线程去处理
 * 处理任务的工作见WorkThread中
 */
/**
 * 这个实现类代表着java原始的BIO监听模式，来一个任务，就new一个线程去处理
 * 处理任务的工作见WorkThread中
 */
public class SimpleRPCServer implements RPCServer {

    private ServiceProvider serviceProvide;

    public void start() {
        int port = serviceProvide.getPort();
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("服务端启动了");
            // BIO的方式监听Socket
            while (true){
                Socket socket = serverSocket.accept();
                // 开启一个新线程去处理
                new Thread(new WorkThread(socket,serviceProvide)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("服务器启动失败");
        }
    }

    public void stop(){
    }
}