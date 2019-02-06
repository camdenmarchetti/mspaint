package Utilities;

public class NumberUtils {

    public static boolean Is(String value, Class<? extends Number> type) {
        if (type.getClass().getName().equals(Integer.class.getName())) {
            return value.matches("\\-?\\d+");
        } else if (type.getClass().getName().equals(Byte.class.getName())) {
            // Just don't deal with these...
            return false;
        }

        return value.matches("\\-?(?:\\d+|\\d*\\.\\d+)");
    }

    public static Number Convert(String value, Class<? extends Number> type) {
        try {
            if (type.getTypeName().equals(Integer.class.getName())) {
                return Integer.parseInt(value);
            } else if (type.getTypeName().equals(Double.class.getName())) {
                return Double.parseDouble(value);
            } else if (type.getTypeName().equals(Short.class.getName())) {
                return Short.parseShort(value);
            } else if (type.getTypeName().equals(Long.class.getName())) {
                return Long.parseLong(value);
            } else if (type.getTypeName().equals(Float.class.getName())) {
                return Float.parseFloat(value);
            }

            return 0;
        } catch (Exception e) {
            return 0;
        }
    }
}
