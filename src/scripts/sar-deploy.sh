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

	if [ "$host" = "$me" -o "$host" = "ari-31-201-15" ]
	then
		echo "Zapping"
	else
		echo "Launching on ${host}"
		ssh -o StrictHostKeyChecking=no "${host}" "cd psar/bin && java membership.MSMain" &
	fi;
done
