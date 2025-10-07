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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>
 * Abstract AbstractTo class.</p>
 *
 * @since 1.0.21
 */
/**
 * Represents an abstract configuration object that holds various common types
 * of data. This class serves as a base for more specific types of configuration
 * data, providing a flexible structure to store and manage different kinds of
 * information for applications, such as system settings, timesheet records, and
 * event documentation.
 */
public abstract class AbstractTo {

    /**
     * Represents the header information or meta data associated with the
     * configuration document.
     * <p>
     * JSON Key: "head"
     * </p>
     * This field holds metadata related to the configuration document, such as
     * the document type, description, constraints, and more. It is used as a
     * reference to a `HeadTo` object.
     */
    public HeadTo headTo;

    /**
     * A map of properties associated with the configuration document.
     * <p>
     * JSON Key: "properties"
     * </p>
     * This field is used to store arbitrary key-value pairs that are specific
     * to the configuration document. Properties provide flexibility in adding
     * additional configuration details that don't fit neatly into other fields.
     * Example: `"timeout": 3000`, `"max_connections": 100`.
     */
    public Map<String, Object> properties = new LinkedHashMap<>();
}
