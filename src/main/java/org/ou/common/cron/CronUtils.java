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

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

import org.ou.common.constants.IEventConst;
import org.ou.process.TriggerQueueEntry;
import org.ou.to.AbstractTo;
import org.ou.to.CalendarDocTo;
import org.ou.to.RootDocTo;
import org.ou.to.SchedulerDocTo;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

/**
 * <p>
 * CronUtils class.</p>
 *

 * @since 1.0.21
 */
public class CronUtils {

    /**
     * <p>
     * scheduleCalendars.</p>
     *
     * @param scheduler a {@link org.quartz.Scheduler} object
     * @param rootTo a {@link org.ou.to.RootDocTo} object
     * @param triggersQueue a {@link java.util.concurrent.BlockingQueue} object
     * @param loggerQueue a {@link java.util.concurrent.BlockingQueue} object
     * @throws org.quartz.SchedulerException if any.
     */
    public static void scheduleCalendars(Scheduler scheduler, RootDocTo rootTo, BlockingQueue<TriggerQueueEntry> triggersQueue, BlockingQueue<Map<String, Object>> loggerQueue) throws SchedulerException {
        JobDataMap jobDataMap = createBasicJobDataMap(rootTo, triggersQueue, loggerQueue);

        Map<Date, Collection<Map<String, Object>>> calendarMap = createCalendarMap(rootTo.calendarTos);
        for (Map.Entry<Date, Collection<Map<String, Object>>> entry : calendarMap.entrySet()) {
            Date date = entry.getKey();
            long delay = date.getTime() - System.currentTimeMillis();
            if (delay < 0) { // Check if the time is in the past
                continue;
            }
            Collection<Map<String, Object>> eventDatas = entry.getValue();
            for (Map<String, Object> eventData : eventDatas) {
                eventData.put(IEventConst.EVENT_SOURCE_CLASS_KEY, IEventConst.EVENT_SOURCE_CLASS_VALUE_TRIGGERS_EVENT_CALENDAR);
                String eventType = (String) eventData.get(IEventConst.EVENT_TYPE_KEY);
                if (eventType == null) {
                    eventType = IEventConst.EVENT_TYPE_VALUE_DEFAULT;
                    eventData.put(IEventConst.EVENT_TYPE_KEY, eventType);
                }

                JobDetail jobDetail = JobBuilder.newJob(CronJob.class) //
                        .withIdentity(UUID.randomUUID().toString()) //
                        .usingJobData(jobDataMap) //
                        .build();
                jobDetail.getJobDataMap().put("eventData", eventData);
                Trigger trigger = TriggerBuilder.newTrigger() //
                        .withIdentity(UUID.randomUUID().toString()) //
                        .startAt(date) //
                        .build();
                scheduler.scheduleJob(jobDetail, trigger);
            }
        }
    }

    /**
     * <p>
     * scheduleSchedulers.</p>
     *
     * @param scheduler a {@link org.quartz.Scheduler} object
     * @param rootTo a {@link org.ou.to.RootDocTo} object
     * @param triggersQueue a {@link java.util.concurrent.BlockingQueue} object
     * @param loggerQueue a {@link java.util.concurrent.BlockingQueue} object
     * @throws org.quartz.SchedulerException if any.
     */
    public static void scheduleSchedulers(Scheduler scheduler, RootDocTo rootTo, BlockingQueue<TriggerQueueEntry> triggersQueue, BlockingQueue<Map<String, Object>> loggerQueue) throws SchedulerException {
        JobDataMap jobDataMap = createBasicJobDataMap(rootTo, triggersQueue, loggerQueue);

        Map<String /* cronExpression */, Collection<Map<String, Object>>> schedulerMap = createSchedulerMap(rootTo.schedulerTos);
        for (Map.Entry<String /* cronExpression */, Collection<Map<String, Object>>> entry : schedulerMap.entrySet()) {
            String cronExpression = entry.getKey();
            Collection<Map<String, Object>> eventDatas = entry.getValue();
            for (Map<String, Object> eventData : eventDatas) {
                eventData.put(IEventConst.EVENT_SOURCE_CLASS_KEY, IEventConst.EVENT_SOURCE_CLASS_VALUE_TRIGGERS_EVENT_SCHEDULER);
                String eventType = (String) eventData.get(IEventConst.EVENT_TYPE_KEY);
                if (eventType == null) {
                    eventType = IEventConst.EVENT_TYPE_VALUE_DEFAULT;
                    eventData.put(IEventConst.EVENT_TYPE_KEY, eventType);
                }

                JobDetail jobDetail = JobBuilder.newJob(CronJob.class) //
                        .withIdentity(UUID.randomUUID().toString()) //
                        .usingJobData(jobDataMap) //
                        .build();

                jobDetail.getJobDataMap().put("eventData", eventData);
                Trigger trigger = TriggerBuilder.newTrigger() //
                        .withIdentity(UUID.randomUUID().toString()) //
                        .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                        .build();
                scheduler.scheduleJob(jobDetail, trigger);
            }
        }
    }

    private static JobDataMap createBasicJobDataMap(RootDocTo rootTo, BlockingQueue<TriggerQueueEntry> triggersQueue, BlockingQueue<Map<String, Object>> loggerQueue) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("configTo", rootTo);
        jobDataMap.put("triggersQueue", triggersQueue);
        jobDataMap.put("loggerQueue", loggerQueue);
        return jobDataMap;
    }

    private static Map<Date, Collection<Map<String, Object>>> createCalendarMap(Collection<AbstractTo> calendarTos) {
        Map<Date, Collection<Map<String, Object>>> resultMap = new TreeMap<Date, Collection<Map<String, Object>>>();
        for (AbstractTo abstractTo : calendarTos) {
            CalendarDocTo calendarTo = (CalendarDocTo) abstractTo;
            Collection<Map<String, Object>> dataMaps = calendarTo.calendarList;
            for (Map<String, Object> dataMap : dataMaps) {
                dataMap.put("calendarTo", calendarTo);
                String scheduledFor = (String) dataMap.get("scheduledFor");
                ZonedDateTime zdtScheduledFor = ZonedDateTime.parse(scheduledFor);
                Date dateScheduledFor = Date.from(zdtScheduledFor.toInstant());

                resultMap.putIfAbsent(dateScheduledFor, new ArrayList<>());
                resultMap.get(dateScheduledFor).add(dataMap);
            }
        }
        return resultMap;
    }

    private static Map<String /* cronExpression */, Collection<Map<String, Object>>> createSchedulerMap(Collection<AbstractTo> schedulerTos) {
        Map<String /* cronExpression */, Collection<Map<String, Object>>> resultMap = new TreeMap<String /* cronExpression */, Collection<Map<String, Object>>>();
        for (AbstractTo abstractTo : schedulerTos) {
            SchedulerDocTo schedulerTo = (SchedulerDocTo) abstractTo;
            Collection<Map<String, Object>> dataMaps = schedulerTo.schedulerList;
            for (Map<String, Object> dataMap : dataMaps) {

                dataMap.put("schedulerTo", schedulerTo);
                String cronExpression = (String) dataMap.get("cron_expression");

                resultMap.putIfAbsent(cronExpression, new ArrayList<>());
                resultMap.get(cronExpression).add(dataMap);
            }
        }
        return resultMap;
    }
}
