#!/usr/bin/env bash
mkdir -p project
find . \( -name '*.java' -or -name 'makefile' -or -name 'Runfile' -or -name 'input.txt' \) -exec cp {} ./project/  \;
zip -r project.zip project
rm -rf project
mv project.zip ~
cd ~
unzip project.zip
cd project
make
sh Runfile < input.txt
cd ..
rm -rf project