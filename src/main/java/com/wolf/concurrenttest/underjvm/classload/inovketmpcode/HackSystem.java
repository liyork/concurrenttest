package com.wolf.concurrenttest.underjvm.classload.inovketmpcode;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

/**
 * Description: 用来代替java.lang.System
 * 除out和err外，其余直接转发给System处理
 * Created on 2021/7/28 2:02 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class HackSystem {
    public final static InputStream in = System.in;
    private static ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    // 将out和err改成使用ByteArrayOutputStream作为打印目标
    public final static PrintStream out = new PrintStream(buffer);
    public final static PrintStream err = out;

    public static String getBufferString() {
        return buffer.toString();
    }

    public static void clearBuffer() {
        buffer.reset();
    }

    public static void setSecurityManager(final SecurityManager s) {
        System.setSecurityManager(s);
    }

    public static SecurityManager getSecurityManager() {
        return System.getSecurityManager();
    }

    public static long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    public static void arraycopy(Object src, int srcPos, Object dest, int destPos, int length) {
        System.arraycopy(src, srcPos, dest, destPos, length);
    }

    public static int identifyHashCode(Object x) {
        return System.identityHashCode(x);
    }
    // 所有剩余方法都与java.lang.System名称一样，实现为转发给System
}
