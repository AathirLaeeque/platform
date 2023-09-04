#!/bin/bash

JAR_NAME=
# sample: backend-1.0.0

ABSOLUTE_PATH_TO_PROPERTIES_DIRECTORY=
# sample: /home/ubuntu/cleen-dwi/backend

ABSOLUTE_PATH_TO_LOGBACK_DIRECTORY=
# sample: /home/ubuntu/cleen-dwi/backend

if [ -z "$JAR_NAME" ]; then
  echo "Error: variable JAR_NAME not set" 1>&2
  exit 1
fi

if [ -z "$ABSOLUTE_PATH_TO_PROPERTIES_DIRECTORY" ]; then
  echo "Error: variable ABSOLUTE_PATH_TO_PROPERTIES_DIRECTORY not set" 1>&2
  exit 1
fi

if [ -z "$ABSOLUTE_PATH_TO_LOGBACK_DIRECTORY" ]; then
  echo "Error: variable ABSOLUTE_PATH_TO_LOGBACK_DIRECTORY not set" 1>&2
  exit 1
fi

# create log directory
LOG_PATH=/tmp/log/leucine/cleen-dwi
mkdir -p $LOG_PATH

JAR=$JAR_NAME.jar
# command for executing the application
java -jar $JAR --spring.profiles.active=default --spring.config.name=application --logging.config=$ABSOLUTE_PATH_TO_LOGBACK_DIRECTORY/logback.xml --spring.config.location=file://$ABSOLUTE_PATH_TO_PROPERTIES_DIRECTORY/ >$LOG_PATH/console.log 2>&1
