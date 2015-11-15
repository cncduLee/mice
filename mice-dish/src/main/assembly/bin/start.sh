#!/bin/bash
cd `dirname $0`
BIN_DIR=`pwd`
cd ..
DEPLOY_DIR=`pwd`
LIB_DIR=$DEPLOY_DIR/lib
LIB_JARS=`ls $LIB_DIR|grep .jar|awk '{print "'$LIB_DIR'/"$0}'|tr "\n" ":"`
CON_DIR=$DEPLOY_DIR/conf

export JAVA_HOME=/usr/java/jdk1.8.0_65
export JAVA_BIN=/usr/java/jdk1.8.0_65/bin
export PATH=$JAVA_BIN:$PATH
export LANG=en_US.UTF-8
export LC_ALL=en_US.UTF-8

nohup java -classpath $CONF_DIR:$LIB_JARS Main &

echo "OK!"
PID=`ps -ef | grep java | grep "$mice-dish" | awk '{print $2}'`
echo $PID > $PID_FILE
echo "PID: $PID"