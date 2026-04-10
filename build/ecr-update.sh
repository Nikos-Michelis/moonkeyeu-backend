#!/bin/bash

REPO_NAME=$1
IMAGE_TAG=$2
REGION="eu-central-1"

if [ -z "$REPO_NAME" ] || [ -z "$IMAGE_TAG" ]; then
  echo "Error: Missing arguments. Usage: ./ecr-update.sh <repo_name> <tag>"
  exit 1
fi

echo "--- Checking AWS ECR for $REPO_NAME:$IMAGE_TAG ---"
# Query AWS ECR for the specific tag
if aws ecr describe-images --repository-name "$REPO_NAME" --image-ids imageTag="$IMAGE_TAG" --region "$REGION" > /dev/null 2>&1; then
  echo "ERROR: Image tag '$IMAGE_TAG' already exists in ECR repository '$REPO_NAME'."
  echo "Please increment the version in your pom.xml before pushing."
  exit 1
else
  echo "Tag '$IMAGE_TAG' not found in ECR. Safe to proceed with build."
  exit 0
fi