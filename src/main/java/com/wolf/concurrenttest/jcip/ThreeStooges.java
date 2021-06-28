package com.wolf.concurrenttest.jcip;

import java.util.HashSet;
import java.util.Set;

/**
 * Description: immutable Class Build Out of Mutable Underlying Objects，只要可变属性不对外即可，内部维护一致性s
 * Created on 2021/6/27 1:48 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ThreeStooges {
    private final Set<String> stooges = new HashSet<>();

    public ThreeStooges() {
        stooges.add("a");
        stooges.add("b");
        stooges.add("c");
    }

    public boolean isStooge(String name) {
        return stooges.contains(name);
    }
}
