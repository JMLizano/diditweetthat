#! /bin/bash

VERSION=$1
shift

if [ -z $VERSION ]
then
  echo "Version argument is emtpy, getting docker image verison from sbt project version..."
  VERSION=$(sbt 'inspect actual version' | grep "Setting: java.lang.String" | cut -d '=' -f2 | tr -d ' ')
fi

source .env

docker run -i -t \
  -e  "VERSION=${VERSION}" \
  -e  "TWITTER_CONSUMER_TOKEN_SECRET=${TWITTER_CONSUMER_TOKEN_SECRET}" \
  -e  "TWITTER_ACCESS_TOKEN_SECRET=${TWITTER_ACCESS_TOKEN_SECRET}" \
  -e  "TWITTER_ACCESS_TOKEN_KEY=${TWITTER_ACCESS_TOKEN_KEY}" \
  -e  "APPLICATION_SECRET=${APPLICATION_SECRET}" \
  -e  "TWITTER_CONSUMER_TOKEN_KEY=${TWITTER_CONSUMER_TOKEN_KEY}" \
  -p 9000:9000 \
  diditweetthat:$VERSION $@
