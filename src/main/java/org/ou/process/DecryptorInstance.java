package org.ou.process;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

public class DecryptorInstance {

    private final Process process;
    private final OutputStream os;
    private final InputStream is;
    private final AtomicBoolean busy = new AtomicBoolean(false);

    public DecryptorInstance(ProcessObj processObj) {
        this.process = processObj.process;
        this.os = processObj.stdinOutputStream;
        this.is = processObj.stdoutInputStream;
    }

    public boolean tryLock() {
        return busy.compareAndSet(false, true);
    }

    public void unlock() {
        busy.set(false);
    }

    public String decrypt(String encrypted, char[] password) throws IOException {
        os.write((new String(encrypted) + "\n").getBytes(StandardCharsets.UTF_8));
        os.write((new String(password) + "\n").getBytes(StandardCharsets.UTF_8));
        os.flush();
        String line = readLine(is);
        return line != null ? line : "";
    }

    /**
     * Reads a line from the given InputStream, stopping at '\n'. Returns the
     * line without the newline character.
     *
     * @param is the InputStream to read from
     * @return the line as a String, or null if end of stream is reached
     * @throws IOException if reading fails
     */
    private static String readLine(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        int b;
        while ((b = is.read()) != -1) {
            if (b == '\n') {
                break;
            }
            sb.append((char) b);
            if (b == -1 && sb.length() == 0) {
                return null; // end of stream and nothing read
            }
        }
        return sb.toString();
    }
}
