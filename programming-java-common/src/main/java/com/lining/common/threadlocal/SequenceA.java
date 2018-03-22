package com.lining.common.threadlocal;

/**
 * description: unsafe implemention
 * 由于number是共享的，所以当并发出现时，这个是不安全的
 * date 2018-03-22
 *
 * @author lining1
 * @version 1.0.0
 */
public class SequenceA implements Sequence {

    /**
     * 线程之间竟然共享了 static 变量, 但是不安全
     */
    private static int number = 0;

    @Override
    public int getNumber() {
        number = number + 1;
        return number;
    }
}
