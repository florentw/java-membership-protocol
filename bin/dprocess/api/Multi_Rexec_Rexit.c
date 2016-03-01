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
	int 		pid[50];
	int 		pid2[50];
	int 		pid3[50];
	int 		pid4[50];
	int 		pid5[50];

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

	for (i=0 ; i<50 ; i++)
	{
		pid[i] = rexecut("localhost", buffer, results.st_size) ;
		printf("Executing process on remote server ...\n");
		printf("==> Rexecut result : %d\n\n", pid[i]);
	}
	for (i=0 ; i<50 ; i++)
	{
		pid2[i] = rexecut("ari-31-201-08", buffer, results.st_size) ;
		printf("Executing process on remote server ...\n");
		printf("==> Rexecut result : %d\n\n", pid2[i]);
	}
	for (i=0 ; i<50 ; i++)
	{
		pid3[i] = rexecut("ari-31-201-07", buffer, results.st_size) ;
		printf("Executing process on remote server ...\n");
		printf("==> Rexecut result : %d\n\n", pid3[i]);
	}
	for (i=0 ; i<50 ; i++)
	{
		pid4[i] = rexecut("ari-31-201-09", buffer, results.st_size) ;
		printf("Executing process on remote server ...\n");
		printf("==> Rexecut result : %d\n\n", pid4[i]);
	}


	for (i=0 ; i<50 ; i++)
	{
		printf("Sending signal %d to process (%d) ...\n", 9, pid3[i]);
		printf("==> Rkill result : %d\n\n", rkill(pid[i], 9));

		printf("Sending signal %d to process (%d) ...\n", 9, pid3[i]);
		printf("==> Rkill result : %d\n\n", rkill(pid3[i], 9));

		printf("Sending signal %d to process (%d) ...\n", 9, pid2[i]);
		printf("==> Rkill result : %d\n\n", rkill(pid2[i], 9));

		printf("Sending signal %d to process (%d) ...\n", 9, pid4[i]);
		printf("==> Rkill result : %d\n\n", rkill(pid4[i], 9));

	}

	rps();

	for (i=0 ; i<50 ; i++)
	{
		pid[i] = rexecut("localhost", buffer, results.st_size) ;
		printf("Executing process on remote server ...\n");
		printf("==> Rexecut result : %d\n\n", pid[i]);
	}
	for (i=0 ; i<50 ; i++)
	{
		pid2[i] = rexecut("ari-31-201-08", buffer, results.st_size) ;
		printf("Executing process on remote server ...\n");
		printf("==> Rexecut result : %d\n\n", pid2[i]);
	}
	for (i=0 ; i<50 ; i++)
		{
			pid3[i] = rexecut("ari-31-201-07", buffer, results.st_size) ;
			printf("Executing process on remote server ...\n");
			printf("==> Rexecut result : %d\n\n", pid3[i]);
		}
	for (i=0 ; i<50 ; i++)
	{
		pid4[i] = rexecut("ari-31-201-09", buffer, results.st_size) ;
		printf("Executing process on remote server ...\n");
		printf("==> Rexecut result : %d\n\n", pid4[i]);
	}

	rps();
	printf("Exiting all processes with status 15 ...\n");
	printf("==> Rexit result : %d\n", rexit(15)) ;

	rps();

	for (i=0 ; i<40 ; i++)
		{
			pid[i] = rexecut("localhost", buffer, results.st_size) ;
			printf("Executing process on remote server ...\n");
			printf("==> Rexecut result : %d\n\n", pid[i]);
		}

		for (i=0 ; i<10 ; i++)
		{
			pid2[i] = rexecut("ari-31-201-08", buffer, results.st_size) ;
			printf("Executing process on remote server ...\n");
			printf("==> Rexecut result : %d\n\n", pid2[i]);
		}
		for (i=0 ; i<10 ; i++)
			{
				pid3[i] = rexecut("ari-31-201-07", buffer, results.st_size) ;
				printf("Executing process on remote server ...\n");
				printf("==> Rexecut result : %d\n\n", pid3[i]);
			}
		for (i=0 ; i<10 ; i++)
				{
					pid4[i] = rexecut("ari-31-201-06", buffer, results.st_size) ;
					printf("Executing process on remote server ...\n");
					printf("==> Rexecut result : %d\n\n", pid4[i]);
				}
		for (i=0 ; i<10 ; i++)
				{
					pid5[i] = rexecut("ari-31-201-05", buffer, results.st_size) ;
					printf("Executing process on remote server ...\n");
					printf("==> Rexecut result : %d\n\n", pid5[i]);
				}

		rps();

			printf("Exiting all processes with status 15 ...\n");
			printf("==> Rexit result : %d\n", rexit(15)) ;

			rps();

	free (status) ;
	fclose (fp);

	return 0 ;
}
