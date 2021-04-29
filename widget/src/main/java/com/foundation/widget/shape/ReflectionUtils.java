package com.foundation.widget.shape;

import android.graphics.Rect;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

/**
 * 低api没有对应set方法
 */
class ReflectionUtils {

    /**
     * 设置属性，直接覆盖
     *
     * @param target 被反射对象
     */
    static void setValue(@Nullable Object target, @NotNull String failedName, Object value) {
        if (target == null) {
            return;
        }
        try {
            Field f = target.getClass().getDeclaredField(failedName);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置rect，如果有则只是更新
     *
     * @return 返回设置后的对象
     */
    static Rect setRect(@Nullable Object target, @NotNull String failedName, Rect value) {
        if (target == null) {
            return null;
        }
        try {
            Field f = target.getClass().getDeclaredField(failedName);
            f.setAccessible(true);
            Object o = f.get(target);
            if (o == null) {
                f.set(target, value);
                return value;
            } else {
                Rect rect = (Rect) o;
                rect.set(value);
                return rect;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
