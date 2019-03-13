package com.bai.utils;

import com.bai.models.User;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class PasswordUtils {

    public static final boolean[] DEFAULT_MASK = {true, true, true, true, true, false, false, false, false, false, false, false, false, false, false, false};
    private static final int SALT_LENGTH = 25;

    public static boolean[] maskToBooleanArray(String maskString) {
        boolean[] mask = new boolean[maskString.length()];
        IntStream.range(0, mask.length).forEach(i -> mask[i] = maskString.charAt(i) == '1');
        return mask;
    }

    public static List<String> generateMasksForUser(User user, int maskCount) {
        List<String> masks = new ArrayList<>();
        for (int i = 0; i < maskCount; i++) {
            String mask;
            do {
                mask = generateMaskAsString(user);
            } while (masks.contains(mask));
            masks.add(mask);
        }
        return masks;
    }

    private static String generateMaskAsString(User user) {
        boolean[] mask = generateMask(user);
        StringBuilder stringBuilder = new StringBuilder();
        for (boolean b : mask)
            if (b)
                stringBuilder.append("1");
            else
                stringBuilder.append("0");
        return stringBuilder.toString();
    }

    private static boolean[] generateMask(User user) {
        Random random = new Random();
        String password = user.getPassword();
        int passwordLength = password.length();
        int maxChars = Math.max(password.length() / 2, 5);
        int minChars = 5;
        int maskLength = minChars + random.nextInt() % (maxChars - minChars + 1);
        boolean[] mask = new boolean[16];
        int maskCharIndex;
        for (int i = 0; i < maskLength; i++) {
            do {
                maskCharIndex = Math.floorMod(random.nextInt(), passwordLength);
            } while (mask[maskCharIndex]);
            mask[maskCharIndex] = true;
        }
        return mask;
    }

    public static String getPartialPassword(String password, String mask) {
        StringBuilder builder = new StringBuilder();
        boolean[] booleanMask = maskToBooleanArray(mask);
        for (int i = 0; i < booleanMask.length; i++) {
            if (booleanMask[i])
                builder.append(password.charAt(i));
        }
        return builder.toString();
    }

    public static String hashPassword(User user) {
        return DigestUtils.md5Hex(user.getPassword() + user.getSalt()).toUpperCase();
    }

    public static String generateSalt() {
        return RandomStringUtils.randomAlphanumeric(RandomUtils.nextInt(SALT_LENGTH, SALT_LENGTH * 2));
    }
}
