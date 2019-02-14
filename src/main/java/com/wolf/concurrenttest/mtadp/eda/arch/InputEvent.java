package com.wolf.concurrenttest.mtadp.eda.arch;

import com.wolf.concurrenttest.mtadp.eda.arch.sync.Event;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/13
 */
public class InputEvent extends Event {

    private final int x;

    private final int y;

    public InputEvent(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
