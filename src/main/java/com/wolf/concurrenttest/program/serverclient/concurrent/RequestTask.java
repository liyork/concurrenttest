package com.wolf.concurrenttest.program.serverclient.concurrent;

import com.wolf.concurrenttest.program.serverclient.Command;
import com.wolf.concurrenttest.program.serverclient.ErrorCommand;
import com.wolf.concurrenttest.program.serverclient.QueryCommand;
import com.wolf.concurrenttest.program.serverclient.ReportCommand;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/20
 */
public class RequestTask implements Runnable {

    private final Socket clientSocket;

    public RequestTask(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {

        try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));) {

            String line = in.readLine();
            Logger.sendMessage(line);
            ParallelCache cache = ConcurrentServer.getCache();
            String ret = cache.get(line);

            if (ret == null) {
                Command command;

                String[] commandData = line.split(";");
                System.out.println("command:" + commandData[0]);
                switch (commandData[0]) {
                    case "q":
                        System.out.println("query");
                        command = new QueryCommand(commandData);
                        break;
                    case "r":
                        System.out.println("report");
                        command = new ReportCommand(commandData);
                        break;
                    case "z":
                        System.out.println("stop");
                        command = new ConcurrentStopCommand(commandData);
                        break;
                    case "s":
                        System.out.println("status");
                        command = new ConcurrentStatusCommand(commandData);
                        break;
                    default:
                        System.out.println("error");
                        command = new ErrorCommand(commandData);
                        break;
                }
                ret = command.execute();
                if (command.isCacheable()) {
                    cache.put(line, ret);
                }
            } else {
                Logger.sendMessage("command " + line + " was found in the cache");
            }
            out.println(ret);

            System.out.println(ret);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
