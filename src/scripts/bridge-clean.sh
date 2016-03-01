#!/bin/bash

me=`hostname -s`

ssh -o StrictHostKeyChecking=no "ari-31-201-15" "pkill mbridge" &

echo "Launching bridge on ari-31-303-01"
ssh -o StrictHostKeyChecking=no "ari-31-303-01" "pkill mbridge" &

echo "Launching bridge on ari-31-303-16"
ssh -o StrictHostKeyChecking=no "ari-31-303-16" "pkill mbridge" &

echo "Launching bridge on ari-31-304-01"
ssh -o StrictHostKeyChecking=no "ari-31-304-01" "pkill mbridge" &

echo "Launching bridge on ari-31-304-16"
ssh -o StrictHostKeyChecking=no "ari-31-304-16" "pkill mbridge" &

echo "Launching bridge on ari-31-308-01"
ssh -o StrictHostKeyChecking=no "ari-31-308-01" "pkill mbridge" &

echo "Launching bridge on ari-31-308-16"
ssh -o StrictHostKeyChecking=no "ari-31-308-16" "pkill mbridge" &

echo "Launching bridge on ari-31-312-01"
ssh -o StrictHostKeyChecking=no "ari-31-312-01" "pkill mbridge" &