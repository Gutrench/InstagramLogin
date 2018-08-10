package com.sociallogin.instagram;

import android.content.Context;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by RISHABH on 10/8/18.
 */
public class InstagramUtils {
    public static String getProperty(String key, Context context) throws IOException {
        Properties properties = new Properties();
        InputStream inputStream = new FileInputStream("instagram.properties");
        properties.load(inputStream);
        return properties.getProperty(key);
    }
}
