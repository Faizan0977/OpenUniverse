#!/bin/sh
set -e  # stop on first error

# Resolve script directory and generated folder
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
GEN_DIR="$SCRIPT_DIR/generated"

# Ensure clean generated directory
rm -rf "$GEN_DIR"
mkdir -p "$GEN_DIR"

# Work inside generated folder
cd "$GEN_DIR"

# Create repo
rm -rf HelloUniverseRepo
mkdir HelloUniverseRepo

cp "$SCRIPT_DIR/../doc/examples/HelloUniverse.md" HelloUniverseRepo/

cd HelloUniverseRepo
git init
git config user.name "Test User"
git config user.email "test@example.com"
git add .
git commit -m "Initial commit"

cd ..
rm -f HelloUniverse.zip
zip -r HelloUniverse.zip HelloUniverseRepo
