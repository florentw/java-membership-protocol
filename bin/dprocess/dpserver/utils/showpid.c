#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <wait.h>

int main (int argc, char ** argv)
{
	if (argc != 2)
	{
		fprintf (stderr, "showpid::Too few arguments\n") ;
		exit(1) ;
	}
	
	int status, spid, pid = fork () ;
	
	switch (pid)
	{
		case -1 :
			fprintf (stderr, "showpid::Error while forking\n") ;
			exit(1) ;
		
		case 0 : /* Son */
			spid = getpid();
			fprintf (stderr, "%d\n", spid) ;
			char * args[] = { argv[1], NULL } ;
			execv (argv[1], args) ;
		
		default : /* Father (we steal our son's exit status) */
			waitpid (pid, &status, 0) ;
			exit (status) ;
	}
}
