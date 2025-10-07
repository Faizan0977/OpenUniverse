#!/bin/sh

######################################################
# OpenUnvierse — The UaC (Universe as Code) platform #
######################################################

######################################################
#                      INTERNAL                      #
######################################################

# Script to tag a new release

set -e

OU_VERSION="1.0.22"
TAG="v${OU_VERSION}"

# Delete local tag if exists
git tag -d "$TAG" 2>/dev/null || true

# Delete remote tag if exists
git push origin :refs/tags/"$TAG" 2>/dev/null || true

# Create and push new tag
git tag -a "$TAG" -m "Release $TAG"
git push origin "$TAG" --force
