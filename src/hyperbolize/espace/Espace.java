/*
	hyperbolize - a java implementation of the H3 algorithm

	Copyright (C) 2008 Philippe Esling

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

package hyperbolize.espace;

import javax.vecmath.*;

public class						Espace 
{
    public static final Point3d 	ORIGIN3 = new Point3d( 0.0, 0.0, 0.0 );
    public static final Point4d 	ORIGIN4 = new Point4d( 0.0, 0.0, 0.0, 1.0 );
    
    public static final Matrix3d 	IDENTITY3 = new Matrix3d( 	1.0, 0.0, 0.0,
     															0.0, 1.0, 0.0,
     															0.0, 0.0, 1.0);
    public static final Matrix4d 	IDENTITY4 = new Matrix4d( 	1.0, 0.0, 0.0, 0.0,
						     									0.0, 1.0, 0.0, 0.0,
						     									0.0, 0.0, 1.0, 0.0,
						     									0.0, 0.0, 0.0, 1.0 );
    
	public static double			distance(Point2d a, Point2d b){ return 0.0; }
	public static double			distance(Point3d a, Point3d b){ return 0.0; }
	public static double			distance(Point4d a, Point4d b){ return 0.0; }
}
