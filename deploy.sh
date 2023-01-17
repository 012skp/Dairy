#!/bin/bash

BASEDIR=$(dirname $0)

echo "Executing Program... with args $@"

if [ $# -eq 0 ]
then
  echo "It expects an argument. It should be one of ENC,DEC,ALL"
else
  if [ $1 == "ALL" ]
  then
    $BASEDIR/gradlew  :encryptAndCleanTask

    echo "Adding the newly added encrypted files..."
    git  add src/main/java/com/shailesh/files/*
    git commit -m "usual update"
    git push
  elif [ $1 == "ENC" ]
  then
    $BASEDIR/gradlew  :encryptTask
  elif [ $1 == "DEC" ]
  then
    $BASEDIR/gradlew  :decryptFileTask -Pfilename=$2
  else
    echo "arg should be one of ENC,DEC,ALL"
  fi
fi

read name