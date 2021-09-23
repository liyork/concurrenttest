package com.wolf.concurrenttest.hcpta.singleton;

import java.net.Socket;
import java.sql.Connection;

/**
 * Description: Volatile+Double-Check
 * volatile可以防止重排序发生，
 * todo 那conn和socket是不是要放在其上?
 * Created on 2021/9/23 9:05 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public final class Singleton4 {  // 不允许被继承
    private byte[] data = new byte[1024];

    // 类变量，仅定义
    private volatile static Singleton4 instance = null;
    Connection conn;
    Socket socket;

    // 私有构造函数
    private Singleton4() {
        // 模拟初始化
        this.conn = null;
        this.socket = null;
    }

    public static Singleton4 getInstance() {
        if (null == instance) {  // 这个判断，避免每次都进入同步代码块，提高效率
            // 为null时，进入同步代码块，只有一个线程获得Singleton3.class关联的monitor
            synchronized (Singleton4.class) {
                // 获取锁后再次判断
                if (null == instance) {
                    instance = new Singleton4();

                }
            }
        }
        return instance;
    }
}
