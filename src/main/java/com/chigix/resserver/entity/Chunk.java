package com.chigix.resserver.entity;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class Chunk {

    private final int size;

    private final String contentHash;

    public Chunk(String contentHash, int size) {
        this.contentHash = contentHash;
        this.size = size;
    }

    public InputStream getInputStream() throws IOException {
        return new InputStream() {
            @Override
            public int read() throws IOException {
                return -1;
            }
        };
    }

    public String getContentHash() {
        return contentHash;
    }

    /**
     * SHA-256 digest which could be compared with the `X-Amz-Content-Sha256`
     *
     * @return
     */
    public static MessageDigest contentHashDigest() {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
        return digest;
    }

    public int getSize() {
        return size;
    }

}
