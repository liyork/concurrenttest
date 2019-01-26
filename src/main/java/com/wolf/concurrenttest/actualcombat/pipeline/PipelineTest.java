package com.wolf.concurrenttest.actualcombat.pipeline;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Description:
 * 由于任务无法分解，相互具有依赖关系，但是还要产生大量任务。那么使用流水线操作
 * (a + b ) * a /2
 * 步骤一：c = a + b
 * 步骤二：d = c * a
 * 步骤三：d / 2
 * 每个线程只干自己的工作，处理好了给下个流水线的线程。
 * <br/> Created on 23/03/2018 11:03 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class PipelineTest {

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        executorService.execute(new Plus());
        executorService.execute(new Multiply());
        executorService.execute(new Divide());

        for (int i = 0; i < 1000; i++) {
            for (int j = 0; j < 1000; j++) {
                Plus.blockingQueue.put(new ComputerTransfer(i,j,"("+i+"+"+j+")*"+i+"/2"));
            }
        }

    }
}
