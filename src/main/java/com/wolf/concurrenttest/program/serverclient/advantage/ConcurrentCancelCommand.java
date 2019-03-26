package com.wolf.concurrenttest.program.serverclient.advantage;

import com.wolf.concurrenttest.program.serverclient.concurrent.Logger;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/20
 */
public class ConcurrentCancelCommand extends ConcurrentCommand {

    public ConcurrentCancelCommand(String[] command) {
        super(command);
    }


    @Override
    protected byte getPriority() {
        return 0;
    }

    @Override
    public boolean isCacheable() {
        return false;
    }

    @Override
    public String execute() {

        ConcurrentServer.cancelTasks(getUsername());

        String message = "Task of user " + getUsername() +
                "has been cancelled.";
        Logger.sendMessage(message);
        return message;
    }

}
