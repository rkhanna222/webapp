#!/bin/bash
# echo "Running Application"
sudo iptables -t nat -A PREROUTING -p tcp --dport 80 -j REDIRECT --to-ports 8080
java -jar /home/ubuntu/webapp-0.0.1-SNAPSHOT.jar --spring.config.location=file:////home/ubuntu/application.properties