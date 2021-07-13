package com.wolf.concurrenttest.jcip.confine;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Description: 线程内约束变量不会被其他线程看到(局部、threadlocal)
 * Created on 2021/6/27 1:00 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ThreadConfinement {
    // 方法内的局部animals变量，不会有任何线程问题
    public int loadTheArk(Collection<String> candidates) {
        SortedSet<String> animals;
        int numPairs = 0;
        String candidate = null;

        // animals confined to method, don't let them escape!
        animals = new TreeSet<>();
        animals.addAll(candidates);

        for (String animal : animals) {
            System.out.println(animal);
        }
        return 0;
    }

    // threadLocal能避免方法传递，还能保证连接线程安全。每个线程有自己的连接
    // 也用于频繁的临时变量的使用
    private static ThreadLocal<Connection> connectionHolder = ThreadLocal.withInitial(() -> {
        try {
            return DriverManager.getConnection("DB_URL");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    });

    public static Connection getConnection() {
        return connectionHolder.get();
    }
}
