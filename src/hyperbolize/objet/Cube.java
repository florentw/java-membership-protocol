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

package hyperbolize.objet;

import hyperbolize.espace.Espace;
import hyperbolize.espace.EspaceHyperbolique;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.vecmath.*;

public class 			Cube implements Objet
{
	private double		rotX, rotY, rotZ, size;
	private Point3d		p3d;
	private Point4d		p4d;
	private Point3d		pPole;
	private Point3d		color;
	private double		alpha;
	private boolean		visible;
	
	public	 			Cube(double s)
	{
		rotX = 0.0;
		rotY = 0.0;
		rotZ = 0.0;
		alpha = 0.2;
		size = s;
		p3d = new Point3d();
		p4d = new Point4d();
		pPole = new Point3d();
		color = new Point3d(1.0, 0.0, 0.0);
		visible = true;
	}
	
	public	 			Cube(double s, Point3d col, double alfa)
	{
		rotX = 0.0;
		rotY = 0.0;
		rotZ = 0.0;
		alpha = alfa;
		size = s;
		p3d = new Point3d();
		p4d = new Point4d();
		pPole = new Point3d();
		color = new Point3d(col);
		visible = true;
	}
	
	public				Cube(Cube c)
	{
		rotX = c.getRotX();
		rotY = c.getRotY();
		rotZ = c.getRotZ();
		alpha = c.getAlpha();
		size = c.getSize();
		p3d = new Point3d(c.getCoord3d());
		p4d = new Point4d(c.getCoord4d());
		pPole = new Point3d(c.getCoordPolaire());
		color = new Point3d(c.getColor());
	}
	
	public	 			Cube(double s, Point3d p1, Point4d p2)
	{
		size = s;
		p3d = p1;
		p4d = p2;
	}
	
	public boolean		isDead()
	{
		return false;
	}
	
	public void			setAlpha(double a)
	{
		alpha = a;
	}
	
	public double		getAlpha()
	{
		return alpha;
	}
	
	public Point3d		getCoord3d()
	{
		return p3d;
	}
	
	public Point4d		getCoord4d()
	{
		return p4d;
	}
	
	public Point3d		getCoordPolaire()
	{
		return pPole;
	}
	
	public Point3d		getColor()
	{
		return color;
	}
	
	public void			setColor(Point3d c)
	{
		color = c;
	}
	
	public void			setCoord4d(Point4d p)
	{
		p4d = p;
		p3d = new Point3d(p.x / p.w, p.y / p.w, p.z / p.w);
	}
	
	public void			setCoordPolaire(Point3d p)
	{
		pPole = p;
	}

	public void 		applyTransform(Matrix4d t)
	{
		t.transform(p4d);
		p3d = new Point3d(p4d.x / p4d.w, p4d.y / p4d.w, p4d.z / p4d.w);
	}

	public void 		draw(GLAutoDrawable d)
	{
		GL				gl = d.getGL();
		
		if (!(visible))
			return;
		gl.glPushMatrix();
		//gl.glDisable(GL.GL_DEPTH_TEST);
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA,GL.GL_ONE);	
		gl.glTranslated(p3d.x, p3d.y, p3d.z);
		gl.glRotated(rotX * (360 / (2 * Math.PI)), 6.0, 0.0, 0.0);
		gl.glRotated(rotY * (360 / (2 * Math.PI)), 0.0, 6.0, 0.0);
		gl.glRotated(rotZ * (360 / (2 * Math.PI)), 0.0, 0.0, 6.0);
		double scaler = EspaceHyperbolique.distance(p3d, Espace.ORIGIN3);
		scaler = (scaler >= 1.8 ? 1.8 : scaler);
		gl.glScaled(0.01 + (size * (1.8 - scaler)) * 0.1, 0.01 + (size * (1.8 - scaler)) * 0.1, 0.01 + (size * (1.8 - scaler)) * 0.1);
		gl.glColor4d(color.x, color.y, color.z, alpha);
		gl.glBegin(GL.GL_QUADS);
		// Front Face
		gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f,  1.0f);	// Bottom Left Of The Texture and Quad
		gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( 1.0f, -1.0f,  1.0f);	// Bottom Right Of The Texture and Quad
		gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( 1.0f,  1.0f,  1.0f);	// Top Right Of The Texture and Quad
		gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-1.0f,  1.0f,  1.0f);	// Top Left Of The Texture and Quad
		// Back Face
		gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f, -1.0f);	// Bottom Right Of The Texture and Quad
		gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-1.0f,  1.0f, -1.0f);	// Top Right Of The Texture and Quad
		gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f( 1.0f,  1.0f, -1.0f);	// Top Left Of The Texture and Quad
		gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( 1.0f, -1.0f, -1.0f);	// Bottom Left Of The Texture and Quad
		// Top Face
		gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-1.0f,  1.0f, -1.0f);	// Top Left Of The Texture and Quad
		gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-1.0f,  1.0f,  1.0f);	// Bottom Left Of The Texture and Quad
		gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( 1.0f,  1.0f,  1.0f);	// Bottom Right Of The Texture and Quad
		gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( 1.0f,  1.0f, -1.0f);	// Top Right Of The Texture and Quad
		// Bottom Face
		gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-1.0f, -1.0f, -1.0f);	// Top Right Of The Texture and Quad
		gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f( 1.0f, -1.0f, -1.0f);	// Top Left Of The Texture and Quad
		gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( 1.0f, -1.0f,  1.0f);	// Bottom Left Of The Texture and Quad
		gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f,  1.0f);	// Bottom Right Of The Texture and Quad
		// Right face
		gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( 1.0f, -1.0f, -1.0f);	// Bottom Right Of The Texture and Quad
		gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( 1.0f,  1.0f, -1.0f);	// Top Right Of The Texture and Quad
		gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f( 1.0f,  1.0f,  1.0f);	// Top Left Of The Texture and Quad
		gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( 1.0f, -1.0f,  1.0f);	// Bottom Left Of The Texture and Quad
		// Left Face
		gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f, -1.0f);	// Bottom Left Of The Texture and Quad
		gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f,  1.0f);	// Bottom Right Of The Texture and Quad
		gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-1.0f,  1.0f,  1.0f);	// Top Right Of The Texture and Quad
		gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-1.0f,  1.0f, -1.0f);	// Top Left Of The Texture and Quad
		gl.glEnd();
		gl.glColor4d(0.2, 0.2, 0.2, alpha * 5);
		gl.glBegin(GL.GL_LINES);
		// Front Face
		gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f,  1.0f);	// Bottom Left Of The Texture and Quad
		gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( 1.0f, -1.0f,  1.0f);	// Bottom Right Of The Texture and Quad
		gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( 1.0f,  1.0f,  1.0f);	// Top Right Of The Texture and Quad
		gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-1.0f,  1.0f,  1.0f);	// Top Left Of The Texture and Quad
		// Back Face
		gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f, -1.0f);	// Bottom Right Of The Texture and Quad
		gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-1.0f,  1.0f, -1.0f);	// Top Right Of The Texture and Quad
		gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f( 1.0f,  1.0f, -1.0f);	// Top Left Of The Texture and Quad
		gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( 1.0f, -1.0f, -1.0f);	// Bottom Left Of The Texture and Quad
		// Top Face
		gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-1.0f,  1.0f, -1.0f);	// Top Left Of The Texture and Quad
		gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-1.0f,  1.0f,  1.0f);	// Bottom Left Of The Texture and Quad
		gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( 1.0f,  1.0f,  1.0f);	// Bottom Right Of The Texture and Quad
		gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( 1.0f,  1.0f, -1.0f);	// Top Right Of The Texture and Quad
		// Bottom Face
		gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-1.0f, -1.0f, -1.0f);	// Top Right Of The Texture and Quad
		gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f( 1.0f, -1.0f, -1.0f);	// Top Left Of The Texture and Quad
		gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( 1.0f, -1.0f,  1.0f);	// Bottom Left Of The Texture and Quad
		gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f,  1.0f);	// Bottom Right Of The Texture and Quad
		// Right face
		gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( 1.0f, -1.0f, -1.0f);	// Bottom Right Of The Texture and Quad
		gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( 1.0f,  1.0f, -1.0f);	// Top Right Of The Texture and Quad
		gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f( 1.0f,  1.0f,  1.0f);	// Top Left Of The Texture and Quad
		gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( 1.0f, -1.0f,  1.0f);	// Bottom Left Of The Texture and Quad
		// Left Face
		gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f, -1.0f);	// Bottom Left Of The Texture and Quad
		gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f,  1.0f);	// Bottom Right Of The Texture and Quad
		gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-1.0f,  1.0f,  1.0f);	// Top Right Of The Texture and Quad
		gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-1.0f,  1.0f, -1.0f);	// Top Left Of The Texture and Quad
		gl.glEnd();
		gl.glDisable(GL.GL_BLEND);
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glPopMatrix();
	}

	public void 		setRotation(double x, double y, double z)
	{
		rotX += x;
		rotY += y;
		rotZ += z;
	}
	
	public void			setSize(double s)
	{
		size = s;
	}
	
	public double		getRotX()
	{
		return rotX;
	}
	
	public double		getRotY()
	{
		return rotY;
	}
	
	public double		getRotZ()
	{
		return rotZ;
	}
	
	public double		getSize()
	{
		return size;
	}

	public boolean isVisible()
	{
		return visible;
	}

	public void setVisible(boolean v)
	{
		visible = v;	
	}
}
