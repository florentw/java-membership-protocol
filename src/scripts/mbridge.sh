#!/bin/bash

echo "Launching bridge on ari-31-201-15"
ssh -o StrictHostKeyChecking=no "ari-31-201-15" "cd psar/src/membership/mbridge && ./mbridge ari-31-303-01" &

echo "Launching bridge on ari-31-303-01"
ssh -o StrictHostKeyChecking=no "ari-31-303-01" "cd psar/src/membership/mbridge && ./mbridge ari-31-201-15" &

echo "Launching bridge on ari-31-303-16"
ssh -o StrictHostKeyChecking=no "ari-31-303-16" "cd psar/src/membership/mbridge && ./mbridge ari-31-304-01" &

echo "Launching bridge on ari-31-304-01"
ssh -o StrictHostKeyChecking=no "ari-31-304-01" "cd psar/src/membership/mbridge && ./mbridge ari-31-303-16" &

echo "Launching bridge on ari-31-304-16"
ssh -o StrictHostKeyChecking=no "ari-31-304-16" "cd psar/src/membership/mbridge && ./mbridge ari-31-308-01" &

echo "Launching bridge on ari-31-308-01"
ssh -o StrictHostKeyChecking=no "ari-31-308-01" "cd psar/src/membership/mbridge && ./mbridge ari-31-304-16" &

echo "Launching bridge on ari-31-308-16"
ssh -o StrictHostKeyChecking=no "ari-31-308-16" "cd psar/src/membership/mbridge && ./mbridge ari-31-312-01" &

echo "Launching bridge on ari-31-312-01"
ssh -o StrictHostKeyChecking=no "ari-31-312-01" "cd psar/src/membership/mbridge && ./mbridge ari-31-308-16" &
