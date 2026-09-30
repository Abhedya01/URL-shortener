package com.linkflow.urlshortener.util;

public class Base62Util {

    private static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public static String encode(long number) {
        if (number == 0) {
            return "0";
        }
        StringBuilder result = new StringBuilder();

        while (number > 0) {
            int remainder = (int) (number % 62);
            result.append(CHARACTERS.charAt(remainder));
            number = number / 62;
        }

        return result.reverse().toString();
    }
}