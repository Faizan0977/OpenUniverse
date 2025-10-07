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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * <p>
 * UnixUtils class.</p>
 *
 *
 * @since 1.0.21
 */
public class UnixUtils {

    public static String getRedirectedStdoutTarget() {
        Path fd1 = Path.of("/proc/self/fd/1");
        if (Files.isSymbolicLink(fd1)) {
            try {
                return Files.readSymbolicLink(fd1).toString();
            } catch (Throwable t) {
                return null;
            }
        }
        return null;
    }

    public static String getCurrentUser() {
        try {
            Process p = new ProcessBuilder("whoami").start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String user = reader.readLine();
                if (user != null && !user.isBlank()) {
                    return user.trim();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * <p>
     * Getter for the field <code>hostname</code>.</p>
     *
     * @return a {@link java.lang.String} object
     * @throws java.io.IOException if any.
     * @throws java.lang.InterruptedException if any.
     */
    public static String getHostname() throws IOException, InterruptedException {
        String hostname = Files.readString(Paths.get("/etc/hostname")).strip();
        if (hostname.isEmpty()) {
            hostname = getHostnameFromKernel();
        }
        return hostname;
    }

    private static String getHostnameFromKernel() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("hostname");
        Process process = processBuilder.start();
        try (InputStream is = process.getInputStream()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    /**
     * <p>
     * getUserGroups.</p>
     *
     * @param username a {@link java.lang.String} object
     * @return a {@link java.util.Collection} object
     * @throws java.io.IOException if any.
     */
    public static Collection<String> getUserGroups(String username) throws IOException {
        Collection<String> groupsList = new LinkedHashSet<>();
        Collection<String> lines = Files.readAllLines(Paths.get("/etc/group"));
        for (String line : lines) {
            String[] parts = line.split(":");
            if (parts.length > 3) {
                // The fourth part contains the list of users in the group
                String[] users = parts[3].split(",");
                for (String user : users) {
                    if (user.equals(username)) {
                        // Add the group name (the first part) to the list of groups
                        groupsList.add(parts[0]);
                        break; // No need to check other users in this group
                    }
                }
            }
        }
        return groupsList;
    }

    public static int getUid() throws Exception {
        Process process = new ProcessBuilder("id", "-u").start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            return Integer.parseInt(reader.readLine().trim());
        }
    }

    public static Collection<Integer> getGroupIds() throws Exception {
        Process process = new ProcessBuilder("id", "-G").start();
        Collection<Integer> gids = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line = reader.readLine();
            if (line != null) {
                for (String part : line.trim().split("\\s+")) {
                    gids.add(Integer.parseInt(part));
                }
            }
        }
        return gids;
    }

    public static Map<String, Integer> getGroups() throws Exception {
        Process process = new ProcessBuilder("id").start();
        Map<String, Integer> groupsMap = new LinkedHashMap<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line = reader.readLine();
            if (line == null) {
                return groupsMap;
            }

            // Example format: uid=1000(john) gid=1000(john) groups=1000(john),27(sudo),1001(docker)
            int groupsIndex = line.indexOf("groups=");
            if (groupsIndex >= 0) {
                String groupsStr = line.substring(groupsIndex + 7);
                String[] groupsArr = groupsStr.split(",");
                for (String group : groupsArr) {
                    String[] parts = group.split("[()]");
                    if (parts.length >= 2) {
                        int gid = Integer.parseInt(parts[0].trim());
                        String name = parts[1].trim();
                        groupsMap.put(name, gid);
                    }
                }
            }
        }
        return groupsMap;
    }

}
