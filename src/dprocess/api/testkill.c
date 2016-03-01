#include <stdio.h>
#include <signal.h>
#include <unistd.h>

void handler(int s)
{
	FILE * fp = fopen ("/tmp/testkill.log", "a+") ;
	fprintf(fp, "Received signal %d\n", s) ;
	fclose (fp) ;
}

int main(void)
{
	int s ;
	struct sigaction act ;
	
	FILE * fp = fopen ("/tmp/testkill.log", "a+") ;
	
	act.sa_handler = handler ;
	sigfillset (&act.sa_mask) ;
	act.sa_flags = 0 ;
	
	for (s = 1; s < NSIG; s++)
		if (sigaction(s, &act, NULL) == -1)
			fprintf (fp, "Cannot handle signal : %d\n", s) ;

	fclose (fp) ;
	
	while (1)
		pause() ;
}
