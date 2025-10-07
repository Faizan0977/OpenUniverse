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
 * IDocTypes interface.</p>
 *

 * @since 1.0.21
 */
public interface IDocTypes {

    /**
     * Root-level JSON configuration document.
     * Represents the root configuration document in JSON format, the top-level configuration.
     */
    String DOC_TYPE_ROOT = "root";

    /**
     * Event-related JSON configuration document.
     * Defines a JSON configuration document related to events, such as event-based triggers or logs.
     */
    String DOC_TYPE_EVENT = "event";

    /**
     * System-related JSON configuration document.
     * Identifies a system-level JSON configuration document, which may include system-wide metadata,
     * connection settings or properties.
     */
    String DOC_TYPE_SYSTEM = "system";

    /**
     * Processor-related JSON configuration document.
     * Represents a processor-specific JSON configuration document, which defines processor behavior or settings.
     */
    String DOC_TYPE_EVENT_PROCESSOR = "event_processor";

    /**
     * Job-related JSON configuration document.
     * Defines a JSON configuration document related to a specific job.
     * A job consists of a series of processes executed across multiple systems,
     * ensuring tasks are performed efficiently in parallel.
     */
    String DOC_TYPE_JOB = "job";

    /**
     * Calendar-related JSON configuration document.
     * Specifies a calendar-related JSON configuration document, typically used for date/time scheduling or calendar events.
     */
    String DOC_TYPE_TRIGGERS_EVENT_CALENDAR = "triggers/event_calendar";

    /**
     * Scheduler-related JSON configuration document.
     * Identifies a JSON configuration document for scheduling tasks or jobs, including timing and dependencies.
     */
    String DOC_TYPE_TRIGGERS_EVENT_SCHEDULER = "triggers/event_scheduler";

    /**
     * Trigger-related JSON configuration document.
     * Represents a JSON configuration document for triggers, which might define actions based on specific events.
     */
    String DOC_TYPE_TRIGGERS_EVENT_PUBLISHER = "triggers/event_provider";
}
