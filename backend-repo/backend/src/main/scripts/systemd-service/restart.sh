#!/bin/bash

echo "Stopping the service..."
/bin/bash stop.sh
echo "Service stopped."

echo "Starting the service..."
/bin/bash start.sh
echo "Service started."