package com.wolf.concurrenttest.jcip.servlet;

import net.jcip.annotations.ThreadSafe;

import javax.servlet.*;
import java.io.IOException;
import java.math.BigInteger;

/**
 * Description: 无状态的servlet，线程安全
 * has no fields and references not fields from other classes
 * Created on 2021/6/26 1:33 PM
 *
 * @author 李超
 * @version 0.0.1
 */
@ThreadSafe
public class StatelessServlet extends AbstractServlet {

    public void service(ServletRequest req, ServletResponse resp) throws ServletException, IOException {
        BigInteger i = extractFromRequest(req);
        BigInteger factors[] = factor(i);
        encodeIntoResponse(resp, factors);
    }
}
