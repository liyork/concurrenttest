package com.wolf.concurrenttest.underjvm.classload.inovketmpcode;

import java.lang.reflect.Method;

/**
 * Description: 提供给外部调用的入口，完成类加载工作
 * Created on 2021/7/28 2:06 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class JavaclassExecutor {

    // 将输入类的byte数组中代表java.lang.System的CONSTANT_Utf8_info常量修改为HackSystem，
    // 使用HotSwapClassLoader加载byte并生成一个Class对象，执行该类的main方法，输出结果为该类向System.out/err输出的信息
    // 每次调用都会生成一个新的类加载器实例，因此同一个类可以实现重复加载
    public static String execute(byte[] classByte) {
        HackSystem.clearBuffer();

        ClassModifier cm = new ClassModifier(classByte);
        byte[] modiBytes = cm.modifyUTF8Constant("java/lang/System", "com/wolf/concurrenttest/underjvm/classload/inovketmpcode/HackSystem");
        HotSwapClassLoader loader = new HotSwapClassLoader();
        Class clazz = loader.loadByte(modiBytes);
        try {
            Method method = clazz.getMethod("main", new Class[]{String[].class});
            method.invoke(null, new String[]{null});
        } catch (Throwable e) {
            e.printStackTrace(HackSystem.out);
        }
        return HackSystem.getBufferString();
    }
}
