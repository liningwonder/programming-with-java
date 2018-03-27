package com.lining.concurrent;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * description:
 * date 2018-03-27
 *
 * @author lining1
 * @version 1.0.0
 */
public class ThreadPerTaskWebServer {

    public static void hadnleRequest(Socket socket) {
        System.out.println("handle request");
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(80);
        while (true) {
            final Socket socket = serverSocket.accept();
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    hadnleRequest(socket);
                }
            };
            new Thread(task).start();
        }
    }
}
