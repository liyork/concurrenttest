package com.wolf.concurrenttest.jvm.compileplugin;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

/**
 * Description:插入式注解处理器
 * 编译器的一个插件，动态检查编写代码的格式是否符合命名要求。
 * 类似checkstyle、hibernate validator annotation processor也是这样的。如果有自己定制需要可以在javac时进行校验
 * * 编译：
 * 定位到com目录
 * javac com/wolf/test/jvm/compileplugin/NameChecker.java
 * javac com/wolf/test/jvm/compileplugin/NameCheckProcessor.java
 * javac -processor com.wolf.test.jvm.compileplugin.NameCheckProcessor com/wolf/test/jvm/compileplugin/BADLY_NAMED_CODE.java
 * <br/> Created on 11/7/17 9:29 AM
 *
 * @author 李超
 * @since 1.0.0
 */
@SupportedAnnotationTypes("*")//支持所有annotations
@SupportedSourceVersion(SourceVersion.RELEASE_6)//支持jdk6代码
public class NameCheckProcessor extends AbstractProcessor {

    private NameChecker nameChecker;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        nameChecker = new NameChecker(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        if (!roundEnv.processingOver()) {
            for (Element element : roundEnv.getRootElements()) {
                nameChecker.checkNames(element);
            }
        }
        return false;
    }


}
