#!/bin/bash

JAR_NAME=
# sample: backend-1.0.0

if [ -z "$JAR_NAME" ]; then
  echo "Error: variable JAR_NAME not set" 1>&2
  exit 1
fi

echo "Stopping the service..."
for pid in $(ps -ef | grep "$JAR_NAME.[j]ar" | awk '{print $2}'); do
  kill -9 $pid
done
echo "Service stopped."