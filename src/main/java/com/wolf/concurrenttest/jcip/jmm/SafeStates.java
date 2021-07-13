package com.wolf.concurrenttest.jcip.jmm;

import net.jcip.annotations.ThreadSafe;

import java.util.HashMap;
import java.util.Map;

/**
 * Description: Initialization Safety for Immutable Object
 * 字段需要final，仅有构造方法对字段进行初始化，其他方法不能更新，只能获取。
 * Initialization safety means that SafeStates could be safely published event through unsafe lazy initialization or
 * stashing a reference to a SafeStates in a public static field with no synchronization, even
 * though it uses no synchronization and relies on the non-thread-safe HashMap
 * <p>
 * however, a number of small changes to SafeStates would take away its thread safety.
 * if states were not final, or if any methdo other than the constructor modified its contents
 * allowing the object to escape during construction invalidates the intialization-sfety guarantee
 * Created on 2021/7/13 1:13 PM
 *
 * @author 李超
 * @version 0.0.1
 */
@ThreadSafe
public class SafeStates {
    private final Map<String, String> states;

    public SafeStates() {
        states = new HashMap<>();
        states.put("aaa", "AK");
        states.put("bbb", "AL");
    }

    public String getAbbreviation(String s) {
        return states.get(s);
    }
}
