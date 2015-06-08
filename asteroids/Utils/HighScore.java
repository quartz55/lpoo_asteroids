package asteroids.Utils;

import java.io.Serializable;

/**
 * Class that represents a score entry
 * Has a name and score parameter
 */
public class HighScore implements Serializable{
	public String name;
	public int score;

	/**
	 * Empty constructor
	 */
	public HighScore() {
		this.name = "Anonymous";
		this.score = 0;
	}

	/**
	 * Default constructor
	 * @param name Player name
	 * @param score Player score
	 */
	public HighScore(String name, int score) {
		this.name = name;
		this.score = score;
	}
}
