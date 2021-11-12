package xienaoban.minecraft.bole.util;

import java.lang.reflect.Field;

public class MiscUtil {
    @SuppressWarnings("unchecked")
    public static <T> T cast(Object obj) {
        return (T) obj;
    }

    public static <T> T getField(Object obj, Class<?> clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return cast(field.get(obj));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
