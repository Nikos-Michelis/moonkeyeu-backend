#!/bin/bash

# 1. Get the version directly from the POM file
MODULE_DIR=$1
if [ -z "$MODULE_DIR" ]; then
  MODULE_DIR="."
fi

echo "Reading version from $MODULE_DIR/pom.xml..."
NEW_VERSION=$(mvn -f "$MODULE_DIR/pom.xml" help:evaluate -Dexpression=project.version -q -DforceStdout)

if [[ -z "$NEW_VERSION" ]]; then
  echo "Error: Could not read version from pom.xml"
  exit 1
fi

# Standardize tag format (e.g., v1.2.3)
NEW_TAG="v$NEW_VERSION"
echo "Target Tag: $NEW_TAG"

# 2. Fetch tags from remote to ensure we are up to date
git fetch --tags

# 3. Check if the tag already exists in the repository
if git rev-parse "$NEW_TAG" >/dev/null 2>&1; then
  echo "Tag $NEW_TAG already exists. Skipping tag creation."
  echo "git-tag=$NEW_TAG" >> $GITHUB_OUTPUT
  exit 0
fi

# 4. Check if the current commit already has a tag
GIT_COMMIT=$(git rev-parse HEAD)
NEEDS_TAG=$(git describe --contains "$GIT_COMMIT" 2>/dev/null)

if [ -z "$NEEDS_TAG" ]; then
  echo "Applying new tag $NEW_TAG to commit $GIT_COMMIT"
  # Configure git for the runner
  git tag "$NEW_TAG"
  git push origin "$NEW_TAG"

  echo "git-tag=$NEW_TAG" >> $GITHUB_OUTPUT
else
  echo "This commit is already tagged as $NEEDS_TAG. No new tag needed."
  echo "git-tag=$NEEDS_TAG" >> $GITHUB_OUTPUT
fi

exit 0