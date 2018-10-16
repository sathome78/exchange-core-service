package me.exrates.util;

import org.apache.commons.lang.RandomStringUtils;

import java.security.SecureRandom;

public class UtilTokenService {
    private static final int KEY_LENGTH = 40;


    public static String generateKey() {
        return RandomStringUtils.random(KEY_LENGTH, 0, 0, true, true, null, new SecureRandom());
    }



}
