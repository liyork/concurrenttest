package com.wolf.concurrenttest.hcpta.immutable;

import com.wolf.concurrenttest.mtadp.common.Utils;

import java.util.stream.IntStream;

/**
 * Description: 线程安全
 * 不可变
 * Created on 2021/9/24 6:32 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public final class IntegerAccumulator2 {  // 不可变对象不允许被继承，防止由于继承重写而导致失去线程安全性
    // 不可变
    private final int init;

    public IntegerAccumulator2(int init) {
        this.init = init;
    }

    // 用另一个accumulator和初始化值
    public IntegerAccumulator2(IntegerAccumulator2 accumulator, int init) {
        this.init = accumulator.getValue() + init;
    }

    // 每次相加都产生一个新对象，不操作原有对象
    public IntegerAccumulator2 add(int i) {
        return new IntegerAccumulator2(this, i);
    }

    public int getValue() {
        return this.init;
    }

    public static void main(String[] args) {
        IntegerAccumulator2 accumulator = new IntegerAccumulator2(0);
        IntStream.range(0, 3).forEach(i -> new Thread(() -> {
            int inc = 0;

            while (true) {
                int oldValue = accumulator.getValue();
                // 各线程操作自己对象，不对别人有影响，就是各自操作各自的了
                int result = accumulator.add(inc).getValue();
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
