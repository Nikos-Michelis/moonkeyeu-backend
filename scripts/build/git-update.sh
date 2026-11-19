#!/bin/bash
# Resolves the image tag for this build and (best effort) creates the release git tag.
#
# Outputs (to $GITHUB_OUTPUT):
#   git-tag   -> unique, immutable image tag: v<pom-version>-<short-sha>  (e.g. v2.0.1-bd31a31)
#   version   -> release tag from the pom: v<pom-version>                  (e.g. v2.0.1)
#
# Every commit gets its own image tag, so dependency bumps, nginx/infra changes, etc.
# can be built and deployed without having to bump the pom version each time.

set -euo pipefail

MODULE_DIR=${1:-.}

echo "Reading version from $MODULE_DIR/pom.xml..."
VERSION=$(mvn -f "$MODULE_DIR/pom.xml" help:evaluate -Dexpression=project.version -q -DforceStdout)

if [[ -z "$VERSION" ]]; then
  echo "Error: Could not read version from pom.xml"
  exit 1
fi

RELEASE_TAG="v$VERSION"
SHORT_SHA=$(git rev-parse --short=7 HEAD)
IMAGE_TAG="$RELEASE_TAG-$SHORT_SHA"

echo "Release tag: $RELEASE_TAG"
echo "Image tag:   $IMAGE_TAG"

git fetch --tags --quiet || true
if git rev-parse -q --verify "refs/tags/$RELEASE_TAG" >/dev/null; then
  echo "Git tag $RELEASE_TAG already exists. Skipping tag creation."
else
  echo "Creating git tag $RELEASE_TAG on $(git rev-parse HEAD)"
  git tag "$RELEASE_TAG"
  git push origin "$RELEASE_TAG" || echo "Git tag $RELEASE_TAG was pushed concurrently. Continuing."
fi

{
  echo "git-tag=$IMAGE_TAG"
  echo "version=$RELEASE_TAG"
} >> "$GITHUB_OUTPUT"
