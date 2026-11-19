#!/bin/bash
# Checks whether an image tag already exists in ECR.
# Writes exists=true|false to $GITHUB_OUTPUT instead of failing, so a re-run of the
# same commit skips the build and still deploys the existing image.

set -uo pipefail

REPO_NAME=${1:-}
IMAGE_TAG=${2:-}
REGION="eu-central-1"

if [ -z "$REPO_NAME" ] || [ -z "$IMAGE_TAG" ]; then
  echo "Error: Missing arguments. Usage: ./ecr-update.sh <repo_name> <tag>"
  exit 1
fi

echo "--- Checking AWS ECR for $REPO_NAME:$IMAGE_TAG ---"
if aws ecr describe-images --repository-name "$REPO_NAME" --image-ids imageTag="$IMAGE_TAG" --region "$REGION" > /dev/null 2>&1; then
  echo "Image $REPO_NAME:$IMAGE_TAG already exists in ECR. Skipping build, reusing it."
  echo "exists=true" >> "$GITHUB_OUTPUT"
else
  echo "Tag '$IMAGE_TAG' not found in ECR. Proceeding with build."
  echo "exists=false" >> "$GITHUB_OUTPUT"
fi
