#!/usr/bin/env bash
rm -rf project
mkdir project
find . \( -name '*.java' -or -name 'makefile' -or -name 'Runfile' -or -name 'input.txt' -or -name 'input2.txt' -or -name 'input_gen' \) -exec cp {} ./project/  \;
zip -r project.zip project
rm -rf project
mv project.zip ~
cd ~
unzip project.zip
cd project
make
echo "************************************************************************************"
sh Runfile < input.txt
echo "************************************************************************************"
cd ..
rm -rf project