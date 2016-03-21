#!/bin/bash
PWD=`pwd`
start () {
export JAVA_HOME=/usr/local/jdk1.7.0_51/
export JAVA_OPTS='-ms128m -mx128m -XX:MaxPermSize=64m'
export CURRENT=$PWD
export PATH=$JAVA_HOME/bin:$PATH
export DISPLAY=:0
export CLASSPATH=$JAVA_HOME/jre/lib/rt.jar:$JAVA_HOME/lib/tools.jar:$CURRENT/original-tsb-comm-ajax.jar:$CURRENT/tsb-comm-ajax.jar:

export MAINCLASS=com.cloudwise.ajax.communicate.AjaxCommMain
nohup java $JAVA_OPTS -cp $CLASSPATH $MAINCLASS &
echo -e '\r'
}
start >  /dev/null 2>&1
