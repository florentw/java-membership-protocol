/**
 * @author Florent Weber
 */

#define _XOPEN_SOURCE 1
#define _POSIX_SOURCE 1

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>
#include <unistd.h>
#include <signal.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <errno.h>

#define TRUE			1
#define FALSE			0

#define MC_PORT			5454
#define MC_ADDR			"232.23.0.0"
#define MC_TTL			5

#define UC_PORT			5555
#define UC_ADDR			"127.0.0.1"

#define MAX_MSG_SIZE	50000

/* Main structure */
struct mbridge_t {
	
	char				*addrUC ;
	char				*addrMC ;
	
	int					portUC ;
	int					portMC ;
	
	int					rSockfdUC, wSockfdUC ;
	int					rSockfdMC, wSockfdMC ;
	int					rLenMC, wLenMC ;
	int					ttlMC ;
	
	struct sockaddr_in	rSockUC, wSockUC ;
	struct sockaddr_in	rSockMC, wSockMC ;

} ;

typedef struct			mbridge_t mbridge ;

pthread_t				unicastServer ;

/** Perform a DNS lookup */
unsigned int			resolve_hostname (const char *host, unsigned long *ip)
{
	unsigned long 		address = inet_addr (host) ;
	
	if (address == INADDR_NONE)
	{
		struct hostent *phe = gethostbyname (host) ;
		if (!phe)
			return FALSE ;
		address = *(((unsigned long **) phe->h_addr_list)[0]) ;
	}
	*ip = address ;
	return TRUE ;
}

/** Initialize unicast (UDP) sockets */
unsigned int initUCSockets (mbridge * mb)
{
	unsigned long resolvedAddr ;
	
	if (!resolve_hostname(mb->addrUC ,&resolvedAddr))
	{
		perror ("resolve_hostname") ;
		return FALSE ;
	}
	
	memset (&mb->rSockUC, 0, sizeof (mb->rSockUC)) ;
	mb->rSockUC.sin_family = AF_INET ;
	mb->rSockUC.sin_addr.s_addr = htonl (INADDR_ANY) ;
	mb->rSockUC.sin_port = htons (mb->portUC) ;

	memset (&mb->wSockUC, 0, sizeof (mb->wSockUC)) ;
	mb->wSockUC.sin_family = AF_INET ;
	mb->wSockUC.sin_addr.s_addr = resolvedAddr ;
	mb->wSockUC.sin_port = htons (mb->portUC) ;

	/* Init client socket */
	if((mb->wSockfdUC = socket (PF_INET, SOCK_DGRAM, 0)) == -1)
	{
		perror ("client socket") ;
		return FALSE ;
	}
	
	/* Init server socket */
	if ((mb->rSockfdUC = socket (PF_INET, SOCK_DGRAM, 0)) < 0)
	{
		perror ("server socket") ;
		return FALSE ;
	}

	if (bind(mb->rSockfdUC, (struct sockaddr *)&mb->rSockUC, sizeof(struct sockaddr_in)) < 0)
	{
		perror ("bind") ;
		if(errno != EADDRINUSE)
			return FALSE ;
	}

	return TRUE ;
}

/** Injects multicast packets received from the unicast socket */
void * treatUCRequests (void * arg)
{
	mbridge				* mb = (mbridge *) arg ;
	char				* msg = malloc (sizeof(char) * MAX_MSG_SIZE) ;
	int					length ;
	socklen_t			sockalen = sizeof(struct sockaddr_in) ;
	struct sockaddr_in	* from = (struct sockaddr_in *) malloc(sizeof(struct sockaddr_in)) ;
	
	fprintf (stdout, "Starting UDP forwarder ...\n") ;
	
	while (1)
	{
		memset (msg, 0, sizeof(char) * MAX_MSG_SIZE) ;
		
		if ((length = recvfrom (mb->rSockfdUC, msg, MAX_MSG_SIZE, 0, (struct sockaddr *)from, &sockalen)) == -1)
		{
			perror ("recvfrom") ;
			pthread_exit (NULL) ;
			return NULL ;
		}
		
		if (sendto (mb->wSockfdMC, msg, length, 0, (struct sockaddr *)&mb->wSockMC, mb->wLenMC) < 0)
		{
			perror ("sendto") ;
			return NULL ;
		}
		
		printf ("-> Forwarded Multicast packet (length :%d)\n", length) ;
	}
	
	return NULL ;
}

/** Launch the unicast server that will forward multicast packets */
unsigned int startUCServer (mbridge * mb)
{
	pthread_attr_t attr;
	
	if (pthread_attr_init (&attr) != 0)
	{
		fprintf (stderr, "! Broadcast pthread_attr_init error\n") ;
		return FALSE ;
	}
	
	if (pthread_attr_setdetachstate (&attr, PTHREAD_CREATE_DETACHED) != 0)
	{
		fprintf (stderr, "! Broadcast pthread_attr_setdetachstate error\n") ;
		return FALSE ;
	}
	
	if (pthread_create (&unicastServer, &attr, treatUCRequests, mb) != 0)
	{
		fprintf (stderr, "! Broadcast thread creation failure\n") ;
		return FALSE ;
	}
	
	return TRUE ;
}

/** Listens for multicast packets and forward them using the unicast bridge */
unsigned int forwardMCPackets (mbridge * mb)
{
	int					length ;
	char				* msg = malloc (sizeof(char) * MAX_MSG_SIZE) ;
	socklen_t			sockalen = sizeof(struct sockaddr_in) ;
	
	fprintf (stdout, "Starting Multicast sniffer ...\n") ;
	
	while (1)
	{
		memset (msg, 0, sizeof(char) * MAX_MSG_SIZE) ;
		
		if ((length = recvfrom (mb->rSockfdMC, msg, MAX_MSG_SIZE, 0, (struct sockaddr *)&mb->rSockMC, &sockalen)) < 0)
		{
			perror("recvfrom") ;
			return FALSE ;
		}
		else if (!length)
			break ;
		
		printf ("<- Sniffed Multicast packet (length :%d)\n", length) ;
		
		if ((sendto (mb->wSockfdUC, msg, length, 0, (struct sockaddr *)&mb->wSockUC, sockalen)) == -1)
		{
			perror ("sendto") ;
			return FALSE ;
		}
	}
	
	return TRUE ;
}

/** Initialize multicast sockets */
unsigned int initMCSockets (mbridge * mb)
{
	int loop = 0 ;
	struct ip_mreq imr ;
	
	/* Create sockets */
	mb->rSockfdMC = socket (PF_INET, SOCK_DGRAM, 0) ;
	if (mb->rSockfdMC < 0)
	{
		perror("socket");
		return FALSE ;
	}
	
	mb->wSockfdMC = socket (PF_INET, SOCK_DGRAM, 0) ;
	if (mb->wSockfdMC < 0)
	{
		perror("socket") ;
		return FALSE ;
	}

	/* Init sockets */
	memset (&mb->rSockMC, 0, sizeof (mb->rSockMC)) ;
	mb->rSockMC.sin_family = AF_INET ;
	mb->rSockMC.sin_port = htons (mb->portMC) ;
	mb->rSockMC.sin_addr.s_addr = htonl (INADDR_ANY) ;

	memset (&mb->wSockMC, 0, sizeof (mb->wSockMC)) ;
	mb->wSockMC.sin_family = AF_INET ;
	mb->wSockMC.sin_port = htons (mb->portMC) ;
	mb->wSockMC.sin_addr.s_addr = inet_addr (mb->addrMC) ;

	mb->rLenMC = sizeof (mb->rSockMC) ;
	mb->wLenMC = sizeof (mb->wSockMC) ;
	
	/* Join the specified multicast group */
	imr.imr_multiaddr.s_addr = inet_addr(mb->addrMC) ;
	imr.imr_interface.s_addr = htonl(INADDR_ANY) ;
	
	if (setsockopt (mb->rSockfdMC, IPPROTO_IP, IP_ADD_MEMBERSHIP, (void *) &imr, sizeof(struct ip_mreq)) < 0)
	{
		perror("setsockopt: IP_ADD_MEMBERSHIP") ;
		return FALSE ;
	}
	
	if (bind (mb->rSockfdMC, (struct sockaddr *)&mb->rSockMC, sizeof(mb->rSockMC)) < 0)
	{
		perror("bind") ;
		return FALSE ;
	}

	/* Set the multicast time to live */
	if (setsockopt (mb->wSockfdMC, IPPROTO_IP, IP_MULTICAST_TTL, &mb->ttlMC, sizeof(mb->ttlMC)) == -1)
	{
		perror("setsockopt: IP_MULTICAST_TTL") ;
		return FALSE ;
	}
	
	/* Unable the loopback on read socket */
	if ((setsockopt(mb->rSockfdMC, IPPROTO_IP, IP_MULTICAST_LOOP, &loop, sizeof(loop))) == -1)
	{
		perror("setsockopt: IP_MULTICAST_LOOP");
		return FALSE ;
	}
	
	/* Unable the loopback on write socket */
	if ((setsockopt(mb->wSockfdMC, IPPROTO_IP, IP_MULTICAST_LOOP, &loop, sizeof(loop))) == -1)
	{
		perror("setsockopt: IP_MULTICAST_LOOP");
		return FALSE ;
	}
	
	return TRUE ;
}

int main (int argc, char ** argv)
{
	mbridge * mb = (mbridge *) malloc (sizeof(mbridge)) ;
	
	mb->portMC = MC_PORT ;
	mb->portUC = UC_PORT ;
	
	mb->ttlMC = MC_TTL ;
	
	if (argc == 2)
	{
		if (!strcmp(argv[1], "-h"))
		{
			printf ("Usage: %s [forward_address [multicast_address]]\n\n", argv[0]) ;
			exit(0) ;
		}
		else if (!strcmp(argv[1], "-v"))
		{
			printf ("mbridge v0.2 ("__DATE__" "__TIME__")"
					" - a multicast tunnel over UDP\n\n") ;
			exit(0) ;
		}
		else
		{
			mb->addrUC = argv[1] ;
			mb->addrMC = MC_ADDR ;
		}
	}
	else if (argc == 3)
	{
		mb->addrUC = argv[1] ;
		mb->addrMC = argv[2] ;
	}
	else
	{
		mb->addrUC = UC_ADDR ;
		mb->addrMC = MC_ADDR ;
	}
	
	printf ("UDP Addr : %s - MC Addr : %s\n", mb->addrUC, mb->addrMC) ;
	
	if (!initMCSockets (mb))
	{
		free (mb) ;
		exit(1) ;
	}
	
	if (!initUCSockets (mb))
	{
		free (mb) ;
		exit(1) ;
	}
	
	if (!startUCServer (mb))
	{
		free (mb) ;
		exit (1) ;
	}
	
	forwardMCPackets (mb) ;
	
	return EXIT_SUCCESS ;
}

