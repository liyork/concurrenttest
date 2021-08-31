package com.wolf.concurrenttest.taojcp.tools;

import java.util.Map;
import java.util.concurrent.*;

/**
 * Description: 银行流水处理服务类
 * Created on 2021/8/31 1:11 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class BankWaterService implements Runnable {
    // 4个屏障，处理完后执行当前类的run
    private CyclicBarrier c = new CyclicBarrier(4, this);

    private ExecutorService executor = Executors.newFixedThreadPool(4);

    private ConcurrentHashMap<String, Integer> sheetBankWaterCount = new ConcurrentHashMap<>();

    private void count() {
        for (int i = 0; i < 4; i++) {
            executor.execute(() -> {
                sheetBankWaterCount.put(Thread.currentThread().getName(), 1);
                try {
                    // 计算完成
                    c.await();
                } catch (BrokenBarrierException | InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    public void run() {
        // 汇总数据
        int result = 0;
        for (Map.Entry<String, Integer> sheet : sheetBankWaterCount.entrySet()) {
            result += sheet.getValue();
        }
        sheetBankWaterCount.put("result", result);
        System.out.println(result);
    }

    public static void main(String[] args) {
        BankWaterService bankWaterService = new BankWaterService();
        bankWaterService.count();
        bankWaterService.executor.shutdown();
    }
}
