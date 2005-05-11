//package DrawStuff;

import java.util.ArrayList;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.Serializable;

public class HighScoreList implements Serializable
{
	private ArrayList highScoreArray;
	private int score;
	private String name;
	private int minimum = 0;
	
	/**
	 * constructs list of the high scores if there isn't one already
	 */
	public HighScoreList()
	{
		highScoreArray = new ArrayList();
	}


	/**
	 * adds an object to the array list
	 * @param anObject the object to add
	 */
	public void add(HighScore aHighScore)
	{
		highScoreArray.add(aHighScore);
		boolean sorted = false;
		while (!sorted)
		{
			sorted = true;
			for (int i = 0; i < highScoreArray.size() - 1; i++)
			{
				if (((HighScore)highScoreArray.get(i)).getScore() < ((HighScore)highScoreArray.get(i+1)).getScore())
				{
					swap(highScoreArray, i, i+1);
					sorted = false;
				}
			}
		}
	}
	
	/**
	 * returns the minimum score in the array list
	 * @return minimum score
	 */
	public int getMinimumScore()
	{
		int minimum = ((HighScore)highScoreArray.get(9)).getScore();

		return minimum;
	}
	
	public void draw(Graphics g, int width, int height)
	{	
		
		
		Graphics2D g2 = (Graphics2D)g;
		for (int j = 0; j < 10; j++)
		{
			HighScore someHighScore = (HighScore)highScoreArray.get(j);
			String someName = someHighScore.getName();
			int someScore = someHighScore.getScore();
			g2.scale(1, -1);
			g2.drawString("High Scores for the Greatest Game on Earth", -(width / 3) + 60, -270);
			g2.drawString("" + someName, -200, (height - 510) + 40 * j);
			g2.drawString("" + someScore, 200, (height - 510) + 40 * j);
			g2.scale(1, -1);
		}
	}
	
	public static void swap(ArrayList a, int i, int j)
	{
		Object temp = a.get(i);
		a.set(i, a.get(j));
		a.set(j, temp);
	}
}
