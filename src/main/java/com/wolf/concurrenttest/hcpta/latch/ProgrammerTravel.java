package com.wolf.concurrenttest.hcpta.latch;

import com.wolf.concurrenttest.mtadp.common.Utils;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Description: 程序员旅游线程——各自执行，最终到达一个地点
 * 模拟程序员乘坐不同的交通工具到达指定地点
 * Created on 2021/9/25 3:58 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ProgrammerTravel extends Thread {
    private final Latch latch;
    private final String programmer;
    // 交通工具
    private final String transportation;

    public ProgrammerTravel(Latch latch, String programmer, String transportation) {
        this.latch = latch;
        this.programmer = programmer;
        this.transportation = transportation;
    }

    @Override
    public void run() {
        System.out.println(programmer + " start take the transportation [" + transportation + "]");
        // 模拟路上花费时间
        Utils.slowly(ThreadLocalRandom.current().nextInt(10));
        System.out.println(programmer + " arrived by" + transportation);
        latch.countDown();
    }
}
