package com.wolf.concurrenttest.underjvm.precompile;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Description: 自动装箱、拆箱与遍历循环，编译后再反编译查看
 * 自动拆箱、装箱在编译后被转化成了对应的包装和还原方法valueOf/intValue
 * 遍历循环把代码还原成了迭代器的实现。
 * 变长参数，在调用时变成了一个数组类型的参数
 * javac com/wolf/concurrenttest/underjvm/precompile/AutoBox.java
 * javap -v com/wolf/concurrenttest/underjvm/precompile/AutoBox
 * Created on 2021/7/29 10:08 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class AutoBox {
    public static void main(String[] args) {
        //beforeCompile();
        //afterCompile();

        beforeAutoBoxTrap();
    }

    private static void beforeCompile() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4);
        int sum = 0;
        for (int i : list) {
            sum += i;
        }
        System.out.println(sum);
    }

    private static void afterCompile() {
        List list = Arrays.asList(new Integer[]{Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3), Integer.valueOf(4)});
        int i = 0;
        for (Iterator iterator = list.iterator(); iterator.hasNext(); ) {
            int j = ((Integer) iterator.next()).intValue();
            i += j;
        }
        System.out.println(i);
    }

    // 自动装箱的陷阱
    private static void beforeAutoBoxTrap() {
        Integer a = 1;
        Integer b = 2;
        Integer c = 3;
        Integer d = 3;
        Integer e = 321;
        Integer f = 321;
        Long g = 3L;
        System.out.println(c == d);
        System.out.println(e == f);
        System.out.println(c == (a + b));
        System.out.println(c.equals(a + b));
        System.out.println(g.equals(a + b));
    }

    // 包装类的==运算在不遇到算数运算的情况下不会自动拆箱
    // equals方法不处理数据类型不一致的问题，不处理转型
    public void afterAutoBoxTrap() {
        Integer integer1 = Integer.valueOf(1);
        Integer integer2 = Integer.valueOf(2);
        Integer integer3 = Integer.valueOf(3);
        Integer integer4 = Integer.valueOf(3);
        Integer integer5 = Integer.valueOf(321);
        Integer integer6 = Integer.valueOf(321);
        Long long_ = Long.valueOf(3L);
        System.out.println((integer3 == integer4));
        System.out.println((integer5 == integer6));
        System.out.println((integer3.intValue() == integer1.intValue() + integer2.intValue()));
        System.out.println(integer3.equals(Integer.valueOf(integer1.intValue() + integer2.intValue())));
        System.out.println(long_.equals(Integer.valueOf(integer1.intValue() + integer2.intValue())));
    }
}
