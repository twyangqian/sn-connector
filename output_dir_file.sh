#!/bin/bash
# get all filename in specified path

path='./build/libs'
files=$(ls $path)
for filename in $files
do
   echo $filename
done