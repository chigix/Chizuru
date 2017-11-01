package com.chigix.resserver.interfaces.handling.http;

import io.netty.handler.codec.http.HttpRequest;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class HttpHeaderUtil {

    public static boolean isGzip(HttpRequest req) {
        String[] accepting_encodings;
        try {
            accepting_encodings = req.headers().get(HttpHeaderNames.ACCEPT_ENCODING).toString().split(",");
        } catch (NullPointerException e) {
            accepting_encodings = new String[]{};
        }
        for (String accepting_encoding : accepting_encodings) {
            if (accepting_encoding.trim().equalsIgnoreCase("gzip")) {
                return true;
            }
        }
        return false;
    }

    public static Range[] decodeRange(String rangeHeader) throws InvalidRangeHeader {
        ArrayList<Range> ranges = new ArrayList<>();
        String byteRangeSetRegex = "(((?<byteRangeSpec>(?<firstBytePos>\\d+)-(?<lastBytePos>\\d+)?)|(?<suffixByteRangeSpec>-(?<suffixLength>\\d+)))(,|$))";
        String byteRangesSpecifierRegex = "bytes=(?<byteRangeSet>" + byteRangeSetRegex + "{1,})";
        Pattern byteRangeSetPattern = Pattern.compile(byteRangeSetRegex);
        Pattern byteRangesSpecifierPattern = Pattern.compile(byteRangesSpecifierRegex);
        Matcher byteRangesSpecifierMatcher = byteRangesSpecifierPattern.matcher(rangeHeader);
        if (byteRangesSpecifierMatcher.matches()) {
            String byteRangeSet = byteRangesSpecifierMatcher.group("byteRangeSet");
            Matcher byteRangeSetMatcher = byteRangeSetPattern.matcher(byteRangeSet);
            while (byteRangeSetMatcher.find()) {
                Range r = new Range();
                if (byteRangeSetMatcher.group("byteRangeSpec") != null) {
                    r.start = byteRangeSetMatcher.group("firstBytePos");
                    r.end = byteRangeSetMatcher.group("lastBytePos");
                } else if (byteRangeSetMatcher.group("suffixByteRangeSpec") != null) {
                    r.suffixLength = byteRangeSetMatcher.group("suffixLength");
                } else {
                    throw new InvalidRangeHeader(rangeHeader);
                }
                ranges.add(r);
            }
        } else {
            throw new RuntimeException("Invalid Range Header:" + rangeHeader);
        }
        return ranges.toArray(new Range[ranges.size()]);
    }

    public static class Range {

        public static final Range ZERO() {
            Range zero = new Range();
            zero.start = "0";
            zero.end = "0";
            return zero;
        }

        public String start;
        public String end;
        public String suffixLength;

    }

    public static class InvalidRangeHeader extends Exception {

        private final String rawRangeHeader;

        public InvalidRangeHeader(String rawRangeHeader) {
            this.rawRangeHeader = rawRangeHeader;
        }

        public String getRawRangeHeader() {
            return rawRangeHeader;
        }

    }
}
