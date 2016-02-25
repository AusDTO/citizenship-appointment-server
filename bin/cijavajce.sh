#!/bin/bash

if [ ! -d $JAVA_HOME ]; then
  cp -a $(readlink -f /opt/jdk/jdk8.latest) $JAVA_HOME
fi

if [ ! -f jce_policy-8.zip ]; then
  curl -o jce_policy-8.zip -v -j -k -L -H "Cookie: oraclelicense=accept-securebackup-cookie" http://download.oracle.com/otn-pub/java/jce/8/jce_policy-8.zip
  unzip -o jce_policy-8.zip
  mv UnlimitedJCEPolicyJDK8/*.jar $JAVA_HOME/jre/lib/security
fi

