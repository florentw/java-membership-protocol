#!/bin/bash

me=`hostname -s`


for i in $(seq 1 15);
do
	if [ $i -lt 10 ]
	then
		host=ari-31-201-0${i}
	else
		host=ari-31-201-${i}
	fi;

	if [ "$host" == "$me" ]
	then
		echo "Zapping"
	else
		echo "Cleaning on ${host}"
		ssh -o StrictHostKeyChecking=no "${host}" "pkill -9 java;pkill mbridge" &
	fi;
done

for i in $(seq 1 16);
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

for i in $(seq 1 16);
do
	if [ $i -lt 10 ]
	then
		host=ari-31-304-0${i}
	else
		host=ari-31-304-${i}
	fi;

	if [ "$host" == "$me" ]
	then
		echo "Zapping"
	else
		echo "Cleaning on ${host}"
		ssh -o StrictHostKeyChecking=no "${host}" "pkill -9 java;pkill mbridge" &
	fi;
done

for i in $(seq 1 16);
do
	if [ $i -lt 10 ]
	then
		host=ari-31-308-0${i}
	else
		host=ari-31-308-${i}
	fi;

	if [ "$host" == "$me" ]
	then
		echo "Zapping"
	else
		echo "Cleaning on ${host}"
		ssh -o StrictHostKeyChecking=no "${host}" "pkill -9 java;pkill mbridge" &
	fi;
done

for i in $(seq 1 16);
do
	if [ $i -lt 10 ]
	then
		host=ari-31-312-0${i}
	else
		host=ari-31-312-${i}
	fi;

	if [ "$host" == "$me" -o "$host" == "ari-31-312-13" ]
	then
		echo "Zapping"
	else
		echo "Cleaning on ${host}"
		ssh -o StrictHostKeyChecking=no "${host}" "pkill -9 java;pkill mbridge" &
	fi;
done

