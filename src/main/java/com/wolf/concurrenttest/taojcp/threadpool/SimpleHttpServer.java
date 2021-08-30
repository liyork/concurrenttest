package com.wolf.concurrenttest.taojcp.threadpool;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Description: 利用线程池(来连接就交给线程池执行runnable)，简单实现http服务
 * 测试不同线程数下，SimpleHttpServer的吞吐量表现
 * 场景：5000次请求，10个线程并发执行，测试内容主要考察响应时间(越小越好)和每秒查询的数量(越高越好)
 * ab -n 5000 -c 10 http://localhost:8080/index.html
 * <p>
 * Created on 2021/8/27 6:25 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class SimpleHttpServer {
    // 处理HttpRequest的线程池
    static ThreadPool<HttpRequestHandler> threadPool = new DefaultThreadPool<>(1);  // 5, 10个线程处理
    // 根路径
    static String basePath;
    static ServerSocket serverSocket;

    static int port = 8080;

    public static void setPort(int port) {
        if (port > 0) {
            SimpleHttpServer.port = port;
        }
    }

    public static void setBasePath(String basePath) {
        if (basePath != null && new File(basePath).exists() && new File(basePath).isDirectory()) {
            SimpleHttpServer.basePath = basePath;
        }
    }

    // 每个请求一个HttpRequestHandler，交到线程池
    public static void start() throws Exception {
        serverSocket = new ServerSocket(port);
        Socket socket;
        while ((socket = serverSocket.accept()) != null) {
            // 接收到客户端Socket，包装，放入线程池，main线程继续处理下个请求
            threadPool.execute(new HttpRequestHandler(socket));
        }
        serverSocket.close();
    }

    static class HttpRequestHandler implements Runnable {
        private Socket socket;

        public HttpRequestHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            String line;
            BufferedReader outputReader = null;
            BufferedReader inputReader = null;
            PrintWriter out = null;
            InputStream in = null;
            try {
                inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String header = inputReader.readLine();
                // header为'GET /index.html HTTP/1.0'
                String filePath = basePath + header.split(" ")[1];
                out = new PrintWriter(socket.getOutputStream());

                if (filePath.endsWith("jpg") || filePath.endsWith("ico")) {
                    // 读取资源并输出
                    in = new FileInputStream(filePath);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    int i;
                    while ((i = in.read()) != -1) {
                        baos.write(i);
                    }
                    byte[] array = baos.toByteArray();
                    out.println("HTTP/1.1 200 OK");
                    out.println("Server: Molly");
                    out.println("Content-Type: image/jpeg");
                    out.println("Content-Length: " + array.length);
                    out.println("");
                    socket.getOutputStream().write(array, 0, array.length);
                } else {
                    outputReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
                    out = new PrintWriter(socket.getOutputStream());
                    out.println("HTTP/1.1 200 OK");
                    out.println("Server: Molly");
                    out.println("Content-Type: text/html; charset=UTF-8");
                    out.println("");
                    while ((line = outputReader.readLine()) != null) {
                        out.println(line);
                    }
                }
                out.flush();
            } catch (Exception e) {
                out.println("HTTP/1.1 500");
                out.println("");
                out.flush();
            } finally {
                close(outputReader, in, inputReader, out, socket);
            }
        }
    }

    private static void close(Closeable... closeables) {
        for (Closeable closeable : closeables) {
            if (closeables != null) {
                try {
                    closeable.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        String path = SimpleHttpServer.class.getClassLoader().getResource("").getPath();
        setBasePath(path.substring(0, path.length() - 1));
        start();
    }
}
