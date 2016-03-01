/*
	dprocess - a distributed process library in java and C

	Copyright (C) 2008 Philippe Esling, Julien Clement and Florent Weber

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

package dprocess.dpserver;

import java.net.Socket;
import java.util.Vector;

/**
 *
 * This class implements the supervisor functionalities for a dynamic pool of threads.
 *
 * @version						1.0
 * @see							DPServer
 * @see							DPServerThread
 *
 */
public class 					DPSupervisor
{
	private Vector<Thread>		pool;
	public boolean				delete;
	public Socket				nextClient;
	private int					waiting;
	private int					actives;
	private int					minWaiting = 5;
	private int					maxWaiting = 15;
	private int					maxThreads = 100;

	/**
	 *
	 * Main constructor for the supervisor :
	 *  - allocating starting threads
	 *  - setting variables counters
	 *
	 * @param wStart : number of threads to start
	 *
	 */
	public 						DPSupervisor(int wStart)
	{
		Thread					tTmp;
		int						i;

		actives = 0;
		waiting = wStart;
		delete = false;
		pool = new Vector<Thread>();
		for (i = 0; i < wStart; i++)
		{
			tTmp = new Thread(new DPServerThread(this));
			pool.add(tTmp);
			tTmp.start();
		}

	}

	/**
	 *
	 * Accept of a new client has been made, affect a thread to it.
	 * Checks if there is enough threads left in the pool.
	 * If this is not the case, adds a new thread.
	 *
	 * @param nC : socket of the new client
	 *
	 */
	public void 				startClient(Socket nC)
	{
		Thread					tTmp;

		actives++;
		waiting--;
		synchronized(this)
		{
			nextClient = nC;
			this.notify();
			if ((waiting < minWaiting) && (waiting + actives < maxThreads))
			{
				tTmp = new Thread(new DPServerThread(this));
				pool.add(tTmp);
				tTmp.start();
			}
		}
	}

	/**
	 *
	 * End of a client.
	 * Check if there is too much threads in the pool.
	 * If this is the case, interrupt one of them.
	 *
	 */
	public void 				endClient()
	{
		actives--;
		waiting++;
		if (waiting > maxWaiting)
			synchronized(this)
			{
				delete = true;
				this.notify();
				waiting--;
			}
	}

}
