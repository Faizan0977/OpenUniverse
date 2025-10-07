package org.ou.common.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Locale;

import org.ou.common.constants.IMessageDigestAlg;

public class JarUtils {

    /**
     * Runs jarsigner verification on the given jar file and returns the report
     * as a string.
     *
     * @param jarPath Path to jar file (e.g. self-jar)
     * @return Full jarsigner report as string
     * @throws IOException if process fails or verification fails
     * @throws InterruptedException if process is interrupted
     */
    public static String createJarSignerReport(Path jarPath, String jarsignerExecutable) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(
                jarsignerExecutable,
                "-verify",
                "-verbose",
                "-certs",
                jarPath.toString()
        );
        pb.redirectErrorStream(true);

        Process process = pb.start();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (InputStream is = process.getInputStream()) {
            is.transferTo(baos);
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            return "";
        }

        return baos.toString(StandardCharsets.UTF_8);
    }

    public static String createJarSHA256Report(Path jarPath) throws IOException, NoSuchAlgorithmException {
        //MessageDigest digest = MessageDigest.getInstance("SHA-256");
        MessageDigest digest = CommonUtils.getMessageDigest(IMessageDigestAlg.SHA256);

        try (InputStream is = new BufferedInputStream(Files.newInputStream(jarPath))) {
            byte[] buffer = new byte[1024000];
            int read;
            while ((read = is.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }
        }
        byte[] hashBytes = digest.digest();
        return HexFormat.of().formatHex(hashBytes).toLowerCase(Locale.ENGLISH);
    }

    /**
     * Returns the path to the currently running JAR file. If not running from a
     * JAR (e.g. from IDE/classes), returns null.
     */
    public static Path getSelfJar() {
        try {
            String pathStr = JarUtils.class
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI()
                    .getPath();

            if (pathStr.endsWith("/")) {
                return null; // Running from IDE or classes, not a JAR
            }
            return Path.of(pathStr);
        } catch (Exception e) {
            e.printStackTrace();
            return null; // fallback if something went wrong
        }
    }

    public static String extractJarSha256FromSha256File(Path jarPath) throws IOException {
        Path sha256file = Path.of(jarPath.toString() + ".sha256");
        String sha256 = Files.readString(sha256file).strip();
        return sha256.substring(0, sha256.indexOf(" ")).toLowerCase(Locale.ENGLISH);
    }
}
