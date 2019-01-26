package com.wolf.concurrenttest.tooltest;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * Description:
 * <br/> Created on 12/03/2018 8:19 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class ForkJoinTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        testBase();
        testStep();
    }


    private static void testBase() throws InterruptedException, ExecutionException {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        CountTask countTask = new CountTask(1, 4);
        ForkJoinTask<Integer> result = forkJoinPool.submit(countTask);

//        result.cancel(true);
        Thread.sleep(2000);//让线程执行会

        System.out.println("countTask.isCompletedAbnormally():" + countTask.isCompletedAbnormally());
        System.out.println("result.isCompletedAbnormally():" + result.isCompletedAbnormally());

        boolean completedAbnormally = result.isCompletedAbnormally();
        if (completedAbnormally) {
            Throwable exception = result.getException();
            System.out.println(exception);
        } else {
            System.out.println("result:" + result.get());//异常或者CancellationException或者null
        }
    }


    private static class CountTask extends RecursiveTask<Integer> {

        private static final int THRESHOLD = 2;

        private int start;
        private int end;

        public CountTask(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        protected Integer compute() {
            System.out.println(Thread.currentThread().getName() + " start:" + start + " end" + end);
            int sum = 0;

            if (end - start < THRESHOLD) {//只计算两个数
                for (int i = start; i <= end; i++) {
                    sum += i;
                    //throw new RuntimeException("test for join thread exception");
                }
            } else {
                int middle = (start + end) / 2;
                CountTask countTaskLeft = new CountTask(start, middle);
                CountTask countTaskRight = new CountTask(middle + 1, end);

                countTaskLeft.fork();
                countTaskRight.fork();

                Integer leftResult = countTaskLeft.join();
                Integer rightResult = countTaskRight.join();

                sum = leftResult + rightResult;
            }
            return sum;
        }
    }

    private static void testStep() {
        int step = 100 / 10;
        int start = 0;
        int end;
        for (int i = 0; i < 10; i++) {
            end = start+step;
            if (end > 100) {
                end = 100;
            }
            System.out.println(start+"-"+end);
            start +=step+1;
        }
    }
}
