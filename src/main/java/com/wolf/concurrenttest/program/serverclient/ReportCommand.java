package com.wolf.concurrenttest.program.serverclient;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/20
 */
public class ReportCommand extends Command {

    public ReportCommand(String[] command) {
        super(command);
    }

    @Override
    public String execute() {
        System.out.println("ReportCommand execute...");
        return "ReportCommand";
    }
}
