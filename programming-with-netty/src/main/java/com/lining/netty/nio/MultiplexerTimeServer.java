package com.lining.netty.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * description:
 * date 2018-04-04
 *
 * @author lining1
 * @version 1.0.0
 */
public class MultiplexerTimeServer implements Runnable {

    private Selector selector;

    private ServerSocketChannel serverChannel;

    private volatile boolean stop;

    //构造器初始化多路复用器，并绑定监听端口
    public MultiplexerTimeServer(int port) {
        try {
            //多路复用器
            selector = Selector.open();
            serverChannel = ServerSocketChannel.open();
            //设置为非阻塞模式
            serverChannel.configureBlocking(false);
            //绑定端口，监听客户端连接
            serverChannel.socket().bind(new InetSocketAddress(port), 1024);
            //将时间注册到多路复用器,并监听ACCEPT事件
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("The time server is start in port : " + port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void stop() {
        this.stop = true;
    }

    @Override
    public void run() {
        while (!stop) {
            try {
                //selector 休眠时间1s
                selector.select(1000);
                // 返回就绪的channel，获取事件的key
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectedKeys.iterator();
                SelectionKey key = null;
                //轮询就绪的key
                while (it.hasNext()) {
                    key = it.next();
                    it.remove();
                    try {
                        //处理事件
                        handleInput(key);
                    } catch (Exception e) {
                        //多路复用器关闭后，其上面注册的资源也会关闭
                        if (key != null) {
                            key.cancel();
                            if (key.channel() != null) {
                                key.channel().close();
                            }
                        }
                        e.printStackTrace();
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        if (selector != null) {
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleInput(SelectionKey key) throws IOException {
        if (key.isValid()) {
            if (key.isAcceptable()) {
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                //监听请求，完成TCP三次握手
                SocketChannel sc = ssc.accept();
                //设置客户端链路为非阻塞模式
                sc.configureBlocking(false);
                //将新的客户端连接注册到多路复用器上
                sc.register(selector, SelectionKey.OP_ACCEPT);
            }
            if (key.isReadable()) {
                //获取Channel
                SocketChannel sc = (SocketChannel) key.channel();
                //创建大小为1024B的缓冲区，即1M，
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                //异步读取消息到缓冲区
                int readBytes = sc.read(readBuffer);
                //>0 读到了字节
                if (readBytes > 0) {
                    readBuffer.flip();
                    byte[] bytes = new byte[readBuffer.remaining()];
                    //吧消息转换成BUffer
                    readBuffer.get(bytes);
                    String body = new String(bytes, "UTF-8");
                    System.out.println("The time server recieve order :" + body);
                    String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? String.valueOf(System.currentTimeMillis()) : "BAD ORDER";
                    //响应
                    doWrite(sc, currentTime);
                    //< 0 链路以关闭，释放资源
                } else if (readBytes < 0) {
                    key.cancel();
                    sc.close();
                } else {;}
            }
        }
    }

    //将应答消息异步发回客户端
    private void doWrite(SocketChannel channel, String response) throws IOException {
        if (response != null && response.trim().length() > 0) {
            //将字符串编码为字节数组
            byte[] bytes = response.getBytes();
            //根据字节数组长度开辟缓冲区
            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
            //复制到缓冲区
            writeBuffer.put(bytes);
            //缓冲区flip操作
            writeBuffer.flip();
            //异步写，会出现半包问题
            channel.write(writeBuffer);
        }
    }

}
