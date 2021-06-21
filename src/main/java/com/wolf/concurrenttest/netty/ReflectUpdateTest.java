package com.wolf.concurrenttest.netty;

import io.netty.util.internal.PlatformDependent;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Description: 尝试修改大小端
 * Created on 2021/6/18 6:27 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ReflectUpdateTest {
    @Test
    public void testUpdateFinal() throws Exception {
        System.out.println(PlatformDependent.BIG_ENDIAN_NATIVE_ORDER);

        Class<PlatformDependent> clazz = PlatformDependent.class;
        Field field = clazz.getDeclaredField("BIG_ENDIAN_NATIVE_ORDER");
        field.setAccessible(true);

        Field modifiers = field.getClass().getDeclaredField("modifiers");
        modifiers.setAccessible(true);
        modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, true);

        System.out.println(PlatformDependent.BIG_ENDIAN_NATIVE_ORDER);
    }
}
