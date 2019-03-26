package com.wolf.concurrenttest.program.serverclient.concurrent;

import java.util.concurrent.ThreadPoolExecutor;

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
    public String execute() {

        StringBuilder sb = new StringBuilder();
        ThreadPoolExecutor executor = ConcurrentServer.getExecutor();
        Logger.sendMessage(sb.toString());
        return sb.toString();
    }
}
