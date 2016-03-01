all: showpid
showpid: showpid.o
	gcc -o showpid showpid.o

showpid.o: showpid.c
	gcc -c -Wall showpid.c -o showpid.o

clean:
	rm -f *.o showpid

