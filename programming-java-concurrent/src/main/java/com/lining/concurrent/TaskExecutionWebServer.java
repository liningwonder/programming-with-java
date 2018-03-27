package com.lining.concurrent;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * description:
 * date 2018-03-27
 *
 * @author lining1
 * @version 1.0.0
 */
public class TaskExecutionWebServer {

    private static final int NTHREADS = 100;
    private static final Executor exec = Executors.newFixedThreadPool(NTHREADS);

    public static void hadnleRequest(Socket socket) {
        System.out.println("handle request");
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(80);
        final Socket socket = serverSocket.accept();
        while (true) {
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    hadnleRequest(socket);
                }
            };
            exec.execute(task);
        }
    }
}
