/*
	showpid

	Copyright (C) Florent Weber

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
