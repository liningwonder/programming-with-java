package com.lining.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

/**
 * description:
 * date 2018-03-27
 *
 * @author lining1
 * @version 1.0.0
 */
public class LifeCycleWebServer {

    private static final Logger LOG = LoggerFactory.getLogger(LifeCycleWebServer.class);
    private static final int NTHREADS = 100;
    private static final ExecutorService exec = Executors.newFixedThreadPool(NTHREADS);

    private void hadnleRequest(Socket socket) {
        System.out.println("Ha ha");
    }

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(80);
        while (!exec.isTerminated()) {
            try {
                final Socket socket = serverSocket.accept();
                exec.execute(new Runnable() {
                    @Override
                    public void run() {
                        hadnleRequest(socket);
                    }
                });
            } catch (RejectedExecutionException e) {
                if (!exec.isShutdown()) {
                    LOG.info("task submission rejected");
                }
            }
        }
    }

    public void stop() {
        exec.shutdownNow();
    }
}
