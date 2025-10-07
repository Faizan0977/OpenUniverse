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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.ou.common.constants.IConstants;
import org.ou.common.constants.IEventConst;
import org.ou.common.constants.IRecordConst;
import org.ou.common.utils.CommonUtils;
import org.ou.to.ProcessorDocTo;
import org.ou.to.RootDocTo;

/**
 * <p>
 * ProcessorThread class.</p>
 *

 * @since 1.0.21
 */
public class ProcessorThread extends Thread {

    private final RootDocTo rootTo;
    private final ProcessorDocTo processorTo;
    private final BlockingQueue<TriggerQueueEntry> triggersQueue;
    private final BlockingQueue<Map<String, Object>> loggerQueue;

    private final ProcessObj processObj;
    private final Process process;
    private final OutputStream stdinOutputStream;
    private final InputStream stdoutInputStream;

    private final Lock lock = new ReentrantLock();

    /**
     * <p>
     * Constructor for ProcessorThread.</p>
     *
     * @param rootTo
     * @param processorTo a {@link org.ou.to.CommandTo} object
     * @param triggersQueue a {@link java.util.concurrent.BlockingQueue} object
     * @param loggerQueue a {@link java.util.concurrent.BlockingQueue} object
     * @param processObj
     * @throws java.lang.Exception if any.
     */
    public ProcessorThread(RootDocTo rootTo, ProcessorDocTo processorTo, BlockingQueue<TriggerQueueEntry> triggersQueue, BlockingQueue<Map<String, Object>> loggerQueue, ProcessObj processObj) throws Exception {
        this.rootTo = rootTo;
        this.processorTo = processorTo;
        this.triggersQueue = triggersQueue;
        this.loggerQueue = loggerQueue;
        this.processObj = processObj;

        process = processObj.process;
        stdinOutputStream = processObj.stdinOutputStream;
        stdoutInputStream = processObj.stdoutInputStream;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        try {
            while (process.isAlive() && !MainProcess.pauseEventReceived) {
                if (processObj.busy)
                    continue;
                processObj.busy = true;
                // Read in message from stdin queue
                TriggerQueueEntry tqe = triggersQueue.take();
                if (!processorTo.headTo.id.equals(tqe.forId)) {
                    lock.lock();         // Lock to prevent removal
                    try {
                        triggersQueue.put(tqe);     // Reinsert the element back
                    } finally {
                        lock.unlock();
                    }
                    processObj.busy = false;
                    continue; // Not intended for tihs command, skipped
                }
                processObj.busy = true;
                byte[] stdinBytes = CommonUtils.om.writeValueAsBytes(tqe.eventDataJson);

                String startDateTime = null;
                String finishDateTime = null;

                // Write message to stdin
               // synchronized (stdinOutputStream) {
                    try {
                        stdinOutputStream.write(stdinBytes);
                        stdinOutputStream.flush();
                        startDateTime = ZonedDateTime.now(ZoneOffset.UTC).toString();
                    } catch (IOException e) {
                        // ignore "Stream Closed"
                        // e.printStackTrace();
                        process.destroyForcibly();
                        break;
                    }
                //}

                // Read out message from command stdout
                String json = null;
                //synchronized (stdoutInputStream) {
                    try {
                        json = CommonUtils.readJsonFromInputStream(stdoutInputStream);
                        finishDateTime = ZonedDateTime.now(ZoneOffset.UTC).toString();
                    } catch (IOException e) {
                        // ignore "Stream Closed"
                        // e.printStackTrace();
                        process.destroyForcibly();
                        break;
                    }
                //}

                List<Map<String, Object>> listOfMaps = CommonUtils.parseJsonToListOfMaps(json);

                processObj.busy = false;
                
                if (listOfMaps == null) { // Invalid
                    continue;
                }
                if (listOfMaps.isEmpty()) { // Valid, but nothing to process
                    continue;
                }

                List<Map<String, Object>> listOfMapsTriggers = new ArrayList<>();
                List<Map<String, Object>> listOfMapsProcessors = new ArrayList<>();

                for (Map<String, Object> map : listOfMaps) {
                    String eventType = (String) map.get(IEventConst.EVENT_TYPE_KEY);
                    if (eventType == null) {
                        listOfMapsProcessors.add(map);
                    } else {
                        listOfMapsTriggers.add(map);
                        if (IEventConst.EVENT_TYPE_VALUE_CONTROL_PAUSE.equals(eventType)) {
                            MainProcess.pauseEventReceived = true;
                            MainProcess.resumeEventReceived = false;
                            break;
                        } else if (IEventConst.EVENT_TYPE_VALUE_CONTROL_RESUME.equals(eventType)) {
                            MainProcess.resumeEventReceived = true;
                            MainProcess.pauseEventReceived = false;
                        }
                    }
                }

                for (Map<String, Object> map : listOfMapsTriggers) {
                    map.put(IEventConst.EVENT_SOURCE_CLASS_KEY, IEventConst.EVENT_SOURCE_CLASS_VALUE_PROCESSOR);
                }
                TriggerUtils.processEvent(listOfMapsTriggers, rootTo, processorTo, triggersQueue, loggerQueue);

                // Add out message to notifires queue
                for (Map<String, Object> dataMap : listOfMapsProcessors) {
                    Map<String, Object> map = new LinkedHashMap<>();
                    Map<String, Object> recordMap = new LinkedHashMap<>();
                    recordMap.put(IRecordConst.RECORD_TYPE_KEY, IRecordConst.RECORD_TYPE_VALUE_PROCESSOR);
                    map.put(IConstants.RECORD_KEY, recordMap);
                    map.put(IConstants.EVENT_KEY, tqe.eventDataJson);
                    map.put(IConstants.PROCESSOR_DATA_KEY, dataMap);

                    Map<String, Object> map0 = new LinkedHashMap<>();
                    map0.put("id", tqe.configHead.id);
                    map0.put("file", tqe.configHead.docFile);
                    map0.put("file_sha1", tqe.configHead.docSha1);
                    map0.put("git_blob_sha1", tqe.configHead.docBlobSha1);
                    map0.put("name", tqe.configHead.name);
                    map0.put("description", tqe.configHead.description);
                    map0.put("tags", tqe.configHead.tags);
                    map0.put("attr", tqe.configHead.attr);
                    map.put("config", map0);

                    map0 = new LinkedHashMap<>();
                    map0.put("id", tqe.jobHead.id);
                    map0.put("file", tqe.jobHead.docFile);
                    map0.put("file_sha1", tqe.jobHead.docSha1);
                    map0.put("git_blob_sha1", tqe.jobHead.docBlobSha1);
                    map0.put("name", tqe.jobHead.name);
                    map0.put("description", tqe.jobHead.description);
                    map0.put("tags", tqe.jobHead.tags);
                    map0.put("attr", tqe.jobHead.attr);
                    map.put("job", map0);

                    map0 = new LinkedHashMap<>();
                    map0.put("id", tqe.systemHead.id);
                    map0.put("file", tqe.systemHead.docFile);
                    map0.put("file_sha1", tqe.systemHead.docSha1);
                    map0.put("git_blob_sha1", tqe.systemHead.docBlobSha1);
                    map0.put("name", tqe.systemHead.name);
                    map0.put("description", tqe.systemHead.description);
                    map0.put("tags", tqe.systemHead.tags);
                    map0.put("attr", tqe.systemHead.attr);
                    map.put("system", map0);

                    if (tqe.triggerHead != null) {
                        map0 = new LinkedHashMap<>();
                        map0.put("id", tqe.triggerHead.id);
                        map0.put("file", tqe.triggerHead.docFile);
                        map0.put("file_sha1", tqe.triggerHead.docSha1);
                        map0.put("git_blob_sha1", tqe.triggerHead.docBlobSha1);
                        map0.put("name", tqe.triggerHead.name);
                        map0.put("description", tqe.triggerHead.description);
                        map0.put("tags", tqe.triggerHead.tags);
                        map0.put("attr", tqe.triggerHead.attr);
                        map.put("trigger", map0);
                    }

                    map0 = new LinkedHashMap<>();
                    map0.put("id", processorTo.headTo.id);
                    map0.put("file", processorTo.headTo.docFile);
                    map0.put("file_sha1", processorTo.headTo.docSha1);
                    map0.put("git_blob_sha1", processorTo.headTo.docBlobSha1);
                    map0.put("name", processorTo.headTo.name);
                    map0.put("description", processorTo.headTo.description);
                    map0.put("tags", processorTo.headTo.tags);
                    map0.put("attr", processorTo.headTo.attr);
                    map0.put("pid", process.pid());
                    map0.put("start_date_time", startDateTime);
                    map0.put("finish_date_time", finishDateTime);
                    map.put("processor", map0);

                    loggerQueue.put(map);
                }
            }
        } catch (InterruptedException e) {
            // ignore
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
