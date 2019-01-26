package com.wolf.concurrenttest.threadpool;

import java.util.LinkedList;
import java.util.List;

/**
 * Description: 抽象对象池
 * AbstractObjectPool具有以下特性：
 * 支持设置最小、最大容量
 * 对象一旦申请就不再释放，避免了GC(总有强引用保持)
 * <p>
 * 一个对象池的基本行为包括：
 * 创建对象newObject()
 * 借取对象getObject()
 * 归还对象freeObject()
 * <p>
 * <br/> Created on 2019-01-18
 *
 * @author 李超
 * @since 1.0.0
 */
public abstract class AbstractObjectPool<T> {

    protected final int min;
    protected final int max;
    protected final List<T> usings = new LinkedList<>();
    protected final List<T> buffer = new LinkedList<>();
    private volatile boolean inited = false;

    public AbstractObjectPool(int min, int max) {

        if (min < 0 || min > max) {
            throw new IllegalArgumentException(String.format(
                    "need 0 <= min <= max <= Integer.MAX_VALUE, given min: %s, max: %s", min, max));
        }

        this.min = min;
        this.max = max;
    }

    //初始化时构造好最小容量
    public void init() {

        for (int i = 0; i < min; i++) {
            buffer.add(newObject());
        }

        inited = true;
    }

    protected void checkInited() {

        if (!inited) {
            throw new IllegalStateException("not inited");
        }
    }

    abstract protected T newObject();

    //借用
    public synchronized T getObject() {

        checkInited();
        //满了
        if (usings.size() == max) {
            return null;
        }

        //buffer中最小容量用没了，再新增
        if (buffer.size() == 0) {
            T newObj = newObject();
            usings.add(newObj);
            return newObj;
        }

        //移除
        T oldObj = buffer.remove(0);
        usings.add(oldObj);

        return oldObj;
    }

    //归还
    public synchronized void freeObject(T obj) {

        checkInited();

        if (!usings.contains(obj)) {
            throw new IllegalArgumentException(String.format("obj not in using queue: %s", obj));
        }

        usings.remove(usings.indexOf(obj));
        buffer.add(obj);
    }
}