package com.wolf.concurrenttest.underjvm.precompile.inaction;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

/**
 * Description: 命名检查，注解处理器
 * Created on 2021/7/30 7:11 AM
 *
 * @author 李超
 * @version 0.0.1
 */
@SupportedAnnotationTypes("*")// 表示支持所有Annotations
@SupportedSourceVersion(SourceVersion.RELEASE_6)// 只支持jdk6的java代码
public class NameCheckProcessor extends AbstractProcessor {
    private NameChecker nameChecker;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        nameChecker = new NameChecker(processingEnv);
    }

    // 对输入的语法树的各个节点进行名称检查
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!roundEnv.processingOver()) {
            for (Element element : roundEnv.getRootElements()) {// 检查当前轮次的每一个RootElement
                nameChecker.checkNames(element);
            }
        }
        return false;
    }
}
