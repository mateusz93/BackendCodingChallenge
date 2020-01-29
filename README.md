Backend Coding Challenge
====

[![Build Status](https://travis-ci.org/mateusz93/BackendCodingChallenge.svg?branch=master)](https://travis-ci.org/mateusz93/BackendCodingChallenge)
[![Coverage Status](https://coveralls.io/repos/mateusz93/BackendCodingChallenge/badge.svg?branch=development)](https://coveralls.io/r/mateusz93/BackendCodingChallenge)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/pl.mateusz93.tech.build/BackendCodingChallenge/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/pl.mateusz93.tech.build/BackendCodingChallenge)

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
- OpenJDK 11

## Logging
Application logs are stored in **logs/app.log** file. Configuration file path **src/main/resource/log4j2.xml** 
 
## Setup
#### How to build the tool
* Use maven command `mvn clean install`. Output .jar file will be available in target directory.
 
#### How to run the tool
* Use java command `java -jar backend-coding-challenge-1.0-SNAPSHOT-jar-with-dependencies.jar`.

## Authors
* **Mateusz Wieczorek** - wskmateusz@gmail.com