#include "api.h"
#include <sys/socket.h>
#include <sys/types.h>
#include <stdlib.h>
#include <unistd.h>
#include <stdio.h>
#include <string.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <sys/stat.h>

int 			main(int argc, char ** argv)
{
	int 		* status = malloc(sizeof(int));
	char 		* exeName = "/bin/cat" ;
	struct stat results ;
	char 		* buffer ;
	int 		pid;

	if (argc > 1)
		exeName = argv[1] ;

	FILE * fp = fopen(exeName, "rb") ;

	if (!fp)
	{
		fputs ("File error",stderr) ;
		exit (1) ;
	}

	if (stat(exeName, &results) != 0)
	{
		fputs ("File error", stderr) ;
		exit (1) ;
	}

	printf ("Exe size: %ld\n", results.st_size) ;

	buffer = (char*) malloc (sizeof(char)*results.st_size) ;
	if (!buffer)
	{
		fputs ("Memory error", stderr) ;
		exit (2);
	}

	if (fread (buffer, 1, results.st_size, fp) != results.st_size)
	{
		fputs ("Reading error",stderr);
		exit (1);
	}

	printf("Executing process on remote server ...\n");
	pid = rexecut("ari-31-201-07", buffer, results.st_size) ;
	printf("==> Rexecut result : %d\n\n", pid);

	printf("Executing process on remote server ...\n");
	pid = rexecut("ari-31-201-08", buffer, results.st_size) ;
	printf("==> Rexecut result : %d\n\n", pid);

	printf("Executing process on remote server ...\n");
	pid = rexecut("ari-31-201-09", buffer, results.st_size) ;
	printf("==> Rexecut result : %d\n\n", pid);

	printf("Exiting process with status 15 ...\n");
	printf("==> Rexit result : %d\n", rexit(15)) ;

	/*
	printf("Sending signal 2 to process (%d) ...\n", pid);
	printf("==> Rkill result : %d\n\n", rkill(pid, 2));

	printf("Waiting for terminated process ...\n");
	printf("==> Rwait result : %d ", rwait(status)) ;
	printf("// Exited status  %d\n", *status);
	*/

	free (status) ;
	fclose (fp);

	return 0 ;
}
