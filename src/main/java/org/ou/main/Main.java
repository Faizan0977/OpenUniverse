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
package org.ou.main;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ou.common.constants.ISystemProperties;
import org.ou.common.utils.FileUtils;
import org.ou.process.MainProcess;

/**
 * <p>
 * Main class.</p>
 *
 */
public class Main {

    /*
     * pom.xml
     *
     * <manifestEntries>
     *     <Build-Time>${maven.build.timestamp}</Build-Time>                           
     *     <Implementation-Title>OpenUnvierse</Implementation-Title>
     *     <Implementation-Version>${project.version}</Implementation-Version>
     *     <Implementation-Vendor>OpenUnvierse project</Implementation-Vendor>
     * </manifestEntries>	    
     */
    public static final String REPO_URL = "https://github.com/ou-org/OpenUniverse.git";
    /**
     * Constant <code>MANIFEST_IMPLEMENTATION_TITLE</code> See pom.xml
     */
    public static final String MANIFEST_IMPLEMENTATION_TITLE;
    /**
     * Constant <code>MANIFEST_IMPLEMENTATION_VERSION</code> See pom.xml
     */
    public static final String MANIFEST_IMPLEMENTATION_VERSION;
    /**
     * Constant <code>MANIFEST_IMPLEMENTATION_VENDOR</code> See pom.xml
     */
    public static final String MANIFEST_IMPLEMENTATION_VENDOR;
    /**
     * Constant <code>MANIFEST_BUILD_TIME</code> See pom.xml
     */
    public static final String MANIFEST_BUILD_TIME;

    public static final String GIT_BUILD_TIME;
    public static final String GIT_BUILD_VERSION;
    public static final String GIT_COMMIT_ID_ABBREV;
    public static final String GIT_COMMIT_ID_FULL;

    static {
        try (InputStream is = Main.class.getResourceAsStream("/META-INF/MANIFEST.MF")) {
            Manifest manifest = new Manifest(is);
            Attributes attributes = manifest.getMainAttributes();

            MANIFEST_IMPLEMENTATION_TITLE = attributes.getValue("Implementation-Title");
            MANIFEST_IMPLEMENTATION_VERSION = attributes.getValue("Implementation-Version");
            MANIFEST_IMPLEMENTATION_VENDOR = attributes.getValue("Implementation-Vendor");
            MANIFEST_BUILD_TIME = attributes.getValue("Build-Time");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (InputStream is = Main.class.getClassLoader().getResourceAsStream("git.properties")) {
            Properties properties = new Properties();
            properties.load(is);
            GIT_BUILD_TIME = properties.getProperty("git.build.time");
            GIT_BUILD_VERSION = properties.getProperty("git.build.version");
            GIT_COMMIT_ID_ABBREV = properties.getProperty("git.commit.id.abbrev");
            GIT_COMMIT_ID_FULL = properties.getProperty("git.commit.id.full");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
//    import org.apache.log4j.Level;
//    import org.apache.log4j.Logger;
//    static {
//      
//        Logger.getRootLogger().setLevel(Level.OFF);
//    }

    static {
        PrintStream filterOut = new PrintStream(System.err) {
            @Override
            public void println(String l) {
                if (!l.startsWith("SLF4J")) {
                    super.println(l);
                }
            }
        };
        System.setErr(filterOut);
    }

    /**
     * <p>
     * main.</p>
     *
     * @param args an array of {@link java.lang.String} objects
     * @throws java.lang.Exception if any.
     */
    public static void main(String[] args) throws Exception {
        Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(Level.OFF); // Suppress all logging

        // Optional: Disable console handler if it exists
        for (var handler : rootLogger.getHandlers()) {
            if (handler instanceof ConsoleHandler) {
                handler.setLevel(Level.OFF);
            }
        }
        System.err.println(createHeader());

        if (args.length < 2) {
            printHelp();
            System.exit(0);
            return;
        }
        boolean printSchema = false;
        boolean printTargets = false;
        boolean printStatus = false;
        boolean start = false;
        boolean stop = false;

        String repoDir = args[0];
        repoDir = FileUtils.convertToRealPath(repoDir).toString();
        String cmd = args[1];
        switch (cmd) {
            case "help", "h", "?", "--help", "-h" -> {
                printHelp();
                return;
            }
            case "start" ->
                start = true;
            case "stop" ->
                stop = true;
            case "schema" ->
                printSchema = true;
            case "targets" ->
                printTargets = true;
            case "status" ->
                printStatus = true;
            default -> {
                System.err.println("Unknown command! Only 'help', 'schema', 'targets', 'status', 'start' and 'stop' are supported.");
                System.exit(-1);
                return;
            }
        }
        boolean outputToConsole = false;
        String noColorEnvVar = System.getenv("NO_COLOR");
        boolean noVerify = false;
        boolean noColor = noColorEnvVar != null;
        boolean stdinSec = false;
        boolean gui = false;
        char[] secret = null;
        boolean assumeYes = false;
        if (args.length > 2) {
            for (int i = 2; i < args.length; i++) {
                switch (args[i]) {
                    case "--stdout", "-o" ->
                        outputToConsole = true;
                    case "--no-verify", "-nv" ->
                        noVerify = true;
                    case "--no-color", "-nc" ->
                        noColor = true;
                    case "--prompt-sec", "-p" ->
                        stdinSec = true;
                    case "--gui-sec", "-g" ->
                        gui = true;
                    case "--secret", "-s" ->
                        secret = args[++i].toCharArray();
                    case "--assume-yes", "-y" ->
                        assumeYes = true;
                    default -> {
                        System.err.println("Unknown flag or option!");
                        System.exit(-1);
                        return;
                    }
                }
            }
        }
        MainProcess.process(//
                args, //
                repoDir, //
                printSchema, //
                printTargets, //
                printStatus, //
                start, //
                stop, //
                secret, //
                stdinSec, //
                gui, //
                assumeYes, //
                noVerify, //
                noColor, //
                outputToConsole //
        );
    }

    /**
     *
     * @return
     */
    public static String createHeader() {
        return "\n" + MANIFEST_IMPLEMENTATION_TITLE + "\nVersion: " + MANIFEST_IMPLEMENTATION_VERSION + "\nJVM version: " + ISystemProperties.JAVA_VERSION + "\nBuild: " + MANIFEST_BUILD_TIME + "\nCommit: " + GIT_COMMIT_ID_FULL + "\nClone: " + REPO_URL + "\n";
    }

    private static void printHelp() {
        StringBuilder sb = new StringBuilder();
//      sb.append("01234567890123456789012345678901234567890123456789012345678901234567890123456789\n");
        sb.append("                                                                                \n");
        sb.append("Usage:                                                                          \n");
        sb.append("                                                                                \n");
        sb.append("ou [repo dir] <command> [options...] [flags...]                                 \n");
        sb.append("                                                                                \n");
        sb.append("Commands:                                                                       \n");
        sb.append("                                                                                \n");
        sb.append(" help              Print help message.                                          \n");
        sb.append(" schema            Print schema.                                                \n");
        sb.append(" targets           Print export targets.                                        \n");
        sb.append(" status            Print repository status.                                     \n");
        sb.append("                                                                                \n");
        sb.append(" start             Start process.                                               \n");
        sb.append(" stop              Stop process.                .                               \n");
        sb.append("                                                                                \n");
        sb.append("Options:                                                                        \n");
        sb.append("                                                                                \n");
        sb.append(" --secret,     -s  Secret for encrypted values.                                 \n");
        sb.append("                                                                                \n");
        sb.append("Flags:                                                                          \n");
        sb.append("                                                                                \n");
        sb.append(" --assume-yes, -y  Start without asking for confirmation.                       \n");
        sb.append(" --prompt-sec, -p  Prompt for the secret (see: --secret).                       \n");
        sb.append(" --gui-sec,    -g  Use GUI dialog to prompt for the secret (see: --prompt-sec). \n");
        sb.append(" --stdout,     -o  Print all records (as newline-delimited JSON) to console.    \n");
        sb.append(" --no-verify,  -nv Disable pre-run platform self-verification phase.            \n");
        sb.append(" --no-color,   -nc Disable ANSI colors (same effect as NO_COLOR env var).       \n");
        sb.append("                                                                                \n");
        sb.append("Optional environment variables:                                                 \n");
        sb.append("                                                                                \n");
        sb.append(" OU_JAVA_HOME      Path to the JRE directory (first priority).                  \n");
        sb.append(" JAVA_HOME         Path to the JRE directory (second priority).                 \n");
        sb.append(" NO_COLOR          Disable ANSI colors (same effect as --no-color, -n flags).   \n");
        sb.append("                                                                                \n");
        sb.append("Optional directories:                                                           \n");
        sb.append("                                                                                \n");
        sb.append(" jre               Local JRE directory (has common parent with 'ou' executable).\n");
        sb.append("                                                                                \n");
        sb.append("Auto-generated repository files:                                                \n");
        sb.append("                                                                                \n");
        sb.append(" node-id.txt       Contains node ID.                                            \n");
        sb.append(" commit.txt        Contains latest commit hash.                                 \n");
        sb.append(" schema.json       Contains discovered data structure for the repository.       \n");
        sb.append(" log.ndjson        System log in newline-delimited JSON format.                 \n");
        sb.append(" error-log.txt     Error log.                                                   \n");
        sb.append("                                                                                \n");
        sb.append("Optional repository files:                                                      \n");
        sb.append("                                                                                \n");
        sb.append(" *.json            JSON container (holds documents).                            \n");
        sb.append(" *.yaml, *.yml     YAML container (holds documents).                            \n");
        sb.append(" *.md              Markdown container (holds documents and resources).          \n");
        sb.append(" .ouignore         Ignore rules (uses the same syntax as .gitignore).           \n");
        sb.append("                                                                                \n");
        sb.append("Temporary repository files:                                                     \n");
        sb.append("                                                                                \n");
        sb.append(" write-lock.json   Write lock file.                                             \n");
        sb.append("                                                                                \n");
        sb.append("Exit Codes:                                                                     \n");
        sb.append("                                                                                \n");
        sb.append("   0               The command was successfully executed without errors.        \n");
        sb.append("   1               An error occurred during the execution of the command.       \n");
        sb.append(" 255               An unknown or invalid command, option or flag was provided.  \n");
        sb.append("                                                                                \n");
//      sb.append("01234567890123456789012345678901234567890123456789012345678901234567890123456789\n");
        System.err.println(sb);
    }
}
