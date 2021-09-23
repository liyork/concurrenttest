package com.wolf.concurrenttest.hcpta.singleton;

import java.net.Socket;
import java.sql.Connection;

/**
 * Description: Double-Check
 * 满足懒加载，保证instance实例唯一性，
 * 多线程下可能引起空指针异常，因为
 * 在构造函数中，需要实例conn和socket两个资源，还有Singleton自身，根据jvm运行时指令重排序和Happens-before规则，
 * 这三者之间的实例化顺序并无前后关系约束，可以重排序(编译时会将实例方法都放入init方法中,可能instance/conn/socket重排序)
 * 可能instance实例化后而conn和socket可能未被初始化
 * Created on 2021/9/23 9:05 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public final class Singleton3 {  // 不允许被继承
    private byte[] data = new byte[1024];

    // 类变量，仅定义
    private static Singleton3 instance = null;
    Connection conn;
    Socket socket;

    // 私有构造函数
    private Singleton3() {
        // 模拟初始化
        this.conn = null;
        this.socket = null;
    }

    public static Singleton3 getInstance() {
        if (null == instance) {  // 这个判断，避免每次都进入同步代码块，提高效率
            // 为null时，进入同步代码块，只有一个线程获得Singleton3.class关联的monitor
            synchronized (Singleton3.class) {
                // 获取锁后再次判断
                if (null == instance) {
                    instance = new Singleton3();

                }
            }
        }
        return instance;
    }
}
