package com.wolf.concurrenttest.jvm.compileplugin;



import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.util.ElementScanner6;
import java.util.EnumSet;
import java.util.SortedSet;

import static javax.lang.model.element.ElementKind.FIELD;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.tools.Diagnostic.Kind.WARNING;

/**
 * Description:
 * <br/> Created on 11/7/17 9:33 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class NameChecker {

    private final Messager messager;

    NameCheckScanner nameCheckScanner = new NameCheckScanner();

    NameChecker(ProcessingEnvironment processingEnvironment) {
        this.messager = processingEnvironment.getMessager();
    }

    //检查：类首字母大写，方法首字母小写，变量首字母小写，常量大写，符合驼峰式命名
    public void checkNames(Element element) {
        nameCheckScanner.scan(element);
    }

    public class NameCheckScanner extends ElementScanner6<Void, Void> {

        //检查类
        @Override
        public Void visitType(TypeElement e, Void aVoid) {
            scan(e.getTypeParameters(), aVoid);
            checkCamelCase(e, true);
            super.visitType(e, aVoid);
            return null;
        }

        //检查方法
        @Override
        public Void visitExecutable(ExecutableElement e, Void aVoid) {
            if (e.getKind() == ElementKind.METHOD) {
                Name name = e.getSimpleName();
                if (name.contentEquals(e.getEnclosingElement().getSimpleName())) {
                    messager.printMessage(WARNING, "一个普通方法 " + name + " 不应当与类名重复，避免与构造函数产生混淆", e);
                }
                checkCamelCase(e, false);
            }
            super.visitExecutable(e, aVoid);
            return null;
        }

        //检查变量
        @Override
        public Void visitVariable(VariableElement e, Void aVoid) {
            if (e.getKind() == ElementKind.ENUM_CONSTANT || e.getConstantValue() != null || heuristicallyConstant(e)) {
                checkAllCaps(e);
            } else {
                checkCamelCase(e, false);
            }

            return null;
        }

        //判断是否为常量
        private boolean heuristicallyConstant(VariableElement e) {
            if (e.getEnclosingElement().getKind() == ElementKind.INTERFACE) {
                return true;
            } else if (e.getKind() == FIELD && e.getModifiers().containsAll(EnumSet.of(PUBLIC, STATIC, FINAL))) {
                return true;
            } else {
                return false;
            }

        }

        /**
         * 驼峰式检查(两个大写不能在一起)
         * @param e
         * @param initialCaps 首字母是否大写
         */
        private void checkCamelCase(Element e, boolean initialCaps) {
            String name = e.getSimpleName().toString();
            boolean previousUpper = false;
            boolean conventional = true;
            int firstCodePoint = name.codePointAt(0);
            //检查首字母是否该大写的小写了，小写的大写了
            if (Character.isUpperCase(firstCodePoint)) {
                previousUpper = true;
                if (!initialCaps) {
                    messager.printMessage(WARNING, "名称 " + name + "应当以小写字母开头", e);
                    return;
                }
            } else if (Character.isLowerCase(firstCodePoint)) {
                if (initialCaps) {
                    messager.printMessage(WARNING, "名称 " + name + " 应当以大写字母开头", e);
                    return;
                }
            } else {
                conventional = false;
            }

            if (conventional) {
                int cp = firstCodePoint;
                //获取cp数量，从1开始，上面已经检查完首字母了
                for (int i = Character.charCount(cp); i < name.length(); i += Character.charCount(cp)) {
                    cp = name.codePointAt(i);
                    if (Character.isUpperCase(cp)) {
                        if (previousUpper) {//两个连续的大写
                            conventional = false;
                            break;
                        }
                        previousUpper = true;
                    } else {
                        previousUpper = false;
                    }
                }

                if (!conventional) {
                    messager.printMessage(WARNING, " 名称 " + name + " 应当符合驼峰式命名(Camel Case Names)", e);
                }
            }
        }

        /**
         * 检查常量格式
         * @param e
         */
        public void checkAllCaps(Element e) {
            String name = e.getSimpleName().toString();
            boolean conventional = true;
            int firstCodePoint = name.codePointAt(0);
            if (!Character.isUpperCase(firstCodePoint)) {
                conventional = false;
            } else {
                boolean previousUnderscore = false;//前一个是下划线
                int cp = firstCodePoint;
                for (int i = Character.charCount(cp); i < name.length(); i += Character.charCount(cp)) {
                    cp = name.codePointAt(i);
                    if (cp == (int) '_') {//连续两个_不行。
                        if (previousUnderscore) {
                            conventional = false;
                            break;
                        }
                        previousUnderscore = true;
                    } else {
                        previousUnderscore = false;
                        if (!Character.isUpperCase(cp) && !Character.isDigit(cp)) {//大写或者数字
                            conventional = false;
                            break;
                        }
                    }
                }
            }

            if (!conventional) {
                messager.printMessage(WARNING, " 常量 " + name + " 应当全部以大写字母或下划线命名，并且以字母开头", e);
            }
        }

    }

    // ==================== 测试使用

    public static void main(String[] args) {
        String s = "Abc";
        int i = s.codePointAt(0);
        System.out.println(i);
        int i1 = Character.charCount(i);
        System.out.println(i1);

        System.out.println(Character.isUpperCase('_'));

//        checkAllCaps("D___ABC");
        checkCamelCase("nameOOfxxx",false);
    }

    public static void checkAllCaps(String name) {
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
                        conventional = false;
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
            System.out.println(" 常量 " + name + " 应当全部以大写字母或下划线命名，并且以字母开头");
        }
    }


    private static void checkCamelCase(String name, boolean initialCaps) {
        boolean previousUpper = false;
        boolean conventional = true;
        int firstCodePoint = name.codePointAt(0);
        if (Character.isUpperCase(firstCodePoint)) {
            previousUpper = true;
            if (!initialCaps) {
                System.out.println("名称 " + name + "应当以小写字母开头");
                return;
            }
        } else if (Character.isLowerCase(firstCodePoint)) {
            if (initialCaps) {
                System.out.println("名称 " + name + " 应当以大写字母开头");
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

            if (!conventional) {
                System.out.println(" 名称 " + name + " 应当符合驼峰式命名(Camel Case Names)");
            }
        }
    }

}
