package com.wolf.concurrenttest.underjvm.postcompile;

/**
 * Description: 触发即时编译的实例代码
 * javac com/wolf/concurrenttest/underjvm/postcompile/GraalDemo.java
 * java -XX:+PrintCompilation -XX:CompileOnly=GraalDemo::workload com.wolf.concurrenttest.underjvm.postcompile.GraalDemo
 * 用graal
 * java --module--path=/Users/chaoli/intellijWrkSpace/graal/compiler/mxbuild/dists/jdk1.8/graal-graphio.jar \
 * --upgrade-module-path=~/graal/compiler/mxbuild/dists/jdk11/jdk.internal.vm.compiler.jar \
 * -XX:+UnlockExperimentalVMOptions -XX:+EnableJVMCI -XX:UseJVMCICompiler -XX:-TieredCompilation -XX:+PrintCompilation -XX:CompileOnly=Demo::workload
 * com.wolf.concurrenttest.underjvm.postcompile.GraalDemo
 * Created on 2021/8/1 6:31 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class GraalDemo {
    private static int workload(int a, int b) {
        return a + b;
    }

    public static void main(String[] args) {
        while (true) {
            workload(14, 2);
        }
    }
}
