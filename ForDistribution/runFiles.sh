#!/bin/bash


for ((i = 1; i <= $1; i++));
do
    printf "Seed $1"
    java Main -p1 $2 -p2 $3 -text -seed $i >> $4
done