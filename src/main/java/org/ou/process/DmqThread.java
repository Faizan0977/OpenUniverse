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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.ou.common.utils.CommonUtils;
import org.ou.common.utils.FileUtils;
import org.ou.to.CommandTo;

/**
 * <p>
 * ProcessorThread class.</p>
 *
 *
 * @since 1.0.21
 */
public class DmqThread extends Thread {

    private final CommandTo dmqCommandTo;

    private volatile List<Process> processes;
    private volatile List<OutputStream> stdinOutputStreams;
    private volatile List<InputStream> stdoutInputStreams;
    private volatile List<Boolean> busys;

    /**
     *
     * @param exportSettingsOsProgram
     * @throws Exception
     */
    public DmqThread(CommandTo dmqCommandTo) throws Exception {
        this.dmqCommandTo = dmqCommandTo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        int instancesCount = dmqCommandTo.instancesCount > 0
                ? dmqCommandTo.instancesCount
                : 0;

        processes = new ArrayList<>();
        stdinOutputStreams = new ArrayList<>();
        stdoutInputStreams = new ArrayList<>();

        busys = new ArrayList<>();

        try {
            for (int i = 0; i < instancesCount; i++) {
                ProcessBuilder processBuilder = new ProcessBuilder();

                String cmd = dmqCommandTo.cmd;
                cmd = FileUtils.convertToRealPath(MainProcess.tmpRepoDir, MainProcess.rootTo.headTo.docFile, cmd).toString();

                List<String> args = dmqCommandTo.args != null ? dmqCommandTo.args : Collections.emptyList();
                String workingDir = dmqCommandTo.workingDir;
                Map<String, String> environment = dmqCommandTo.envVars != null ? dmqCommandTo.envVars : Collections.emptyMap();

                String errorLogFile = dmqCommandTo.errorLogFile;
                if (errorLogFile != null) {
                    Path errorLogFilePath = FileUtils.convertToRealPath(MainProcess.repoPath, MainProcess.rootTo.headTo.docFile, errorLogFile);
                    if (!Files.exists(errorLogFilePath)) {
                        Files.createDirectories(errorLogFilePath.getParent());
                    }
                    processBuilder.redirectError(errorLogFilePath.toFile());
                } else {
                    processBuilder.redirectErrorStream(false);
                }

                String[] command = ProcessUtils.createCommandArray(cmd, args);
                processBuilder.command(command);
                if (workingDir != null) {
                    processBuilder.directory(FileUtils.convertToRealPath(MainProcess.repoPath, MainProcess.rootTo.headTo.docFile, workingDir).toFile());
                }

                processBuilder.environment().putAll(environment);

                Process process = processBuilder.start();
                processes.add(process);
                stdinOutputStreams.add(process.getOutputStream());
                stdoutInputStreams.add(process.getInputStream());
                busys.add(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param jsonObj
     * @param jsonBs
     */
    public void sendString(Map<String, Object> jsonMap) throws Throwable {
        while (true) {
            for (int i = 0; i < stdinOutputStreams.size(); i++) {
                boolean busy = busys.get(i);
                if (busy) {
                    continue;
                }
                OutputStream stdinOutputStream = stdinOutputStreams.get(i);
                InputStream stdoutInputStream = stdoutInputStreams.get(i);
                BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(stdoutInputStream));

                synchronized (stdinOutputStream) {
                    busys.set(i, true);
                    byte[] jsonBs = CommonUtils.om.writeValueAsBytes(jsonMap);

                    stdinOutputStream.write(jsonBs);
                    stdinOutputStream.write((byte) '\n');
                    stdinOutputStream.flush();
                    String line = stdoutReader.readLine();
                    busys.set(i, false);
                    if (line == null) {
                        throw new Exception("Message was not delivered to DMQ: stream closed");
                    }
                    if (!"0".equals(line.strip())) {
                        throw new Exception("Message was not delivered to DMQ: " + line);
                    }
                    return;
                }
            }
        }
    }

    public void close() {
        try {
            if (processes != null) {
                for (Process proc : processes) {
                    proc.destroy();
                }
            }
        } catch (Throwable t) {
            MainProcess.appendToErrorLog(t, false);
        }
    }
}
