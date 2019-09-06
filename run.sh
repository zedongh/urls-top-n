#!/usr/bin/env bash

set -e

JAR_FILE="target/url-count-top-n-1.0-SNAPSHOT.jar"

mvn clean & mvn compile & mvn package

echo "------------------------------------------------------------------------"

java -Xmx1g -jar "${JAR_FILE}" "$@"

echo "------------------------------------------------------------------------"
if [ -n "$1" ]; then
  echo "top 100 result write into file $1-result!"
fi
echo "------------------------------------------------------------------------"

