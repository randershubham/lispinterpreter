#!/usr/bin/env bash
rm -rf project
mkdir project
find . \( -name '*.java' -or -name 'makefile' -or -name 'Runfile' -or -name 'input.txt' -or -name 'defun_input.txt' -or -name 'long_input.txt' \) -exec cp {} ./project/  \;
zip -r project.zip project
rm -rf project
mv project.zip ~
cd ~
unzip project.zip
cd project
make
echo "************************************************************************************"
sh Runfile < input.txt
sh Runfile < defun_input.txt
sh Runfile < long_input.txt
echo "************************************************************************************"
cd ..
rm -rf project