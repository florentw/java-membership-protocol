#!/bin/bash

me=`hostname -s`

# salles 201 - 303 - 304 - 308 - 312

for i in $(seq 1 14);
do
	if [ $i -lt 10 ]
	then
		host=ari-31-201-0${i}
	else
		host=ari-31-201-${i}
	fi;

	if [ "$host" = "$me" ]
	then
		echo "Zapping"
	else
		echo "Launching on ${host}"
		ssh -o StrictHostKeyChecking=no "${host}" "cd psar/bin && { java membership.MSMain &\java dprocess.dpserver.DPServer & }" &
	fi;
done

for i in $(seq 2 15);
do
	if [ $i -lt 10 ]
	then
		host=ari-31-303-0${i}
	else
		host=ari-31-303-${i}
	fi;

	if [ "$host" = "$me" ]
	then
		echo "Zapping"
	else
		echo "Launching on ${host}"
		ssh -o StrictHostKeyChecking=no "${host}" "cd psar/bin && { java membership.MSMain &\java dprocess.dpserver.DPServer & }" &
	fi;
done

for i in $(seq 2 15);
do
	if [ $i -lt 10 ]
	then
		host=ari-31-304-0${i}
	else
		host=ari-31-304-${i}
	fi;

	if [ "$host" = "$me" ]
	then
		echo "Zapping"
	else
		echo "Launching on ${host}"
		ssh -o StrictHostKeyChecking=no "${host}" "cd psar/bin && { java membership.MSMain &\java dprocess.dpserver.DPServer & }" &
	fi;
done

for i in $(seq 2 15);
do
	if [ $i -lt 10 ]
	then
		host=ari-31-308-0${i}
	else
		host=ari-31-308-${i}
	fi;

	if [ "$host" = "$me" ]
	then
		echo "Zapping"
	else
		echo "Launching on ${host}"
		ssh -o StrictHostKeyChecking=no "${host}" "cd psar/bin && { java membership.MSMain &\java dprocess.dpserver.DPServer & }" &
	fi;
done

for i in $(seq 2 16);
do
	if [ $i -lt 10 ]
	then
		host=ari-31-312-0${i}
	else
		host=ari-31-312-${i}
	fi;

	if [ "$host" = "$me" -o "$host" = "ari-31-312-13" -o "$host" = "ari-31-312-12" -o "$host" = "ari-31-312-09" -o "$host" = "ari-31-312-10" -o "$host" = "ari-31-312-11" ]
	then
		if [ "$host" != "$me" -a "$host" != "ari-31-312-13" ]
		then
			ssh -o StrictHostKeyChecking=no "${host}" "cd psar/bin && { java membership.MSMain & }" &
		fi;
		echo "Zapping"
	else
		echo "Launching on ${host}"
		ssh -o StrictHostKeyChecking=no "${host}" "cd psar/bin && { java membership.MSMain &\java dprocess.dpserver.DPServer & }" &
	fi;
done
