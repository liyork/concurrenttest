package com.wolf.concurrenttest.program.serverclient.advantage;

import com.wolf.concurrenttest.program.serverclient.concurrent.Logger;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/20
 */
public class ConcurrentStatusCommand extends ConcurrentCommand {

    public ConcurrentStatusCommand(String[] command) {
        super(command);
        setCacheable(false);
    }

    private void setCacheable(boolean b) {
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

        StringBuilder sb = new StringBuilder();
        Logger.sendMessage(sb.toString());
        return sb.toString();
    }
}
