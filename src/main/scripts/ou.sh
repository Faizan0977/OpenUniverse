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

SCRIPT_DIR=$(cd "$(dirname "$0")" >/dev/null 2>&1 && pwd)

JAVA_EXEC=""
if [ -x "$SCRIPT_DIR/jre/bin/java" ]; then
  JAVA_EXEC="$SCRIPT_DIR/jre/bin/java"
elif [ -n "$OU_JAVA_HOME" ]; then
  JAVA_EXEC="$OU_JAVA_HOME/bin/java"
elif [ -n "$JAVA_HOME" ]; then
  JAVA_EXEC="$JAVA_HOME/bin/java"
elif command -v java >/dev/null 2>&1; then
  JAVA_EXEC=java
fi

if [ -z "$JAVA_EXEC" ]; then
  echo "Java not found."
  exit 1
fi

# JVM options
JVM_ARGS_FILE="jvm.options"
if [ -f "$JVM_ARGS_FILE" ]; then
  JVM_ARGS=$(cat "$JVM_ARGS_FILE")
else
  JVM_ARGS=""
fi

exec "$JAVA_EXEC" $JVM_ARGS \
  -Dorg.ou.selfcontained=true \
  -jar "$0" "$@"
