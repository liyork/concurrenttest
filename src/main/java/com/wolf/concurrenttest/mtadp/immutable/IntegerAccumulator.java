package com.wolf.concurrenttest.mtadp.immutable;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/01
 */
public class IntegerAccumulator {

    private int init;

    public IntegerAccumulator(int init) {
        this.init = init;
    }

    public int add(int i) {
        this.init += i;
        return this.init;
    }

    public int getValue() {
        return this.init;
    }

    public static void main(String[] args) {

        IntegerAccumulator accumulator = new IntegerAccumulator(0);

        IntStream.range(0, 3).forEach(i -> new Thread(() -> {
            int inc = 0;
            while (true) {

                int oldValue;
                int result;
                //加锁，若只加accumulator.add则因为比对的是old和增加后的值，old获取后若是人家拿了锁，你再进入再+1就不对了
                //只对getValue和add单纯加锁没有用，合在一起就不是原子了。
                synchronized (IntegerAccumulator.class) {
                    oldValue = accumulator.getValue();
                    result = accumulator.add(inc);
                }
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
