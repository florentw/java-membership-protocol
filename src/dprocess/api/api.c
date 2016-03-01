/*
	api of dprocess - a distributed process library in C language

	Copyright (C) 2008 Julien Clement

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

#include <sys/socket.h>
#include <sys/types.h>
#include <stdlib.h>
#include <unistd.h>
#include <stdio.h>
#include <string.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <sys/stat.h>

#define APISERVERPORT	1818
#define APISERVERNAME	"localhost"

#define FALSE			0
#define TRUE			1
#define RETURN_LENGTH 1024

/*
** Error handler for connections
*/
int								my_error(int err)
{
	if (err == 1)
		printf("Socket 'PORT' Error\n");
	if (err == 2)
		printf("Bind Error\n");
	if (err == 3)
		printf("Accept Error\n");
	if (err == -1)
		printf("API::Socket Error\n");
	if (err == -2)
		printf("API::Hostname not found\n");
	if (err == -3)
		printf("API::Not Connected\n");
	if (err == -4)
		printf("Usage: chat_client [-p] <hostname> <port>\n");
	if (err == -5)
		printf("Bad address\n");

	return err;
}

/*
** Connect to the address 'host' and port 'port'
*/
int								my_connection(char *host, int port)
{
	struct sockaddr_in			sin;
	struct hostent				*hostent = NULL;
	struct in_addr				addr;
	int							s;

	if ((s = socket(PF_INET, SOCK_STREAM, 0)) == -1)
		return -1;
	if ((hostent = gethostbyname(host)) == NULL)
		return -2;
	sin.sin_family = PF_INET;
	sin.sin_port = htons(port);
	memcpy(&addr, hostent->h_addr, hostent->h_length);
	sin.sin_addr.s_addr = inet_addr(inet_ntoa(addr));
// 	printf("addr : %s\n",inet_ntoa(addr));
	if ((connect(s, (struct sockaddr *)&sin, sizeof (struct sockaddr_in))) == -1)
	{
		perror("connect");
		return -3;
	}
	return s;
}

int								extract_status (char * answer, int * status)
{
	char * oldans = answer ;

	//printf("Treat rwait answer...\n");

	while (*answer)
	{
		if (*answer == ':')
		{
			*answer = '\0' ;
			*status = atoi(++answer) ;

			return atoi(oldans) ;
		}

		answer++ ;
	}

	printf("API::Error, wrong answer received : could not extract status.\n");
	return -1 ;
}

char							*itoa_pad(int size)
{
	char						*ret = malloc(9 * sizeof(char));
	int						i;

	for (i = 7; i >= 0; i--, size /= 10)
	  ret[i] = (size % 10) + '0';
	ret[8] = 0;
	return ret;
}

int							envoyerRequete(char * requete, int rw, int * status, int size)
{
	int						socket;
	int						codeRetour;
	char						*req2 = (char *) malloc (sizeof(char) * (size+32));
	char						*ret = (char *) malloc (sizeof(char) * RETURN_LENGTH);
	char						*lengthStr;

	socket = my_connection(APISERVERNAME, APISERVERPORT);
	if (socket < 0)
	{
		my_error(socket);
		return -1;
	}
	lengthStr = itoa_pad(size);
	//printf("%s\n", lengthStr);
	sprintf(req2, "%s", lengthStr) ;
	memcpy(req2+strlen(lengthStr), requete, size);
	//printf("-> Requete : %s\n", req2) ;
	size += strlen(lengthStr);
	if(write(socket, req2, size) != size)
	{
	   printf("API::Can't write on socket\n");
	   return -1;
	}
	//else printf("j ai bien ecrit sur la socket la requete\n");
	if(read(socket, ret, sizeof(char) * RETURN_LENGTH) == -1)
	{
	   printf("API::Can't read on socket\n");
	   return -1;
	}
	else
	{
		if (rw) /** Treat rwait answer */
			codeRetour = extract_status(ret, status);
		else
			codeRetour = atoi(ret) ;

		//printf("j ai bien lu le resultat sur la socket\n");
	}

	if(close(socket) != 0)
	{
		printf("API::Can't close socket\n");
		return -1;
	}

	free (ret) ;
	free (requete) ;
	free (req2) ;
	free (lengthStr) ;

	return codeRetour;
}

int rexecut(char * nomMachine, char * code, long size)
{
	char * requete = (char *) malloc(sizeof(char)*(size+strlen(nomMachine)+7));
	memset (requete, '\0', sizeof(char)*(size+strlen(nomMachine)+7)) ;
	sprintf(requete, "rexec:%s:", nomMachine);
	memcpy(requete+strlen(requete), code, size) ;

	return envoyerRequete(requete, 0, NULL, size+strlen(nomMachine)+7);
}

int rkill(int uid, int sig)
{
	char * requete = (char *) malloc(sizeof(char)*128);
	memset (requete, '\0', sizeof(char)*128) ;
	sprintf(requete, "rkill:%d:%d", uid, sig);

	return envoyerRequete(requete, 0, NULL, strlen(requete));
}

int rwait(int * status)
{
	char * requete = (char *) malloc(sizeof(char)*128);
	memset (requete, '\0', sizeof(char)*128) ;
	sprintf(requete, "rwait");

	return envoyerRequete(requete, 1, status, strlen(requete)) ;
}

int rexit(int status)
{
	char * requete = (char *) malloc(sizeof(char)*128);
	memset (requete, '\0', sizeof(char)*128) ;
	sprintf(requete, "rexit:%d", status);

	return envoyerRequete(requete, 0, NULL, strlen(requete));
}

void rps()
{
	char * requete = (char *) malloc(sizeof(char)*128);
	memset (requete, '\0', sizeof(char)*128) ;
	sprintf(requete, "rlist");
	envoyerRequete(requete, 0, NULL, strlen(requete)) ;
}


