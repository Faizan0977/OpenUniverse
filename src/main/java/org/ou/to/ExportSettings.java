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
package org.ou.to;

import java.util.Objects;

/**
 * This class holds the settings for configuring export targets.
 * It defines the necessary parameters to specify where and how data should be exported, 
 * including options for compression, TLS encryption, and digital signing.
 * 
 * The fields correspond to the keys in the JSON configuration that defines the export settings.
 * This allows the application to load, transform, and export data in different formats and destinations, 
 * such as files, databases, or HTTP servers.
 * 
 * Example JSON configuration:
 * {
 *   "id": "http-server123",
 *   "type": "http-client",
 *   "store_as_array": true,
 *   "enable_compression": true,
 *   "compression_level": 5,
 *   "keys_black_list": ["password", "secretKey"],
 *   "keys_white_list": ["userId", "email"],
 *   "js_transformer_func": "(record) => { return { transformed: record.x * 2 }; }",
 * }
 */
public class ExportSettings {

    /**
     * Unique identifier for the export target instance.
     * This ID is used to distinguish different export targets in a system.
     * 
     * JSON Key: "id"
     * Example: "http-server123"
     */
    public String id;

    /**
     * Whether to store record data as an array (values only) instead of an object (key-value pairs).
     * Storing data as an array can significantly reduce payload size, leading to 
     * lower network traffic and improved transmission efficiency. This is especially 
     * beneficial when sending large amounts of structured data with repetitive keys.  
     * 
     * JSON Key: "store_as_array"
     * Example:  
     * - `true` → `["value1", "value2", "value3"]` (JSON array, values only)  
     * - `false` → `{"key1": "value1", "key2": "value2", "key3": "value3"}` (JSON object, key-value pairs)
     */
    public Boolean storeAsArray;

    /**
     * Enables compression for stored data.
     * If enabled, the data will be compressed to reduce storage space.
     * 
     * JSON Key: "enable_compression"
     * Example: true (to enable compression), false (to disable)
     */
    public Boolean enableCompression;

    /**
     * Compression level to use when compression is enabled.
     * Higher values provide better compression but may slow down processing.
     * 
     * JSON Key: "compression_level"
     * Example: 5 (medium compression level)
     * Range: 1 (fast, less compression) to 9 (slow, high compression)
     */
    public Integer compresionLevel;

    /**  
     * Represents the command execution configuration.
     *
     * This field holds an instance of {@link CommandTo}, which defines the parameters 
     * required to execute a system command. It includes the command itself, its working 
     * directory, environment variables, arguments, error logging, and concurrency settings.
     *
     * <h2>Usage Example:</h2>
     * <pre>
     * commandTo = new CommandTo();
     * commandTo.cmd = "java";
     * commandTo.workingDir = "/home/user/projects";
     * commandTo.envVars = Map.of("JAVA_HOME", "/usr/lib/jvm/java-17");
     * commandTo.args = List.of("-jar", "app.jar", "--debug");
     * commandTo.errorLogFile = "/var/logs/app_errors.log";
     * commandTo.instancesCount = 3;
     * </pre>
     *
     * <h2>JSON Representation:</h2>
     * When serialized to JSON, this field will be represented as an embedded object.
     * <pre>
     * {
     *   "command": {
     *     "cmd": "java",
     *     "working_dir": "/home/user/projects",
     *     "env_vars": { "JAVA_HOME": "/usr/lib/jvm/java-17" },
     *     "args": ["-jar", "app.jar", "--debug"],
     *     "error_log_file": "/var/logs/app_errors.log",
     *     "instances_count": 3
     *   }
     * }
     * </pre>
     *
     * <h2>Considerations:</h2>
     * - Ensure that {@code commandTo} is properly initialized before use.
     * - If {@code commandTo} is null, execution-related operations may fail.
     */
    public CommandTo commandTo = new CommandTo();

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ExportSettings other = (ExportSettings) obj;
        return Objects.equals(this.id, other.id);
    }

}
