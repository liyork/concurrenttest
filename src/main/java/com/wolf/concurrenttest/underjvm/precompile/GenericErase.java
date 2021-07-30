package com.wolf.concurrenttest.underjvm.precompile;

import java.util.HashMap;

/**
 * Description: 反省擦除查看反编译码和jdk5之前使用方式一样
 * javac com/wolf/concurrenttest/underjvm/precompile/GenericErase.java
 * javap -v com/wolf/concurrenttest/underjvm/precompile/GenericErase
 * 用反编译器看GenericErase.class，泛型类型变回了裸类型
 * Created on 2021/7/29 6:44 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class GenericErase {
    public static void main(String[] args) {
        beforeErase();
        afterErase();
    }

    private static void beforeErase() {
        HashMap<String, String> map = new HashMap<>();
        map.put("hello", "111");
        map.put("how are you?", "2222");
        System.out.println(map.get("hello"));
        System.out.println(map.get("how are you?"));
    }

    private static void afterErase() {
        HashMap map = new HashMap();
        map.put("hello", "111");
        map.put("how are you?", "2222");
        System.out.println((String) map.get("hello"));
        System.out.println((String) map.get("how are you?"));
    }
}
