package com.wolf.concurrenttest.jvm;


import java.util.HashMap;

/**
 * Description:
 * <br/> Created on 2017/6/26 9:43
 *
 * @author 李超
 * @since 1.0.0
 */
public class JVMTest {

    public static void main(String[] args) {
        //testHook();
    }

    private static void testHook() {
        final Runtime runtime = Runtime.getRuntime();
        runtime.addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.printf("maxMemory : %.2fM\n", runtime.maxMemory()*1.0/1024/1024);
                System.out.printf("totalMemory : %.2fM\n", runtime.totalMemory()*1.0/1024/1024);
                System.out.printf("freeMemory : %.2fM\n", runtime.freeMemory()*1.0/1024/1024);
            }
        });

        HashMap<String, String> hashMap = new HashMap<String, String>();
        for(int i = 0; i < 10; i++) {
            String key = "i" + i;
            hashMap.put(key, "i" + i + 2);
            System.out.println("key:" + key + " ");
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.exit(0);//如果使用intellij的stop按钮，不会触发钩子
    }
}
