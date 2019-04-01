#!/usr/bin/env bash
find -name "*.java" > sources.txt
javac @sources.txt
find -name "*.class" -exec mv -t ./ {} +

./Runfile < ./src/main/resources/input.txt
./Runfile < ./src/main/resources/input2.txt
./Runfile < ./src/main/resources/input_gen

find -name "*.class" -exec rm -f {} \;