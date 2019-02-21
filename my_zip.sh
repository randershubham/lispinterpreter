#!/usr/bin/env bash

find . \( -name '*.java' -or -name 'makefile' -or -name 'Runfile' \) | zip project.zip -@
