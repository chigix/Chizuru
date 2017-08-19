package com.chigix.resserver.endpoint.GetBucket;

import java.util.Iterator;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
class Util {

    /**
     *
     * @param key The object's key.
     * @param presetPrefix Keys that begin with the indicated prefix.
     * @param delimiter Causes keys that contain the same string between the
     * prefix and the first occurrence of the delimiter to be rolled up into a
     * single result element in the CommonPrefixes collection.
     * @return
     */
    public static final String extractCommonPrefix(String key, String presetPrefix, String delimiter) {
        if (delimiter == null) {
            return null;
        }
        if (presetPrefix == null) {
            presetPrefix = "";
        }
        int cut_pos = key.indexOf(delimiter, presetPrefix.length());
        if (cut_pos > 0) {
            return key.substring(0, cut_pos + delimiter.length());
        } else {
            return null;
        }
    }

}
