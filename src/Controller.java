/****
 * Created on Apr 12, 2005
 */
//package DrawStuff;

/***
 * @author peter
 */

import java.awt.Dimension;
//import  sun.audio.*;
import java.awt.Toolkit;
import java.awt.*;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.Font;
import javax.swing.Timer;
import java.util.Random;
import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.awt.geom.Rectangle2D;
import java.awt.Point;
import java.awt.Image;
import java.awt.Cursor;
import javax.swing.JPanel;
import java.awt.font.FontRenderContext;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Controller extends JPanel 
{
	private Toolkit myToolKit = Toolkit.getDefaultToolkit();
	private boolean startYet = false, readyToStart = false, viewScores = false, howToPlay = false, fromScoresToGame = false, musicOn = true;
	private boolean shipDestroyed = false,  lasersOkay = true, baseDestroyed = false, paused = false, drawRectangleCursor = false, useCircularAsteroids = false;
	private int asteroidsThatHitBase, points, asteroidHits, lasersFired, asteroidRate, asteroidWidth, laserSize, laserSpeed, asteroidSize, starRate, level, lasersMissed;
	private int lives, accuracyOnAsteroidHits, laserBurnoutCounter = 0;
	private int startGameButtonX, startGameButtonY, startGameButtonWidth, startGameButtonHeight = 35;
	private int viewScoresButtonX, viewScoresButtonY, viewScoresButtonWidth, viewScoreButtonHeight = 35;
	private int quitGameButtonX, quitGameButtonY, quitGameButtonWidth, quitGameButtonHeight = 35;
	private int howToPlayButtonX, howToPlayButtonY, howToPlayButtonWidth, howToPlayButtonHeight = 35;
	private int soundOnOffButtonWidth,soundOnOffButtonHeight = 35, soundOnOffButtonX, soundOnOffButtonY;
	private int musicCounter = 0, warningCounter = 0;
	int screenSizeX = (int)(myToolKit.getScreenSize()).getWidth();
	int screenSizeY = (int)(myToolKit.getScreenSize()).getHeight() - 50; //estimating that menu bar and title bar sum to 50 pixels
	private double asteroidSpeed, rateOfLasersFired;
	SpaceShip petersShip;
	ArrayList asteroidList = new ArrayList();
	ArrayList laserList = new ArrayList();
	ArrayList starArray = new ArrayList();
	Random generator = new Random();
	Time grandTime = new Time();
	Timer grandTimer = new Timer(50, grandTime);
	private String gunStatus;
	private Cursor cursor;
	private File f = new File("GalaxyQuestMultimedia/HighScoresList.dat");
	HighScoreList myHighScoreList = null;
	Image cursorImage;
	Image asteroidImage;
	Image blownUpAsteroidImage;
	String lastNameEntered = null;
	//AudioStream laserFiredSound = null, backgroundMusic = null, asteroidSound = null, youLoseMusic = null, warningSound = null;
	
	public Controller()
	{
		/**
		 * imports the highScore list and makes a new one if it is nonexistent
		 */
		if (f.exists())
		{
			System.out.println("high score list exists");
			ObjectInputStream in = null;
			try
			{
				in = new ObjectInputStream(new FileInputStream("GalaxyQuestMultimedia/HighScoresList.dat"));
				System.out.println("It has read the high score list");
				myHighScoreList = (HighScoreList)in.readObject();
				System.out.println("minimum score = " + myHighScoreList.getMinimumScore());
				in.close();
			}
			catch (IOException e2)
			{
				e2.printStackTrace();
			}
			catch (ClassNotFoundException e3)
			{
				e3.printStackTrace();
			}
		}

		else 
		{
			System.out.println("high score list doesn't exist");	
			myHighScoreList = new HighScoreList();
			for (int i = 0; i < 10; i++)
			{
				HighScore thisHighScore = new HighScore("Peter is cool", 0);
				myHighScoreList.add(thisHighScore);
			}
		}


		
		setFocusable(true);
		resetEverything();
		laserList.add(new Laser(0, -10000, 0, 0));//this makes the animation more fluid
		petersShip = new SpaceShip(0, -50, myToolKit);
		addKeyListener(new shipLaserPauseKeyListener());
		addMouseListener(new shipLaserMouseInputListener());
		addMouseMotionListener(new shipLaserMouseInputListener());
		grandTimer.start(); //starts the grand timer
		this.setPreferredSize(new Dimension(screenSizeX, screenSizeY));
		cursorImage = myToolKit.getImage("GalaxyQuestMultimedia/spaceshipimage.gif");
		if (cursorImage != null) cursor = myToolKit.createCustomCursor(cursorImage, new Point(21, 25), "cImage");
		else drawRectangleCursor = true;
		setCursor(cursor);
		setBackground(Color.BLACK);
		/**
		 * this makes the several hundred stars that move in the background
		 */
		for (int i = 0; i < 100; i++)
		{
			double starX = generator.nextInt(1800) - 900;
			double starY = generator.nextInt(1000) - 500;
			Star thisStar = new Star(starX, starY);
			starArray.add(thisStar);
		}	
		try
		{
			asteroidImage = myToolKit.getImage("GalaxyQuestMultimedia/asteroidimage.gif");
			if(asteroidImage != null)
			{
				System.out.println("Astreroid image has been loaded");
				useCircularAsteroids = false;
				MediaTracker mediaTracker = new MediaTracker(new Container());
				mediaTracker.addImage(asteroidImage, 0);
				mediaTracker.waitForID(0);
			}
		}
		catch (Exception e){System.out.println("couldn't load asteroid image");}
		
		try
		{
			blownUpAsteroidImage = myToolKit.getImage("GalaxyQuestMultimedia/blownUpAsteroidImage.gif");
			if(blownUpAsteroidImage != null)
			{
				System.out.println("Blown up asteroid image has been loaded");
				MediaTracker mediaTracker = new MediaTracker(new Container());
				mediaTracker.addImage(asteroidImage, 0);
				mediaTracker.waitForID(0);
			}
		}
		catch (Exception e){System.out.println("couldn't load blown up image");}
		
		if (asteroidImage == null){useCircularAsteroids = true; System.out.println("no asteroid image");}
	}
	
	public void resetEverything()
	{		
		asteroidList.clear();
		asteroidsThatHitBase = 0;
		lasersMissed = 0;
		points = 0;
		lives = 3;
		laserList.clear();
		startYet = false;
		readyToStart = false;
		shipDestroyed = false;
		lasersOkay = true;
		baseDestroyed = false;
		paused = false;
		fromScoresToGame = false;
		starRate = 10;
		laserList.add(new Laser(0, -10000, 0, 0));//this makes the animation more fluid
		setCursor(cursor);
		rateOfLasersFired = 0;
	}
	
	public void newHighScore()
	{
		System.out.println("You did better than the worst on the list");
		String input = JOptionPane.showInputDialog("What is your name?");
		String yourName = input;
		HighScore thisHighScore = new HighScore(yourName, points * accuracyOnAsteroidHits / 100);
		myHighScoreList.add(thisHighScore);
		saveThisFile();
		resetEverything();
		viewScores = true;
		lastNameEntered = yourName;
	}
	
	public void saveThisFile()
	{
		try
		{
			System.out.println("File is written");
			ObjectOutputStream out = new ObjectOutputStream (new FileOutputStream(f));
			out.writeObject(myHighScoreList);
			out.close();
		}
		catch (IOException e){System.out.println(e.getMessage());}
	}
	
	/**
	 * this class contains the actions performed by the timer
	 */
	class Time implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			if(warningCounter >= 22) warningCounter = 0;
			if(rateOfLasersFired >= 500 && warningCounter == 0)
			{
				/*
				if(musicOn == true)
				{
					try
					{
						InputStream warningIn = new FileInputStream("GalaxyQuestMultimedia/warningSound.au");
						warningSound = new AudioStream(warningIn);        
					}
					catch (FileNotFoundException e)
					{
						System.out.println("No file");
					}
					catch (IOException e)
					{
						System.out.println("IOException");
					}
					
					AudioPlayer.player.start(warningSound);  
				}
				*/
			}

			if(asteroidsThatHitBase >= 4500 && warningCounter == 0)
			{
				/*
				if(musicOn == true)
				{
					try
					{
						InputStream warningIn = new FileInputStream("GalaxyQuestMultimedia/warningSound.au");
						warningSound = new AudioStream(warningIn);        
					}
					catch (FileNotFoundException e)
					{
						System.out.println("No file");
					}
					catch (IOException e)
					{
						System.out.println("IOException");
					}
					
					AudioPlayer.player.start(warningSound);  
				}
				*/
			}
			warningCounter++;
			
			/*
			if(musicCounter >= 480) musicCounter = 0; //restarts the music
			
			if (musicOn == true && musicCounter == 0)
			{
				AudioPlayer.player.stop(backgroundMusic);
				try
				{
					InputStream backgroundIn = new FileInputStream("GalaxyQuestMultimedia/backgroundMusic.au");
					backgroundMusic = new AudioStream(backgroundIn);        
				}
				catch (FileNotFoundException e)
				{
					System.out.println("No file");
				}
				catch (IOException e)
				{
					System.out.println("IOException");
				}
				
				AudioPlayer.player.start(backgroundMusic);  
			}
			
			if(musicOn == false)  AudioPlayer.player.stop(backgroundMusic);
			musicCounter++;
			*/
			
			setCursor(cursor);
			
			accuracyOnAsteroidHits = (int) (100 * ((double)asteroidHits / ((double)lasersMissed + asteroidHits)));
			
			if (asteroidsThatHitBase > 0) asteroidsThatHitBase--;
			
			setCursor(cursor);
			
			if (rateOfLasersFired > 0 && lasersOkay == true) rateOfLasersFired--;
			
			if (lasersOkay == false) laserBurnoutCounter++;
			if (lasersOkay == false && laserBurnoutCounter > 50)
			{
				lasersOkay = true;
				rateOfLasersFired = 450;
				laserBurnoutCounter = 0;
			}
			
			/**
			 * this is what changes depending on the points accumulated
			 */
			if(points < 1000)
			{
				level = 1;
				asteroidRate = 3;
				asteroidSize = 22;
				laserSize = 5;
				laserSpeed = 5;
				starRate = 10;
			}
			else if (points < 2000)
			{
				level = 2;
				asteroidRate = 5;
				asteroidSize = 17;
				laserSize = 6;
				laserSpeed = 9;
				starRate = 15;
			}
			else if (points < 3000)
			{
				level = 3;
				asteroidRate = 7;
				asteroidSize = 12;
				laserSize = 7;
				laserSpeed = 14;
				starRate = 22;
			}
			else if (points >=3000)
			{
				level = 4;
				asteroidRate = 9;
				asteroidSize = 7;
				laserSize = 7;
				laserSpeed = 18;
				starRate = 30;
			}
			
			/**
			 * this produces 1 asteroid for every ___ frames
			 */
			int testRandom = generator.nextInt(100);
			if (testRandom < asteroidRate && startYet == true) //won't start producing asteroids until told to do so (boolean startYet)
			{
				System.out.println("Make a new asteroid");
				double asteroidStartX = generator.nextDouble() * getWidth() - getWidth() / 2; //random x placement
				double asteroidSpeed = -(generator.nextDouble() * 6 + 3); //random speed
				int actualAsteroidSize = (int)(generator.nextDouble() * asteroidSize + 20 + asteroidSize * 1.3);
				System.out.println("new asteroid position: " + asteroidStartX);
				Asteroid testAsteroid = new Asteroid(asteroidStartX, getHeight() / 2 + 20, actualAsteroidSize, asteroidSpeed, false); //constructs the asteroid
				asteroidList.add(testAsteroid); //adds asteroid to array list
			}
		
			
			/**
			 * this serves as the menu.  you must shoot either "Start Game" or "View High Scores"
			 * it is only functional while the game has not yet started (which is declared when a laser hits "Start Game")
			 */
			if (startYet == false)
			{
				for (int k = 0; k < laserList.size(); k++)  //k sorts through all bullets
				{
					double someLaserX = ((Laser)laserList.get(k)).getLaserCoordinateX();
					double someLaserY = ((Laser)laserList.get(k)).getLaserCoordinateY();
					double someLaserWidth = ((Laser)laserList.get(k)).getLaserWidth();
					double someLaserHeight = ((Laser)laserList.get(k)).getLaserHeight();
					
					if(((	howToPlayButtonX < someLaserX - someLaserWidth && howToPlayButtonX + howToPlayButtonWidth > someLaserX - someLaserWidth) ||
						(	howToPlayButtonX < someLaserX && howToPlayButtonX + howToPlayButtonWidth > someLaserX) ||
						(	howToPlayButtonX < someLaserX + someLaserWidth && howToPlayButtonX + howToPlayButtonWidth > someLaserX + someLaserWidth)) 
							
							&&
							
						((	howToPlayButtonY - howToPlayButtonHeight < someLaserY - someLaserHeight && howToPlayButtonY > someLaserY - someLaserHeight) ||
						(	howToPlayButtonY - howToPlayButtonHeight < someLaserY && howToPlayButtonY > someLaserY) ||
						(	howToPlayButtonY - howToPlayButtonHeight < someLaserY + someLaserHeight && howToPlayButtonY > someLaserY + someLaserHeight))
							&& viewScores == false && howToPlay == false && readyToStart == false)
						{
							laserList.clear();
							howToPlay = true;
							System.out.println("Show how to play");
						}
					
					if(((		soundOnOffButtonX < someLaserX - someLaserWidth && soundOnOffButtonX + soundOnOffButtonWidth > someLaserX - someLaserWidth) ||
							(	soundOnOffButtonX < someLaserX && soundOnOffButtonX + soundOnOffButtonWidth > someLaserX) ||
							(	soundOnOffButtonX < someLaserX + someLaserWidth && soundOnOffButtonX + soundOnOffButtonWidth > someLaserX + someLaserWidth)) 
								
								&&
								
							((	soundOnOffButtonY - soundOnOffButtonHeight < someLaserY - someLaserHeight && soundOnOffButtonY > someLaserY - someLaserHeight) ||
							(	soundOnOffButtonY - soundOnOffButtonHeight < someLaserY && soundOnOffButtonY > someLaserY) ||
							(	soundOnOffButtonY - soundOnOffButtonHeight < someLaserY + someLaserHeight && soundOnOffButtonY > someLaserY + someLaserHeight))
								&& viewScores == false && howToPlay == false && readyToStart == false)
							{
								laserList.clear();
								musicOn = !musicOn;
								musicCounter = 0;
								System.out.println("Sound preference has changed");
							}
					
					if(((	quitGameButtonX < someLaserX - someLaserWidth && quitGameButtonX + quitGameButtonWidth > someLaserX - someLaserWidth) ||
						(	quitGameButtonX < someLaserX && quitGameButtonX + quitGameButtonWidth > someLaserX) ||
						(	quitGameButtonX < someLaserX + someLaserWidth && quitGameButtonX + quitGameButtonWidth > someLaserX + someLaserWidth)) 
							
							&&
							
						((	quitGameButtonY - quitGameButtonHeight < someLaserY - someLaserHeight && quitGameButtonY > someLaserY - someLaserHeight) ||
						(	quitGameButtonY - quitGameButtonHeight < someLaserY && quitGameButtonY > someLaserY) ||
						(	quitGameButtonY - quitGameButtonHeight < someLaserY + someLaserHeight && quitGameButtonY > someLaserY + someLaserHeight))
							&& viewScores == false && howToPlay == false && readyToStart == false)
						{
							System.out.println("Quitting time");
							System.exit(0);
						}
					
					if(((	startGameButtonX < someLaserX - someLaserWidth && startGameButtonX + startGameButtonWidth > someLaserX - someLaserWidth) ||
						(	startGameButtonX < someLaserX && startGameButtonX + startGameButtonWidth > someLaserX) ||
						(	startGameButtonX < someLaserX + someLaserWidth && startGameButtonX + startGameButtonWidth > someLaserX + someLaserWidth)) 
							
							&&
							
						((	startGameButtonY - startGameButtonHeight < someLaserY - someLaserHeight && startGameButtonY > someLaserY - someLaserHeight) ||
						(	startGameButtonY - startGameButtonHeight < someLaserY && startGameButtonY > someLaserY) ||
						(	startGameButtonY - startGameButtonHeight < someLaserY + someLaserHeight && startGameButtonY > someLaserY + someLaserHeight))
							&& viewScores == false && howToPlay == false && readyToStart == false)
						{
							resetEverything();
							readyToStart = true;
							System.out.println("Ready to start");
						}
					
					if(((	viewScoresButtonX < someLaserX - someLaserWidth && viewScoresButtonX + viewScoresButtonWidth > someLaserX - someLaserWidth) ||
						(	viewScoresButtonX < someLaserX && viewScoresButtonX + viewScoresButtonWidth > someLaserX) ||
						(	viewScoresButtonX < someLaserX + someLaserWidth && viewScoresButtonX + viewScoresButtonWidth > someLaserX + someLaserWidth)) 
								
							&&
								
						((	viewScoresButtonY - viewScoreButtonHeight < someLaserY - someLaserHeight && viewScoresButtonY > someLaserY - someLaserHeight) ||
						(	viewScoresButtonY - viewScoreButtonHeight < someLaserY && viewScoresButtonY > someLaserY) ||
						(	viewScoresButtonY - viewScoreButtonHeight < someLaserY + someLaserHeight && viewScoresButtonY > someLaserY + someLaserHeight))
							&& viewScores == false && howToPlay == false && readyToStart == false)
						{
							laserList.clear();
							viewScores = true;
							System.out.println("Ready to view scores");		
						}
					if (someLaserY > getHeight()/2 - 5) fromScoresToGame = true;
				}
			}
			
		
			/**
			 * animates the asteroids and adds 5 points (and erases them) if they pass the screen entirely
			 * ends the game and displays a loser message if ship contacts any asteroid
			 */
			for (int i = 0; i < asteroidList.size(); i++)
			{
				Asteroid anAsteroid = (Asteroid)asteroidList.get(i);
				if (anAsteroid.getAsteroidCoordinateY() > - getHeight() / 2 && anAsteroid.isItHit() == false) anAsteroid.translateAsteroid(0, anAsteroid.getAsteroidSpeed());
				if (anAsteroid.getAsteroidCoordinateY() <= - getHeight() / 2)
				{
					asteroidsThatHitBase += 1000;
					asteroidList.remove(i);
				}
				
				//these are the bounds for when the ship hits an asteroid
				double shipX = petersShip.getShipCoordinateX();
				double shipY = petersShip.getShipCoordinateY();
				double shipWidth = 40; //this is the width of the cursor
				double shipHeight = 50; //this is the height of the cursor
				double asteroidX = anAsteroid.getAsteroidCoordinateX();
				double asteroidY = anAsteroid.getAsteroidCoordinateY();
				double asteroidWidth = anAsteroid.getAsteroidWidth();
				double asteroidHeight = anAsteroid.getAsteroidHeight();
				
				if (asteroidsThatHitBase >= 5000) //you die if three asteroids hit your base
				{
					baseDestroyed = true;
					lives--;
					System.out.println("Your base has been destroyed by asteroids");
					repaint();
					if (lives >= 0) 
					{
						/*
						AudioPlayer.player.stop(backgroundMusic);
						if(musicOn == true)
						{
							try
							{
								InputStream asteroidMusicIn = new FileInputStream("GalaxyQuestMultimedia/asteroidExplosion.au");
								asteroidSound = new AudioStream(asteroidMusicIn);        
							}
							catch (FileNotFoundException e)
							{
								System.out.println("No file");
							}
							catch (IOException e)
							{
								System.out.println("IOException");
							}
							
							AudioPlayer.player.start(asteroidSound);  
						}
						*/
						JOptionPane.showMessageDialog(null, "Your base has been destroyed by asteroids");
						musicCounter = 0;
						petersShip.resetShip();
						asteroidList.clear();
						asteroidsThatHitBase = 0;
						rateOfLasersFired = 0;
					}
					else 
					{ 
						/*
						if(musicOn == true)
						{
							AudioPlayer.player.stop(backgroundMusic);  
							try
							{
								InputStream youLoseIn = new FileInputStream("GalaxyQuestMultimedia/youLoseMusic.au");
								youLoseMusic = new AudioStream(youLoseIn);        
							}
							catch (FileNotFoundException e)
							{
								System.out.println("No file");
							}
							catch (IOException e)
							{
								System.out.println("IOException");
							}
							AudioPlayer.player.start(youLoseMusic);  
						}
						*/
						JOptionPane.showMessageDialog(null, "Game Over");
						JOptionPane.showMessageDialog(null, "Accuracy: " + accuracyOnAsteroidHits + "%");
						musicCounter = 0;
						if (points >= myHighScoreList.getMinimumScore())
						{
							newHighScore();
							resetEverything();
							viewScores = true;
						}
						else {resetEverything(); viewScores = true;}
					}
				}
				
				if (((	asteroidX - asteroidWidth < shipX + shipWidth / 4 && asteroidX + asteroidWidth > shipX + shipWidth / 4) ||
						
					(	asteroidX - asteroidWidth < shipX - shipWidth / 4 && asteroidX + asteroidWidth > shipX - + shipWidth / 4)) 
						
						&&
						
					((	asteroidY - asteroidHeight < shipY - shipHeight/2 && asteroidY + asteroidHeight > shipY - shipHeight/2) ||
						
					(	asteroidY - asteroidHeight < shipY + shipHeight/3 && asteroidY + asteroidHeight > shipY + shipHeight/3)))
				{
					shipDestroyed = true;
					lives--;
					System.out.println("HIT!");
					if (lives >= 0) 
					{
						/*
						if(musicOn == true)
						{
							try
							{
								InputStream asteroidMusicIn = new FileInputStream("GalaxyQuestMultimedia/asteroidExplosion.au");
								asteroidSound = new AudioStream(asteroidMusicIn);        
							}
							catch (FileNotFoundException e)
							{
								System.out.println("No file");
							}
							catch (IOException e)
							{
								System.out.println("IOException");
							}
							
							AudioPlayer.player.start(asteroidSound);  
						}
						*/
						JOptionPane.showMessageDialog(null, "Your ship has been destroyed by asteroids");
						petersShip.resetShip();
						asteroidList.clear();
						asteroidsThatHitBase = 0;
						rateOfLasersFired = 0;
					}
					else 
					{
						/*
						if(musicOn == true)
						{
							AudioPlayer.player.stop(backgroundMusic); 
							try
							{
								InputStream youLoseIn = new FileInputStream("GalaxyQuestMultimedia/youLoseMusic.au");
								youLoseMusic = new AudioStream(youLoseIn);        
							}
							catch (FileNotFoundException e)
							{
								System.out.println("No file");
							}
							catch (IOException e)
							{
								System.out.println("IOException");
							}
							AudioPlayer.player.start(youLoseMusic);  
						}
						*/
						JOptionPane.showMessageDialog(null, "Game Over");
						JOptionPane.showMessageDialog(null, "Accuracy: " + accuracyOnAsteroidHits + "%");
						musicCounter = 0;
						if (points >= myHighScoreList.getMinimumScore()) 
						{
							newHighScore();
							resetEverything();
							viewScores = true;
						}

						else {resetEverything(); viewScores = true;}
					}
				}
			}

			//these are the bounds for the asteroids and bullets
			for (int k = 0; k < laserList.size(); k++)  //k sorts through all bullets
			{
				for (int j = 0; j < asteroidList.size() && k < laserList.size(); j++) //j sorts through asteroids
				{	
					double someLaserX = ((Laser)laserList.get(k)).getLaserCoordinateX();
					double someLaserY = ((Laser)laserList.get(k)).getLaserCoordinateY();
					double someAsteroidX = ((Asteroid)asteroidList.get(j)).getAsteroidCoordinateX();
					double someAsteroidY = ((Asteroid)asteroidList.get(j)).getAsteroidCoordinateY();
					double someLaserWidth = ((Laser)laserList.get(k)).getLaserWidth();
					double someLaserHeight = ((Laser)laserList.get(k)).getLaserHeight();
					double someAsteroidWidth = ((Asteroid)asteroidList.get(j)).getAsteroidWidth() - 3;
					double someAsteroidHeight = ((Asteroid)asteroidList.get(j)).getAsteroidHeight() - 3;
					
					if (((Asteroid)asteroidList.get(j)).getCounter() >= 13) asteroidList.remove(j);
					
					if (((	someAsteroidX - someAsteroidWidth < someLaserX - someLaserWidth && someAsteroidX + someAsteroidWidth > someLaserX - someLaserWidth) ||
							
						(	someAsteroidX - someAsteroidWidth < someLaserX && someAsteroidX + someAsteroidWidth > someLaserX) ||
							
						(	someAsteroidX - someAsteroidWidth < someLaserX + someLaserWidth && someAsteroidX + someAsteroidWidth > someLaserX + someLaserWidth)) 
							
							&&
							
						((	someAsteroidY - someAsteroidHeight < someLaserY - someLaserHeight && someAsteroidY + someAsteroidHeight > someLaserY - someLaserHeight) ||
							
						(	someAsteroidY - someAsteroidHeight < someLaserY && someAsteroidY + someAsteroidHeight > someLaserY) ||
							
						(	someAsteroidY - someAsteroidHeight < someLaserY + someLaserHeight && someAsteroidY + someAsteroidHeight > someLaserY + someLaserHeight))
						
							&&
							
							((Asteroid)asteroidList.get(j)).isItHit() == false	) 
						
						{
						/*
						if (musicOn == true)
						{
							try
							{
								InputStream asteroidMusicIn = new FileInputStream("GalaxyQuestMultimedia/asteroidExplosion.au");
								asteroidSound = new AudioStream(asteroidMusicIn);        
							}
							catch (FileNotFoundException e)
							{
								System.out.println("No file");
							}
							catch (IOException e)
							{
								System.out.println("IOException");
							}
							
							AudioPlayer.player.start(asteroidSound);  
						}
						*/
						((Asteroid)asteroidList.get(j)).blowUp();
						laserList.remove(k);
						points += (600 / someAsteroidWidth);
						asteroidHits++;
						}
					
					
				}
				repaint();
			}

			
			/**
			 * animates the lasers and removes them once they are sufficiently off the screen
			 */
			for (int a = 0; a < laserList.size(); a++)
			{
				Laser someLaser = (Laser)laserList.get(a);
				someLaser.translateLaser(0, laserSpeed);
				if (someLaser.getLaserCoordinateY() > getHeight() / 2)
				{
					System.out.println("Laser has been erased");
					laserList.remove(a);
					repaint();
					if (startYet == true)
					{
						lasersMissed++;
						System.out.println("misses: " + lasersMissed);
					}
				}
			}
			
			/**
			 * this animates the stars in the background and recycles them (by moving them to the top once they reach the bottom)
			 */
			for (int b = 0; b < starArray.size(); b++)
			{
				Star someStar = (Star)starArray.get(b);
				someStar.translateStar(0, -starRate);
				if (someStar.getStarCoordinateY() < -500)
				{
					someStar.resetCoordinateY();
					repaint();
				}
			}		
		}
	}
	
	/**
	 * draws everything
	 */
	public void paint(Graphics g)
	{
		Graphics2D g2 = (Graphics2D)g;
		Font standardFont = new Font (null, Font.BOLD, 16);
		g2.setFont(standardFont);
		FontRenderContext context= g2.getFontRenderContext();
		
		g2.translate(getWidth() / 2,getHeight() / 2);
		
		g2.scale(1, -1);
		g2.setBackground(Color.BLACK);///i'm not sure why this command doesn't work, so i made a huge rectangle instead
		Rectangle2D backgroundRect = new Rectangle2D.Double(-1000, -1000, 2000, 2000); //this is the huge rectangle
		g2.fill(backgroundRect);


		for (int i = 0; i < starArray.size(); i++)
		{
			Star aStar = (Star)starArray.get(i);
			g2.setColor(Color.YELLOW);
			aStar.draw(g2);
			g2.setColor(Color.LIGHT_GRAY);
		}
		
		for (int i = 0; i < asteroidList.size(); i++)
		{
			Asteroid anAsteroid = (Asteroid)asteroidList.get(i);
			if (drawRectangleCursor == true) anAsteroid.draw(g2);
			double thisAsteroidX = anAsteroid.getAsteroidCoordinateX() - anAsteroid.getAsteroidWidth(); //the width and height are only
			double thisAsteroidY = anAsteroid.getAsteroidCoordinateY() - anAsteroid.getAsteroidHeight(); //half of the actual (ie dist from center)
			double thisAsteroidWidth = anAsteroid.getAsteroidWidth() * 2; //must double width since actual is twice radius
			if (anAsteroid.isItHit() == false)
			{
				g.drawImage(asteroidImage, (int)thisAsteroidX, (int)thisAsteroidY, (int)thisAsteroidWidth, (int)thisAsteroidWidth, this);
			}
			if (anAsteroid.isItHit() == true && anAsteroid.getCounter() <= 13)
			{
				g.drawImage(blownUpAsteroidImage, (int)thisAsteroidX, (int)thisAsteroidY, (int)thisAsteroidWidth, (int)thisAsteroidWidth, this);
				anAsteroid.increaseCounter();
			}
		}
		
		if (drawRectangleCursor == true) petersShip.draw(g2); 

		
		int width = getWidth()/2;
		int height = getHeight()/2;
		
		for (int i = 0; i < laserList.size(); i++)
		{
			Laser aLaser = (Laser)laserList.get(i);
			aLaser.draw(g2);
		}
		
		g2.scale(1, -1); //switches to normal scale (y increases going down) so text is right side up

		
		if (startYet == true)
		{
			//left side of the screen:
			String levelString = "Level: " + level;
			Rectangle2D levelStringBounds = standardFont.getStringBounds(levelString, context);
			int levelStringWidth = (int)levelStringBounds.getWidth();
			g2.drawString(levelString, -getWidth() / 2 + 60 -levelStringWidth/2, -getHeight() / 2 + 20);
			
			String pointsString = "Points: " + points * accuracyOnAsteroidHits / 100;
			Rectangle2D pointsStringBounds = standardFont.getStringBounds(pointsString, context);
			int pointsStringWidth = (int)pointsStringBounds.getWidth();
			g2.drawString(pointsString, -getWidth() / 2 + 60 -pointsStringWidth / 2, -getHeight() / 2 + 40);
			
			String accuracyString;
			if(accuracyOnAsteroidHits == 0){accuracyString = "Accuracy: " + 100 + "%";}
			else{accuracyString = "Accuracy: " + accuracyOnAsteroidHits + "%";}
			Rectangle2D accuracyStringBounds = standardFont.getStringBounds(accuracyString, context);
			int accuracyStringWidth = (int)accuracyStringBounds.getWidth();
			g2.drawString(accuracyString, -getWidth() / 2 + 60 -accuracyStringWidth / 2, -getHeight() / 2 + 60);
			
			if(lives >=0)
			{
				String livesString = "Lives: " + lives;
				Rectangle2D livesStringBounds = standardFont.getStringBounds(livesString, context);
				int livesStringWidth = (int)livesStringBounds.getWidth();
				g2.drawString(livesString, -getWidth() / 2 + 60 - livesStringWidth / 2, -getHeight() / 2 + 80);
			}
			if(lives<0) //this is so once you die for the last time, it still says 0 lives, not -1
			{
				String livesString = "Lives: " + 0;
				Rectangle2D livesStringBounds = standardFont.getStringBounds(livesString, context);
				int livesStringWidth = (int)livesStringBounds.getWidth();
				g2.drawString(livesString, -getWidth() / 2 + 60 - livesStringWidth / 2, -getHeight() / 2 + 80);
			}
			
			//the right side of the screen
			if (rateOfLasersFired < 150) {gunStatus = "Good"; lasersOkay = true;}
			else if (rateOfLasersFired < 250) {gunStatus = "Not good"; g2.setColor(Color.YELLOW); lasersOkay = true;}
			else if (rateOfLasersFired < 500) {gunStatus = "Bad"; g2.setColor(Color.ORANGE); lasersOkay = true;}
			else if (rateOfLasersFired >= 500) {gunStatus = "Overheated"; g2.setColor(Color.RED); lasersOkay = false;}
			String gunString = "Laser Status:" + gunStatus;
			Rectangle2D gunStringBounds = standardFont.getStringBounds(gunString, context);
			int gunStringWidth = (int)gunStringBounds.getWidth();
			g2.fill(new Rectangle2D.Double(getWidth() / 2 - 150, - getHeight() / 2 + 27, rateOfLasersFired / 5, 15));
			g2.drawString(gunString, getWidth() / 2 - 100 - gunStringWidth / 2, -getHeight() / 2 + 20);
			g2.setColor(Color.LIGHT_GRAY);
			
			if(asteroidsThatHitBase <= 5000)
			{
				if (asteroidsThatHitBase >= 3500) g2.setColor(Color.RED);
				else if (asteroidsThatHitBase >= 1900) g2.setColor(Color.ORANGE);
				String damageString = "Damage to Base: " + (int)(asteroidsThatHitBase * 20 / 1000) + "%";
				Rectangle2D damageStringBounds = standardFont.getStringBounds(damageString, context);
				int damageStringWidth = (int)damageStringBounds.getWidth();
				g2.drawString(damageString, getWidth() / 2 - 100 - damageStringWidth/2, (float)-getHeight() / 2 + 60);
				g2.fill(new Rectangle2D.Double(getWidth() / 2 - 150, - getHeight() / 2 + 67, asteroidsThatHitBase / 50, 15));
				g2.setColor(Color.LIGHT_GRAY);
			}
			if(asteroidsThatHitBase > 5000) //this is so it doesn't ever say damage is greater than 100%
			{
				g2.setColor(Color.RED);
				String damageString = "Damage to Base: 100%";
				Rectangle2D damageStringBounds = standardFont.getStringBounds(damageString, context);
				int damageStringWidth = (int)damageStringBounds.getWidth();
				g2.drawString(damageString, getWidth() / 2 - 100 - damageStringWidth/2, (float)-getHeight() / 2 + 60);
				g2.fill(new Rectangle2D.Double(getWidth() / 2 - 150, - getHeight() / 2 + 67, 100, 15));
				g2.setColor(Color.LIGHT_GRAY);
			}
		}
		if (startYet == false)
		{
			if (readyToStart == false && viewScores == false && howToPlay == false)
			{
				g2.drawString("Made by Peter Compernolle (2005)", -getWidth()/2 + 25, getHeight()/2 - 25);
				
				String startGameString = "New Game";
				g2.setColor(Color.MAGENTA);
				Rectangle2D startGameStringBounds = standardFont.getStringBounds(startGameString, context);
				startGameButtonWidth = (int)startGameStringBounds.getWidth();
				startGameButtonX =  -(startGameButtonWidth/2);
				startGameButtonY = 0;
				g2.drawString(startGameString, startGameButtonX, -startGameButtonY);
				g2.setColor(Color.LIGHT_GRAY);
				
				String scoresString = "View High Scores";
				Rectangle2D scoresStringBounds = standardFont.getStringBounds(scoresString, context);
				viewScoresButtonWidth = (int)scoresStringBounds.getWidth();
				viewScoresButtonX = getWidth() / 5 - (viewScoresButtonWidth/2);
				viewScoresButtonY = 0;
				g2.drawString(scoresString, viewScoresButtonX, viewScoresButtonY);
				
				String soundOnOffString;
				if (musicOn == true) soundOnOffString = "Sound: On";
				else soundOnOffString = "Sound: Off";
				Rectangle2D soundOnOffBounds = standardFont.getStringBounds(soundOnOffString, context);
				soundOnOffButtonWidth = (int)soundOnOffBounds.getWidth();
				soundOnOffButtonX = -getWidth() / 10 - soundOnOffButtonWidth/2;
				soundOnOffButtonY = 75;
				g2.drawString(soundOnOffString, soundOnOffButtonX, -soundOnOffButtonY);
				
				String quitString = "Quit";
				Rectangle2D quitStringBounds = standardFont.getStringBounds(quitString, context);
				quitGameButtonWidth = (int)quitStringBounds.getWidth();
				quitGameButtonX = getWidth() / 10 - (quitGameButtonWidth/2);
				quitGameButtonY = 75;
				g2.drawString(quitString, quitGameButtonX, -quitGameButtonY);
				
				String howToPlayString = "How to Play";
				Rectangle2D howToPlayStringBounds = standardFont.getStringBounds(howToPlayString, context);
				howToPlayButtonWidth = (int)howToPlayStringBounds.getWidth();
				howToPlayButtonX = -getWidth() / 5 - (howToPlayButtonWidth/2);
				howToPlayButtonY = 0;
				g2.drawString(howToPlayString, howToPlayButtonX, howToPlayButtonY);
			}
			else if (readyToStart == true) startYet = true;
			else if (viewScores == true) 
			{
				laserList.add(new Laser(0, -10000, 0, 0));//this makes the animation more fluid
				g2.scale(1, -1); 
				myHighScoreList.draw(g2, width, height);
				if (fromScoresToGame == true)
				{
					fromScoresToGame = false;
					viewScores = false;
				}
			}
			else if (howToPlay == true)
			{
				laserList.add(new Laser(0, -10000, 0, 0));//this makes the animation more fluid
				//draw how to play
				String[] how = new String[19];
				how[0] = "           Your Mission:";
				how[1] = ""; how[2] = "";
				how[3] = "         Your mission, should you choose to accept it, is very clear. You must protect our base, Smolarkian-9, from any asteroids.";
				how[4] = "The inhabitants of Smolarkian-9 can repair the damage from asteroids, though it takes some time. The danger level of the base is";
				how[5] = "located at the top-right of your window.  These asteroids, though a large threat, can be easily destroyed by using your laser. To";
				how[6] = "use your laser, simply click the mouse or hit the SPACE bar. Your lasers will improve with the more asteroids you destroy. If you";
				how[7] = "use your lasers too frequently, they will overheat and be rendered useless until they cool sufficiently.  This can be monitored by";
				how[8] = "the guage in the top-right of your window. Using you lasers efficiently will also help your score, which is determined by both the ";
				how[9] = "number of asteroids destroyed and your laser accuracy. At any time, you can abaort your mission, senslessly ending the lives of";
				how[10]= "milllions of Smolarkian-9 inhabitants, by pressing ESC.  You can pause the game by pressing RETURN.";
				how[11]= ""; how[12]="";
				how[13]= "         Good luck, and may God be with you.";
				how[14]=""; how[15]=""; how[16]=""; how[17]="";
				how[18] = "                                                                                            - Captain Jorgen Blurgenmorkldnmeskynj";
		
				for (int h = 0; h < 19; h++)
				{
					g2.drawString(how[h], -getWidth() / 3 - 50, -getHeight() / 4 - 75 + h * 30);
				}
				
				if (fromScoresToGame == true)
				{
					fromScoresToGame = false;
					howToPlay = false;
				}
			}
		}
		if (paused == true) g2.drawString("Paused", -20, 0);
		g2.scale(1, -1); //switches back to useful scale (y increases going up)	
	}
	
	/**
	 * listens for key input and calls translateShip (if arrows) or pauses game (if return) or fires laser (if space)
	 */
	class shipLaserPauseKeyListener implements KeyListener
	{

		public void keyPressed(KeyEvent e)
		{
			int key = e.getKeyCode();
			switch(key)
			{	
				case KeyEvent.VK_LEFT: petersShip.translateShip(-8, 0); break;
				case KeyEvent.VK_RIGHT: petersShip.translateShip(8, 0); break;
				case KeyEvent.VK_UP: petersShip.translateShip(0, 8); break;
				case KeyEvent.VK_DOWN: petersShip.translateShip(0, -8); break; 
				case KeyEvent.VK_SPACE:
				{
					if (lasersOkay = true)
					{
						Laser petersLaser = new Laser(petersShip.getShipCoordinateX() + 20, //this is the width of the ship 
								petersShip.getShipCoordinateY() + petersShip.getShipHeight(), laserSize, laserSize);
						laserList.add(petersLaser);
						/*
						if(musicOn == true)
						{
							try
							{
								InputStream in = new FileInputStream("GalaxyQuestMultimedia/laserSound.au");
								laserFiredSound = new AudioStream(in);        
							}
							catch (FileNotFoundException e1)
							{
								System.out.println("No file");
							}
							catch (IOException e1)
							{
								System.out.println("IOException");
							}
							
							AudioPlayer.player.start(laserFiredSound);  
						}
						*/
						lasersFired++;
						rateOfLasersFired += 15;
					}
				}; break;
				case KeyEvent.VK_ENTER: 
				{
					if (paused == false)
					{
						System.out.println("Paused");
						grandTimer.stop();
						paused = true;
					}
					else if (paused == true)
					{
						System.out.println("Resumed");
						grandTimer.start();
						paused = false;
					}
				}; break;
				case KeyEvent.VK_ESCAPE: 
				{
					asteroidList.clear();
					if (points >= myHighScoreList.getMinimumScore()) newHighScore();
					resetEverything();
				}
			}
			repaint();
		}
		//do nothing 
		public void keyTyped(KeyEvent e) {}
		public void keyReleased(KeyEvent e) {}
		
	}
	
	/**
	 * listens for mouse motion and moves ship accordingly
	 */
	class shipLaserMouseInputListener implements MouseMotionListener, MouseListener
	{
		public void mouseMoved(MouseEvent e)
		{
			e.translatePoint(-getWidth() / 2, -getHeight() / 2);
			petersShip.sendShipTo(e.getX(), -e.getY());
		}
		public void mousePressed(MouseEvent event1)
		{
			if (lasersOkay == true)
			{
				Laser petersLaser = new Laser(petersShip.getShipCoordinateX() - laserSize / 2 - 2,//this is the ship width 
						petersShip.getShipCoordinateY() + 21, laserSize, laserSize * 2); //mult by 2 to make them long and narrow (like lasers)
				laserList.add(petersLaser);
				if (startYet == true) lasersFired++;
				rateOfLasersFired += 15;

				/*
				if(musicOn == true)
				{
					try
					{
						InputStream in = new FileInputStream("GalaxyQuestMultimedia/laserSound.au");
						laserFiredSound = new AudioStream(in);        
					}
					catch (FileNotFoundException e)
					{
						System.out.println("No file");
					}
					catch (IOException e)
					{
						System.out.println("IOException");
					}
					
					AudioPlayer.player.start(laserFiredSound);  
				}
				*/
			}
			else {System.out.println("not okay to fire lasers");}
		}
		
		///do nothing methods
		public void mouseDragged(MouseEvent event) {}
		public void mouseReleased(MouseEvent event) {}
		public void mouseClicked(MouseEvent event) {}
		public void mouseEntered(MouseEvent event) {}
		public void mouseExited(MouseEvent event) {}
	}
}