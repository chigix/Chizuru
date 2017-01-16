package com.chigix.resserver.mapdbimpl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class ChunkNode {

    private final String nodeKey;

    private final String parentResourceKeyHash;

    private final String contentHash;

    private ChunkNode next;

    public ChunkNode(String parentResourceKeyHash, String contentHash) {
        this.parentResourceKeyHash = parentResourceKeyHash;
        this.contentHash = contentHash;
        nodeKey = generateNodeKey();
    }

    private String generateNodeKey() {
        String keytohash = MessageFormat.format("[resource: {0}, content: {1}]", parentResourceKeyHash, contentHash);
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

    public String getParentResourceKeyHash() {
        return parentResourceKeyHash;
    }

    public String getContentHash() {
        return contentHash;
    }

    public ChunkNode getNext() {
        return next;
    }

    public void setNext(ChunkNode next) {
        this.next = next;
    }

    public String getNodeKey() {
        return nodeKey;
    }

    @Override
    public String toString() {
        return "ChunkNode#{NodeKey: " + nodeKey + ", Resource: " + parentResourceKeyHash + ", Hash: " + contentHash + "}";
    }

}
