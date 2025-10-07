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
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.FooterLine;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.ou.common.constants.IMessageDigestAlg;

/**
 * <p>
 * GitUtils class.</p>
 *
 *
 * @since 1.0.21
 */
public class GitUtils {
    //public static final String INTERNAL_COMMIT_MSG_PREFIX = "### ";

    /**
     * <p>
     * copyRepoToDir.</p>
     *
     * @param git a {@link org.eclipse.jgit.api.Git} object
     * @param repository a {@link org.eclipse.jgit.lib.Repository} object
     * @param targetDirPath a {@link java.nio.file.Path} object
     * @return a {@link org.eclipse.jgit.revwalk.RevCommit} object
     * @throws java.lang.Exception if any.
     */
    public static RevCommit copyRepoToDir(Git git, Repository repository, Path targetDirPath) throws Exception {
        // Get the latest commit
        Iterable<RevCommit> logs = git.log().setMaxCount(1).call();
        RevCommit latestCommit = logs.iterator().next();

        // Get the tree associated with the latest commit
        TreeWalk treeWalk = new TreeWalk(repository);
        treeWalk.addTree(latestCommit.getTree());
        treeWalk.setRecursive(true);

        while (treeWalk.next()) {
            if (treeWalk.getPathString().startsWith(".git")) {
                continue;
            }
            Path targetFile = targetDirPath.resolve(treeWalk.getPathString());
            if (treeWalk.isSubtree()) {
                // Create directories
                Files.createDirectories(targetFile);
            } else {
                // Copy file with attributes
                Path sourceFilePath = Paths.get(repository.getDirectory().getParent(), treeWalk.getPathString());
                Files.createDirectories(targetFile.getParent()); // Ensure parent directory exists
                Files.copy(sourceFilePath, targetFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
            }
        }
        return latestCommit;
    }

    // /**
    //  *
    //  * @param git
    //  * @param file
    //  * @throws Exception
    //  */
    // public static synchronized void commitFile(Git git, String file) throws Exception {
    // //    git.add().addFilepattern(file).call();
    // //    git.commit().setMessage(INTERNAL_COMMIT_MSG_PREFIX + "Commit " + file).call();
    // }y
    /**
     * <p>
     * revCommitToMap.</p>
     *
     * @param revCommit a {@link org.eclipse.jgit.revwalk.RevCommit} object
     * @return a {@link java.util.Map} object
     * @throws java.lang.Exception if any.
     */
    public static Map<String, Object> revCommitToMap(RevCommit revCommit) throws Exception {
        Map<String, Object> map = new LinkedHashMap<>();

        map.put("id", revCommit.getId().getName());
        map.put("short_id", revCommit.getId().abbreviate(7).name());
        map.put("tree_id", revCommit.getTree().getId().getName());
        map.put("full_message", revCommit.getFullMessage());
        map.put("short_message", revCommit.getShortMessage());
        map.put("commit_time", Instant.ofEpochSecond(revCommit.getCommitTime()).atZone(ZoneOffset.UTC).toString());
        //        
        PersonIdent authorIdent = revCommit.getAuthorIdent();
        Map<String, Object> map0 = new LinkedHashMap<>();
        map0.put("name", authorIdent.getName());
        map0.put("email_address", authorIdent.getEmailAddress());
        map0.put("when", authorIdent.getWhenAsInstant().atZone(ZoneOffset.UTC).toString());
        map0.put("time_zone", authorIdent.getTimeZone().getID());
        map0.put("time_zone_offset", authorIdent.getTimeZoneOffset());
        map.put("author", map0);
        //
        PersonIdent committerIdent = revCommit.getCommitterIdent();
        map0 = new LinkedHashMap<>();
        map0.put("name", committerIdent.getName());
        map0.put("email_address", committerIdent.getEmailAddress());
        map0.put("when", committerIdent.getWhenAsInstant().atZone(ZoneOffset.UTC).toString());
        map0.put("time_zone", committerIdent.getTimeZone().getID());
        map0.put("time_zone_offset", committerIdent.getTimeZoneOffset());
        map.put("committer", map0);
        //        
        map0 = new LinkedHashMap<>();
        for (FooterLine footerLine : revCommit.getFooterLines()) {
            String key = footerLine.getKey().toLowerCase(Locale.ENGLISH);
            String value = footerLine.getValue().strip().replace('-', '_');
            map0.put(key, value); // Add all footer lines to the map
        }
        map.put("footer_lines", map0);

        return map;
    }

    /**
     * <p>
     * computeGitBlobSHA1.</p>
     *
     * @param bs an array of {@link byte} objects
     * @return a {@link java.lang.String} object
     */
    public static String computeGitBlobSHA1(byte[] bs) {
        StringBuilder sb = new StringBuilder();
        sb.append("blob");
        sb.append(' ');
        sb.append(bs.length);
        sb.append('\0');
        byte[] headerBs = sb.toString().getBytes(StandardCharsets.US_ASCII);
        byte[] targetBs = new byte[headerBs.length + bs.length];
        System.arraycopy(headerBs, 0, targetBs, 0, headerBs.length);
        System.arraycopy(bs, 0, targetBs, headerBs.length, bs.length);
//        return computeSHA1(targetBs);
        return CommonUtils.digest(IMessageDigestAlg.SHA1, targetBs);
    }

    // /**
    //  * <p>
    //  * computeSHA1.</p>
    //  *
    //  * @param bs an array of {@link byte} objects
    //  * @return a {@link java.lang.String} object
    //  */
    // public static String computeSHA1(byte[] bs) {
    //     try {
    //         MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
    //         messageDigest.update(bs);
    //         byte[] hashBytes = messageDigest.digest();
    //         StringBuilder hexString = new StringBuilder();
    //         for (byte b : hashBytes) {
    //             String hex = Integer.toHexString(0xff & b);
    //             if (hex.length() == 1) {
    //                 hexString.append('0');
    //             }
    //             hexString.append(hex);
    //         }
    //         return hexString.toString();
    //     } catch (NoSuchAlgorithmException e) {
    //         throw new RuntimeException(e);
    //     }
    // }

    public static void runCommit(String repoDir, String commitMessage, boolean gpgSignEnabled) throws Exception {
        String[] command = {
            "sh", "-c",
            "cd \"" + repoDir + "\" && git add . && git commit" + (gpgSignEnabled ? " -S" : "") + " -m \""
            + commitMessage.replace("\"", "\\\"") + "\""
        };

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true); // merge stdout + stderr
        Process process = pb.start();

        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        int exitCode = process.waitFor();
        String gitOutput = output.toString();

        if (exitCode == 0) {
            System.err.println("INFO: %s OK".formatted(commitMessage));
        } else if (exitCode == 1 && gitOutput.contains("nothing to commit")) {
            System.err.println("INFO: No changes to commit. Working tree clean.");
        } else if (exitCode == 128) {
            throw new RuntimeException("ERROR: Not a git repository: " + gitOutput);
        } else {
            throw new RuntimeException("ERROR: Git commit failed (code " + exitCode + "):\n" + gitOutput);
        }
    }
    // /**
    //  *
    //  * @param git
    //  * @param filePath
    //  * @return
    //  * @throws Exception
    //  */
    // public static boolean isCommitted(Git git, Path filePath) throws Exception {
    //     String fileName = filePath.getFileName().toString();
    //     Status status = git.status().call();
    //     if (status.getUntracked().contains(fileName)
    //             || status.getModified().contains(fileName)
    //             || status.getAdded().contains(fileName)
    //             || status.getChanged().contains(fileName)
    //             || status.getRemoved().contains(fileName)
    //             || status.getMissing().contains(fileName)) {
    //         return false;
    //     }
    //     return true;
    // }
}
