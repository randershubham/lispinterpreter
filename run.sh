#!/usr/bin/env bash
find -name "*.java" > sources.txt
javac @sources.txt
find -name "*.class" -exec mv -t ./ {} +
./Runfile < ./src/main/resources/input.txt
find -name "*.class" -exec rm -f {} \;