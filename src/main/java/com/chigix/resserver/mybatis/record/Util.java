package com.chigix.resserver.mybatis.record;

import java.math.BigInteger;
import org.apache.ibatis.session.RowBounds;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class Util {

    public static final RowBounds ONE_ROWBOUND = new RowBounds(0, 1);

    /**
     * In the array returned, lower values are in smaller indexes which are
     * greater than or equal to 0.
     * 
     * [4-byte value][byte value]
     *   arr[1]          arr[0]
     *
     * @param num
     * @return
     */
    public static int[] toBase256(BigInteger num) {
        int prev_place_value = 0;
        String hex = num.toString(16);
        if (hex.length() < 9) {
            return new int[]{Integer.parseUnsignedInt(hex, 16)};
        } else {
            prev_place_value = Integer.parseUnsignedInt(
                    hex.substring(hex.length() - 8), 16);
        }
        return new int[]{
            prev_place_value,
            Integer.parseUnsignedInt(hex.substring(0, hex.length() - 8), 16)
        };
    }

    public static BigInteger fromBase256(int[] place_value) {
        StringBuilder hex = new StringBuilder();
        for (int i = place_value.length - 1; i >= 0; i--) {
            String hex_part = Integer.toHexString(place_value[i]);
            hex.append(("00000000" + hex_part).substring(hex_part.length()));
        }
        return new BigInteger(hex.toString());
    }

}
