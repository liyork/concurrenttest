package com.wolf.concurrenttest.netty.jjzl.clone;

/**
 * Description: 深克隆
 * Created on 2021/6/5 10:15 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class UserInfo2 implements Cloneable {
    private int age;
    private Address address;

    public Object clone() throws CloneNotSupportedException {
        UserInfo2 clone = (UserInfo2) super.clone();
        clone.setAddress((Address) address.clone());
        return clone;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "age=" + age +
                ", address=" + address +
                '}';
    }
}
