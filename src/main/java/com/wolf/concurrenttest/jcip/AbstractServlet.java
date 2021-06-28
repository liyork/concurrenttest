package com.wolf.concurrenttest.jcip;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.math.BigInteger;

/**
 * Description: 用于空方法重用
 * Created on 2021/6/26 3:56 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class AbstractServlet /*implements Servlet*/ {
    protected BigInteger[] factor(BigInteger i) {
        return null;
    }

    protected void encodeIntoResponse(ServletResponse resp, BigInteger[] factors) {
    }


    protected BigInteger extractFromRequest(ServletRequest req) {
        return null;
    }
}
