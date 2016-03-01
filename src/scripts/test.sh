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

	if [ $host = $me ]
	then
		echo "Zapping"
	else
		echo "Launching on ${host}"
	fi;

#	ssh -o StrictHostKeyChecking=no "${host}" "cd psar/bin && java membership.MSMain" &
done


#echo "Launching bridge on ari-31-201-15"
#ssh -o StrictHostKeyChecking=no "ari-31-201-15" "cd psar/src/membership/mbridge && ./mbridge ari-31-312-01" &

#echo "Launching bridge on ari-31-312-01"
#ssh -o StrictHostKeyChecking=no "ari-31-312-01" "cd psar/src/membership/mbridge && ./mbridge ari-31-201-15" &

#echo "Launching bridge on ari-31-312-15"
#ssh -o StrictHostKeyChecking=no "ari-31-312-15" "cd psar/src/membership/mbridge && ./mbridge ari-31-304-01" &

#echo "Launching bridge on ari-31-304-01"
#ssh -o StrictHostKeyChecking=no "ari-31-304-01" "cd psar/src/membership/mbridge && ./mbridge ari-31-312-15" &