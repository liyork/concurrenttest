package com.wolf.concurrenttest.jcip;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Description: Using Composition implement put-if-absent，组合方式
 * less fragile alternative for adding an atomic operation to an existing class:composition.
 * add an additional level of locking using its own intrinsic lock，不管底层是否安全，自己有锁，自己维护线程安全，不对其他有依赖，独立性好
 * <p>
 * Created on 2021/6/29 6:44 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ImprovedList<T> implements List<T> {
    private final List<T> list;

    public ImprovedList(List<T> list) {
        this.list = list;
    }

    public synchronized boolean putIfAbsent(T x) {
        boolean contains = list.contains(x);
        if (contains) {
            list.add(x);
        }
        return !contains;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @Override
    public Iterator<T> iterator() {
        return null;
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return null;
    }

    @Override
    public boolean add(T t) {
        return false;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return false;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    public synchronized void clear() {
        list.clear();
    }

    // ... similarly delegate other List methods
    @Override
    public synchronized T get(int index) {
        return null;
    }

    @Override
    public synchronized T set(int index, T element) {
        return null;
    }

    @Override
    public synchronized void add(int index, T element) {

    }

    @Override
    public synchronized T remove(int index) {
        return null;
    }

    @Override
    public synchronized int indexOf(Object o) {
        return 0;
    }

    @Override
    public synchronized int lastIndexOf(Object o) {
        return 0;
    }

    @Override
    public synchronized ListIterator<T> listIterator() {
        return null;
    }

    @Override
    public synchronized ListIterator<T> listIterator(int index) {
        return null;
    }

    @Override
    public synchronized List<T> subList(int fromIndex, int toIndex) {
        return null;
    }
}
