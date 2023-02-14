package com.autotest.LiuMa.common.utils;

import java.util.Date;
import java.util.Random;


public class StringUtils {

    public final static String[] word = {
            "a", "b", "c", "d", "e", "f", "g",
            "h", "j", "k", "m", "n",
            "p", "q", "r", "s", "t",
            "u", "v", "w", "x", "y", "z",
            "A", "B", "C", "D", "E", "F", "G",
            "H", "J", "K", "M", "N",
            "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z"
    };

    public final static String[] num = {
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
    };

    public final static String[] symbol = {
            "!", "@", "#", "$", "%", "^", "&", "*",
            "(", ")", "{", "}", "[", "]" , ".", "?", "_",
            "`", "-", ",", ";", ":", "'", "|", "~"
    };

    public static String randomSimpleString(int length) {
        StringBuilder stringBuffer = new StringBuilder();
        Random random = new Random(new Date().getTime());
        for (int i = 0; i < length; i++) {
            int flag = random.nextInt(2);
            if (flag==0) {
                stringBuffer.append(num[random.nextInt(num.length)]);
            } else {
                stringBuffer.append(word[random.nextInt(word.length)]);
            }
        }
        return stringBuffer.toString();
    }

    public static String randomSpecialString(Integer length) {
        StringBuilder stringBuffer = new StringBuilder();
        Random random = new Random(new Date().getTime());
        for (int i = 0; i < length; i++) {
            int flag = random.nextInt(3);
            switch (flag) {
                case 0:
                    stringBuffer.append(word[random.nextInt(word.length)]);
                    break;
                case 1:
                    stringBuffer.append(num[random.nextInt(num.length)]);
                    break;
                case 2:
                    stringBuffer.append(symbol[random.nextInt(symbol.length)]);
                    break;
                default:
                    break;
            }
        }
        return stringBuffer.toString();
    }
}
