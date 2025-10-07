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
package org.ou.common.cron;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import org.ou.process.TriggerQueueEntry;
import org.ou.to.CalendarDocTo;
import org.ou.to.RootDocTo;
import org.ou.to.SchedulerDocTo;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.ou.process.TriggerUtils;
import org.ou.to.AbstractTo;

/**
 * <p>
 * CronJob class.</p>
 *

 * @since 1.0.21
 */
public class CronJob implements Job {

    /**
     * <p>
     * Constructor for CronJob.</p>
     */
    public CronJob() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        RootDocTo rootTo = (RootDocTo) jobDataMap.get("configTo");

        Map<String, Object> eventData = new LinkedHashMap((Map) jobDataMap.get("eventData"));
        CalendarDocTo calendarTo = (CalendarDocTo) eventData.remove("calendarTo");
        SchedulerDocTo schedulerTo = (SchedulerDocTo) eventData.remove("schedulerTo");

        BlockingQueue<TriggerQueueEntry> triggersQueue = (BlockingQueue) jobDataMap.get("triggersQueue");
        BlockingQueue<Map<String, Object>> loggerQueue = (BlockingQueue) jobDataMap.get("loggerQueue");

        AbstractTo abstractTo;
        if (calendarTo != null) {
            abstractTo = calendarTo;
        } else if (schedulerTo != null) {
            abstractTo = schedulerTo;
        } else {
            throw new IllegalArgumentException();
        }
        List<Map<String, Object>> listOfMaps = new ArrayList<>(1);
        listOfMaps.add(eventData);
        try {
            TriggerUtils.processEvent(listOfMaps, rootTo, abstractTo, triggersQueue, loggerQueue);
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
    }
}
