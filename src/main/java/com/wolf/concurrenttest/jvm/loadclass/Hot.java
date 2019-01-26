package com.wolf.concurrenttest.jvm.loadclass;

/**
 * Description:反射被调用
 * 用于修改，测试热加载
 * <br/> Created on 2018/1/22 9:45
 *
 * @author 李超
 * @since 1.0.0
 */
public class Hot {

    public void hot(){
        System.out.println(" hot1 version 4 "+this.getClass().getClassLoader());
    }
}
