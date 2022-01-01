package com.ruppyrup.bigfun.utils;

import java.util.Random;

public class CommonUtil {

    private static Random random = new Random();

    public static String getRandomRGBColor() {
        return get2DigitColor() + get2DigitColor() + get2DigitColor();
    }

    public static String get2DigitColor() {
        StringBuilder sb = new StringBuilder();
        sb.append(Integer.toHexString(random.nextInt(255)));
        if (sb.length() < 2) {
            sb.insert(0, '0'); // pad with leading zero if needed
        }
        return sb.toString();
    }

    public static Random getRandom() {
        return random;
    }
}
