#!/usr/bin/env bash

docker build -t appa:v10 -f applicationa/Dockerfile applicationa/
docker build -t appb:v10 -f applicationb/Dockerfile applicationb/
docker build -t gateway:v10 -f gateway/Dockerfile gateway/