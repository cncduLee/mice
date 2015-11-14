#!/bin/bash
[  -e `dirname $0`/prepare.sh ] && . `dirname $0`/prepare.sh

if [ -n "$PID" ]; then
    echo "PID: $PID"
    if [ -n "$PID" ] && [ 1 -eq `ps -p $PID -f | grep $APP_NAME | wc -l` ]; then
        kill $PID
        echo "The $APP_NAME has been stopped!"
    fi
fi