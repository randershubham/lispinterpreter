#!/usr/bin/env bash
mkdir -p project
find . \( -name '*.java' -or -name 'makefile' -or -name 'Runfile' \) -exec cp {} ./project/  \;
zip project.zip project