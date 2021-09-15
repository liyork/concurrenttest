package com.wolf.concurrenttest.bfbczm.action;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Description: tl的内存泄露问题
 * Created on 2021/9/14 10:20 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class HelloWorldExample extends HttpServlet {
    private static final long serialVersionUID = 1L;

    static class LocalVariable {
        private Long[] a = new Long[1024 * 1024 * 1024];
    }

    final static ThreadLocal<LocalVariable> localVariable = new ThreadLocal<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        localVariable.set(new LocalVariable());

        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        out.println("<html>");
        out.println("<head>");
        out.println("<title>" + "title" + "</title>");
        out.println("</head>");

        out.println("<body bgcolor=\"white\">");
        out.println(this.toString());
        out.println(Thread.currentThread().toString());

        out.println("</body>");
        out.println("</html>");
    }
}
