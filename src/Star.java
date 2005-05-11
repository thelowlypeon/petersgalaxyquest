/*
 * Created on Apr 13, 2005
 */
//package DrawStuff;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.util.Random;

/**
 * @author peter
 */
public class Star 
{
	Random generator = new Random();
	private double starX, starY;
	private Ellipse2D.Double someStar;
	
	public Star(double starCoordinateX, double starCoordinateY)
	{
		starX = starCoordinateX;
		starY = starCoordinateY;
		Ellipse2D.Double star = new Ellipse2D.Double(starX, starY, 1, 1);
	}
	
	public void translateStar(double translateStarX, double translateStarY)
	{
		starX += translateStarX;
		starY += translateStarY;
	}
	
	public double getStarCoordinateX()
	{
		return starX;
	}
	
	public double getStarCoordinateY()
	{
		return starY;
	}
	
	public void draw(Graphics g)
	{
		Graphics2D g2 = (Graphics2D)g;
		//for the time being, the laser is simply a circle of radius 1
		someStar = new Ellipse2D.Double(starX, starY, 1, 1);
		g2.draw(someStar);
	}
	
	public void resetCoordinateY()
	{
		starY = 500; //this is the top of the set of stars, so it restarts there
	}
}
