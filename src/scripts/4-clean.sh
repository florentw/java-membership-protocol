#!/bin/bash

ssh -o StrictHostKeyChecking=no "ari-31-312-09" "pkill -9 java" &
ssh -o StrictHostKeyChecking=no "ari-31-312-10" "pkill -9 java" &
ssh -o StrictHostKeyChecking=no "ari-31-312-11" "pkill -9 java" &
ssh -o StrictHostKeyChecking=no "ari-31-312-12" "pkill -9 java" &
