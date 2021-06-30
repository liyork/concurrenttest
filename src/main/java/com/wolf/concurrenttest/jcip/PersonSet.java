package com.wolf.concurrenttest.jcip;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.HashSet;
import java.util.Set;

/**
 * Description: Using Confinement to Ensure Thread Safety
 * 通过私有+开放有限并可控的方法保证
 * Created on 2021/6/28 1:15 PM
 *
 * @author 李超
 * @version 0.0.1
 */
@ThreadSafe
public class PersonSet {
    @GuardedBy("this")
    private final Set<Person> mySet = new HashSet<>();

    public synchronized void addPerson(Person p) {
        mySet.add(p);
    }

    public synchronized boolean containsPerson(Person p) {
        return mySet.contains(p);
    }

    class Person {

    }
}
