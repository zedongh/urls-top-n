#!/usr/bin/env bash

set -e

JAR_FILE="target/url-count-top-n-1.0-SNAPSHOT.jar"

if [ ! -f "${JAR_FILE}" ]; then
  mvn clean & mvn compile & mvn package
fi

if [ -n "$1" ] && [ -n "$2" ]; then
  java -cp "${JAR_FILE}" util.URLGenerator "$1" "$2"
else
  java -cp "${JAR_FILE}" util.URLGenerator
  echo "Reference Generation Infomation:"
  echo "  url number  ~ file size  "
  echo "  20  million ~ 1G         "
  echo "  200 million ~ 10G        "
  echo "  3   billion ~ 100G       "
fi