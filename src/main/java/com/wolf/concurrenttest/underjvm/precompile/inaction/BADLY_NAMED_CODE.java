package com.wolf.concurrenttest.underjvm.precompile.inaction;

/**
 * Description: 不规范命名代码样例
 * 可通过javac的-processor来执行编译时需要附带的直接处理器，-XprintRounds和-XprintProcessorInfo查看注解处理器运行的详细信息
 * /Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/Home/bin/javac com/wolf/concurrenttest/underjvm/precompile/inaction/NameChecker.java
 * /Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/Home/bin/javac com/wolf/concurrenttest/underjvm/precompile/inaction/NameCheckProcessor.java
 * /Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/Home/bin/javac -processor com.wolf.concurrenttest.underjvm.precompile.inaction.NameCheckProcessor -XprintRounds -XprintProcessorInfo com/wolf/concurrenttest/underjvm/precompile/inaction/BADLY_NAMED_CODE.java
 * Created on 2021/7/30 9:06 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class BADLY_NAMED_CODE {
    enum colors {
        red, blue, green;
    }

    static final int _FORTY_TWO = 42;

    public static int NOT_A_CONSTANT = _FORTY_TWO;

    protected void BADLY_NAMED_CODE() {
        return;
    }

    public void NOTcamlCASEmethodNAME() {
        return;
    }
}
