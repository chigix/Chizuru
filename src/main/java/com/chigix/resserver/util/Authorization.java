package com.chigix.resserver.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class Authorization {

    public static byte[] SHA256(byte[] content) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
        digest.update(content);
        return digest.digest();
    }

    public static byte[] HmacSHA256(byte[] data, byte[] key) {
        final Mac mac;
        try {
            mac = Mac.getInstance("HmacSHA256");
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
        try {
            mac.init(new SecretKeySpec(key, "HmacSHA256"));
        } catch (InvalidKeyException ex) {
            throw new RuntimeException(ex);
        }
        return mac.doFinal(data);
    }

    public static String HexEncode(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte cipher_byte : data) {
            sb.append(String.format("%02x", cipher_byte & 0xff));
        }
        return sb.toString();
    }

    public static byte[] getSignatureKey(String key, String dateStamp, String regionName, String serviceName) {
        byte[] result;
        try {
            result = HmacSHA256(dateStamp.getBytes("UTF8"), ("AWS4" + key).getBytes("UTF8"));
            result = HmacSHA256(regionName.getBytes(), result);
            result = HmacSHA256(serviceName.getBytes("UTF8"), result);
            result = HmacSHA256("aws4_request".getBytes(), result);
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
        return result;
    }

}
