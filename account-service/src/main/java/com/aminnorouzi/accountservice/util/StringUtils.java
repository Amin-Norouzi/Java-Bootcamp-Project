package com.aminnorouzi.accountservice.util;

import com.aminnorouzi.accountservice.model.Type;

public class StringUtils {

    private static final String SPACE = " - ";

    public static String generateTitle(String fullName, Type type) {
        return fullName + SPACE + type;
    }
}
