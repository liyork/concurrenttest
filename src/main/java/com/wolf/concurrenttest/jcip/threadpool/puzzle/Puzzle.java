package com.wolf.concurrenttest.jcip.threadpool.puzzle;

import java.util.*;

/**
 * Abstraction for puzzles like the 'sliding blocks puzzle'
 */
public interface Puzzle<P, M> {// position and move

    P initialPosition();

    // 目标p是否所要的
    boolean isGoal(P position);

    // computing the list of legal moves from a given position
    Set<M> legalMoves(P position);

    // computing the result of applying a move to a position
    P move(P position, M move);
}
