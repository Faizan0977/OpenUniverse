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
 * ISystemProperties interface.</p>
 *

 * @since 1.0.21
 */
public interface ISystemProperties {
    String JAVA_HOME = System.getProperty("java.home");
    String USER_NAME = System.getProperty("user.name");
    String USER_DIR = System.getProperty("user.dir");
    String USER_HOME = System.getProperty("user.home");
    String JAVA_VERSION = System.getProperty("java.version");
    String JAVA_VENDOR = System.getProperty("java.vendor");
    String OS_NAME = System.getProperty("os.name");
    String OS_VERSION = System.getProperty("os.version");
    String OS_ARCH = System.getProperty("os.arch");
    String OU_STANDALONE = System.getProperty("org.ou.selfcontained");
}
