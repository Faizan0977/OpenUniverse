package org.ou.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ou.common.constants.IPropPrefixes;

public class EncryptedPlaceholderUtils {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{" + IPropPrefixes.SEC + ":([^}]+)}");

    /**
     * Interface for providing decryption logic.
     */
    public interface IDecryptor {
        String decrypt(String encrypted, char[] password) throws Exception;
    }

    /**
     * Replaces all ${encrypted:...} placeholders in the input string with decrypted values.
     * If decryption fails or returns an empty string, the original placeholder is preserved.
     *
     * @param decryptor implementation of decryption logic
     * @param input     the string possibly containing encrypted placeholders
     * @param password  the decryption password
     * @return string with all placeholders replaced by decrypted content
     */
    public static String replaceEncryptedPlaceholders(IDecryptor decryptor, String input, char[] password) {
        if (input == null || password == null || decryptor == null) return input;

        Matcher matcher = PLACEHOLDER_PATTERN.matcher(input);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String encryptedPart = matcher.group(1);
            String fullPlaceholder = matcher.group(0);
            String decryptedString;

            try {
                decryptedString = decryptor.decrypt(encryptedPart, password);
                if (decryptedString == null || decryptedString.isEmpty()) {
                    decryptedString = fullPlaceholder; // preserve original placeholder
                }
            } catch (Exception e) {
                decryptedString = fullPlaceholder; // preserve on error
            }

            matcher.appendReplacement(result, Matcher.quoteReplacement(decryptedString));
        }

        matcher.appendTail(result);
        return result.toString();
    }

}
