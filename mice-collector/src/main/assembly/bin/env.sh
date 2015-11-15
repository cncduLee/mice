if [ ! -n "$JAVA_HOME" ]; then
    export JAVA_HOME=/usr/java/jdk1.8.0_65
fi
if [ ! -n "$FLUME_HOME" ]; then
    export FLUME_HOME=/export/local/mice-collector-1.0-SNAPSHOT
fi
export LOG_BASE="/export/log"
export JAVA_OPTS="-Xms512m -Xmx1024m"
export ZOOKEEPER=192.168.1.102:2181