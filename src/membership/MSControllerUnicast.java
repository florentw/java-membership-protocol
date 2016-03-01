/*
	jmmp - a java implementation of a multicast membership protocol

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

package membership;

public abstract class 		MSControllerUnicast extends Thread implements MSController
{
	protected MSMain		infos;
	
	public 					MSControllerUnicast(MSMain i)
	{
		infos = i;
	}
	
	/**
	 * 
	 * Demarrre le pool de thread
	 * 
	 */
	public void				connect()
	{
		
	}
	
	/**.
	 * 
	 * Boucle d'ecoute infinie
	 * 
	 */
	public void				listen()
	{	
		
	}
	
	/**
	 * 
	 * Libere les ressources
	 * 
	 */
	public void				shutdown()
	{
		
	}
	
	public void				sendMessage(String multiAddr, MSMessage msg)
	{
		
	}
	
	public String			toString()
	{
		return infos.toString();
	}
}
