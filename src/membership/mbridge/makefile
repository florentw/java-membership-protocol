#!/bin/bash

# Makefile definitions
CC      = gcc
CFLAGS  = -Wall -ansi -pedantic 
SOURCE	= mbridge.c 
HEADERS	= include
BINARY	= mbridge 
OBJETS	= ${SOURCE:.c=.o}

.PHONY: clean run

all: ${OBJETS}
	${CC} -g ${CFLAGS} $^ -o ${BINARY} -lpthread

%.o: %.c
	${CC} ${CFLAGS} -g -o $@ -c $<

clean:
	rm -f *~ *.o ${BINARY}
