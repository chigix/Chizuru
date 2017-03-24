package com.chigix.resserver.mybatis.bean;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public interface ResourceExtension {

    Integer getId();

    void setId(Integer id);

    String getKeyHash();

    public static String hashKey(String bucket_uuid, String resource_key) {
        String keytohash = MessageFormat.format("[bucket: {0}, key: {1}]", bucket_uuid, resource_key);
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
        digest.update(keytohash.getBytes());
        StringBuilder sb = new StringBuilder();
        byte[] hashed = digest.digest();
        for (byte cipher_byte : hashed) {
            sb.append(String.format("%02x", cipher_byte & 0xff));
        }
        return sb.toString();
    }
}
