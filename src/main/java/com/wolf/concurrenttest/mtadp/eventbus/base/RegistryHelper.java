package com.wolf.concurrenttest.mtadp.eventbus.base;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Description: 维护topic和Register关系
 *
 * @author 李超
 * @date 2019/02/10
 */
class RegistryHelper {

    private final ConcurrentHashMap<String, ConcurrentLinkedQueue<Register>> registerContainer =
            new ConcurrentHashMap<>();

    public void bind(Object register) {

        List<Method> registerMethods = getRegisterMethods(register);
        registerMethods.forEach(method -> tierRegister(register, method));
    }

    public void unbind(Object register) {

        registerContainer.forEach((key, queue) -> {
            queue.forEach(s -> {
                if (s.getRegisterObject() == register) {
                    s.setDisable(true);
                }
            });
        });
    }

    public ConcurrentLinkedQueue<Register> getRegister(final String topic) {

        return registerContainer.get(topic);
    }

    private void tierRegister(Object register, Method method) {

        Registry registry = method.getDeclaredAnnotation(Registry.class);
        String topic = registry.topic();
        ConcurrentLinkedQueue<Register> registers = registerContainer.computeIfAbsent(topic,
                key -> new ConcurrentLinkedQueue<>());
        registers.add(new Register(register, method));
    }

    //获取所有带有@Registry注解的方法(包括父类)
    private List<Method> getRegisterMethods(Object register) {

        List<Method> methods = new ArrayList<>();

        Class<?> tempClass = register.getClass();
        while (tempClass != null) {
            Method[] declaredMethods = tempClass.getDeclaredMethods();
            Arrays.stream(declaredMethods)
                    .filter(method -> method.isAnnotationPresent(Registry.class) &&
                            method.getParameterCount() == 1 &&
                            method.getModifiers() == Modifier.PUBLIC)
                    .forEach(methods::add);
            tempClass = tempClass.getSuperclass();
        }

        return methods;
    }
}
