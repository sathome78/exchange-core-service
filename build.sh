#!/usr/bin/env bash

ENVIRONMENT_VARIABLE=$1

DOCKER_DEPLOY_DIRECTORY=${PWD}
cd target

TARGET_DIRECTORY=${PWD}

ARTIFACT_PATH=$TARGET_DIRECTORY"/authorization-service.jar"

echo $ARTIFACT_PATH

cd $DOCKER_DEPLOY_DIRECTORY

docker build -t core-api --build-arg ENVIRONMENT=${ENVIRONMENT_VARIABLE} .

docker run -p 80:8080 core-api