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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ou.common.utils.EncryptedPlaceholderUtils;
import org.ou.common.utils.FileUtils;
import org.ou.common.utils.TemplateUtils;
import org.ou.to.AbstractTo;
import org.ou.to.CommandTo;

/**
 * <p>
 * ProcessUtils class.</p>
 *

 * @since 1.0.21
 */
public class ProcessUtils {

    /**
     * <p>
     * getProcessObj.</p>
     *
     * @param commandTo a {@link org.ou.to.CommandTo} object
     * @param getStdinOutputStream a boolean
     * @param getStdoutInputStream a boolean
     * @param processMap a {@link java.util.Map} object
     * @return a {@link org.ou.process.ProcessObj} object
     * @throws java.lang.Exception if any.
     */
    public static List<ProcessObj> getProcessObjs(AbstractTo abstractTo, CommandTo commandTo, boolean getStdinOutputStream, boolean getStdoutInputStream, Map<String /* id */, List<ProcessObj>> processMap, EncryptedPlaceholderUtils.IDecryptor decryptor, char[] password) throws Exception {
        String id = abstractTo.headTo.id;
        String docFile = abstractTo.headTo.docFile;

        int instancesCount = commandTo.instancesCount;

        List<ProcessObj> processObjs = processMap.get(id);
        if (processObjs != null) {
            return processObjs;
        }

        processObjs = new ArrayList<>(instancesCount);

        ProcessBuilder processBuilder = new ProcessBuilder();

        Map<String, Object> properties = abstractTo.properties;
        
        String cmd = commandTo.cmd;
        cmd = (String) TemplateUtils.transform(cmd, properties, decryptor, password);
        cmd = FileUtils.convertToRealPath(MainProcess.tmpRepoDir, docFile, cmd).toString();

        List<String> args = commandTo.args;
        String workingDir = commandTo.workingDir;
        Map<String, String> environment = commandTo.envVars;

        String errorLogFile = commandTo.errorLogFile;

        if (errorLogFile == null) {
            processBuilder.redirectErrorStream(false);
        } else {            
            errorLogFile = (String) TemplateUtils.transform(errorLogFile, properties, decryptor, password);
            Path errorLogFilePath = FileUtils.convertToRealPath(MainProcess.repoPath, docFile, errorLogFile);
            if (!Files.exists(errorLogFilePath)) {
                Files.createDirectories(errorLogFilePath.getParent());
            }
            processBuilder.redirectError(errorLogFilePath.toFile());
        }

        String[] command = createCommandArray(cmd, args);
        command = (String[]) TemplateUtils.transform(command, properties, decryptor, password);
        processBuilder.command(command);
        if (workingDir != null) {
            workingDir = (String) TemplateUtils.transform(workingDir, properties, decryptor, password);
            processBuilder.directory(FileUtils.convertToRealPath(MainProcess.repoPath, docFile, workingDir).toFile());
        }

        environment = (Map<String, String>) TemplateUtils.transform(environment, properties, decryptor, password);
        processBuilder.environment().putAll(environment);

        for (int i = 0; i < instancesCount; i++) {
            ProcessObj processObj = new ProcessObj();
            processObj.process = processBuilder.start();
            if (getStdinOutputStream) {
                processObj.stdinOutputStream = processObj.process.getOutputStream();
            }
            if (getStdoutInputStream) {
                processObj.stdoutInputStream = processObj.process.getInputStream();
            }
            processObjs.add(processObj);
        }

        processMap.put(id, processObjs);
        return processObjs;
    }

    /**
     * Creates a command array by combining a command string with a list of arguments.
     *
     * @param cmd The command string to be used as the first element of the array.
     * @param args The list of arguments to be included in the array.
     * @return A string array where the first element is the command and the rest are the arguments.
     */
    public static String[] createCommandArray(String cmd, List<String> args) {
        String[] commandArray = new String[1 + args.size()];
        commandArray[0] = cmd;
        for (int i = 0; i < args.size(); i++) {
            commandArray[i + 1] = args.get(i);
        }
        return commandArray;
    }
}
