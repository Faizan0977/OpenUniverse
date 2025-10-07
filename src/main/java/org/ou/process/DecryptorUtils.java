package org.ou.process;

import java.io.IOException;
import java.util.List;

public class DecryptorUtils {

    /**
     * Attempts to find an available (non-busy) decryptor process and send input.
     * Waits until one becomes free.
     *
     * @param pool             List of DecryptorInstance objects (wrapped Processes)
     * @param encrypted        Encrypted input string
     * @param password         Password for decryption
     * @return Decrypted string, or empty string if decryption fails
     * @throws IOException if interrupted or IO issues occur
     */
    public static String decrypt(List<DecryptorInstance> pool, String encrypted, char[] password) throws IOException {
        while (true) {
            for (DecryptorInstance instance : pool) {
                if (instance.tryLock()) {
                    try {
                        return instance.decrypt(encrypted, password);
                    } finally {
                        instance.unlock();
                    }
                }
            }

            // No available decryptor yet — wait briefly and retry
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("Interrupted while waiting for decryptor", e);
            }
        }
    }
}
