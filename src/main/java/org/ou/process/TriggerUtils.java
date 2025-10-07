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
package org.ou.process;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.ou.common.constants.IConstants;
import org.ou.common.constants.IEventConst;
import org.ou.common.constants.IRecordConst;
import org.ou.common.utils.CommonUtils;
import org.ou.to.AbstractTo;
import org.ou.to.EventDocTo;
import org.ou.to.JobDocTo;
import org.ou.to.ProcessorDocTo;
import org.ou.to.RootDocTo;
import org.ou.to.SystemDocTo;

/**
 *
 * @author k2Xzny
 */
public class TriggerUtils extends Thread {

    /**
     *
     * @param mapOrListOfMaps
     * @param rootTo
     * @param abstractTo
     * @param triggersQueue
     * @param loggerQueue
     * @throws Exception
     */
    public static void processEvent(Object mapOrListOfMaps, RootDocTo rootTo, AbstractTo abstractTo, BlockingQueue<TriggerQueueEntry> triggersQueue, BlockingQueue<Map<String, Object>> loggerQueue) throws Exception {
        List<Map<String, Object>> listOfMaps = CommonUtils.toList(mapOrListOfMaps);
        ZonedDateTime eventDateTimeZ = ZonedDateTime.now(ZoneOffset.UTC);
        String eventDateTime = eventDateTimeZ.toString();
        if (listOfMaps.isEmpty()) { // Valid, but nothing to processable
            return;
        }

        String recordType = IRecordConst.RECORD_TYPE_VALUE_TRIGGER;

        for (Map<String, Object> mapEvent : listOfMaps) {
            mapEvent = new LinkedHashMap<>(mapEvent);
            String eventType = (String) mapEvent.get(IEventConst.EVENT_TYPE_KEY);
            if (eventType == null) {
                eventType = IEventConst.EVENT_TYPE_VALUE_DEFAULT;
                mapEvent.put(IEventConst.EVENT_TYPE_KEY, eventType);
            }
            if (eventType.equals(IEventConst.EVENT_TYPE_VALUE_HEALTH_OU)) {
                mapEvent.put(IEventConst.EVENT_SOURCE_CLASS_KEY, IEventConst.EVENT_SOURCE_CLASS_VALUE_HEALTH);
            } else if (eventType.equals(IEventConst.EVENT_TYPE_VALUE_HEALTH_JVM)) {
                mapEvent.put(IEventConst.EVENT_SOURCE_CLASS_KEY, IEventConst.EVENT_SOURCE_CLASS_VALUE_HEALTH);
            }

            String eventClass = (String) mapEvent.get(IEventConst.EVENT_SOURCE_CLASS_KEY);
            if ( //
                    !eventClass.equals(IEventConst.EVENT_SOURCE_CLASS_VALUE_INTERNAL_ERROR)
                    && //
                    !eventClass.equals(IEventConst.EVENT_SOURCE_CLASS_VALUE_CONTROL)
                    && //
                    !eventClass.equals(IEventConst.EVENT_SOURCE_CLASS_VALUE_HEALTH)
                    && //
                    !eventClass.equals(IEventConst.EVENT_SOURCE_CLASS_VALUE_REPO) //
                    ) {
                EventDocTo eventTo = (EventDocTo) MainProcess.allEventsMap.get(eventType);
                if (eventTo == null) {
                    continue;
                }
            }
            Map<String, Object> map = new LinkedHashMap<>();
            mapEvent.put(IEventConst.EVENT_DATE_TIME_KEY, eventDateTime);
            map.put(IConstants.EVENT_KEY, mapEvent);

            TriggerQueueEntry tqe = new TriggerQueueEntry();
            tqe.configHead = rootTo.headTo;
            if (abstractTo != null) {
                tqe.triggerHead = abstractTo.headTo;
            }
            tqe.eventDateTime = eventDateTime;
            tqe.eventDataJson = mapEvent;

            Map<String, Object> map0;

            map0 = new LinkedHashMap<>();
            map0.put(IRecordConst.RECORD_TYPE_KEY, recordType);
            map.put(IConstants.RECORD_KEY, map0);

            map0 = new LinkedHashMap<>();
            if (abstractTo != null) {
                map0.put("id", tqe.triggerHead.id);
                map0.put("file", tqe.triggerHead.docFile);
                map0.put("indexInFile", tqe.triggerHead.indexInFile);
                map0.put("file_sha1", tqe.triggerHead.docSha1);
                map0.put("git_blob_sha1", tqe.triggerHead.docBlobSha1);
                map0.put("name", tqe.triggerHead.name);
                map0.put("description", tqe.triggerHead.description);
                map0.put("tags", tqe.triggerHead.tags);
                map0.put("attr", tqe.triggerHead.attr);
                map0.put(IEventConst.EVENT_DATE_TIME_KEY, tqe.eventDateTime);
                map.put("trigger", map0);
            }

            map0 = new LinkedHashMap<>();
            map0.put("id", tqe.configHead.id);
            map0.put("file", tqe.configHead.docFile);
            map0.put("indexInFile", tqe.configHead.indexInFile);
            map0.put("file_sha1", tqe.configHead.docSha1);
            map0.put("git_blob_sha1", tqe.configHead.docBlobSha1);
            map0.put("name", tqe.configHead.name);
            map0.put("description", tqe.configHead.description);
            map0.put("tags", tqe.configHead.tags);
            map0.put("attr", tqe.configHead.attr);
            map.put("config", map0);

            loggerQueue.put(map);

            if (IEventConst.EVENT_TYPE_VALUE_CONTROL_PAUSE.equals(eventType)) {
                MainProcess.pauseEventReceived = true;
                MainProcess.resumeEventReceived = false;
                return;
            } else if (IEventConst.EVENT_TYPE_VALUE_CONTROL_RESUME.equals(eventType)) {
                MainProcess.resumeEventReceived = true;
                MainProcess.pauseEventReceived = false;
            }
        }

        for (JobDocTo jobTo : rootTo.jobTos) {
            for (ProcessorDocTo processorTo : jobTo.processorTos) {
                for (SystemDocTo systemTo : jobTo.systemTos) {
                    for (Map<String, Object> mapEvent : listOfMaps) {
                        mapEvent = new LinkedHashMap<>(mapEvent);
                        String eventType = (String) mapEvent.get(IEventConst.EVENT_TYPE_KEY);

                        if (eventType == null) {
                            eventType = IEventConst.EVENT_TYPE_VALUE_DEFAULT;
                            mapEvent.put(IEventConst.EVENT_TYPE_KEY, eventType);
                        }

                        EventDocTo eventTo = (EventDocTo) MainProcess.allEventsMap.get(eventType);
                        if (eventTo == null) {
                            continue;
                        } else {
                            if (!eventTo.processable) {
                                continue;
                            }
                        }

                        mapEvent.put(IConstants.SYSTEM_KEY, systemTo.systemDefJson);
                        mapEvent.put(IEventConst.EVENT_DATE_TIME_KEY, eventDateTime);

                        TriggerQueueEntry tqe = new TriggerQueueEntry();
                        tqe.configHead = rootTo.headTo;
                        if (abstractTo != null) {
                            tqe.triggerHead = abstractTo.headTo;
                        }
                        tqe.systemHead = systemTo.headTo;
                        tqe.jobHead = jobTo.headTo;
                        tqe.eventDateTime = eventDateTime;
                        tqe.forId = processorTo.headTo.id;
                        tqe.eventDataJson = mapEvent;
                        triggersQueue.put(tqe);
                    }
                }
            }
        }
    }
}
