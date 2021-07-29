package com.wolf.concurrenttest.underjvm.classload;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;

/**
 * Description: 动态调用实战
 * Created on 2021/7/27 10:39 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class InvokeDynamicAction {
    class GrandFather {
        void thinking() {
            System.out.println("i am grandfather");
        }
    }

    class Father extends GrandFather {
        @Override
        void thinking() {
            System.out.println("i am father");
        }
    }

    // 这里想实现调用祖父类的thinking()方法
    // java语言层面无法在Son的thinking()方法中获取一个实际类型是GrandFather的对象引用，而invokevirtual指令的分派逻辑是固定的，只能按照方法接收者的实际类型进行分派
    // 这个逻辑完全固化在虚拟机中，程序员无法改变
    class Son extends Father {
        @Override
        void thinking() {
            try {
                // jdk7之前，下面代码可以解决问题
                // 但之后被视为一个潜在的安全性缺陷修正了，原因是必须保证findSpecial查找方法版本时受到访问约束应与使用invokespecial一样，
                // 两者必须保持精确对等，包括上面的场景中它只能访问到其直接父类中的方法版本。
                //MethodType mt = MethodType.methodType(void.class);
                //MethodHandle mh = lookup().findSpecial(GrandFather.class, "thinking", mt, getClass());
                //mh.invoke(this);

                // jdk7以后，可行 // todo 不过好像也没有成功，将代码放到Father也不行
                MethodType mt = MethodType.methodType(void.class);
                Field lookupImpl = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
                lookupImpl.setAccessible(true);
                MethodHandle mh = ((MethodHandles.Lookup) lookupImpl.get(null)).findSpecial(GrandFather.class, "thinking", mt, getClass());
                mh.invoke(this);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Throwable {
        (new InvokeDynamicAction().new Son()).thinking();
    }
}
