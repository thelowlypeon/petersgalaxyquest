/****
 * Created on Apr 11, 2005
 */
//package BasicDraw;

/***
 * @author peter
 */

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

public class Asteroid
{
	/**
	 * constructs an asteroid, based on the applet width, that is randomly placed at the top of the applet
	 * @param appletWidth the width of the appplet, used for the bounds of the asteroid placement
	 * @param anAsteroidWidth the width of the asteroid, both used to know how large the asteroid is, and again for the bounds
	 */
	public Asteroid(double x, double y, int size, double speed, boolean hit)
	{
		asteroidX = x;
		asteroidY = y;
		asteroidSpeed = speed;
		asteroidWidth = size;
		asteroidHeight = size;
		hitCounter = 0;
	}
	
	/**
	 * this method draws an asteroid (in this case, an ellipse)
	 * @param g
	 */
	public void draw(Graphics g)
	{
		Graphics2D g2 = (Graphics2D)g;
		//for the time being, the ship is simply a rectangle, centered at the given coordinates
		Ellipse2D asteroid = new Ellipse2D.Double(asteroidX - (asteroidWidth / 2), asteroidY - (asteroidWidth / 2), asteroidWidth, asteroidHeight);
		g2.fill(asteroid);
	}
	
	public void increaseCounter()
	{
		hitCounter++;
	}
	
	public int getCounter()
	{
		return hitCounter;
	}
	
	public boolean isItHit()
	{
		return hit;
	}
	
	public void blowUp()
	{
		hit = true;
	}
	
	public void translateAsteroid(double translateAsteroidX, double translateAsteroidY)
	{
		asteroidX += translateAsteroidX;
		asteroidY += translateAsteroidY;
	}
	
	public void returnAsteroid() //returns asteroid to birth place
	{
		asteroidX = 0;
		asteroidY = 100;
	}
	
	public double getAsteroidCoordinateX()
	{
		return asteroidX;
	}
	
	public double getAsteroidCoordinateY()
	{
		return asteroidY;
	}
	
	/**
	 * this method returns the maximum distance from the center
	 * @return the max distance from center
	 */
	public double getAsteroidWidth()
	{
		return asteroidWidth / 2;
	}
	
	public double getAsteroidHeight()
	{
		return asteroidHeight / 2;
	}
	
	public double getAsteroidSpeed()
	{
		return asteroidSpeed;
	}
	
	private double asteroidX, asteroidY, asteroidWidth, asteroidHeight, asteroidSpeed;
	private boolean hit;
	public int hitCounter;
	//private double asteroidSpeed;  this will be here once the moveAsteroid method is complete
}
