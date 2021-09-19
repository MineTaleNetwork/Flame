package cc.minetale.flame.util;

/**
 * String helper functions.
 *
 * @author Gili Tzabari
 */
public final class Strings {
    /**
     * Prevent construction.
     */
    private Strings() {
    }

    /**
     * @param str    a String
     * @param prefix a prefix
     * @return true if {@code start} starts with {@code prefix}, disregarding case sensitivity
     */
    public static boolean startsWithIgnoreCase(String str, String prefix) {
        return str.regionMatches(true, 0, prefix, 0, prefix.length());
    }

    public static boolean endsWithIgnoreCase(String str, String suffix) {
        int suffixLength = suffix.length();
        return str.regionMatches(true, str.length() - suffixLength, suffix, 0, suffixLength);
    }

}