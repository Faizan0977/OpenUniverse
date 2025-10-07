package org.ou.common.utils;

import java.util.ArrayList;
import java.util.List;

public class StringUtils {

    /**
     * Parse a string of digits in given base into raw bytes.
     * Supports base 2, 8, 10, and 16.
     *
     * @param valueString string of digits (e.g. "01100001" in base 2, "141" in base 8, "065" in base 10, "61" in base 16)
     * @param radix base (2=binary, 8=octal, 10=decimal with 3 digits per byte, 16=hex)
     * @return byte array
     */
    public static byte[] parseBytes(String valueString, int radix) {
        List<Byte> result = new ArrayList<>();
        int chunkSize;

        switch (radix) {
            case 2:  chunkSize = 8; break;  // 8 bits = 1 byte
            case 8:  chunkSize = 3; break;  // up to "377" = 255
            case 10: chunkSize = 3; break;  // fixed 3 digits per byte (000–255)
            case 16: chunkSize = 2; break;  // 2 hex chars = 1 byte
            default:
                throw new IllegalArgumentException("Unsupported base: " + radix);
        }

        for (int i = 0; i < valueString.length(); i += chunkSize) {
            String chunk = valueString.substring(i, Math.min(i + chunkSize, valueString.length()));
            int val = Integer.parseInt(chunk, radix);
            if (val < 0 || val > 255) {
                throw new IllegalArgumentException("Invalid byte value: " + chunk + " in base " + radix);
            }
            result.add((byte) val);
        }

        byte[] arr = new byte[result.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = result.get(i);
        }
        return arr;
    }
}