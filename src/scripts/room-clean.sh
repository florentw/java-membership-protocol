#!/bin/bash

me=`hostname -s`

for i in $(seq 2 15);
do
	if [ $i -lt 10 ]
	then
		host=ari-31-303-0${i}
	else
		host=ari-31-303-${i}
	fi;

	if [ "$host" == "$me" ]
	then
		echo "Zapping"
	else
		echo "Cleaning on ${host}"
		ssh -o StrictHostKeyChecking=no "${host}" "pkill -9 java;pkill mbridge" &
	fi;
done