package com.wolf.concurrenttest.jjzl.clone;

/**
 * Description:
 * Created on 2021/6/5 10:16 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class Address implements Cloneable {
    private String city;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "Address{" +
                "city='" + city + '\'' +
                '}';
    }
}
