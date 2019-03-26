package com.wolf.concurrenttest.program.serverclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Description: 串行服务器版本
 * q;xx;xx;xx
 *
 * @author 李超
 * @date 2019/02/20
 */
public class SerialServer {

    public static void main(String[] args) {

        boolean stopServer = false;
        System.out.println("initialization completed.");

        try {
            ServerSocket serverSocket = new ServerSocket(8080);

            do {
                try {
                    Socket clientSocket = serverSocket.accept();
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                    String line = in.readLine();
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
                            command = new StopCommand(commandData);
                            stopServer = true;
                            break;
                        default:
                            System.out.println("error");
                            command = new ErrorCommand(commandData);
                    }
                    String response = command.execute();
                    out.println(response);

                    System.out.println(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (!stopServer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
