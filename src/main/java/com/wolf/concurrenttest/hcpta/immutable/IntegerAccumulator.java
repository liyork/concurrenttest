package com.wolf.concurrenttest.hcpta.immutable;

import com.wolf.concurrenttest.mtadp.common.Utils;

import java.util.stream.IntStream;

/**
 * Description: 非线程安全
 * 共享资源被多个线程操作，未加任何同步控制，出现数据不一致问题
 * Created on 2021/9/24 6:32 PM
 *
 * @author 李超
 * @version 0.0.1
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
                int oldValue = accumulator.getValue();
                // 多线程操作，可能被多加或者覆盖等异常数据冲突
                int result = accumulator.add(inc);
                System.out.println(oldValue + "+" + inc + "=" + result);
                if (inc + oldValue != result) {
                    System.out.println("ERROR:" + oldValue + "+" + inc + "=" + result);
                }
                inc++;
                Utils.slowly();
            }
        }).start());
    }
}
