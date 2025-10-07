/*
 * The MIT License
 * Copyright © 2025 OpenUniverse
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.ou.common.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author k2Xzny
 */
/**
 * Command-line instruction for decrypting an encrypted text.
 *
 * **Placeholder Variables:** - `${text}`: The encrypted string that needs to
 * be decrypted. - `${password}`: The secret passphrase or key used for
 * decryption.
 *
 * Examples of command-line encryption and decryption:
 *
 * GPG (GNU Privacy Guard) =======================
 *
 * Encrypt a message using GPG (Symmetric Encryption with AES-256): echo -n
 * "Hello, World!" | gpg --symmetric --cipher-algo AES256 --passphrase
 * "MySecretPassword" --batch --yes --quiet --output - 2>/dev/null | base64 | tr
 * -d '\n'
 *
 * Decrypt the message using GPG: base64 -d
 * <<< "${text}" | gpg --decrypt --passphrase "${password}" --batch --yes --quiet 2>/dev/null
 *
 * OpenSSL =======
 *
 * Encrypt a message using OpenSSL (AES-256-CBC): echo -n "Hello, World!" |
 * openssl enc -aes-256-cbc -salt -base64 -pass pass:"MySecretPassword"
 * 2>/dev/null | tr -d '\n'
 *
 * Decrypt the message using OpenSSL: echo "${text}" | openssl enc -aes-256-cbc
 * -d -base64 -pass pass:"${password}" 2>/dev/null
 *
 * PASS (Standard UNIX Password Manager) =====================================
 *
 * Retrieve a stored password using pass: pass "${text}" 2>/dev/null | tr -d
 * '\n'
 */
public class TemplateUtils {

    public static Map<String, Object> overrideProperties(Map<String, Object> parentProperties, Map<String, Object> childProperties, EncryptedPlaceholderUtils.IDecryptor decryptor, char[] password) {
        Map<String, Object> newParentProperties = new LinkedHashMap(parentProperties);
        Map<String, Object> newChildProperties = new LinkedHashMap(childProperties);
        for (Map.Entry<String, Object> childEntry : newChildProperties.entrySet()) {
            Object childValue = childEntry.getValue();
            if (childValue instanceof String childValueStr) {
                childValueStr = insertValuesFromMap(childValueStr, newParentProperties, decryptor, password);
                childEntry.setValue(childValueStr);
            }
        }
        newParentProperties.putAll(newChildProperties);
        return newParentProperties;
    }

    public static Object transform(Object value, Map<String, Object> properties, EncryptedPlaceholderUtils.IDecryptor decryptor, char[] password) {
        if (value instanceof Map<?, ?> map) {
            Map<String, Object> result = new HashMap<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                Object key = entry.getKey();
                if (key instanceof String keyStr) {
                    result.put(keyStr, transform(entry.getValue(), properties, decryptor, password));
                }
            }
            return result;
        } else if (value instanceof String[] array) {
            String[] transformed = new String[array.length];
            for (int i = 0; i < array.length; i++) {
                Object element = transform(array[i], properties, decryptor, password);
                transformed[i] = element instanceof String ? (String) element : array[i];
            }
            return transformed;
        } else if (value instanceof Collection<?> collection) {
            Collection<Object> result = new ArrayList<>();
            for (Object item : collection) {
                result.add(transform(item, properties, decryptor, password));
            }
            return result;
        } else if (value instanceof String s) {
            return insertValuesFromMap(s, properties, decryptor, password);
        } else {
            return value;
        }
    }

    private static String insertValuesFromMap(String s, Map<String, Object> map, EncryptedPlaceholderUtils.IDecryptor decryptor, char[] password) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String placeholder = "${" + entry.getKey() + "}";
            Object value = entry.getValue();
            if (value != null) {
                s = s.replace(placeholder, value.toString());
            }
        }
        if (decryptor == null || password == null) {
            return s;
        }
        return EncryptedPlaceholderUtils.replaceEncryptedPlaceholders(decryptor, s, password);
    }
}
