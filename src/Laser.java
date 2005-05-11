/****
 * Created on Apr 11, 2005
 */
//package DrawStuff;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

/***
 * @author peter
 */
public class Laser 
{
	private double laserCoordinateX;
	private double laserCoordinateY;
	private Ellipse2D.Double bullet;
	private double laserWidth, laserHeight;

	
	public Laser(double laserX, double laserY, double width, double height)
	{
		laserCoordinateX = laserX;
		laserCoordinateY = laserY;
		laserWidth = width;
		laserHeight = height;
	}
	
	
	
	public void draw(Graphics g)
	{
		Graphics2D g2 = (Graphics2D)g;
		//for the time being, the laser is simply a circle of radius 1
		bullet = new Ellipse2D.Double(laserCoordinateX, laserCoordinateY, laserWidth, laserHeight);
		g2.setColor(Color.MAGENTA);
		g2.fill(bullet);
		g2.setColor(Color.LIGHT_GRAY);
	}
	
	public void translateLaser(double translateLaserX, double translateLaserY)
	{
		laserCoordinateX += translateLaserX;
		laserCoordinateY += translateLaserY;
	}
	
	public double getLaserCoordinateX()
	{
		return laserCoordinateX;
	}
	
	public double getLaserCoordinateY()
	{
		return laserCoordinateY;
	}
	
	public double getLaserWidth()
	{
		return laserWidth;
	}
	
	public double getLaserHeight()
	{
		return laserHeight;
	}
	
}
