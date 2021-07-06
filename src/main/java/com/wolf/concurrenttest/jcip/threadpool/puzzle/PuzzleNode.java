package com.wolf.concurrenttest.jcip.threadpool.puzzle;

import net.jcip.annotations.Immutable;

import java.util.LinkedList;
import java.util.List;

/**
 * Link node for the puzzle solving framework
 * represents a position that has been reached through some series of moves, holding a reference to the
 * move that created the position and the previous node
 */
@Immutable
public class PuzzleNode<P, M> {
    final P pos;
    final M move;
    final PuzzleNode<P, M> prev;

    public PuzzleNode(P pos, M move, PuzzleNode<P, M> prev) {
        this.pos = pos;
        this.move = move;
        this.prev = prev;
    }

    // 向前查找得到路径结果
    List<M> asMoveList() {
        List<M> solution = new LinkedList<M>();
        for (PuzzleNode<P, M> n = this; n.move != null; n = n.prev)
            solution.add(0, n.move);
        return solution;
    }
}
