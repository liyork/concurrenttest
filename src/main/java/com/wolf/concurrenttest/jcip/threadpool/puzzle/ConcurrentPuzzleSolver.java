package com.wolf.concurrenttest.jcip.threadpool.puzzle;

import java.util.*;
import java.util.concurrent.*;

/**
 * Concurrent version of puzzle solver
 * performs a breadth-first search，不过也是将可遍历范围交给executor，对于每次run中也是深度
 * compute next moves and evaluate the goal condition in parallel
 */
public class ConcurrentPuzzleSolver<P, M> {
    private final Puzzle<P, M> puzzle;
    private final ExecutorService exec;
    private final ConcurrentMap<P, Boolean> seen;
    //阻塞直到找到结果
    protected final ValueLatch<PuzzleNode<P, M>> solution = new ValueLatch<PuzzleNode<P, M>>();

    public ConcurrentPuzzleSolver(Puzzle<P, M> puzzle) {
        this.puzzle = puzzle;
        this.exec = initThreadPool();
        this.seen = new ConcurrentHashMap<P, Boolean>();
        if (exec instanceof ThreadPoolExecutor) {
            ThreadPoolExecutor tpe = (ThreadPoolExecutor) exec;
            // to avoid having to deal with RejectedExecutionException, the rejected execution handler should be set to discard submitted tasks.
            tpe.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        }
    }

    private ExecutorService initThreadPool() {
        return Executors.newCachedThreadPool();
    }

    public List<M> solve() throws InterruptedException {
        try {
            P p = puzzle.initialPosition();
            exec.execute(newTask(p, null, null));// 线程池执行task，task内会自己产生新task并放入池
            // block until solution found
            PuzzleNode<P, M> solnPuzzleNode = solution.getValue();// 阻塞等待结果
            return (solnPuzzleNode == null) ? null : solnPuzzleNode.asMoveList();
        } finally {
            exec.shutdown();
        }
    }

    protected Runnable newTask(P p, M m, PuzzleNode<P, M> n) {
        return new SolverTask(p, m, n);
    }

    protected class SolverTask extends PuzzleNode<P, M> implements Runnable {
        SolverTask(P pos, M move, PuzzleNode<P, M> prev) {
            super(pos, move, prev);
        }

        public void run() {
            // already solved or seen this position
            if (solution.isSet() || seen.putIfAbsent(pos, true) != null) {
                return;
            }
            if (puzzle.isGoal(pos)) {
                solution.setValue(this);
            } else {
                for (M m : puzzle.legalMoves(pos))// 当前节点的每个周边点都构造一个task执行
                    exec.execute(newTask(puzzle.move(pos, m), m, this));
            }
        }
    }
}
