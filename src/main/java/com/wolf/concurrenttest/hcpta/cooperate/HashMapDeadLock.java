package com.wolf.concurrenttest.hcpta.cooperate;

import java.util.HashMap;

/**
 * Description:
 * Created on 2021/9/18 6:20 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class HashMapDeadLock {
    private final HashMap<String, String> map = new HashMap<>();

    public void add(String key, String value) {
        this.map.put(key, value);
    }

    public static void main(String[] args) {
        final HashMapDeadLock hmdl = new HashMapDeadLock();
        for (int x = 0; x < 2; x++) {
            new Thread(() -> {
                for (int i = 1; i < Integer.MAX_VALUE; i++) {
                    hmdl.add(String.valueOf(i), String.valueOf(i));
                }
            }).start();
        }
    }
}
