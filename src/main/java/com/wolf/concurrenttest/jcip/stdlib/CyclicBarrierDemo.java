package com.wolf.concurrenttest.jcip.stdlib;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Description: 演示CyclicBarrier
 * Barrieers are similar to latches in that they block a group of threads until some event has occurred.
 * the key difference is that with a barrier, all the threads must come together at a barrier point at the same time in
 * order to proceed.
 * latches are for waiting for events' barriers are for waiting for other threads.像是开晨会，所有人都到齐了再开始进行下一步
 * <p>
 * CyclicBarrier allows a fixed number of parties to rendezvous repeatedly at a barrier point and is userful in parallel
 * iterative alogorithm that break down a problem into a fix number of independent subproblems.
 * 线程一旦到达则await，都到达则都释放开。并获得一个到达index，这时Barrier可以重用。
 * 若有一个超时或被interrupted那么所有调用await的会抛出异常BrokenBarrierException.
 * <p>
 * for computational problems like this that do no I/O and access no shared data, Ncpu or Ncpu+1 threads yield optimal
 * throuhput; more thread do not help, and may in fact degrade performance as the threads compete for CPU and memory resources
 * <p>
 * 另一种barrier形式参见ExchangerTest
 * Created on 2021/6/30 7:24 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class CyclicBarrierDemo {
    private final Board mainBoard;
    private final CyclicBarrier barrier;
    private final Worker[] workers;

    public CyclicBarrierDemo(Board board) {
        this.mainBoard = board;
        int count = Runtime.getRuntime().availableProcessors();
        this.barrier = new CyclicBarrier(count, () -> mainBoard.commitNewValues());// 都到达则执行
        this.workers = new Worker[count];
        for (int i = 0; i < count; i++) {
            workers[i] = new Worker(mainBoard.getSubBoard(count, i));
        }
    }

    class Worker implements Runnable {
        private final Board board;

        public Worker(Board board) {
            this.board = board;
        }

        @Override
        public void run() {
            // 各自执行各自的部分，完成后等待其他
            while (!board.hasConverged()) {
                // 计算任务，并设定新任务
                for (int i = 0; i < board.getMaxX(); i++) {
                    for (int y = 0; y < board.getMaxY(); y++) {
                        board.setNewValue(i, y, computeValue(i, y));
                    }
                }
                try {
                    barrier.await();  // 等待下一轮继续操作
                } catch (InterruptedException ex) {
                    return;
                } catch (BrokenBarrierException ex) {
                    return;
                }
            }
        }
    }

    private Object computeValue(int i, int y) {
        return null;
    }

    public void start() {
        for (int i = 0; i < workers.length; i++) {
            mainBoard.awitForConvergence();
        }
    }

    private class Board {
        public void commitNewValues() {
        }

        public Board getSubBoard(int count, int i) {
            return null;
        }

        public boolean hasConverged() {
            return false;
        }

        public void awitForConvergence() {
        }

        public int getMaxX() {
            return 0;
        }

        public int getMaxY() {
            return 0;
        }

        public void setNewValue(int i, int y, Object computeValue) {
        }
    }
}
