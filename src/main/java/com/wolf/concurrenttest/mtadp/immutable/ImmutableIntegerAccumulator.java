package com.wolf.concurrenttest.mtadp.immutable;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * Description: 不可变对象,final修饰，共享资源的操作不能被重写，不给外部修改资源的机会
 *
 * @author 李超
 * @date 2019/02/01
 */
public final class ImmutableIntegerAccumulator {//不可变对象不允许继承

    private final int init;//不允许修改，只能构造时赋值

    public ImmutableIntegerAccumulator(int init) {
        this.init = init;
    }

    public ImmutableIntegerAccumulator(ImmutableIntegerAccumulator accumulator, int init) {
        this.init = accumulator.getValue() + init;
    }

    public ImmutableIntegerAccumulator add(int i) {
        return new ImmutableIntegerAccumulator(this, i);
    }

    public int getValue() {
        return this.init;
    }

    public static void main(String[] args) {

        ImmutableIntegerAccumulator accumulator = new ImmutableIntegerAccumulator(0);

        IntStream.range(0, 3).forEach(i -> new Thread(() -> {
            int inc = 0;
            while (true) {

                int oldValue = accumulator.getValue();
                //每次add之后都是一个新值，在新对象的值上+1
                int result = accumulator.add(inc).getValue();

                System.out.println(oldValue + "+" + inc + "=" + result);
                if ((inc + oldValue) != result) {
                    System.out.println("Error:" + oldValue + "+" + inc + "!=" + result);
                }
                inc++;

                try {
                    TimeUnit.MILLISECONDS.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start());
    }
}
