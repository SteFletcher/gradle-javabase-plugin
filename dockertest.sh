#!/bin/bash
echo "Starting JDK 7 Tests"
docker run --rm -v "$PWD":/usr/src/myapp -v "$HOME/.gradle":/root/.gradle -w /usr/src/myapp openjdk:7 ./gradlew clean test integTest
echo "Starting JDK 8 Tests"
docker run --rm -v "$PWD":/usr/src/myapp -v "$HOME/.gradle":/root/.gradle -w /usr/src/myapp openjdk:8 ./gradlew clean test integTest