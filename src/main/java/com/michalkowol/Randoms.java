package com.michalkowol;

import java.util.Random;

final class Randoms {

    private static final Random RANDOM = new Random();

    private Randoms() {
    }

    public static String randomPassword() {
        return "admin" + RANDOM.nextInt(10_000);
    }

    public static String randomEmail() {
        return "admin" + RANDOM.nextInt(10_000) + "@gmail.com";
    }
}
