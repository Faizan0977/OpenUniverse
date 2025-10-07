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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * <p>
 * HeadTo class.</p>
 *
 * @since 1.0.21
 */
/*
|JSON Key       |Data&nbsp;Type    |Required|Description                                                                                               
|---------------|------------------|--------|----------------------------------------------------------------------------------------------------------
|`"name"`       |`String`          |Yes     |The name of the document.                                                                                   
|`"doc_type"`   |`String`          |Yes     |Defines the nature of the JSON configuration document. <br> Possible values: <br> **- "root"**: Root-level JSON configuration document. <br> **- "event"**: Event-related JSON configuration document. <br> **- "timesheet"**: Timesheet-related JSON configuration document. <br> **- "system"**: System-related JSON configuration document. <br> **- "event_processor"**: Processor-specific JSON configuration document. <br> **- "job"**: Job-related JSON configuration document. <br> **- "triggers/event_calendar"**: Calendar-related JSON configuration document. <br> **- "triggers/event_scheduler"**: Scheduler-related JSON configuration document. <br> **- "triggers/event_provider"**: Trigger-related JSON configuration document. |
|`"disabled"`   |`Boolean`         |No      |Indicates whether the document is disabled (`true`) or active (`false`). Default: `false`.                
|`"description"`|`String`          |No      |A brief description of the document. Default: No description.                                              
|`"constraint"` |`Object`          |No      |Constraints such as time intervals and user access restrictions. Default: No constraints.                  
|`"tags"`       |`Array of Strings`|No      |A collection of tags to categorize or label the document. Default: No tags.                               
|`"attr"`       |`Object`          |No      |A map of attributes where keys are attribute names, and values can be of any type. Default: No attributes.
*/
/**
 * Represents a document configuration object that can describe various types of JSON configuration documents. 
 * This class is used to define the properties and metadata of configuration documents, including document 
 * type, name, description, constraints, tags, and attributes. The field values are designed to be flexible 
 * for use in different contexts such as event management, job scheduling, system configuration, etc.
 *
 * <h2>Example Use Cases:</h2>
 * <ul>
 *   <li><b>System Configuration Document:</b> Defines the overall structure of a system configuration JSON.</li>
 *   <li><b>Event Processor Configuration:</b> Stores settings related to event processing, such as filters and triggers.</li>
 *   <li><b>Job Execution Configuration:</b> Defines the tasks to be executed across multiple systems, with configurations for scheduling, retries, and logging.</li>
 * </ul>
 */
public class HeadTo {

    /**
     * The specification of the document, which defines the version and structure of the JSON configuration.
     * <p>
     * JSON Key: "doc_spec"
     * <p>
     * Example: `"OpenUniverseSpecVer-1.01"``.
     * </p>
     */
    public String docSpec;

    /**
     * The document type, which defines the nature of the JSON configuration document.
     * <p>
     * JSON Key: "doc_type"
     * <p>
     * Possible values for docType:
     * - {@link org.ou.common.constants.IDocTypes#DOC_TYPE_ROOT}                     - "root"`: Root-level JSON configuration document, representing the top-level configuration.
     * - {@link org.ou.common.constants.IDocTypes#DOC_TYPE_EVENT}                    - "event"`: Event-related JSON configuration document, such as event-based triggers or logs.
     * - {@link org.ou.common.constants.IDocTypes#DOC_TYPE_TIMESHEET}                - "timesheet"`: Timesheet-related JSON configuration document used to manage work hours, time intervals, and timesheet settings.
     * - {@link org.ou.common.constants.IDocTypes#DOC_TYPE_SYSTEM}                   - "system"`: System-related JSON configuration document, including system-wide metadata and connection settings.
     * - {@link org.ou.common.constants.IDocTypes#DOC_TYPE_EVENT_PROCESSOR}          - "event_processor"`: Processor-specific JSON configuration document defining processor behavior or settings.
     * - {@link org.ou.common.constants.IDocTypes#DOC_TYPE_JOB}                      - "job"`: Job-related JSON configuration document, representing tasks executed across multiple systems in parallel.
     * - {@link org.ou.common.constants.IDocTypes#DOC_TYPE_TRIGGERS_EVENT_CALENDAR}  - "triggers/event_calendar"`: Calendar-related JSON configuration document used for date/time scheduling or calendar events.
     * - {@link org.ou.common.constants.IDocTypes#DOC_TYPE_TRIGGERS_EVENT_SCHEDULER} - "triggers/event_scheduler"`: Scheduler-related JSON configuration document for scheduling tasks, including timing and dependencies.
     * - {@link org.ou.common.constants.IDocTypes#DOC_TYPE_TRIGGERS_EVENT_PROVIDER}  - "triggers/event_provider"`: Trigger-related JSON configuration document, defining actions based on specific events.
     * </p>
     */
    public String docType;

    /**
     * Indicates whether the document is disabled or not.
     * Optional, default value: false
     * <p>
     * JSON Key: "disabled"
     * <p>
     * When set to true, this document is disabled and should not be processed.
     * If false, the document is active and will be processed as expected.
     * </p>
     */
    public Boolean disabled;

    /**
     * The name of the document.
     * <p>
     * JSON Key: "name"
     * </p>
     * This field holds the name of the document. The name is used for identification and reference purposes. 
     * Example: `"System Configuration"` or `"Event Processor Settings"`.
     */
    public String name;

    /**
     * A brief description of the document.
     * Optional, default: no description 
     * <p>
     * JSON Key: "description"
     * </p>
     * This field provides a human-readable explanation of what the document represents or contains.
     * Example: `"Configuration document for system settings"` or `"Defines parameters for event processor"` 
     * If no description is provided, it defaults to an empty or null value.
     */
    public String description;

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
     * commandTo.instancesCount = 1;
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
     *     "instances_count": 1
     *   }
     * }
     * </pre>
     */
    public List<CommandTo> validatorCommands;

    /**
     * A collection of tags that can be used to categorize or label the document.
     * Optional, default: no tags 
     * <p>
     * JSON Key: "tags"
     * </p>
     * Tags help organize and classify documents based on common themes or categories. 
     * Example: `"config", "event", "timesheet"`.
     */
    public Collection<String> tags = new HashSet<>();

    /**
     * A map of additional attributes for the document, where the key is the attribute name, and the value can be any type.
     * Optional, default: no attributes 
     * <p>
     * JSON Key: "attr"
     * </p>
     * Attributes allow for flexible extension of the document, enabling it to store extra information that does not fit into the other predefined fields.
     * Example: `"priority": "high"`, `"version": "1.0"`, or `"owner": "admin"`.
     */
    public Map<String, Object> attr = new HashMap<>();

    /**
     * A unique identifier for the document, generated by UUID.
     * <p>
     * This field ensures that each document can be uniquely identified. It is often used for tracking 
     * and distinguishing between different document instances.
     */
    public String id = UUID.randomUUID().toString();

    /**
     * The file path where the document is stored or will be written.
     * This is used to store the actual document data and may be null if the document is not yet saved to disk.
     */
    public String docFile;

    /**
     * The index of the document within its respective file or system.
     * This field helps maintain the position of the document if multiple documents are part of a larger collection.
     */
    public int indexInFile = -1;

    /**
     * The SHA-1 hash of the document to ensure data integrity and verify that the document has not been tampered with.
     */
    public String docSha1;

    /**
     * The SHA-1 hash of the document’s blob content for verifying its integrity.
     */
    public String docBlobSha1;
}
