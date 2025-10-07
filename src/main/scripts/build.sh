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

#
# OpenUniverse Build Script
#

set -eu

usage() {
    cat <<EOF
Usage: $0 <OU_VERSION> <PROPERTIES_FILE> <OUT_DIR>

Positional args:
  OU_VERSION       Version string to use (required)
  PROPERTIES_FILE  Path to properties file (required)
  OUT_DIR          Output directory (required)

Examples:
  $0 1.2.3 ./project.properties ./dist
  $0 1.2.3 ~/configs/props ~/out
EOF
}

# Require exactly 3 args
if [ "$#" -ne 3 ]; then
    echo "ERROR: wrong number of arguments" >&2
    echo >&2
    usage
    exit 1
fi

export OU_VERSION="$1"

PROPERTIES_FILE=$2
OUT_DIR=$3

set -a
. "$PROPERTIES_FILE"
set +a

# -----------------------------
# URLs
# -----------------------------
REPO_URL="https://github.com/ou-org/OpenUniverse.git"

# -----------------------------
# DIRS
# -----------------------------
CACHE_DIR="$HOME/.cache/build-tools"
mkdir -p "$CACHE_DIR"

# -----------------------------
# PREREQUISITE CHECKS
# -----------------------------
for cmd in git curl rsync tar mvn openssl; do
  if ! command -v "$cmd" >/dev/null 2>&1; then
    echo "Error: $cmd is required but not installed." >&2
    exit 1
  fi
done

# -----------------------------
# ENSURE KEYSTORE EXISTS
# -----------------------------
if [ ! -f "$SIGN_JAR_KEYSTORE" ]; then
  echo "Keystore not found: $SIGN_JAR_KEYSTORE"
  exit 1
else
  echo "Using keystore: $SIGN_JAR_KEYSTORE"
fi

# -----------------------------
# TEMP WORK DIR
# -----------------------------
WORKDIR="$(mktemp -d)"
#trap 'rm -rf "$WORKDIR"' EXIT INT TERM
echo "Working in temp dir: $WORKDIR"
cd "$WORKDIR"
REPO_DIR="$WORKDIR/repo"
TARGET_DIR="$REPO_DIR/target"

# -----------------------------
# CLONE REPO
# -----------------------------
TAG="v${OU_VERSION}"
git clone --depth=1 --branch "$TAG" "$REPO_URL" "$REPO_DIR"
cd "$REPO_DIR"

# -----------------------------
# BUILD PROJECT
# -----------------------------
echo "Running Maven build..."
mvn clean verify -e -DskipTests

VER_DIR="${OUT_DIR}/ou-${OU_VERSION}"

rm -rf "$VER_DIR"
mkdir -p "$VER_DIR"

cp "$TARGET_DIR/ou-${OU_VERSION}.jar" "$VER_DIR"
cp "$TARGET_DIR/ou-${OU_VERSION}.jar.sha256" "$VER_DIR"

cp "$TARGET_DIR/ou-${OU_VERSION}-src.jar" "$VER_DIR"
cp "$TARGET_DIR/ou-${OU_VERSION}-src.jar.sha256" "$VER_DIR"

cp "$TARGET_DIR/ou" "$VER_DIR"
cp "$TARGET_DIR/ou.sha256" "$VER_DIR"

echo "Build completed. Artifacts should be in $VER_DIR"

# EOF
