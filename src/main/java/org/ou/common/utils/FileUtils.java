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

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

import org.ou.common.constants.ISystemProperties;

/**
 * <p>
 * FileUtils class.</p>
 *
 * @since 1.0.21
 */
public class FileUtils {

    /**
     * <p>
     * deleteDir.</p>
     *
     * @param dir a {@link java.nio.file.Path} object
     * @throws java.io.IOException if any.
     */
    public static void deleteDir(Path dir) throws IOException {
        try (Stream<Path> pathStream = Files.walk(dir)) {
            pathStream.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    /**
     * <p>
     * convertToRealPath.</p>
     *
     * @param basePath
     * @param relativePath a {@link java.lang.String} object
     * @return a {@link java.nio.file.Path} object
     * @throws java.io.IOException if any.
     */
    public static Path convertToRealPath(Path basePath, String docFile, String relativePath) throws IOException {
        try {
            if (relativePath.startsWith("/")) {
                // nothing to do
            } else if (relativePath.startsWith("~/")) {
                relativePath = ISystemProperties.USER_HOME + relativePath.substring(1);
            } else if (relativePath.startsWith("!")) {
                relativePath = relativePath.substring(1);
                if (relativePath.startsWith("/")) {
                    relativePath = relativePath.substring(1);
                }
                Path repoDirPath = basePath;
                return repoDirPath.resolve(relativePath).toRealPath();
            } else if (relativePath.startsWith("#")) {
                relativePath = relativePath.substring(1);
                if (relativePath.startsWith("/")) {
                    relativePath = relativePath.substring(1);
                }
                Path repoDirPath = basePath;
                return repoDirPath.resolve(docFile).getParent().resolve(relativePath).toRealPath();
            }
            return FileSystems.getDefault().getPath(relativePath).toRealPath();
        } catch (NoSuchFileException e) {
            return Path.of(e.getFile());
        }
    }

    public static Path convertToRealPath(String relativePath) throws IOException {
        try {
            if (relativePath.startsWith("~/")) {
                relativePath = ISystemProperties.USER_HOME + relativePath.substring(1);
            }
            return FileSystems.getDefault().getPath(relativePath).toRealPath();
        } catch (NoSuchFileException e) {
            return Path.of(e.getFile());
        }
    }

    public static String getFileExt(String name) {
        int i = name.lastIndexOf('.');
        if (i <= 0 || i == name.length() - 1) {
            return ""; // no dot, dot is first char, or ends with dot
        }
        return name.substring(i + 1);
    }
}
