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

/**
 * <p>
 * ProcessorTo class.</p>
 * 
 * The Processor is a standard operating system command-line program that waits for an event (in JSON format) from a trigger via standard input (stdin).
 * Upon receiving the event, the Processor
 * generates a response, which can be:
 *
 * A new event triggered by the Processor itself, or
 * A log entry created by the Processor.
 * The response is output via standard output (stdout).
 *
 * Both the Processor and the Trigger are key objects in the system architecture, playing a central role in the event-driven workflow.
 * The Processor handles the event data, processes it, and generates
 * the appropriate response, while the Trigger is responsible for initiating the process by sending events to the Processor.
 *
 * To perform its tasks, the Processor connects to the System using the connection parameters defined in the System object. These connection parameters are essential for enabling communication between
 * the Processor and the System, ensuring that the Processor can access necessary resources or services.
 *

 * @since 1.0.21
 */
public class ProcessorDocTo extends AbstractTo {

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
}
