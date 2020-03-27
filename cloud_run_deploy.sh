#!/bin/bash -xue

PROJECT=diditweetthat-272410
IMAGE_NAME=diditweetthat
VERSION=$(sbt 'inspect actual version' | grep "Setting: java.lang.String" | cut -d '=' -f2 | tr -d ' ')

gcloud config set project ${PROJECT}

# Build & publish image
docker build  --build-arg VERSION=${VERSION} --target prod -t ${IMAGE_NAME}:$VERSION .
docker tag ${IMAGE_NAME}:$VERSION eu.gcr.io/${PROJECT}/${IMAGE_NAME}:$VERSION
docker push eu.gcr.io/${PROJECT}/${IMAGE_NAME}:$VERSION

## Deploy to cloud run
source .env || true # This file contains all required secrets
APPLICATION_SECRET=$(head -c 32 /dev/urandom | base64)

gcloud run deploy diditweethat \
  --image eu.gcr.io/${PROJECT}/${IMAGE_NAME}:$VERSION \
  --platform managed \
  --max-instances=2 \
  --memory=2Gi \
  --cpu=1 \
  --port=9000 \
  --set-env-vars=VERSION=${VERSION},TWITTER_CONSUMER_TOKEN_KEY=${TWITTER_CONSUMER_TOKEN_KEY},TWITTER_CONSUMER_TOKEN_SECRET=${TWITTER_CONSUMER_TOKEN_SECRET},TWITTER_ACCESS_TOKEN_KEY=${TWITTER_ACCESS_TOKEN_KEY},TWITTER_ACCESS_TOKEN_SECRET=${TWITTER_ACCESS_TOKEN_SECRET},APPLICATION_SECRET=${APPLICATION_SECRET} \
  --allow-unauthenticated \
  --region=europe-west1

