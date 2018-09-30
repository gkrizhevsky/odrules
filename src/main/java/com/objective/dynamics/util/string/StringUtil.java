package com.objective.dynamics.util.string;

/**
 * @author Gennady Krizhevsky
 * Date: 29/09/18
 * Time: 11:14 AM
 */
public class StringUtil {


    public static boolean isEmpty(String value) {
        return value == null || value.trim().length() == 0;
    }

    public static boolean isNotEmpty(String value) {
        return !isEmpty(value);
    }

    /**
     * Left padding of value
     *
     * @param value       value to left pad
     * @param padding     padding character
     * @param totalLength total Length of the result
     * @return left padded value. If value is null - returns null
     */
    public static String lpad(String value, char padding, int totalLength) {
        if (value == null) {
            return null;
        }
        if (value.length() >= totalLength) {
            return value;
        }
        final StringBuilder sb = new StringBuilder(totalLength);
        for (int i = 0; i < totalLength - value.length(); i++) {
            sb.append(padding);
        }
        return sb.append(value).toString();
    }

    public static String nvl(String value) {
        return nvl(value, "");
    }

    public static String nvl(String value, String defaultValue) {
        return value == null ? defaultValue : value;
    }


}
