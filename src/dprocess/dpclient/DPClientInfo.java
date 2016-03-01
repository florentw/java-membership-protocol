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

package dprocess.dpclient;

/**
 *
 * This class encapsulates all information about a process ran through a DPClient.
 * It contains the server name, the process name and the local PID of this remote process.
 * This class is mainly used for GUI purposes.
 *
 * @version				1.0
 * @see					hypership.Hypership
 *
 */
public class 			DPClientInfo
{
	private String		serverName ;
	private int			localProcessId ;
	private String 		processName;

	/**
	 *
	 * Main constructor for the DPClientInfo class.
	 *
	 * @param name : name of the server
	 * @param pid : local PID
	 *
	 */
	public				DPClientInfo(String name, int pid)
	{
		serverName = name ;
		localProcessId = pid ;
	}

	/**
	 *
	 * Main constructor for the DPClientInfo class.
	 *
	 * @param name : name of the server
	 * @param pid : local PID
	 * @param pname : name of the process
	 *
	 */
	public 				DPClientInfo(String name, int pid, String pname)
	{
		serverName = name ;
		localProcessId = pid ;
		processName = pname;
	}

	/**
	 *
	 * Gets the server name where the process is running
	 *
	 * @return a string containing the server name
	 *
	 */
	public String 		getServerName()
	{
		return serverName ;
	}

	/**
	 *
	 * Gets the local PID of the process
	 *
	 * @return local PID of the process
	 *
	 */
	public int 			getLocalPID()
	{
		return localProcessId ;
	}

	/**
	 *
	 * Gets the name of the process
	 *
	 * @return name of the process
	 *
	 */
	public String 		getProcessName()
	{
		return processName;
	}
}
