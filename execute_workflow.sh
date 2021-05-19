#! /bin/bash


# act will read a .env file in the same folder
act \
    -P ubuntu-latest=nektos/act-environments-ubuntu:18.04 \
    -s TWITTER_CONSUMER_TOKEN_KEY \
    -s TWITTER_CONSUMER_TOKEN_SECRET \
    -s TWITTER_ACCESS_TOKEN_KEY \
    -s TWITTER_ACCESS_TOKEN_SECRET \
    -s APPLICATION_SECRET \
    pull_request