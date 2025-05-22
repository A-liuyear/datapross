package com.admin.common.util;

import java.security.MessageDigest;
import java.util.Base64;

public class MD5Utils {
    public static String encode(String str){
        try {
            MessageDigest md5 = MessageDigest.getInstance("md5");
            byte []afterEncode = md5.digest(str.getBytes());
            String encodeToString = Base64.getEncoder().encodeToString(afterEncode);
            return encodeToString;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        System.out.println(MD5Utils.encode("123456"));
    }
}
