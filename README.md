## Table of contents
* [General info](#general-info)
* [Requirements](#requirements)
* [Logging](#logging)
* [Setup](#setup)
* [Authors](#authors)

## General info
Simple string protocol communicating over TCP sockets.
Application listening on port **50000** and generate unique UUID for every new session

## Requirements
- Maven 3
- JRE 8

## Logging
Application logs are stored in **logs/app.log** file. Configuration file path **src/main/resource/log4j2.xml** 
 
## Setup
#### How to build the tool
* Use maven command `mvn clean install`. Output .jar file will be available in target directory.
 
#### How to run the tool
* Use java command `java -jar interview-app-1.0-SNAPSHOT.jar`.

## Authors
* **Mateusz Wieczorek** - wskmateusz@gmail.com