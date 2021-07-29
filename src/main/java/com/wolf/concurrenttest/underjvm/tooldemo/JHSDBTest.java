package com.wolf.concurrenttest.underjvm.tooldemo;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Description: JHSDB测试代码
 * 由于JHSDB本身对压缩指针的支持存在缺陷，建议用64位系统时禁用压缩指针
 * -Xmx10m -XX:+UseSerialGC -XX:-UseCompressedOops
 * Created on 2021/7/21 11:41 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class JHSDBTest {
    static class Test {
        // 随着Test的类型信息存放在方法区
        static ObjectHolder staticObj = new ObjectHolder();
        // 随着Test的对象实例存放在java堆
        ObjectHolder instanceObj = new ObjectHolder();

        void foo() throws InterruptedException {
            // 存放在foot方法栈帧的局部变量表
            ObjectHolder localObj = new ObjectHolder();
            System.out.println("done");
            TimeUnit.SECONDS.sleep(1111);
        }
    }

    // 为了统一查找方便，三个变量都用了一种类型
    private static class ObjectHolder {
    }

    public static void main(String[] args) throws InterruptedException {
        Properties properties = System.getProperties();
        properties.get("version");
        Test test = new Test();
        test.foo();
    }
}
