package com.wolf.concurrenttest.program.serverclient.concurrent;

import com.wolf.concurrenttest.program.serverclient.Command;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/21
 */
public abstract class ConcurrentCommand extends Command  {

    public ConcurrentCommand(String[] command) {
        super(command);
    }

}
