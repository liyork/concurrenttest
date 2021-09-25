package com.wolf.concurrenttest.hcpta.immutable;

import com.wolf.concurrenttest.mtadp.common.Utils;

import java.util.stream.IntStream;

/**
 * Description: 线程安全
 * 对init的操作加同步
 * Created on 2021/9/24 6:32 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class IntegerAccumulator1 {
    private int init;

    public IntegerAccumulator1(int init) {
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
        IntegerAccumulator1 accumulator = new IntegerAccumulator1(0);
        IntStream.range(0, 3).forEach(i -> new Thread(() -> {
            int inc = 0;
            while (true) {
                int oldValue;
                int result;
                // 在线程的逻辑执行单元中增加同步控制对多个操作，保证其原子性
                synchronized (IntegerAccumulator1.class) {
                    oldValue = accumulator.getValue();
                    result = accumulator.add(inc);
                }
                System.out.println(oldValue + "+" + inc + "=" + result);
                if (inc + oldValue != result) {
                    System.out.println("ERROR:" + oldValue + "+" + inc + "=" + result);
                }
                inc++;
                Utils.slowly(1);
            }
        }).start());
    }
}
