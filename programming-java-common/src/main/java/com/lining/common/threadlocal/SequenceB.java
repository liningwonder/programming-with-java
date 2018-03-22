package com.lining.common.threadlocal;

/**
 * description: safe implemention
 * 各个线程互不干扰 这也就是线程局部变量的意思
 * date 2018-03-22
 *
 * @author lining1
 * @version 1.0.0
 */
public class SequenceB implements Sequence {

    private static ThreadLocal<Integer> numberContainer = new ThreadLocal<Integer>(){
        @Override
        public Integer initialValue() {
            return 0;
        }
    };

    @Override
    public int getNumber() {
        numberContainer.set(numberContainer.get() + 1);
        return numberContainer.get();
    }
}
