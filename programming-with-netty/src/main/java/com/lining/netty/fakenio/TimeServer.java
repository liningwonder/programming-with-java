package com.lining.netty.fakenio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * description: BIO
 * date 2018-04-04
 *
 * @author lining1
 * @version 1.0.0
 */
public class TimeServer {

    public static void main(String[] args) throws IOException {
        int port = 8080;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        ServerSocket server = null;
        try {
            server = new ServerSocket(port);
            System.out.println("The time server is start in port: " + port);
            Socket socket = null;
            TimerServerHandlerExecutorPool singleExecutor = new TimerServerHandlerExecutorPool(50, 10000);
            while (true) {
                socket = server.accept();
                //开启一个线程处理请求
                singleExecutor.execute(new TimerServerHandler(socket));
            }
        } finally {
            if (server != null) {
                System.out.println("The time server close");
                server.close();
                server = null;
            }
        }
    }
}
