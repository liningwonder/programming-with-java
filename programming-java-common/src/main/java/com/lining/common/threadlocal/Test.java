package com.lining.common.threadlocal;

/**
 * description:
 * date 2018-03-22
 *
 * @author lining1
 * @version 1.0.0
 */
public class Test {

    public static void main(String[] args) {
        Sequence sequence = new SequenceA();
        ClientThread clientThread1 = new ClientThread(sequence);
        ClientThread clientThread2 = new ClientThread(sequence);
        ClientThread clientThread3 = new ClientThread(sequence);

        clientThread1.start();
        clientThread2.start();
        clientThread3.start();

        Sequence sequence1 = new SequenceB();
        ClientThread thread1 = new ClientThread(sequence1);
        ClientThread thread2 = new ClientThread(sequence1);
        ClientThread thread3 = new ClientThread(sequence1);
        thread1.start();
        thread2.start();
        thread3.start();
    }
}
