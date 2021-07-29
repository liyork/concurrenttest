package com.wolf.concurrenttest.underjvm.classload.inovketmpcode;

/**
 * Description: 为多次载入执行类而加入的加载器
 * Created on 2021/7/28 1:49 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class HotSwapClassLoader extends ClassLoader {
    public HotSwapClassLoader() {
        // 指定加载HotSwapClassLoader类的类加载器作为父类加载器，可以实现提交的执行代码可以方服务端引用类库
        super(HotSwapClassLoader.class.getClassLoader());
    }

    public Class loadByte(byte[] classByte) {
        return defineClass(null, classByte, 0, classByte.length);
    }
}
