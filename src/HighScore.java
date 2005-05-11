

//package DrawStuff;

import java.io.Serializable;

public class HighScore implements Serializable
{
	private String name;
	private int score;
	
	public HighScore()
	{
		name = "Peter is Cool";
		score = 0;
	}
	
	public HighScore(String aName, int aScore)
	{
		name = aName;
		score = aScore;
	}
	
	public String getName()
	{
		return name;
	}
	
	public int getScore()
	{
		return score;
	}
	
	public int compareTo(Object otherObject)
	{
		HighScore other = (HighScore)otherObject;
		if (score < other.getScore()) return -1;
		if (score == other.getScore()) return 0;
		return 1;
	}

}
