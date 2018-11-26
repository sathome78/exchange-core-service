package me.exrates.service.utils;

import me.exrates.exception.MissingBodyParamException;
import org.apache.commons.lang.StringUtils;

import java.util.Base64;
import java.util.Map;

public class RestApiUtils {
    private static final String PASSWORD_ENCODE_KEY = "3255c246-4b9f-43a5-b2dd-63524f959953";


    public static String decodePassword(String password, String keyString) {
        final byte[] txt = Base64.getDecoder().decode(password);
        final byte[] key = keyString.getBytes();
        final byte[] result = new byte[txt.length];
        for (int i = 0; i < txt.length; i++) {
            result[i] = (byte) (txt[i] ^ key[i % key.length]);
        }
        return new String(result);
    }

    public static String decodePassword(String password) {
        final byte[] txt = Base64.getDecoder().decode(password);
        final byte[] key = PASSWORD_ENCODE_KEY.getBytes();
        final byte[] result = new byte[txt.length];
        for (int i = 0; i < txt.length; i++) {
            result[i] = (byte) (txt[i] ^ key[i % key.length]);
        }
        return new String(result);
    }

    public static String retrieveParamFormBody(Map<String, String> body, String paramName, boolean required) {
        String paramValue = body.get(paramName);
        if (required && StringUtils.isEmpty(paramValue)) {
            throw new MissingBodyParamException("Param " + paramName + " missing");
        }
        return paramValue;
    }

    public static String constructAbsoluteURI(String host, String port, String endpoint) {
        return String.join("", host, ":", port, endpoint);
    }
}