#!/bin/bash
#
# Usage: start.sh [debug]
#
[  -e `dirname $0`/env.sh ] && . `dirname $0`/env.sh
[  -e `dirname $0`/functions.sh ] && . `dirname $0`/functions.sh
[  -e `dirname $0`/digger.sh ] && . `dirname $0`/mice.sh

if [ ! -d "$JAVA_HOME" ] ;then
    echo "ERROR: Cannot Found JAVA Installed in $JAVA_HOME" >&2
    exit 1
fi
if [ ! -d "$FLUME_HOME" ] ;then
    echo "ERROR: Cannot Found Flume in $FLUME_HOME" >&2
    exit 1
fi
LOG_DIR="$LOG_BASE/$APP_NAME"
if [ ! -d "$LOG_DIR" ] ;then
    mkdir "$LOG_DIR"
    if [ $? -ne 0 ] ;then
        echo "Cannot create $LOG_DIR" >&2
        exit 1
    fi
fi

[  -e `dirname $0`/prepare.sh ] && . `dirname $0`/prepare.sh

if [ -n "$PID" ] && [ 1 -eq `ps -p $PID -f | grep $APP_NAME | wc -l` ]; then
    echo "ERROR: The $APP_NAME already started!"
    echo "PID: $PID"
    exit 1
fi

display_help() {
  cat <<EOF
Usage: $0 [options]...

options:
  --log-name,-n <log name>  the app name of collected log, must specify.
  --log-file,-f <log file>  the log file which is collected. if not specify, will be ${LOG_BASE}/${LOG_NAME}_detail.log
EOF
}

while [ -n "$*" ] ; do
  arg=$1
  shift
  case "$arg" in
    --log-name|-n)
      [ -n "$1" ] || error "Option --log-name requires an argument" 1
      LOG_NAME=$1
      shift
      ;;
    --log-file|-f)
      [ -n "$1" ] || error "Option --log-file requires an argument" 1
      LOG_FILE=$1
      shift
      ;;
    --help|-h)
      display_help
      exit 0
      ;;
  esac
done

if [ ! -n "$LOG_NAME" ]; then
    LOG_NAME=`ps -ef | grep java | grep /export/server/ | sed -r 's/.*\/export\/server\/([^\/]*)\/.*/\1/g'`
fi

if [ ! -n "$LOG_NAME" ]; then
    display_help
    exit 0
fi

if [ ! -n "$LOG_FILE" ]; then
    LOG_FILE=$LOG_BASE/$LOG_NAME/${LOG_NAME}_detail.log
fi

if [ ! -f "$LOG_FILE" ]; then
    echo "$LOG_FILE is not exist"
    exit 1
fi

if [ ! -r "$LOG_FILE" ]; then
    echo "$LOG_FILE can not read"
    exit 1
fi

IP=`get_ip`
IP=`echo $IP | sed -r 's/\./_/g'`

echo "Starting the $APP_NAME ..."
echo "JAVA_HOME: $JAVA_HOME"
echo "FLUME_HOME: $FLUME_HOME"
echo "LOG_NAME: $LOG_NAME"
echo "LOG_FILE: $LOG_FILE"
echo "IP: $IP"

APP_JARS=`ls $APP_LIB|grep .jar|awk '{print "'$APP_LIB'/"$0}'|tr "\n" ":"`
ZK_ROOT=/flume/conf/agent/$LOG_NAME
AGENT_NAME=$IP
AGENT_CONF_DIR=$APP_HOME/conf
echo "AGENT_CONF_DIR: $AGENT_CONF_DIR"
echo "ZK_ROOT: $ZK_ROOT"
echo "AGENT_NAME: $AGENT_NAME"

sh $FLUME_HOME/bin/flume-ng agent  --conf $AGENT_CONF_DIR -z $ZOOKEEPER -p $ZK_ROOT -n $AGENT_NAME --classpath $APP_JARS -Dmice.log.name=$LOG_NAME -Dmice.log.file=$LOG_FILE &

COUNT=0
while [ $COUNT -lt 1 ]; do    
    sleep 1
    COUNT=`ps -ef | grep java | grep "$APP_HOME" | awk '{print $2}' | wc -l`
    echo "ps check count[$COUNT]"
    if [ $COUNT -gt 0 ]; then
        break
    fi
done

echo "OK!"
PID=`ps -ef | grep java | grep "$APP_HOME" | awk '{print $2}'`
echo $PID > $PID_FILE
echo "PID: $PID"



