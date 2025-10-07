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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * CommandTo class.</p>
 *
 * @since 1.0.21
 */
/**
 * Represents a system command execution configuration.
 *
 * The {@code CommandTo} class defines the parameters needed to execute a command-line 
 * process, including the command itself, its working directory, environment variables, 
 * arguments, error logging, and concurrency settings.
 *
 * This class provides a structured way to configure and manage the execution of 
 * system commands or scripts, making it useful for automation, scripting, and process management.
 *
 * <h2>Usage Example:</h2>
 * <pre>
 * CommandTo command = new CommandTo();
 * command.cmd = "java";
 * command.workingDir = "/home/user/projects";
 * command.envVars = Map.of("JAVA_HOME", "/usr/lib/jvm/java-17");
 * command.args = List.of("-jar", "app.jar", "--debug");
 * command.errorLogFile = "/var/logs/app_errors.log";
 * command.instancesCount = 3;
 * </pre>
 *
 * <h2>JSON Representation:</h2>
 * This class can be serialized into a JSON structure for easy storage and transmission.
 *
 * <table border="1">
 *   <tr>
 *     <th>JSON Key</th>
 *     <th>Data Type</th>
 *     <th>Description</th>
 *     <th>Example</th>
 *   </tr>
 *   <tr>
 *     <td>{@code cmd}</td>
 *     <td>String</td>
 *     <td>The command or executable to be run.</td>
 *     <td>{@code "java"} or {@code "/usr/bin/python3"}</td>
 *   </tr>
 *   <tr>
 *     <td>{@code working_dir}</td>
 *     <td>String (nullable)</td>
 *     <td>The working directory where the command will execute.</td>
 *     <td>{@code "/home/user/projects"}</td>
 *   </tr>
 *   <tr>
 *     <td>{@code env_vars}</td>
 *     <td>Object (Map)</td>
 *     <td>A map of environment variables to set before execution.</td>
 *     <td>{@code {"JAVA_HOME": "/usr/lib/jvm/java-17", "PATH": "/bin"}}</td>
 *   </tr>
 *   <tr>
 *     <td>{@code args}</td>
 *     <td>Array of strings</td>
 *     <td>List of arguments to append to the command.</td>
 *     <td>{@code ["-jar", "app.jar", "--debug"]}</td>
 *   </tr>
 *   <tr>
 *     <td>{@code error_log_file}</td>
 *     <td>String (nullable)</td>
 *     <td>The file where error logs will be written.</td>
 *     <td>{@code "/var/logs/app_errors.log"}</td>
 *   </tr>
 *   <tr>
 *     <td>{@code instances_count}</td>
 *     <td>Integer (nullable)</td>
 *     <td>Number of concurrent command instances.</td>
 *     <td>{@code 3}</td>
 *   </tr>
 * </table>
 *
 * <h2>Thread Safety:</h2>
 * This class is not inherently thread-safe. If used in a multi-threaded environment, 
 * external synchronization should be considered when modifying its properties.
 *
 * <h2>Limitations:</h2>
 * - The command must be available on the system where it is executed.
 * - The working directory must be valid, or execution may fail.
 * - Environment variables should be correctly set to avoid unintended behavior.
 * - If the specified error log file is not writable, error logging may fail.
 * - If running multiple instances, system resource limits should be considered.
 */
/**  
| JSON Key          | Data Type          | Description                                         | Example                                                   |  
|-------------------|--------------------|-----------------------------------------------------|-----------------------------------------------------------|  
| `cmd`             | String             | The command or executable to be run. This should be a valid system command, script, or application path. | `"java"` or `"/usr/bin/python3"`                          |  
| `working_dir`     | String (nullable)  | The working directory in which the command will be executed. If null, the default working directory of the process will be used. | `"/home/user/projects"`                                   |  
| `env_vars`        | Object             | A map of environment variables to be set before executing the command. These values will override the system environment variables if there are conflicts. | `{"JAVA_HOME": "/usr/lib/jvm/java-17", "PATH": "/bin"}`   |  
| `args`            | Array of strings   | A list of arguments to be passed to the command. These arguments will be appended to the command during execution. Example: If `cmd` is `"java"` and `args` contains `["-jar", "app.jar"]`, the final command will be `"java -jar app.jar"`. | `["-jar", "app.jar", "--debug"]`                          |  
| `error_log_file`  | String (nullable)  | The file where error logs will be written, if specified. If null, errors may be printed to the console or handled differently depending on implementation. | `"/var/logs/app_errors.log"`                              |  
| `instances_count` | Integer (nullable) | The number of instances of the command to be executed concurrently. This allows running multiple instances of the same command in parallel. If null or less than 1, the default behavior may be to run a single instance. | `3`                                                       |  
 */
public class CommandTo {

    /**  
     * JSON Key: {@code cmd}
     * 
     * The command or executable to be run.
     * This should be a valid system command, script, or application path.
     *
     * <p>Example:</p>
     * <pre>
     * cmd = "java";
     * cmd = "/usr/bin/python3";
     * </pre>
     */
    public String cmd;

    /**  
     * JSON Key: {@code working_dir}
     * 
     * The working directory in which the command will be executed.
     * If null, the default working directory of the process will be used.
     *
     * <p>Example:</p>
     * <pre>
     * workingDir = "/home/user/projects";
     * </pre>
     */
    public String workingDir;

    /**  
     * JSON Key: {@code env_vars}
     * 
     * A map of environment variables to be set before executing the command.
     * These values will override the system environment variables if there are conflicts.
     *
     * <p>Example:</p>
     * <pre>
     * envVars = Map.of("JAVA_HOME", "/usr/lib/jvm/java-17", "PATH", "/bin");
     * </pre>
     */
    public Map<String, String> envVars;

    /**  
     * JSON Key: {@code args}
     * 
     * A list of arguments to be passed to the command.
     * These arguments will be appended to the command during execution.
     *
     * <p>Example:</p>
     * <pre>
     * args = List.of("-jar", "app.jar", "--debug");
     * </pre>
     */
    public List<String> args = new ArrayList<>();

    /**  
     * JSON Key: {@code error_log_file}
     * 
     * The file where error logs will be written, if specified.
     * If null, errors may be printed to the console or handled differently 
     * depending on implementation.
     *
     * <p>Example:</p>
     * <pre>
     * errorLogFile = "/var/logs/app_errors.log";
     * </pre>
     */
    public String errorLogFile;

    /**  
     * JSON Key: {@code instances_count}
     * 
     * The number of instances of the command to be executed concurrently.
     * This allows running multiple instances of the same command in parallel.
     * If null or less than 1, the default behavior may be to run a single instance.
     *
     * <p>Example:</p>
     * <pre>
     * instancesCount = 3;
     * </pre>
     */
    public int instancesCount = 1;
}
