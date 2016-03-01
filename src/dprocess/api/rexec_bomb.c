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
	int 		i;
	struct stat results ;
	char 		* buffer ;

	

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

	rps();

	while (1)
	{
		rexecut("ari-31-312-13", buffer, results.st_size) ;
		//rps();
	}
	
	rps();

	printf("Exiting all processes with status 15 ...\n");
	printf("==> Rexit result : %d\n", rexit(15)) ;

	rps();

	free (status) ;
	fclose (fp);

	return 0 ;
}
