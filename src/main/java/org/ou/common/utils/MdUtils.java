package org.ou.common.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MdUtils {

    /**
     * Recursively traverses a directory and processes all .md files with
     * extractCodeBlocks.
     *
     * @param dir directory to scan
     * @throws IOException if file operations fail
     */
    public static void processMarkdownDir(Path dir) throws IOException {
        Files.walkFileTree(dir, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().toLowerCase(Locale.ENGLISH).endsWith(".md")) {
                    extractCodeBlocks(file);
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Extracts code blocks with filenames from a Markdown file and writes them
     * into a directory structure inside the same directory as the Markdown
     * file, in a subdirectory named after the Markdown file (without
     * extension).
     *
     * Example: docs/example.md -> docs/example/...
     *
     * @param mdFile Path to the markdown file
     * @throws IOException if file operations fail
     */
    private static void extractCodeBlocks(Path mdFile) throws IOException {
        List<String> lines = Files.readAllLines(mdFile, StandardCharsets.UTF_8);

        if (!isMdValid(lines)) {
            System.out.println("Skipping " + mdFile + " (invalid <head>, no matching doc-spec, or disabled=true)");
            return;
        }

        String mdFileName = mdFile.getFileName().toString();
        String mdBaseName = mdFileName.contains(".")
                ? mdFileName.substring(0, mdFileName.lastIndexOf('.'))
                : mdFileName;
        Path outDir = mdFile.getParent().resolve(mdBaseName);
        Files.createDirectories(outDir);

        extractBlocks(lines, outDir);
    }

    private static boolean isMdValid(List<String> lines) {
        boolean inHead = false, foundDocSpec = false, disabled = false;

        Pattern metaPattern = Pattern.compile(
                "<meta\\s+[^>]*name=['\"]([^'\"]+)['\"][^>]*content=['\"]([^'\"]+)['\"][^>]*/?>",
                Pattern.CASE_INSENSITIVE);

        for (String line : lines) {
            line = line.trim();
            if (line.equalsIgnoreCase("<head>")) {
                inHead = true;
                continue;
            }
            if (line.equalsIgnoreCase("</head>")) {
                break;
            }
            if (inHead) {
                Matcher m = metaPattern.matcher(line);
                if (m.matches()) {
                    String name = m.group(1).trim();
                    String content = m.group(2).trim();
                    if (name.equalsIgnoreCase("doc-spec") && "OpenUniverseSpecVer-1.01".equals(content)) {
                        foundDocSpec = true;
                    }
                    if (name.equalsIgnoreCase("doc-disabled") && "true".equals(content)) {
                        disabled = true;
                    }
                }
            }
        }
        return inHead && foundDocSpec && !disabled;
    }

    private static void extractBlocks(List<String> lines, Path outDir) throws IOException {
        // filename in backticks, optional +x in second backticks on same line
        Pattern fileLinePattern = Pattern.compile("^`([^`]+)`(?:\\s+`(\\+x)`)?\\s*$");
        Pattern codeFenceStart = Pattern.compile("^```(\\w+)?\\s*$");
        Pattern codeFenceEnd = Pattern.compile("^```\\s*$");

        String currentFileName = null;
        boolean currentExe = false;
        String currentLang = null;
        StringBuilder codeBuffer = null;
        boolean insideCodeBlock = false;

        for (String line : lines) {
            Matcher fnMatch = fileLinePattern.matcher(line);
            if (!insideCodeBlock && fnMatch.matches()) {
                currentFileName = fnMatch.group(1).trim();
                if (currentFileName.startsWith("/")) {
                    currentFileName = currentFileName.substring(1);
                }
                currentExe = fnMatch.group(2) != null; // true if `+x` present
                continue;
            }

            // Start code fence
            Matcher startMatch = codeFenceStart.matcher(line);
            if (!insideCodeBlock && startMatch.matches()) {
                insideCodeBlock = true;
                codeBuffer = new StringBuilder();
                currentLang = startMatch.group(1) != null
                        ? startMatch.group(1).strip()
                        : null;
                continue;
            }

            // End code fence
            if (insideCodeBlock && codeFenceEnd.matcher(line).matches()) {
                insideCodeBlock = false;
                if (currentFileName != null && codeBuffer != null) {
                    Path outFile = outDir.resolve(currentFileName);
                    byte[] bytes = processCodeBlock(codeBuffer.toString(), currentLang);
                    writeFile(outFile, bytes, currentExe);
                }
                currentFileName = null;
                currentExe = false;
                currentLang = null;
                codeBuffer = null;
                continue;
            }

            // Inside code block
            if (insideCodeBlock && codeBuffer != null) {
                codeBuffer.append(line).append(System.lineSeparator());
            }
        }
    }

    private static byte[] processCodeBlock(String content, String lang) throws IOException {
        if (lang == null) {
            return content.getBytes(StandardCharsets.UTF_8);
        }

        final String PREFIX = "bytes:";
        if (lang.startsWith(PREFIX)) {
            String cleaned = content.strip().replaceAll("\\s+", "");
            String suffix = lang.substring(PREFIX.length());
            switch (suffix) {
                case "bin":
                    return StringUtils.parseBytes(cleaned, 2);
                case "oct":
                    return StringUtils.parseBytes(cleaned, 8);
                case "dec":
                    return StringUtils.parseBytes(cleaned, 10);
                case "hex":
                    return StringUtils.parseBytes(cleaned, 16);
                case "base64":
                    return Base64.getDecoder().decode(cleaned);
                case "base64url":
                    return Base64.getUrlDecoder().decode(cleaned);
                default:
                    throw new IOException("Unsupported bytes format: " + suffix);
            }
        }
        return content.getBytes(StandardCharsets.UTF_8);
    }

    private static void writeFile(Path outFile, byte[] data, boolean exe) throws IOException {
        Files.createDirectories(outFile.getParent());
        Files.write(outFile, data);

        if (exe) {
            try {
                outFile.toFile().setExecutable(true);
            } catch (UnsupportedOperationException e) {
                System.err.println("Skipping chmod on non-POSIX FS: " + outFile);
            }
        }
    }
}
