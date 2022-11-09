# CSYE6225-Cloud Computing
Web application built with Spring Boot
####

## Created a CICD Pipeline
- Building Custom Application AMI using Packer
- GitHub Actions Workflow for Web App
  Run the unit test.
  Build the application artifact (war, jar, zip, etc.).
  Build the AMI with application dependencies and set up the application by copying the application artifacts and the configuration files.
  Configure the application to start automatically when VM is launched.
  
  


## Launched EC2 Instance on the custom AMI
- Parameter	Value 
- Amazon Machine Image (AMI)	Your custom AMI 
- Instance Type	t2.micro 
- Protect against accidental termination	No 
- Root Volume Size	50 
- Root Volume Type	General Purpose SSD (GP2)

## Build Instructions
-  Clone this repository  into the local system
-  Open the CLI
-  mvn clean install
-  mvn spring-boot:run

## Run the test
-  mvn test -DskipTests=false

## Kill the process on a port
-  kill $(lsof -t -i:8080)

## Assignment 1
-   Created a organization called 'RaghavCloud222' and made a repo called webapp
-   Forked the webapp from organization into my personal workspace
-   Created a branch called 'a01' and pushed my Java SpringBoot application
-   Created an endpoint called healthz on port 3000
-   Endpoint - 'http://localhost:3000/healthz'

## Assignment 4
-   Packer & AMIs
-   Building Custom Application AMI using Packer
-   Continuous Integration: Add New GitHub Actions Workflow for Web App
-   VPC & Networking
-   App Security Group
-   EC2 Instance
- 

--------------------------------------------------------

--------------------------------------------------------
## Some GitHub Commands
-   git status
-   git add .
-   git commit -m 'COMMENT'
-   git push rk a01
-   Pull request from Namespace - Personal workspace to Organization repo
-   git pull upstream main


----------------------------
| Name | NEU ID | Email Address              |
|------| --- |----------------------------|
| Raghav Khanna | 001088094 | khanna.ra@northeastern.edu |

