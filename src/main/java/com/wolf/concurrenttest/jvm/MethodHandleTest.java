package com.wolf.concurrenttest.jvm;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;

import static java.lang.invoke.MethodHandles.lookup;

/**
 * Description:动态执行方法
 * reflection模拟java代码层的方法调用，重量级，包含很多方法信息，仅支持java
 * methodhandle模拟字节码层次的方法调用，轻量级，仅包含执行该方法的信息，支持虚拟机上的语言
 * invokedynami指令所面向的使用者是非java语言，与methodhandle机制作用一样，
 * 都是解决原有invoke*指令方法分派规则固化在虚拟机中的问题，现在都将决定权交给用户。
 * <br/> Created on 11/3/17 9:26 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class MethodHandleTest {

    static class ClassA {
        public void println(String s) {
            System.out.println(s);
        }

        public static MethodHandle getPrintlnMH(Object receiver) throws NoSuchMethodException, IllegalAccessException {
            MethodType methodType = MethodType.methodType(void.class, String.class);
            //寻找接收者的虚拟方法
            return lookup().findVirtual(receiver.getClass(), "println", methodType).bindTo(receiver);
        }
    }


    public static void main(String[] args) throws Throwable {
//        Object obj = System.currentTimeMillis() % 2 == 0 ? System.out : new ClassA();
//        getPrintlnMH(obj).invokeExact("icyfenix");


        new Son().xx();
    }

}


//=========调用上上级方法,这个实验没成功，只能把father中去掉才可以，不是明明说好的指定调用方法吗，和实际类型无关。。但是为什么还调用了子类father的方法？？？
class GrandFather {
    void thinking() {
        System.out.println("i an grandfather");
    }
}

class Father extends GrandFather {
    void thinking() {
        System.out.println("i am father");
    }
}

class Son extends Father {
    void xx() {
        try {
            MethodType methodType = MethodType.methodType(void.class);
            MethodHandle thinking = lookup().findSpecial(GrandFather.class, "thinking", methodType, getClass());
            thinking.invoke(this);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
