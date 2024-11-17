package org.example.backend.global.utils;

import java.util.Random;

public class RandomCodeGenerator {
    private final static String CODE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    public static String generate(int length) {
        Random random = new Random();
        StringBuilder token = new StringBuilder();
        for (int i = 0; i < length; i++) {
            token.append(CODE_CHARS.charAt(random.nextInt(CODE_CHARS.length())));
        }
        return token.toString();
    }
}
