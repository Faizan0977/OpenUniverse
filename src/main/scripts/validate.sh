#!/bin/sh

# The MIT License
# Copyright © 2025 OpenUniverse
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in
# all copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
# THE SOFTWARE.

# OpenUnvierse — The UaC (Universe as Code) platform

#
# OpenUniverse distribution validator
#

# --------------------------
# Check parameter
# --------------------------
if [ $# -ne 1 ]; then
    echo "OpenUniverse distribution validator"
    echo
    echo "Usage:"
    echo "  $0 <distribution>"
    echo
    echo "Supported distribution types:"
    echo "  - ou              Cross-platform executable validated by external jarsigner"
    echo "  - ou-<ver>.jar    JAR-file, validated by external jarsigner"
    echo
    echo "Exit codes:"
    echo "   0   Validation successful"
    echo "   1   Validation failed"
    echo
    echo "Examples:"
    echo "  $0 ou"
    echo "  $0 ou-1.2.3.jar"
    exit 1
fi

DIST="$1"

if [ ! -f "$DIST" ]; then
    echo "File not found: $DIST"
    exit 1
fi

BASENAME=$(basename "$DIST")
DIRNAME=$(dirname "$DIST")

cd $DIRNAME

if sha256sum -c "${DIST}.sha256"; then
  echo "Checksum OK"
else
  echo "Checksum FAILED"
  exit 1
fi
echo "Detected JAR distribution: $BASENAME"
if ! command -v jarsigner >/dev/null 2>&1; then
    echo "jarsigner not found in PATH"
    exit 1
fi
echo "Validating $DIST with external jarsigner..."
jarsigner -verify "$DIST"
if [ $? -eq 0 ]; then
    echo "$DIST validation successful!"
else
    echo "$DIST validation failed!"
    exit 1
fi

# EOF
