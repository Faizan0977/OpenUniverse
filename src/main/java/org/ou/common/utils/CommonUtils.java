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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HexFormat;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.zip.Deflater;

import org.ou.common.constants.IDocFileExt;
import org.ou.common.constants.IRecordConst;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

/**
 * <p>
 * CommonUtils class.</p>
 *
 *
 * @since 1.0.21
 */
public class CommonUtils {

    /**
     * Constant <code>omYaml</code>
     */
    public static final ObjectMapper omYaml = new YAMLMapper();

    /**
     * Constant <code>om</code>
     */
    public static final ObjectMapper om = createObjectMapper();
    /**
     * Constant <code>omFormat</code>
     */
    public static final ObjectMapper omFormat = createObjectMapperWithFormatting();

    private static ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = JsonMapper.builder().enable( //
                JsonReadFeature.ALLOW_TRAILING_COMMA, //
                JsonReadFeature.ALLOW_MISSING_VALUES //
        )
                .findAndAddModules()
                .build();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.enable(JsonParser.Feature.INCLUDE_SOURCE_IN_LOCATION);
        objectMapper.enable(JsonParser.Feature.STRICT_DUPLICATE_DETECTION);
        objectMapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
        objectMapper.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        return objectMapper;
    }

    private static final Collection<String> excludeKeys = new TreeSet<>();

    static {
        excludeKeys.add(IRecordConst.RECORD_ID_KEY);
    }

    // One ThreadLocal<MessageDigest> per algorithm
    private static final ConcurrentMap<String, ThreadLocal<MessageDigest>> MD_CACHE_MAP = new ConcurrentHashMap<>();

    public static MessageDigest getMessageDigest(String algorithm) {
        ThreadLocal<MessageDigest> tl = MD_CACHE_MAP.computeIfAbsent(algorithm, alg
                -> ThreadLocal.withInitial(() -> {
                    try {
                        return MessageDigest.getInstance(alg);
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException("Unsupported algorithm: " + alg, e);
                    }
                })
        );
        MessageDigest md = tl.get();
        md.reset();
        return md;
    }

    public static InetSocketAddress parseHostPort(String hostPort) {
        if (hostPort == null || hostPort.isEmpty()) {
            throw new IllegalArgumentException("hostPort cannot be null or empty");
        }

        String host;
        int port = 0;

        int colonIndex = hostPort.indexOf(':');
        if (colonIndex >= 0) {
            host = hostPort.substring(0, colonIndex);
            try {
                port = Integer.parseInt(hostPort.substring(colonIndex + 1));
                if (port < 0 || port > 65535) {
                    throw new IllegalArgumentException("Port out of range: " + port);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid port: " + hostPort, e);
            }
        } else {
            host = hostPort;
        }
        return new InetSocketAddress(host, port);
    }

    // public static String bytesToHex(byte[] bytes) {
    //     StringBuilder hexString = new StringBuilder(bytes.length * 2);
    //     for (byte b : bytes) {
    //         hexString.append(String.format("%02x", b));
    //     }
    //     return hexString.toString();
    // }
    // public static String hashBytes(byte[] bs, String algorithm) {
    //     try {
    //         MessageDigest digest = MessageDigest.getInstance(algorithm);
    //         byte[] hashBytes = digest.digest(bs);
    //         return bytesToHex(hashBytes);
    //     } catch (NoSuchAlgorithmException e) {
    //         throw new IllegalArgumentException("Invalid hash algorithm: " + algorithm, e);
    //     }
    // }
    public static String digest(String algorithm, byte[] bs) {
        return HexFormat.of().formatHex(getMessageDigest(algorithm).digest(bs));
    }

    /**
     *
     * @param file
     * @return
     */
    public static ObjectMapper getObjectMapperByFileExt(String fileExt) {
        if ( //
                IDocFileExt.JSON.equals(fileExt) //
                || //
                IDocFileExt.JS.equals(fileExt) //
                || //
                IDocFileExt.HJSON.equals(fileExt) //
                || //
                IDocFileExt.NDJSON.equals(fileExt) //
                ) {
            return CommonUtils.om;
        } else if ( //
                IDocFileExt.YAML.equals(fileExt) //
                || //
                IDocFileExt.YML.equals(fileExt) //
                ) {
            return CommonUtils.omYaml;
        }
        return null;
    }

    private static ObjectMapper createObjectMapperWithFormatting() {
        DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
        prettyPrinter.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);

        ObjectMapper objectMapper = createObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.setDefaultPrettyPrinter(prettyPrinter);

        return objectMapper;
    }

    /**
     * <p>
     * exitWithMsg.</p>
     *
     * @param message a {@link java.lang.String} object
     */
    public static void exitWithMsg(String message, Object... params) {
        System.err.println(message.formatted(params));
        System.exit(1);
    }

    /**
     *
     * @param object
     * @return
     */
    public static List<Map<String, Object>> toList(Object object) {
        List<Map<String, Object>> listOfMaps;
        if (object instanceof Map map) {
            listOfMaps = new ArrayList<>(1);
            listOfMaps.add(map);
        } else if (object instanceof List list) {
            listOfMaps = new ArrayList<>(list);
        } else {
            listOfMaps = new ArrayList<>(0);
        }
        for (Iterator<Map<String, Object>> iterator = listOfMaps.iterator(); iterator.hasNext();) {
            Map<String, Object> map = iterator.next();
            if (map.isEmpty()) {
                iterator.remove();
            }
        }
        return listOfMaps;
    }

    /**
     * <p>
     * parseJsonToListOfMaps.</p>
     *
     * @param json a {@link java.lang.String} object
     * @return a {@link java.util.List} object
     * @throws com.fasterxml.jackson.core.JsonProcessingException if any.
     */
    public static List<Map<String, Object>> parseJsonToListOfMaps(String json) throws JsonProcessingException {
        json = json.strip();
        if (json.isEmpty()) { // Invalid
            return null;
        }
        if (!(json.startsWith("{") || json.startsWith("["))) { // Invalid
            return null;
        }
        if (!(json.endsWith("}") || json.endsWith("]"))) { // Invalid
            return null;
        }
        Object parsedJson = om.readValue(json, Object.class);
        // normalize any JSON to list of maps
        return toList(parsedJson);
    }

    /**
     * <p>
     * flattenMap.</p>
     *
     * @param separator a {@link java.lang.String} object
     * @param source a {@link java.util.Map} object
     * @param prefix a {@link java.lang.String} object
     * @param target a {@link java.util.Map} object
     */
    public static void flattenMap(String separator, Map<String, Object> source, String prefix, Map<String, Object> target) {
        for (Map.Entry<String, Object> entry : new LinkedHashMap<>(source).entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            String newKey = prefix.isEmpty() ? key : prefix + separator + key;

            if (value instanceof Map) {
                flattenMap(separator, (Map<String, Object>) value, newKey, target);
            } else {
                target.put(newKey, value);
            }
        }
    }

    /**
     * <p>
     * suffixMapKeyNames.</p>
     *
     * @param map a {@link java.util.Map} object
     * @param excludeKeys
     * @return a {@link java.util.Map} object
     */

    /*
     * <!-- Dynamic field definitions allow using convention over configuration
     * for fields via the specification of patterns to match field names.
     * EXAMPLE: name="*_i" will match any field ending in _i (like myid_i, z_i)
     * RESTRICTION: the glob-like pattern in the name attribute must have a "*" only at the start or the end. -->
     * <dynamicField name="*_i" type="pint" indexed="true" stored="true"/>
     * <dynamicField name="*_is" type="pints" indexed="true" stored="true"/>
     * <dynamicField name="*_s" type="string" indexed="true" stored="true"/>
     * <dynamicField name="*_ss" type="strings" indexed="true" stored="true"/>
     * <dynamicField name="*_l" type="plong" indexed="true" stored="true"/>
     * <dynamicField name="*_ls" type="plongs" indexed="true" stored="true"/>
     * <dynamicField name="*_b" type="boolean" indexed="true" stored="true"/>
     * <dynamicField name="*_bs" type="booleans" indexed="true" stored="true"/>
     * <dynamicField name="*_f" type="pfloat" indexed="true" stored="true"/>
     * <dynamicField name="*_fs" type="pfloats" indexed="true" stored="true"/>
     * <dynamicField name="*_d" type="pdouble" indexed="true" stored="true"/>
     * <dynamicField name="*_ds" type="pdoubles" indexed="true" stored="true"/>
     * <dynamicField name="*_dt" type="pdate" indexed="true" stored="true"/>
     * <dynamicField name="*_dts" type="pdates" indexed="true" stored="true"/>
     * <dynamicField name="*_t" type="text_general" indexed="true" stored="true" multiValued="false"/>
     * <dynamicField name="*_txt" type="text_general" indexed="true" stored="true"/>
     * <dynamicField name="random_*" type="random"/>
     * <dynamicField name="ignored_*" type="ignored"/>
     * <dynamicField name="*_c" type="currency" indexed="true" stored="true"/>
     * <!-- Subfields used with currency fieldType -->
     * <dynamicField name="*_s_ns" type="string" indexed="true" stored="false"/>
     * <dynamicField name="*_l_ns" type="plong" indexed="true" stored="false"/>
     * <!-- uncomment the following to ignore any fields that don't already match an existing
     * field name or dynamic field, rather than reporting them as an error.
     * alternately, change the type="ignored" to some other type e.g. "text" if you want
     * unknown fields indexed and/or stored by default -->
     * <!-- dynamicField name="*" type="ignored" multiValued="true" / -->
     * <!-- Field to use to determine and enforce document uniqueness.
     * Unless this field is marked with required="false", it will be a required field
     * -->
     */
    public static Map<String, Object> suffixMapKeyNames(Map<String, Object> map, Collection<String> excludeKeys) {
        Map<String, Object> resultMap = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value == null) {
                continue;
            }
            String p;
            if (value instanceof String) {
                p = "_s";
            } else if (value instanceof Integer) {
                p = "_i";
            } else if (value instanceof Long) {
                p = "_l";
            } else if (value instanceof Float) {
                p = "_f";
            } else if (value instanceof Double) {
                p = "_d";
            } else if (value instanceof Boolean) {
                p = "_b";
            } else if (value instanceof ZonedDateTime) {
                p = "_dt";
            } else if (value instanceof Collection && !((Collection) value).isEmpty() && ((Collection) value).iterator().next() instanceof String) {
                p = "_ss";
            } else if (value instanceof Collection && !((Collection) value).isEmpty() && ((Collection) value).iterator().next() instanceof Integer) {
                p = "_is";
            } else if (value instanceof Collection && !((Collection) value).isEmpty() && ((Collection) value).iterator().next() instanceof Long) {
                p = "_ls";
            } else if (value instanceof Collection && !((Collection) value).isEmpty() && ((Collection) value).iterator().next() instanceof Float) {
                p = "_fs";
            } else if (value instanceof Collection && !((Collection) value).isEmpty() && ((Collection) value).iterator().next() instanceof Double) {
                p = "_ds";
            } else if (value instanceof Collection && !((Collection) value).isEmpty() && ((Collection) value).iterator().next() instanceof Boolean) {
                p = "_bs";
            } else if (value instanceof Collection && !((Collection) value).isEmpty() && ((Collection) value).iterator().next() instanceof ZonedDateTime) {
                p = "_dts";
            } else {
                throw new IllegalArgumentException(key + " = " + value);
            }
            if (!excludeKeys.contains(key)) {
                if (!key.endsWith(p)) {
                    key += p;
                }
            }

            if (value instanceof ZonedDateTime) {
                value = value.toString();
            } else if (value instanceof Collection && !((Collection) value).isEmpty() && ((Collection) value).iterator().next() instanceof ZonedDateTime) {
                Collection zdtCollection = (Collection) value;
                Collection<String> stringCollection = new ArrayList<>(zdtCollection.size());
                for (Object obj : zdtCollection) {
                    ZonedDateTime zdt = (ZonedDateTime) obj;
                    stringCollection.add(zdt.toString());
                }
                value = stringCollection;
            }
            resultMap.put(key, value);
        }
        return resultMap;
    }

    /**
     *
     * @param map
     * @return
     */
    public static Map<String, Object> suffixMapKeyNames(Map<String, Object> map) {
        return suffixMapKeyNames(map, excludeKeys);
    }

    /**
     * <p>
     * readJsonFromInputStream.</p>
     *
     * @param inputStream a {@link java.io.InputStream} object
     * @return a {@link java.lang.String} object
     * @throws java.io.IOException if any.
     */
    public static String readJsonFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder sb = new StringBuilder();
        int ch;
        int braceCount = 0;
        boolean insideString = false;

        while ((ch = inputStream.read()) != -1) {
            if (ch == '"') {
                insideString = !insideString;
            }

            if (!insideString) {
                if (ch == '{' || ch == '[') {
                    braceCount++;
                } else if (ch == '}' || ch == ']') {
                    braceCount--;
                }
            }

            sb.append((char) ch);

            // End reading if balanced and reached the end of JSON
            if (braceCount == 0 && (ch == '}' || ch == ']')) {
                break;
            }
        }
        return sb.toString().strip();
    }

    /**
     *
     * @param t
     * @return
     */
    public static String getStackTraceAsString(Throwable t) {
        try (Writer stringWriter = new StringWriter(); PrintWriter printWriter = new PrintWriter(stringWriter);) {
            t.printStackTrace(printWriter);
            printWriter.flush();
            return stringWriter.toString();
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     *
     * @param compresionLevel
     * @param data
     * @return
     * @throws Exception
     */
    public static byte[] compress(int compresionLevel, byte[] data) throws Exception {
        Deflater deflater = compresionLevel < 0 ? new Deflater() : new Deflater(compresionLevel);
        deflater.setInput(data);
        deflater.finish();

        byte[] buffer = new byte[1024];
        int compressedLength;

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            while (!deflater.finished()) {
                compressedLength = deflater.deflate(buffer);
                bos.write(buffer, 0, compressedLength);
            }
            return bos.toByteArray();
        }
    }

    /**
     *
     * @param headersList
     * @return
     */
    public static Map<String, String> convertHeadersToMap(Collection<String> headersList) {
        Map<String, String> headersMap = new LinkedHashMap<>(headersList.size());
        for (String header : headersList) {
            String[] parts = header.split(":", 2);
            if (parts.length == 2) {
                headersMap.put(parts[0].strip(), parts[1].strip());
            }
        }
        return headersMap;
    }

    /**
     *
     * @param headers
     * @return
     */
    public static String[] convertHeadersToArray(Collection<String> headers) {
        return headers.stream()
                .flatMap(header -> {
                    String[] parts = header.split(":", 2);
                    return parts.length == 2 ? List.of(parts[0].strip(), parts[1].strip()).stream() : List.<String>of().stream();
                })
                .toArray(String[]::new);
    }
}
