package com.wolf.concurrenttest.hcpta.singleton;

/**
 * Description: 饿汉式
 * 若主动使用Singleton类，那么instance实例会直接完成创建，包含实例变量都得到初始化，不过可能加载后距离使用时还有些时间
 * 若一个类中的成员属性比较少，且占用的内存资源不多，此方式可以。若类成员比较重，那不推荐
 * 就是无法进行懒加载
 * Created on 2021/9/23 9:05 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public final class Singleton {  // 不允许被继承
    private byte[] data = new byte[1024];

    // 类变量，定义时直接实例化
    private static Singleton instance = new Singleton();

    // 私有构造函数
    private Singleton() {

    }

    public static Singleton getInstance() {
        return instance;
    }
}
