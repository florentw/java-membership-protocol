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
 * This class encapsulates the data for a local API message.
 *
 * @version						1.0
 *
 */
public class 					APIMessage
{
	public String				commandName ;
	public int					numArgs ;
	public String []			args ;
	public byte []				execute;

	/**
	 *
	 * Main constructor for an API message.
	 *
	 * @param commandName	: desired operation (REXEC/RKILL/RWAIT/REXIT)
	 * @param numArgs		: number of supplied arguments
	 * @param args			: supplied arguments
	 *
	 */
	public 						APIMessage(String commandName, int numArgs, String[] args, byte [] exec)
	{
		this.commandName		= commandName ;
		this.numArgs			= numArgs ;
		this.args				= args ;
		this.execute			= exec;
	}
}
