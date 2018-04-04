package com.lining.netty.nio;

/**
 * description:
 * date 2018-04-04
 *
 * @author lining1
 * @version 1.0.0
 */
public class NioTimeServer {

    public static void main(String[] args) {
        int port = 8080;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        MultiplexerTimeServer timeServer = new MultiplexerTimeServer(port);
        new Thread(timeServer, "NIO-MultiplexerTimerServer-001").start();

    }
}
