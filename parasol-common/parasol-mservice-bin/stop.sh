    #!/bin/bash
    cd `dirname $0`

    BIN_DIR=`pwd`
    cd ..



    SERVER_NAME=$1

    if [[ -z "${SERVER_NAME}" ]]; then
        echo please input stop micro service name
        exit 1
    fi

    SERVER_NAME=parasol-${SERVER_NAME}-web

    PIDS=`ps -ef | grep java | grep "$SERVER_NAME" |awk '{print $2}'`
    if [ -z "$PIDS" ]; then
        echo "ERROR: The $SERVER_NAME does not started!"
        exit 1
    fi

    if [ "$2" != "skip" ]; then
        $BIN_DIR/dump.sh $1
    fi

    echo -e "Stopping the $SERVER_NAME ...\c"
    for PID in $PIDS ; do
        kill $PID > /dev/null 2>&1
    done

    COUNT=0
    while [ $COUNT -lt 1 ]; do    
        echo -e ".\c"
        sleep 1
        COUNT=1
        for PID in $PIDS ; do
            PID_EXIST=`ps -f -p $PID | grep java`
            if [ -n "$PID_EXIST" ]; then
                COUNT=0
                break
            fi
        done
    done

    echo "OK!"
    echo "PID: $PIDS"