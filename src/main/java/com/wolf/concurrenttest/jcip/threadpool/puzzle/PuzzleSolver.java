package com.wolf.concurrenttest.jcip.threadpool.puzzle;

import java.util.concurrent.atomic.*;

/**
 * Solver that recognizes when no solution exists
 * 解决ConcurrentPuzzleSolver可能没有真正结果导致的无法退出问题
 * 对于时限可以：
 * + time limit, 让getValue中done.await();使用时间，and shutting down the Executor and declaring failure if getValue times out
 * + another is some sort of puzzle-specific metric such as searching only up to a certain number of positions
 * + provide a cancellation mechanism and let the client make its own decision about when to stop searching
 */
public class PuzzleSolver<P, M> extends ConcurrentPuzzleSolver<P, M> {
    PuzzleSolver(Puzzle<P, M> puzzle) {
        super(puzzle);
    }

    private final AtomicInteger taskCount = new AtomicInteger(0);

    protected Runnable newTask(P p, M m, PuzzleNode<P, M> n) {
        return new CountingSolverTask(p, m, n);
    }

    // keep a cont of active solver tasks and set the solution to null when the count drops to zero
    class CountingSolverTask extends SolverTask {
        CountingSolverTask(P pos, M move, PuzzleNode<P, M> prev) {
            super(pos, move, prev);
            taskCount.incrementAndGet();// 每启动一个线程都+1
        }

        public void run() {
            try {
                super.run();
            } finally {
                // 由最后一个线程执行设定null通知main线程
                if (taskCount.decrementAndGet() == 0) solution.setValue(null);
            }
        }
    }
}
