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
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.ou.common.constants.IEventConst;
import org.ou.common.utils.CommonUtils;
import org.ou.to.AbstractTo;
import org.ou.to.RootDocTo;

/**
 * <p>
 * TriggerThread class.</p>
 *

 * @since 1.0.21
 */
public class TriggerThread extends Thread {

    private final RootDocTo rootTo;
    private final AbstractTo abstractTo;
    private final BlockingQueue<TriggerQueueEntry> triggersQueue;
    private final BlockingQueue<Map<String, Object>> loggerQueue;

    private final Process process;
    private final InputStream stdoutInputStream;

    /**
     * <p>
     * Constructor for TriggerThread.</p>
     *
     * @param rootTo a {@link org.ou.to.RootDocTo} object
     * @param abstractTo a {@link org.ou.to.CommandTo} object
     * @param triggersQueue a {@link java.util.concurrent.BlockingQueue} object
     * @param loggerQueue a {@link java.util.concurrent.BlockingQueue} object
     * @param processObj
     * @throws java.lang.Exception if any.
     */
    public TriggerThread(RootDocTo rootTo, AbstractTo abstractTo, BlockingQueue<TriggerQueueEntry> triggersQueue, BlockingQueue<Map<String, Object>> loggerQueue, ProcessObj processObj) throws Exception {
        this.rootTo = rootTo;
        this.abstractTo = abstractTo;
        this.triggersQueue = triggersQueue;
        this.loggerQueue = loggerQueue;

        process = processObj.process;
        stdoutInputStream = processObj.stdoutInputStream;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        try {
            while (process.isAlive() && !MainProcess.pauseEventReceived) {
                // Read out message from stdout
                String json = null;
                synchronized (stdoutInputStream) {
                    try {
                        json = CommonUtils.readJsonFromInputStream(stdoutInputStream);
                    } catch (IOException e) {
                        e.printStackTrace();
                        // ignore "Stream Closed"
                        // e.printStackTrace();
                        break;
                    }
                }
                List<Map<String, Object>> listOfMaps = CommonUtils.parseJsonToListOfMaps(json);
                if (listOfMaps == null) { // Invalid
                    continue;
                }
                for (Map<String, Object> map : listOfMaps) {
                    map.put(IEventConst.EVENT_SOURCE_CLASS_KEY, IEventConst.EVENT_SOURCE_CLASS_VALUE_TRIGGERS_EVENT_PROVIDER);
                }
                TriggerUtils.processEvent(listOfMaps, rootTo, abstractTo, triggersQueue, loggerQueue);
            }
        } catch (InterruptedException e) {
            // ignore
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
