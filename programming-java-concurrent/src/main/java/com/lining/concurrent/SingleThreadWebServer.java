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
public class SingleThreadWebServer {

    public static void hadnleRequest(Socket socket) {
        System.out.println("handle request");
    }

    public static void main(String[] args) throws IOException {
        ServerSocket socket = new ServerSocket(80);
        while (true) {
            Socket conn = socket.accept();
            hadnleRequest(conn);
        }
    }
}
