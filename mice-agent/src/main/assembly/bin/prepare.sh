export APP_NAME=mice-agent
cd `dirname $0`
export APP_BIN=`pwd`
cd ..
export APP_HOME=`pwd`
export APP_HOME_REAL=`readlink -f $APP_HOME`
export APP_CONF=$APP_HOME/conf
export APP_LIB=$APP_HOME/lib
export PID_FILE="$APP_HOME/$APP_NAME.pid"
if [ -f $PID_FILE ]; then
    export PID=`cat $PID_FILE`
fi