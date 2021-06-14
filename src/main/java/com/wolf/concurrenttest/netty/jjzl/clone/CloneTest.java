package com.wolf.concurrenttest.netty.jjzl.clone;

/**
 * Description:
 * Created on 2021/6/5 10:16 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class CloneTest {
    public static void main(String[] args) throws CloneNotSupportedException {
        testShallowCopy();
        //testDeepCopy();
    }

    private static void testShallowCopy() throws CloneNotSupportedException {
        UserInfo userInfo = new UserInfo();
        userInfo.setAge(10);

        Address address = new Address();
        address.setCity("xxx");

        userInfo.setAddress(address);
        System.out.println("The old userInfo value is: " + userInfo);

        UserInfo cloneUser = (UserInfo) userInfo.clone();
        cloneUser.setAge(20);
        cloneUser.getAddress().setCity("yyy");
        System.out.println("The new userInfo value is: " + userInfo);
    }

    private static void testDeepCopy() throws CloneNotSupportedException {
        UserInfo2 userInfo2 = new UserInfo2();
        userInfo2.setAge(10);

        Address address = new Address();
        address.setCity("xxx");

        userInfo2.setAddress(address);
        System.out.println("The old userInfo2 value is: " + userInfo2);

        UserInfo2 cloneUser = (UserInfo2) userInfo2.clone();
        cloneUser.setAge(20);
        cloneUser.getAddress().setCity("yyy");
        System.out.println("The new userInfo2 value is: " + userInfo2);
    }
}
