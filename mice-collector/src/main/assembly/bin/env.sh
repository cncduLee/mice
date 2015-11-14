if [ ! -n "$JAVA_HOME" ]; then
    export JAVA_HOME=/export/local/jdk1.6
fi
if [ ! -n "$FLUME_HOME" ]; then
    export FLUME_HOME=/export/local/flume1.6
fi
export LOG_BASE="/export/log"
export JAVA_OPTS="-Xms512m -Xmx1024m"