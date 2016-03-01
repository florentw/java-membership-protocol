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

int main(int argc, char ** argv)
{
	int *status = malloc(sizeof(int));
	char * exeName = "/bin/cat" ;
//	int i;
	status = NULL ;

	status = (int *) malloc (sizeof(int)) ;

	if (argc > 1)
		exeName = argv[1] ;

	FILE * fp = fopen(exeName, "rb") ;
	struct stat results ;
	char * buffer ;

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
		//printf("Fichier :::::: ");
		//for(i=0 ; i<results.st_size ; i++)
		//	printf("%c", buffer[i]);

	int pid = rexecut("localhost", buffer, results.st_size) ;
	int pid2 = rexecut("localhost", buffer, results.st_size) ;
	printf("resultat de rexecut : %d\n", pid);
	printf("resultat de rexecut 2: %d\n", pid2);

	status = (int *) malloc (sizeof(int)) ;

	printf("resultat de rkill : %d\n", rkill(pid, 1)) ;
	printf("resultat de rkill : %d\n", rkill(pid, 2)) ;
	printf("resultat de rkill process 2 : %d\n", rkill(pid2, 2)) ;

	rps();


	printf("resultat de rexit : %d\n", rexit(15)) ;
	printf("resultat de rwait : %d ", rwait(status)) ;
	printf("// Exited status  %d\n", *status);
// 	printf("status : %d\n", *status) ;

	free (status) ;
	fclose (fp);

	return 0 ;
}
