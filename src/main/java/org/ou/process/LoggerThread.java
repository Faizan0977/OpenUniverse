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
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

import org.eclipse.jgit.api.Git;
import org.ou.common.constants.IConstants;

/**
 * <p>
 * LoggerThread class.</p>
 *
 *
 * @since 1.0.21
 */
public class LoggerThread extends Thread {

    private final boolean outputToConsole;
    private final Git git;
    private final Path logPath;
    private final BlockingQueue<Map<String, Object>> loggerQueue;
    private final Map<String, Object> hostInfoMap;
    private final Map<String, Object> repoInfoMap;
    private final Map<String, Object> gitRevCommitMap;
    private final String actionUuid = UUID.randomUUID().toString();
    private final String hashAlgorithm;

    /**
     *
     * @param outputToConsole
     * @param exportThreads
     * @param git
     * @param logPath
     * @param loggerQueue
     * @param nodeInfoMap
     * @param repoInfoMap
     * @param gitRevCommitMap
     * @param privateKey
     * @param signatureAlgorithm
     * @throws IOException
     */
    public LoggerThread(boolean outputToConsole, Git git, Path logPath, BlockingQueue<Map<String, Object>> loggerQueue, Map<String, Object> nodeInfoMap, Map<String, Object> repoInfoMap, Map<String, Object> gitRevCommitMap, String hashAlgorithm) throws IOException {
        this.outputToConsole = outputToConsole;
        this.git = git;
        this.logPath = logPath;
        this.loggerQueue = loggerQueue;
        this.hostInfoMap = nodeInfoMap;
        this.repoInfoMap = repoInfoMap;
        this.gitRevCommitMap = gitRevCommitMap;
        this.hashAlgorithm = hashAlgorithm;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        try {
            long serialNo = 0;
            String prev_record_chain_hash = "";
            while (true) {
                Map<String, Object> map = loggerQueue.take();
                hostInfoMap.put(IConstants.ACTIVITY_ID_KEY, actionUuid);
                try {
                    prev_record_chain_hash = LoggerUtils.printLoggerMap(prev_record_chain_hash, outputToConsole, MainProcess.exportConsoles, MainProcess.dlqThreads, git, logPath, actionUuid, map, ++serialNo, hostInfoMap, repoInfoMap, gitRevCommitMap, hashAlgorithm);
                } catch (Throwable t) {
                    t.printStackTrace();
                    //MainProcess.appendToErrorLog(t, false);
                }
                if (!MainProcess.keepRunning && loggerQueue.isEmpty()) {
                    break;
                }
            }
        } catch (InterruptedException e) {
            // ignore
        }
    }
}
