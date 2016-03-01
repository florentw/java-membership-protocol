all: api testkill killer

api: api.o
	ar cr api.a api.o
	ranlib api.a

testkill: testkill.o
	gcc -o testkill testkill.o

killer: killer.o api.c
	gcc -o killer killer.o api.c

Simple_Rexec_Rexit: Simple_Rexec_Rexit.o api.c
	gcc -o Simple_Rexec_Rexit Simple_Rexec_Rexit.o api.c

rexec_bomb: rexec_bomb.o api.c
	gcc -o rexec_bomb rexec_bomb.o api.c
	
Simple_3Rexec_Rexit: Simple_3Rexec_Rexit.o api.c
	gcc -o Simple_3Rexec_Rexit Simple_3Rexec_Rexit.o api.c
	
Multi_Rexec_Rexit: Multi_Rexec_Rexit.o api.c
	gcc -o Multi_Rexec_Rexit Multi_Rexec_Rexit.o api.c
	
Simple_Rkill: Simple_Rkill.o api.c
	gcc -o Simple_Rkill Simple_Rkill.o api.c

Simple_Rwait: Simple_Rwait.o api.c
	gcc -o Simple_Rwait Simple_Rwait.o api.c
	
Multi_Rkill: Multi_Rkill.o api.c
	gcc -o Multi_Rkill Multi_Rkill.o api.c

testkill.o: testkill.c
	gcc -c -Wall testkill.c -o testkill.o

api.o: api.c api.h
	gcc -c -Wall api.c -o api.o

killer.o: killer.c api.h
	gcc -c -Wall killer.c -o killer.o -Lapi

Simple_Rexec_Rexit.o: Simple_Rexec_Rexit.c api.h
		gcc -c -Wall Simple_Rexec_Rexit.c -o Simple_Rexec_Rexit.o -Lapi
		
Simple_3Rexec_Rexit.o: Simple_3Rexec_Rexit.c api.h
		gcc -c -Wall Simple_3Rexec_Rexit.c -o Simple_3Rexec_Rexit.o -Lapi

rexec_bomb.o: rexec_bomb.c api.h
		gcc -c -Wall rexec_bomb.c -o rexec_bomb.o -Lapi
		
Multi_Rexec_Rexit.o: Multi_Rexec_Rexit.c api.h
		gcc -c -Wall Multi_Rexec_Rexit.c -o Multi_Rexec_Rexit.o -Lapi

Simple_Rkill.o: Simple_Rkill.c api.h
		gcc -c -Wall Simple_Rkill.c -o Simple_Rkill.o -Lapi

Simple_Rwait.o: Simple_Rwait.c api.h
		gcc -c -Wall Simple_Rwait.c -o Simple_Rwait.o -Lapi
		
Multi_Rkill.o: Multi_Rkill.c api.h
		gcc -c -Wall Multi_Rkill.c -o Multi_Rkill.o -Lapi

clean:
	rm -f *.o api.a testkill killer Simple_Rexec_Rexit Multi_Rexec_Rexit Simple_Rkill Multi_Rkill

