package com.wolf.concurrenttest.hcpta.singleton;

/**
 * Description: 枚举方式，改成懒加载
 * Created on 2021/9/23 9:05 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class Singleton7 {
    private byte[] data = new byte[1024];

    private Singleton7() {
    }

    // 用枚举充当holder
    private enum EnumHolder {
        INSTANCE;
        private Singleton7 instance;

        EnumHolder() {
            this.instance = new Singleton7();
        }

        private Singleton7 getInstance() {
            return instance;
        }
    }

    // 主动使用操作
    public static Singleton7 getInstance() {
        return EnumHolder.INSTANCE.getInstance();
    }
}
