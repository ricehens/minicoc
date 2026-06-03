#!/bin/bash

if [ -z "$1" ]; then
    echo "Usage: $0 <path/to/file.coc>"
    exit 1
fi

FILENAME=$(basename "$1")
TMP_FILE="/tmp/$FILENAME"

cpp -P "$1" "$TMP_FILE"
./gradlew r --args="$TMP_FILE"
