package com.wolf.concurrenttest.bfbczm.foundation2;

/**
 * Description: 测试缓存行的竞争，用列写数组-慢
 * Created on 2021/9/4 7:21 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class TestForContent2 {
    static final int LINE_NUM = 1024;
    static final int COLUM_NUM = 1024;

    public static void main(String[] args) {
        long[][] array = new long[LINE_NUM][COLUM_NUM];

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < COLUM_NUM; i++) {
            for (int j = 0; j < LINE_NUM; j++) {
                array[j][i] = i * 2 + j;  // 按列填充
            }
        }
        long endTime = System.currentTimeMillis();
        long cacheTime = endTime - startTime;
        System.out.println("no cache time:" + cacheTime);
    }
}
