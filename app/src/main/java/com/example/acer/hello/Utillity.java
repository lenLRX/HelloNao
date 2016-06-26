package com.example.acer.hello;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utillity {
    public static boolean isIPValid(String addr)
    {
        if(addr.length() < 7 || addr.length() > 15 || "".equals(addr))
        {
            return false;
        }
        /**
         * 判断IP格式和范围
         */
        String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";

        Pattern pat = Pattern.compile(rexp);

        Matcher mat = pat.matcher(addr);

        boolean ipAddress = mat.find();

        return ipAddress;
    }

    public static byte[] RBG888ToARBG8888(byte[] rbg888){
        int numOfPix = rbg888.length / 3;
        byte[] arbg8888 = new byte[numOfPix * 4];
        for(int i = 0;i < numOfPix;i++){
            //RGBA
            arbg8888[4 * i] = rbg888[3 * i];
            arbg8888[4 * i + 1] = rbg888[3 * i + 1];
            arbg8888[4 * i + 2] = rbg888[3 * i + 2];
            arbg8888[4 * i + 3] = (byte)255;
        }
        return arbg8888;
    }
}