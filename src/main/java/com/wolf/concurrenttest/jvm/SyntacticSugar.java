package com.wolf.concurrenttest.jvm;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Description:
 * <br/> Created on 11/6/17 8:25 PM
 *
 * @author 李超
 * @since 1.0.0
 */
public class SyntacticSugar {

    //语法糖包含泛型、可变参数、自动装箱、拆箱、for循环
    public static void main(String[] args) {
//        demo();
        errorUsage();
    }

    private static void demo() {
        Integer[] integers = {1, 2, 3, 4};
        ArrayList<Integer> list = new ArrayList<>(integers.length);
        Collections.addAll(list, integers);

        int sum = 0;
        for (Integer integer : list) {
            sum += integer;
        }

        System.out.println(sum);
    }

    private static void errorUsage() {
        Integer a = 1;
        Integer b = 2;
        Integer c = 3;
        Integer d = 3;
        Integer e = 321;
        Integer f = 321;
        Long g = 3L;
        System.out.println(c == d);//缓存
        System.out.println(e == f);//没有缓存
        //包装类的等号，遇到运算自动拆箱
        System.out.println(d == (a + b));
        System.out.println(g == (a + b));
        System.out.println(g.equals(a + b));//类型不同，直接false
    }
}
