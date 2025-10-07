package org.ou.process;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.ou.common.utils.FileUtils;
import org.ou.to.CommandTo;

public class CommandExecutorUtils {

    /**
     * Executes the command based on the given {@link CommandTo} configuration.
     * Always returns a list of Process objects, even for a single instance.
     *
     * @param commandTo the command configuration
     * @return list of {@link Process} objects (never empty)
     * @throws IOException if an I/O error occurs when starting any process
     */
    public static List<ProcessObj> execute(CommandTo commandTo) throws IOException {
        List<ProcessObj> processes = new ArrayList<>();

        int count = Math.max(1, commandTo.instancesCount);
        for (int i = 0; i < count; i++) {
            processes.add(startSingleProcess(commandTo));
        }

        return processes;
    }

    private static ProcessObj startSingleProcess(CommandTo commandTo) throws IOException {
        // Build the command
        List<String> fullCommand = new ArrayList<>();
        fullCommand.add(commandTo.cmd);
        if (commandTo.args != null) {
            fullCommand.addAll(commandTo.args);
        }

        ProcessBuilder processBuilder = new ProcessBuilder(fullCommand);

        // Set working directory
        if (commandTo.workingDir != null) {
            Path workingDirPath = FileUtils.convertToRealPath(MainProcess.repoPath, MainProcess.rootTo.headTo.docFile, commandTo.workingDir);
            processBuilder.directory(workingDirPath.toFile());
        }

        // Set environment variables
        if (commandTo.envVars != null) {
            processBuilder.environment().putAll(commandTo.envVars);
        }

        // Handle error log file
        if (commandTo.errorLogFile == null) {
            processBuilder.redirectErrorStream(false); // stderr remains separate
        } else {
            Path errorLogFilePath = FileUtils.convertToRealPath(MainProcess.repoPath, MainProcess.rootTo.headTo.docFile, commandTo.errorLogFile);
            if (!Files.exists(errorLogFilePath)) {
                Files.createDirectories(errorLogFilePath.getParent());
            }
            processBuilder.redirectError(errorLogFilePath.toFile());
        }

        ProcessObj processObj = new ProcessObj();
        processObj.process = processBuilder.start();
        processObj.stdinOutputStream = processObj.process.getOutputStream();
        processObj.stdoutInputStream = processObj.process.getInputStream();
        return processObj;
    }
}
