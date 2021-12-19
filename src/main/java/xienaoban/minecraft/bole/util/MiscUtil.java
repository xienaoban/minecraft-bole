package xienaoban.minecraft.bole.util;

public class MiscUtil {
    @SuppressWarnings("unchecked")
    public static <T> T cast(Object obj) {
        return (T) obj;
    }

    // public static Field getField(Class<?> clazz, String fieldName) {
    //     try {
    //         Field field = clazz.getDeclaredField(fieldName);
    //         field.setAccessible(true);
    //         return field;
    //     } catch (NoSuchFieldException e) {
    //         throw new RuntimeException(e);
    //     }
    // }
    //
    // public static <T> T getFieldValue(Object obj, Field field) {
    //     try {
    //         return cast(field.get(obj));
    //     }
    //     catch (Exception e) {
    //         throw new RuntimeException(e);
    //     }
    // }
    //
    // public static <T> T getFieldValue(Object obj, Class<?> clazz, String fieldName) {
    //     return cast(getFieldValue(obj, getField(clazz, fieldName)));
    // }
    //
    // public static void setFieldValue(Object obj, Field field, Object value) {
    //     try {
    //         field.set(obj, value);
    //     }
    //     catch (Exception e) {
    //         throw new RuntimeException(e);
    //     }
    // }
    //
    // public static void setFieldValue(Object obj, Class<?> clazz, String fieldName, Object value) {
    //     setFieldValue(obj, getField(clazz, fieldName), value);
    // }
}
