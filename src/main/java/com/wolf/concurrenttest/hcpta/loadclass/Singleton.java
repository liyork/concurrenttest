package com.wolf.concurrenttest.hcpta.loadclass;

/**
 * Description:
 * 方式2：
 * 准备阶段：x=0,y=0,instance=null
 * 初始化阶段：x=0,y=0,instance=Singleton@1234
 * 方式1：
 * 准备阶段：instance=null,x=0,y=0
 * 初始化阶段：instance=Singleton@1234,x=0,y=1
 * Created on 2021/9/20 10:13 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class Singleton {
    private static Singleton instance = new Singleton();  // 方式1
    private static int x = 0;
    private static int y;
    //private static Singleton instance = new Singleton();  // 方式2

    private Singleton() {
        x++;
        y++;
    }

    public static Singleton getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        Singleton singleton = Singleton.getInstance();
        System.out.println(singleton.x);
        System.out.println(singleton.y);
    }
}
