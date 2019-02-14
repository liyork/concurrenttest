package com.wolf.concurrenttest.mtadp.eda.arch;

import com.wolf.concurrenttest.mtadp.eda.arch.sync.Event;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/13
 */
public class ResultEvent extends Event {

    private final int result;

    public ResultEvent(int result) {
        this.result = result;
    }

    public int getResult() {
        return result;
    }
}
