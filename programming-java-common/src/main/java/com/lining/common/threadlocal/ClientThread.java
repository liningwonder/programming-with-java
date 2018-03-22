package com.lining.common.threadlocal;

/**
 * description:
 * date 2018-03-22
 *
 * @author lining1
 * @version 1.0.0
 */
public class ClientThread extends Thread {

    private Sequence sequence;

    public ClientThread(Sequence sequence) {
        this.sequence = sequence;
    }

    @Override
    public void run() {
        for (int i = 0; i < 3; i++) {
            System.out.println(Thread.currentThread().getName() + "=>" + sequence.getNumber());
        }
    }
}
