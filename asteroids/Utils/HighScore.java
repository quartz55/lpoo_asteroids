package asteroids.Utils;

import java.io.Serializable;

public class HighScore implements Serializable{
	public String name;
	public int score;

	public HighScore() {
		this.name = "Anonymous";
		this.score = 0;
	}
	public HighScore(String name, int score) {
		this.name = name;
		this.score = score;
	}
}
