package com.wolf.concurrenttest.program.serverclient.advantage;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/20
 */
public class ConcurrentReportCommand extends ConcurrentCommand {

    public ConcurrentReportCommand(String[] command) {
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
        System.out.println("ConcurrentReportCommand execute...");
        return "ConcurrentReportCommand";
    }
}
