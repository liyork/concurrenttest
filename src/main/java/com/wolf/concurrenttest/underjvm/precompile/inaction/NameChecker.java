package com.wolf.concurrenttest.underjvm.precompile.inaction;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.util.ElementScanner6;
import javax.tools.Diagnostic;
import java.util.EnumSet;

/**
 * Description: 程序名称规范的编译器插件
 * 若不符合规范，输出一个编译器的WARNING信息
 * 以Visitor模式完成对语法树的遍历，执行visitType、visitVariable、visitExecutable来访问类、字段和方法
 * Created on 2021/7/30 7:14 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class NameChecker {
    private final Messager messager;

    NameCheckScanner nameCheckScanner = new NameCheckScanner();

    public NameChecker(ProcessingEnvironment processingEnv) {
        this.messager = processingEnv.getMessager();
    }

    // 对java程序命名进行检查，根据<java语言规范>，java程序命名应当符合下列格式：
    // 类或接口：符合驼式命名法，首字母大写
    // 方法：符合驼式命名法，首字母小写
    // 字段
    //   类、实例变量：符合驼式命名法，首字母小写
    //   常量：全部大写
    public void checkNames(Element element) {
        nameCheckScanner.scan(element);
    }

    // 名称检查器实现类，将会以Visitor模式访问抽象语法树中元素
    private class NameCheckScanner extends ElementScanner6<Void, Void> {
        // 检查java类
        @Override
        public Void visitType(TypeElement e, Void p) {
            scan(e.getTypeParameters(), p);
            checkCamelCase(e, true);
            super.visitType(e, p);
            return null;
        }

        // 检查方法命名是否合法
        @Override
        public Void visitExecutable(ExecutableElement e, Void p) {
            if (e.getKind() == ElementKind.METHOD) {
                Name name = e.getSimpleName();
                if (name.contentEquals(e.getEnclosingElement().getSimpleName())) {
                    messager.printMessage(Diagnostic.Kind.WARNING, "普通方法 " + name + " 不应当与类名重复，避免与构造函数产生混淆");
                }
            }
            super.visitExecutable(e, p);
            return null;
        }

        // 检查变量命名是否合法
        @Override
        public Void visitVariable(VariableElement e, Void unused) {
            // 若这个Variable是枚举或常量，按大写命名检查，否则按照驼式命名法规则检查
            if (e.getKind() == ElementKind.ENUM_CONSTANT || e.getConstantValue() != null || heuristicallyConstant(e)) {
                checkAllCaps(e);
            } else {
                checkCamelCase(e, false);
            }
            return null;
        }

        // 判断一个变量是否是常量
        private boolean heuristicallyConstant(VariableElement e) {
            if (e.getEnclosingElement().getKind() == ElementKind.INTERFACE) {
                return true;
            } else if (e.getKind() == ElementKind.FIELD && e.getModifiers().containsAll(EnumSet.of(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL))) {
                return true;
            } else {
                return false;
            }
        }

        // 检查传入的Element是否符合驼式命名法，弱不符合输出警告信息
        private void checkCamelCase(Element e, boolean initialCaps) {
            String name = e.getSimpleName().toString();
            boolean previousUpper = false;
            boolean conventional = true;
            int firstCodePoint = name.codePointAt(0);

            if (Character.isUpperCase(firstCodePoint)) {
                previousUpper = true;
                if (!initialCaps) {
                    messager.printMessage(Diagnostic.Kind.WARNING, "名称 " + name + "应当以小写字母开头", e);
                    return;
                }
            } else if (Character.isLowerCase(firstCodePoint)) {
                if (initialCaps) {
                    messager.printMessage(Diagnostic.Kind.WARNING, "名称 " + name + "应当以大写字母开头", e);
                    return;
                }
            } else {
                conventional = false;
            }

            if (conventional) {
                int cp = firstCodePoint;
                for (int i = Character.charCount(cp); i < name.length(); i += Character.charCount(cp)) {
                    cp = name.codePointAt(i);
                    if (Character.isUpperCase(cp)) {
                        if (previousUpper) {
                            conventional = false;
                            break;
                        }
                        previousUpper = true;
                    } else {
                        previousUpper = false;
                    }
                }
            }
            if (!conventional) {
                messager.printMessage(Diagnostic.Kind.WARNING, "名称 " + name + "应当符合驼式命名法(Camel Case Names)", e);
            }
        }

        // 大写命名检查，要求第一个字母必须大写英文字母，其余部分可以是下划线、数字、大写字母
        private void checkAllCaps(Element e) {
            String name = e.getSimpleName().toString();

            boolean conventional = true;
            int firstCodePoint = name.codePointAt(0);

            if (!Character.isUpperCase(firstCodePoint)) {
                conventional = false;
            } else {
                boolean previousUnderscore = false;
                int cp = firstCodePoint;
                for (int i = Character.charCount(cp); i < name.length(); i += Character.charCount(cp)) {
                    cp = name.codePointAt(i);
                    if (cp == (int) '_') {
                        if (previousUnderscore) {
                            conventional = false;// 两个_不允许
                            break;
                        }
                        previousUnderscore = true;
                    } else {
                        previousUnderscore = false;
                        if (!Character.isUpperCase(cp) && !Character.isDigit(cp)) {
                            conventional = false;
                            break;
                        }
                    }
                }
            }
            if (!conventional) {
                messager.printMessage(Diagnostic.Kind.WARNING, "常量 " + name + " 应当全部以大写字母、数字、下划线命名，并以字母开头");
            }
        }
    }

    //public static void main(String[] args) {
    //    ArrayList<Integer> integers = new ArrayList<>();
    //    integers.add(1);
    //    integers.add(2);
    //
    //    HashSet<Integer> set = new HashSet<>();
    //    set.add(1);
    //    set.add(2);
    //    set.add(3);
    //    System.out.println(integers.containsAll(set));
    //}

    //public static void main(String[] args) {
    //    String name = "abc";
    //    int cp = 0;
    //    // If the specified character is equal to or greater than 0x10000, then the method returns 2. Otherwise, the method returns 1.
    //    for (int i = Character.charCount(cp); i < name.length(); i += Character.charCount(cp)) {
    //        System.out.println(name.codePointAt(i));
    //    }
    //}
}
