package com.wolf.concurrenttest.jcip.safedemo;

import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Description: Delegating Thread Safety to Multiple Underlying State Variables，这里用到CopyOnWriteArrayList
 * 由于两个变量之间没有关系，独立，可以分别依赖他们进行线程安全
 * Created on 2021/6/28 6:28 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class VisualComponent {
    private final List<KeyListener> keyListeners = new CopyOnWriteArrayList<>();
    private final List<MouseListener> mouseListeners = new CopyOnWriteArrayList<>();

    public void addKeyListener(KeyListener listener) {
        keyListeners.add(listener);
    }

    public void addMouseListner(MouseListener listener) {
        mouseListeners.add(listener);
    }

    public void removeKeyListener(KeyListener listener) {
        keyListeners.remove(listener);
    }

    public void removeMouseListener(MouseListener listener) {
        mouseListeners.remove(listener);
    }
}
