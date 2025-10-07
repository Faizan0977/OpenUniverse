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
package org.ou.common.constants;

/**
 * <p>
 * IMsg interface.</p>
 *
 * @since 1.0.21
 */
public interface IMsg {

    String GIT_REPOSITORY_IS_NOT_CLEAN = "ERROR: Git repository is not clean. Commit required.";
    String REPOSITORY_IS_LOCKED = "ERROR: Repository is locked. Another process may be in progress. Lock file: %s.";
    String CONSTRAINT_VIOLATION = "ERROR: Document constraint violation: %s. Document: %s.";
    String INVALID_RECORD_TYPE = "ERROR: Invalid record type: %s. Valid record types are: start, stop, calendar, scheduller, trigger, processor, Document: %s.";
    String DUPLICATED_KEY_IN_DOC = "ERROR: Duplicated key (type + name) found in doc %s";
    String DUPLICATED_KEY_IN_FILE = "ERROR: Duplicated key (type + name) found in document file %s, index: %d.";
    String GIT_REPOSITORY_DIR_NOT_EXIST = "ERROR: Git repository directory %s does not exist.";
    String GIT_REPOSITORY_DIR_NOT_DIR = "ERROR: Git repository directory %s is not a directory.";
    String INVALID_GIT_REPOSITORY = "ERROR: Invalid Git repository. Subdirectory .git not found in repository directory %s.";
    String NO_EXPORT_CONFIG_FOUND = "ERROR: No export configuration file found.";
    String MULTIPLE_EXPORT_CONFIG_FOUND = "ERROR: Multiple export configuration files found.";
    String NO_PROPERTIES_FOUND = "ERROR: No properties file found.";
    String MULTIPLE_PROPERTIES_FOUND = "ERROR: Multiple properties files found.";
    String NOTHIG_TO_STOP = "ERROR: Nothig to stop.";
    String CAN_NOT_STOP = "ERROR: Can't stop."; 
    String INFO_PROCESS_STOPPED = "INFO: Process stopped.";
    String COMMIT_SIGNATURE_VERIFICATION_FAILED = "ERROR: Commit signature verification failed.";
    String JAR_SIGNATURE_VERIFICATION_FAILED = "ERROR: JAR signature verification failed.";
    String JAR_SHA_256_VERIFICATION_FAILED = "ERROR: JAR SHA256 verification failed.";
}
