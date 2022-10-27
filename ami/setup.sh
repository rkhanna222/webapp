#!/bin/bash
sudo add-apt-repository ppa:openjdk-r/ppa
sudo apt-get update
sudo apt-cache search openjdk-17
echo "Installing Java"
sudo apt install openjdk-17-jdk -y
echo "Java Version"
java -version
echo "Setting JAVA PATH"
# /usr/lib/jvm/java-17-openjdk-amd64/
echo "export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64/" >>~/.bashrc
echo "export PATH=$PATH:$JAVA_HOME/bin" >>~/.bashrc
echo "Java Location"
update-alternatives --list java
echo "Installing Maven"
sudo apt install maven -y

#echo "Installing Tomcat"
#sudo wget https://dlcdn.apache.org/tomcat/tomcat-10/v10.0.27/bin/apache-tomcat-10.0.27.tar.gz
#sudo tar -xvf apache-tomcat-10.0.27.tar.gz -C /usr/local
#sudo useradd -r tomcat
#sudo chown -R tomcat:tomcat /usr/local/apache-tomcat-10.0.27
## TOMCAT SCRIPT
#sudo tee /etc/systemd/system/tomcat.service<<EOF
#[Unit]
#Description=Tomcat Server
#After=syslog.target network.target
#
#[Service]
#Type=forking
#User=ubuntu
#Group=ubuntu
#
#Environment=CATALINA_HOME=/usr/local/apache-tomcat-10.0.27
#Environment=CATALINA_BASE=/usr/local/apache-tomcat-10.0.27
#Environment=CATALINA_PID=/usr/local/apache-tomcat-10.0.27/temp/tomcat.pid
#
#ExecStart=/usr/local/apache-tomcat-10.0.27/bin/catalina.sh start
#ExecStop=/usr/local/apache-tomcat-10.0.27/bin/catalina.sh stop
#
#RestartSec=12
#Restart=always
#
#[Install]
#WantedBy=multi-user.target
#EOF
#
#echo "Installing mysql server"
#sudo apt-get install mysql-server -y
#sudo mysql <<EOF
#CREATE DATABASE mydatabase;
#CREATE USER 'user'@'localhost' IDENTIFIED BY 'user';
#GRANT ALL PRIVILEGES ON mydatabase.* TO 'user'@'localhost' WITH GRANT OPTION;
#FLUSH PRIVILEGES;
#EOF

echo "Installing HTOP"
sudo apt-get install htop
#sudo htop



# echo "Installing Application"
# scp -i /Users/raghavkhanna/.ssh/aws.pem /Users/raghavkhanna/Desktop/WEBAPP/webapp/target/webapp-0.0.1-SNAPSHOT.jar ubuntu@ec2-107-22-128-160.compute-1.amazonaws.com:~ |
# java -jar webapp-0.0.1-SNAPSHOT.jar


#echo "Starting Application"
#sudo java -jar webapp-0.0.1-SNAPSHOT.jar &
#
#AMI_ID=$(jq -r '.builds[-1].artifact_id' manifest.json | cut -d ":" -f2)
#echo $AMI_ID










