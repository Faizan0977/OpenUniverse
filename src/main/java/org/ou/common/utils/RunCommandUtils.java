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
package org.ou.common.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ou.process.MainProcess;
import org.ou.to.AbstractTo;
import org.ou.to.CommandTo;

public class RunCommandUtils {

    public static int runCommand(AbstractTo abstractTo, CommandTo commandTo, byte[] bs, EncryptedPlaceholderUtils.IDecryptor decryptor, char[] secret) throws IOException, InterruptedException {
        if (commandTo.args == null) {
            commandTo.args = new ArrayList<>();
        }
        List<String> command = new ArrayList<>();
        command.add(commandTo.cmd);
        command.addAll(commandTo.args);

        Map<String, Object> properties = abstractTo.properties;
        for (int i = 0; i < command.size(); i++) {
            command.set(i, (String) TemplateUtils.transform(command.get(i), properties, decryptor, secret));
        }

        ProcessBuilder builder = new ProcessBuilder(command);

        if (commandTo.workingDir != null) {
            commandTo.workingDir = (String) TemplateUtils.transform(commandTo.workingDir, properties, decryptor, secret);
            builder.directory(FileUtils.convertToRealPath(MainProcess.repoPath, abstractTo.headTo.docFile, commandTo.workingDir).toFile());
        }

        if (commandTo.envVars != null) {
            for (Map.Entry<String, String> entry : commandTo.envVars.entrySet()) {
                String val = entry.getValue();
                val = (String) TemplateUtils.transform(val, properties, decryptor, secret);
                entry.setValue(val);
            }
            Map<String, String> environment = builder.environment();
            environment.putAll(commandTo.envVars);
        }

        if (commandTo.errorLogFile != null) {
            commandTo.errorLogFile = (String) TemplateUtils.transform(commandTo.errorLogFile, properties, decryptor, secret);
            builder.redirectError(FileUtils.convertToRealPath(MainProcess.repoPath, abstractTo.headTo.docFile, commandTo.errorLogFile).toFile());
        }

        builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        //builder.redirectInput(ProcessBuilder.Redirect.INHERIT);
        builder.redirectInput(ProcessBuilder.Redirect.PIPE);

        Process process = builder.start();
        // Write bytes to process's stdin if provided
        if (bs != null && bs.length > 0) {
            try (var out = process.getOutputStream()) {
                out.write(bs);
                out.flush();
            } catch (IOException e) {
                String message = e.getMessage();
                if (message == null || (!message.contains("Broken pipe") && !message.contains("Stream closed"))) {
                    throw e; // Real IO error
                }
                // Else: ignore broken pipe / stream closed — process just doesn’t read input
            }
        } else {
            process.getOutputStream().close();
        }
        return process.waitFor();
    }
}
