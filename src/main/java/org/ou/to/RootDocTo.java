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
package org.ou.to;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * ConfigTo class.</p>
 *

 * @since 1.0.21
 */
public class RootDocTo extends AbstractTo {

    private static final String DEFAULT_HASH_ALGORITHM = "SHA-256";
    /**
     * Affected jobs
     * Constraint-based query
     */
    public String jobs;
    public Collection<JobDocTo> jobTos = new LinkedHashSet<>();

    /**
     * Affected triggers
     * Constraint-based query
     */
    public String triggers;
    public Collection<AbstractTo> eventPublishersTos = new LinkedHashSet<>();
    public Collection<AbstractTo> calendarTos = new LinkedHashSet<>();
    public Collection<AbstractTo> schedulerTos = new LinkedHashSet<>();
    public Map<String, Object> signSettingsMap;
    public Map<String, Object> timestampSettingsMap;
    public Map<String, Object> otsSettingsMap;
    public List<String> ntpServersList;
    public List<String> stunServersList;
    public Collection<CommandTo> exportDmqCommandTos = new LinkedHashSet<>();
    public Collection<ExportSettings> exportTargets = new LinkedHashSet<>();
    public String hashAlgorithm = DEFAULT_HASH_ALGORITHM;

    /**
     * Command-line instruction for decrypting an encrypted text.
     *
     * This field stores a shell command that can be executed to decrypt a given encrypted string
     * using various encryption tools such as GPG, OpenSSL, or the UNIX password manager (pass).
     * The command should be structured in a way that it can be run directly in a terminal.
     *
     * **Placeholder Variables:**
     * - `${text}`: The encrypted string that needs to be decrypted.
     * - `${password}`: The secret passphrase or key used for decryption.
     *
     * Examples of command-line encryption and decryption:
     *
     * GPG (GNU Privacy Guard)
     * =======================
     *
     * Encrypt a message using GPG (Symmetric Encryption with AES-256):
     * echo -n "Hello, World!" | gpg --symmetric --cipher-algo AES256 --passphrase "MySecretPassword" --batch --yes --quiet --output - 2>/dev/null | base64 | tr -d '\n'
     *
     * Decrypt the message using GPG:
     * base64 -d <<< "${text}" | gpg --decrypt --passphrase "${password}" --batch --yes --quiet 2>/dev/null
     *
     * OpenSSL
     * =======
     *
     * Encrypt a message using OpenSSL (AES-256-CBC):
     * echo -n "Hello, World!" | openssl enc -aes-256-cbc -salt -base64 -pass pass:"MySecretPassword" 2>/dev/null | tr -d '\n'
     *
     * Decrypt the message using OpenSSL:
     * echo "${text}" | openssl enc -aes-256-cbc -d -base64 -pass pass:"${password}" 2>/dev/null
     *
     * PASS (Standard UNIX Password Manager)
     * =====================================
     *
     * Retrieve a stored password using pass:
     * pass "${text}" 2>/dev/null | tr -d '\n'
     */
    public CommandTo decryptCommandTo;
}
