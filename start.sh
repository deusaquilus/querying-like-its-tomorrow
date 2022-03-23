#!/bin/bash

docker-compose down && docker-compose build && docker-compose up -d db && docker ps
