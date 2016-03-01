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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;

import dprocess.RemoteProcess;

/**
 *
 * This class implements the remote process functionalities.
 * It encapsulates the information relative to a remote process (local PID, exit status and process representation).
 * This class implements the RemoteProcess interface and is exported and send to the remote client.
 * The remote client can use directly all the functionalities thanks to the RMI implementation.
 *
 * @version							1.5
 * @see								RemoteProcess
 *
 */
public class 						RemoteProcessIpl implements RemoteProcess
{
	private	Process					localProcess;
	private byte []					executeData;
	public boolean					finished;
	private int						exitStatus;
	private int						localPID;
	private File					localExeFile;

	/**
	 *
	 * Main constructor for the remote process
	 * Saves the binary data to a file which will be launched later
	 *
	 * @param data : the binary executable data.
	 * @throws RemoteException
	 *
	 */
	public							RemoteProcessIpl(byte [] data) throws RemoteException
	{
		exitStatus = -1;
		finished = false;
		executeData = data;
	}


	/**
	 *
	 * Launches the binary executable by first writing the sent data to a temporary file.
	 * The file is set with the executable rights and then launched.
	 * The local PID of the process is then parsed directly on the output stream.
	 *
	 * @return true if successful, false otherwise
	 * @throws RemoteException
	 *
	 */
	public boolean					launchProcess() throws RemoteException
	{
		FileOutputStream 			fo;
		byte []						data = new byte[128];
		String						outputErr = "" ;
		int 						pos;

		try
		{
			localExeFile = File.createTempFile("RemoteProcess", ".dp");
			localExeFile.setWritable(true) ;
			localExeFile.setExecutable(true) ;
			fo = new FileOutputStream(localExeFile) ;
			fo.write(executeData) ;
			fo.close() ;
		}
		catch (IOException e)
		{
			System.out.println("RemoteProcessIpl::Can't write received file on disk.");
			e.printStackTrace();
			return false;
		}
		try
		{
			String[] exeArgs = {System.getProperty("user.dir")+"/../src/dprocess/dpserver/utils/showpid", localExeFile.toURI().getRawPath()};
			localProcess = Runtime.getRuntime().exec(exeArgs);
		}
		catch (IOException e)
		{
			System.out.println("RemoteProcessIpl::Can't run process / File is not a valid executable.");
			e.printStackTrace();
			return false;
		}
		try
		{
			System.out.println("* Process launched *");
			System.out.println("* File : "+localExeFile.toURI().getRawPath()+" - Length: " + executeData.length);
			if (localProcess.getErrorStream().read(data) != -1)
				outputErr = new String(data);
			pos	= outputErr.indexOf("\n");
			if (pos == -1)
			{
				System.err.println("RemoteProcessIpl::Answer malformed / Position : " + pos + " / Invalid PID");
				return false ;
			}
			localPID = Integer.parseInt(outputErr.substring(0, pos));
			System.out.println("* PID : " + localPID);
			return true;
		}
		catch (IOException e)
		{
			System.out.println("RemoteProcessIpl::Fatal problem while getting PID.");
			e.printStackTrace();
			return false;
		}
	}

	/**
	 *
	 * Waits for the end of the process
	 *
	 * @return Exit status of the process
	 * @throws RemoteException
	 *
	 */
	public int 						rwait() throws RemoteException
	{
		System.out.println("* Waiting (rwait)") ;
		try
		{
			localProcess.waitFor();
			finished = true;
		}
		catch (InterruptedException e) { e.printStackTrace(); }
		InputStream is = localProcess.getInputStream();
		byte [] data = new byte[2048];
		try
		{
			while (is.read(data) > 0)
				System.out.println(new String(data));
		}
		catch (IOException e)
		{
			System.out.println("RemoteProcessIpl::Process has been exited.");
		}
		exitStatus = localProcess.exitValue();
		localProcess.destroy();
		localExeFile.delete();
		return exitStatus;
	}

	/**
	 *
	 * Sends a POSIX signal to the process
	 *
	 * @param signal : integer value of the signal
	 * @return Exit status of the process
	 * @throws RemoteException
	 *
	 */
	public int 						rkill(int signal) throws RemoteException
	{
		System.out.println("* Kill " + localPID + " - Sig : " + signal + " (rkill)");
		try
		{
			String[] killArgs = { "kill", "-" + signal, localPID + ""};
			Runtime.getRuntime().exec(killArgs);
			return 0;
		}
		catch (IOException e)
		{
			System.err.println("RemoteProcess::Can't send kill signal.");
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 *
	 * Exits the process
	 *
	 * @param status : status of termination
	 * @return Exit status of the process
	 * @throws RemoteException
	 *
	 */
	public int 						rexit(int status) throws RemoteException
	{
		System.out.println("* Exiting with status : " + status + " (rexit)");

		finished = true;
		localExeFile.delete();
		localProcess.destroy();
		try
		{
			exitStatus = localProcess.exitValue();
		}
		catch (IllegalThreadStateException e)
		{
			String[] killArgs = { "kill", "-" + 9, localPID+"" };
			try
			{
				Runtime.getRuntime().exec(killArgs);
				exitStatus = localProcess.exitValue();
				System.out.println("RemoteProcess::The process hasn't exited : forced ! ("+exitStatus+")") ;
			}
			catch (IllegalThreadStateException e1)
			{
				System.err.println("RemoteProcess::Can't exit process");
				e1.printStackTrace();
				return -1;
			}
			catch (IOException e1)
			{
				System.err.println("RemoteProcess::Can't exit process");
				e1.printStackTrace();
				return -1;
			}
		}
		return exitStatus;
	}

	/**
	 *
	 * Checks if the process is finished
	 *
	 * @return true if finished / false otherwise
	 * @throws RemoteException
	 *
	 */
	public boolean 					isFinished() throws RemoteException
	{
		return finished;
	}

	/**
	 *
	 * Gets the unique identifier of the process
	 *
	 * @return String value of the UID.
	 * @throws RemoteException
	 *
	 */
	public String					getUid() throws RemoteException
	{
		return RemoteObject.toStub(this).toString();
	}
}