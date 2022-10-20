#!/bin/bash
export CATALINA_HOME=/opt/tomcat/apache-tomcat-10.0.27
export CATALINA_BASE=$CATALINA_HOME
$CATALINA_HOME/bin/startup.sh
echo "Starting mysql server"
sudo service mysql start
java -jar /home/ubuntu/webapp-0.0.1-SNAPSHOT.jar