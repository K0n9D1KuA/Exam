package com.apxy.courseSystem.util;

import java.util.Random;

/**
 * 生成随机的六位验证码
 */
public class Generate6Number {
    public static String randomCode() {
        StringBuilder str = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            str.append(random.nextInt(10));
        }
        return str.toString();
    }
}
