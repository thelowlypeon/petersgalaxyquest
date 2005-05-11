/****
 * Created on Apr 11, 2005
 */
//package DrawStuff;

/***
 * @author peter
 */
import java.awt.*;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;


public class SpaceShip 
{
	
	private Image myImage;
	private Toolkit myToolKit;
	

	/**
	 * constructs a space ship with given x and y cooordinates
	 * @param shipStartX the starting x coordinate
	 * @param shipStartY the starting y coordinate
	 */
	public SpaceShip(int shipStartX, int shipStartY, Toolkit aToolKit)
	{
		shipX = shipStartX;
		shipY = shipStartY;
		myToolKit = aToolKit;
		myImage = myToolKit.getImage("Idunno.jpg");

	}
	
	/**
	 * this method translates the ship's coordinates
	 * @param translateX the change in X axis
	 * @param translateY the change in Y axis
	 */
	public void translateShip(double translateX, double translateY) 
	{
//		Dimension screenSize = myToolKit.getScreenSize();
//		double screenWidth = screenSize.getWidth();
//		double screenHeight = screenSize.getHeight();
//		
////		if ((shipX < screenWidth / 2 && translateX > 0) || (shipX > -screenWidth / 2 && translateX < 0))
		shipX += translateX;
		
//		else if ((shipY < -50 && translateY > 0) || (shipY > (-screenHeight / 2) + 50 && translateY < 0))
		shipY += translateY;
	}
	
	/**
	 * this method sends the ship to a given point
	 * @param sendX
	 * @param sendY
	 */
	public void sendShipTo(int sendX, int sendY)
	{
		shipX = sendX;
		shipY = sendY;
	}
	
	/**
	 * this method returns the ship to the center
	 */
	public void resetShip()
	{
		shipX = 0;
		shipY = -50;
	}
	
	
	/**
	 * this method draws the ship
	 */	
	public void draw(Graphics g)
	{
		Graphics2D g2 = (Graphics2D)g;
	
		ship = new Rectangle2D.Double(shipX - (SHIP_WIDTH / 2), shipY - (SHIP_HEIGHT / 2), SHIP_WIDTH, SHIP_HEIGHT);
		
		g2.fill(ship);
	}
	
	
	/**
	 * this method returns the x coordinate of the center of the ship
	 * @return shipX
	 */
	public double getShipCoordinateX()
	{
		return shipX;
	}
	
	
	/**
	 * this method returns the y coordinate of the center of the ship
	 * @return shipX
	 */
	public double getShipCoordinateY()
	{
		return shipY;
	}
	
	/**
	 * this method returns the width of the ship (form center)
	 * @return the maximum distance from the center
	 */
	public double getShipWidth()
	{
		return SHIP_WIDTH / 2;
	}
	
	public double getShipHeight()
	{
		return SHIP_HEIGHT / 2;
	}
	
	private int shipX, shipY;
	private Rectangle2D.Double ship;
	private final double SHIP_HEIGHT = 30, SHIP_WIDTH = 10;
}
