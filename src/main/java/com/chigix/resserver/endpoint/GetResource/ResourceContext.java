package com.chigix.resserver.endpoint.GetResource;

import com.chigix.resserver.application.Context;
import com.chigix.resserver.interfaces.handling.http.HttpHeaderUtil;
import com.chigix.resserver.interfaces.handling.http.HttpHeaderUtil.Range;
import io.netty.handler.codec.http.HttpHeaderNames;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.LinkedList;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class ResourceContext extends Context {

    private final HttpHeaderUtil.Range range;

    public ResourceContext(Context src) throws HttpHeaderUtil.InvalidRangeHeader {
        super(src);
        final CharSequence range_string = getRoutedInfo().getRequestMsg().headers().get(HttpHeaderNames.RANGE);
        HttpHeaderUtil.Range[] raws = new HttpHeaderUtil.Range[0];
        if (range_string != null) {
            raws = HttpHeaderUtil.decodeRange(range_string.toString());
        }
        final BigInteger endpoint_byte = BigInteger.ZERO
                .max(
                        new BigInteger(src.getResource().getSize()).subtract(BigInteger.ONE)
                );
        LinkedList<Range> temp_ranges = new LinkedList<>();
        Range start_zero = Range.ZERO();
        temp_ranges.offerLast(start_zero);
        for (HttpHeaderUtil.Range raw_range : raws) {
            final BigInteger raw_start;
            final BigInteger raw_end;
            if (raw_range.suffixLength != null) {
                raw_end = endpoint_byte;
                raw_start = BigInteger.ZERO.max(raw_end.subtract(new BigInteger(raw_range.suffixLength)));
            } else {
                raw_start = BigInteger.ZERO.max(new BigInteger(raw_range.start));
                raw_end = raw_range.end == null ? endpoint_byte : endpoint_byte.min(new BigInteger(raw_range.end));
            }
            if (raw_start.signum() == -1) {
                throw invalidRangeHeader(raw_range);
            }
            if (raw_end.compareTo(raw_start) < 0) {
                throw invalidRangeHeader(raw_range);
            }
            HttpHeaderUtil.Range last = temp_ranges.peekLast();
            final BigInteger last_end = new BigInteger(last.end);
            if (last_end.compareTo(raw_end) >= 0) {
                continue;
            }
            if (raw_start.subtract(last_end).compareTo(BigInteger.ONE) <= 0) {
                last.end = raw_end.toString();
                continue;
            }
            Range updated_raw_range = new Range();
            updated_raw_range.start = raw_start.toString();
            updated_raw_range.end = raw_end.toString();
            temp_ranges.offerLast(updated_raw_range);
        }
        if (start_zero.end.equals("0")) {
            temp_ranges.pollFirst();
        }
        range = temp_ranges.pollFirst();
        if (range_string != null && range == null) {
            throw new HttpHeaderUtil.InvalidRangeHeader(range_string.toString());
        }
    }

    public HttpHeaderUtil.Range getRange() {
        return range;
    }

    private HttpHeaderUtil.InvalidRangeHeader invalidRangeHeader(Range range) {
        return new HttpHeaderUtil.InvalidRangeHeader(range.suffixLength == null
                ? MessageFormat.format("{0}-{1}", range.start, range.end)
                : "-" + range.suffixLength);
    }
}
