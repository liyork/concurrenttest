package com.wolf.concurrenttest.program.serverclient;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/20
 */
public class QueryCommand extends Command {

    public QueryCommand(String[] command) {
        super(command);
    }

    @Override
    public String execute() {
        System.out.println("QueryCommand execute...");
        return "QueryCommand";
    }
}
