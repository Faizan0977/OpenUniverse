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
package org.ou.common.constants;

/**
 * <p>
 * IEventConst interface.</p>
 *

 * @since 1.0.21
 */
public interface IEventConst {
    String EVENT_DATE_TIME_KEY = "event_date_time";
    String EVENT_SOURCE_CLASS_KEY = "event_source_class";
    String EVENT_SOURCE_CLASS_VALUE_INTERNAL_ERROR = "internal_error";
    String EVENT_SOURCE_CLASS_VALUE_CONTROL = "control";
    String EVENT_SOURCE_CLASS_VALUE_HEALTH = "health";
    String EVENT_SOURCE_CLASS_VALUE_REPO = "repo";
    String EVENT_SOURCE_CLASS_VALUE_TRIGGERS_EVENT_CALENDAR = "triggers_event_calendar";
    String EVENT_SOURCE_CLASS_VALUE_TRIGGERS_EVENT_SCHEDULER = "triggers_event_scheduler";
    String EVENT_SOURCE_CLASS_VALUE_TRIGGERS_EVENT_PROVIDER = "triggers_event_provider";
    String EVENT_SOURCE_CLASS_VALUE_PROCESSOR = "processor";
    String EVENT_TYPE_KEY = "event_type";

    /* Default event type (used when event type is null) */

    String EVENT_TYPE_VALUE_DEFAULT = "unknown";

    /* Special event types */

    String EVENT_TYPE_VALUE_INTERNAL_ERROR = "internal_error";
    String EVENT_TYPE_VALUE_CONTROL_START = "start";
    String EVENT_TYPE_VALUE_CONTROL_STOP = "stop";
    String EVENT_TYPE_VALUE_CONTROL_PAUSE = "pause";
    String EVENT_TYPE_VALUE_CONTROL_RESUME = "resume";
    String EVENT_TYPE_VALUE_REPO_COMMIT = "commit";
    
    String EVENT_TYPE_VALUE_HEALTH_OS = "health_os";
    String EVENT_TYPE_VALUE_HEALTH_JVM = "health_jvm";
    String EVENT_TYPE_VALUE_HEALTH_OU = "health_ou";
    
    String EVENT_ERROR_MSG_KEY = "message";
    String EVENT_ERROR_MSG_LOCALIZED_KEY = "message_localized";
    String EVENT_ERROR_STACK_TRACE_AS_STRING_KEY = "stack_trace_as_string";
}
