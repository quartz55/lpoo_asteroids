package asteroids;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.applet.Applet;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import asteroids.GameObjects.Asteroid;
import asteroids.GameObjects.Bullet;
import asteroids.GameObjects.GameObject;
import asteroids.GameObjects.Ship;
import asteroids.GameObjects.Star;
import asteroids.Managers.ParticleManager;
import asteroids.Utils.HighScore;
import asteroids.Engine.*;

/**
 * Main game class
 * Contains all the game objects and the main game loop
 * Is also responsible for drawing to the screen
 */
public class GamePanel extends JPanel{
	static final int MS_PER_FRAME = 16;

	static final int MAX_ROCKS_DEFAULT = 8;
	static int MAX_ROCKS = 8;

	static final int MAX_SHIPS = 3;
	static final long FIRE_DELAY = 200;

	static final int[] DIFFICULTY_LEVELS = {2, 3, 5};
	static final String[] DIFFICULTY_NAMES = {"Easy", "Medium", "Hard"};
	static final int[] POINTS = {0, 25, 50, 75, 150};

	JFrame parent_frame;

	// Game data.
	int currScore;
	ArrayList<HighScore> highScores;
	int difficulty = 1;

	// Flags for game state
	boolean testing = false;
	boolean firstStart = true;
	boolean done = false;
	boolean paused = false;
	boolean playing = false;
	boolean loaded = false;

	// Ship data.
	int lives;
	boolean canFire = true;

	Image gImage;
	Dimension dim;
	Graphics doubleBuffer;

	// Game Objects
	Ship player;
	ArrayList<Asteroid> asteroids_al;
	ArrayList<Bullet> bullets_al;
	ArrayList<Star> stars_al;

	ParticleManager pm;

	/**
	 * Initializes all game parameters and runs the main game loop
	 * @param f Parent JFrame
	 */
	public void start(JFrame f) {
		parent_frame = f;
		Dimension d = getSize();

		GameObject.width = d.width;
		GameObject.height = d.height;

		loadScores();

		newGame();
		player.setAlive(false);

		loaded = true;

		run();

		saveScores();
	}

	private void newGame() {
		currScore = 0;
		lives = MAX_SHIPS;
		MAX_ROCKS = MAX_ROCKS_DEFAULT;
		Asteroid.MAX_SIZE_MULT = DIFFICULTY_LEVELS[difficulty];

		initShip();
		bullets_al = new ArrayList<Bullet>();
		initAsteroids();
		pm = new ParticleManager();

		this.stars_al = new ArrayList<Star>();
		for (int i = 0; i < 100; i++) {
			Dimension d = getSize();
			double x = Math.random()*d.width - d.width/2;
			double y = Math.random()*d.height - d.height/2;
			stars_al.add(new Star(x, y));
		}

		InputEngine.getInstance().clearBools();

		if (!firstStart)
			playing = true;
		paused = false;
	}

	/**
	 * Main game loop
	 * Gets input, updates game objects and draws to screen
	 */
	public void run() {
		long startTime = System.currentTimeMillis();

		// Main game loop
		while (!done) {
			// Input
			getInput();

			// Update
			if (!paused) {
				for (int i = 0; i < stars_al.size(); i++) stars_al.get(i).update();
				pm.update();
				updateBullets();
				updateAsteroids();
				if (player.isAlive()) player.update();
			}

			// Draw
			repaint();

			// Wait for next frame
			try {
				long currTime = System.currentTimeMillis();
				startTime += MS_PER_FRAME;

				long nextFrame = Math.max(0, startTime - currTime);
				Thread.sleep(nextFrame);
			} catch (InterruptedException e) { done = true; }
		}
	}

	private void getInput() {
		if (player.isAlive()) {
			if (InputEngine.getInstance().KEY_LEFT)
				player.rotLeft();
			else if (InputEngine.getInstance().KEY_RIGHT)
				player.rotRight();
			if (InputEngine.getInstance().KEY_UP) {
				pm.addParticle(
						player.getX(),
						player.getY(),
						Math.sin(player.getAng()+Math.random()*1 - 0.5),
						-Math.cos(player.getAng()+Math.random()*1 - 0.5),
						2);
				player.moveFwd();
			}
			else if (InputEngine.getInstance().KEY_DOWN) {
				pm.addParticle(
						player.getX(),
						player.getY(),
						-Math.sin(player.getAng()+Math.random()*1 - 0.5),
						Math.cos(player.getAng()+Math.random()*1 - 0.5),
						2);
				player.moveBack();
			}
			if (InputEngine.getInstance().KEY_SPACE) {
				if (canFire) {
					canFire = false;
					bullets_al.add(new Bullet(player.getX(), player.getY(), player.getAng()));
					SoundEngine.getInstance().playSound("asteroids_shoot.wav");

					Thread fireDelayThread = new Thread(){
						public void run() {
							try {Thread.sleep(FIRE_DELAY); } catch (InterruptedException e) { e.printStackTrace(); }
							canFire = true;
						}
					};
					fireDelayThread.start();
				}
			}
		}

		if (InputEngine.getInstance().checkInput('s') && !playing) {
			firstStart = false;
			newGame();
		}
		if (InputEngine.getInstance().checkInput('p'))
			if (playing) paused = !paused;

		if (InputEngine.getInstance().checkInput('m'))
			SoundEngine.getInstance().toggleMute();

		if (InputEngine.getInstance().checkInput('h'))
			showHelp();

		if (InputEngine.getInstance().checkInput('o') && !playing)
			changeDifficulty();

		if (InputEngine.getInstance().checkInput('q'))
			done = true;

		InputEngine.getInstance().clearInput();
	}

	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Dimension d = getSize();
		int i;
		String s;

		if (doubleBuffer == null || d.width != dim.width || d.height != dim.height) {
			dim = d;
			gImage = createImage(d.width, d.height);
			doubleBuffer = gImage.getGraphics();
		}

		// Clear screen
		doubleBuffer.setColor(Color.black);
		doubleBuffer.fillRect(0, 0, d.width, d.height);

		if (!loaded) return;

		// Draw Stars
		for (i = 0; i < stars_al.size(); i++) stars_al.get(i).draw(doubleBuffer);

		// Draw bullets.
		for (i = 0; i < bullets_al.size(); i++) bullets_al.get(i).draw(doubleBuffer);

		// Draw the asteroids.
		for (i = 0; i < asteroids_al.size(); i++) asteroids_al.get(i).draw(doubleBuffer);

		// Draw particles
		pm.draw(doubleBuffer);

		// Draw player
		if (player.isAlive())
			player.draw(doubleBuffer);


		// Data for the screen font.
		Font font = new Font("Arial", Font.BOLD, 16);
		FontMetrics fm = getFontMetrics(font);
		int fontWidth = fm.getMaxAdvance();
		int fontHeight = fm.getHeight();

		// Display status and messages.
		doubleBuffer.setFont(font);
		doubleBuffer.setColor(Color.white);

		doubleBuffer.drawString("Score: " + currScore, fontWidth, fontHeight + 20);

		int highscore = (currScore > highScores.get(0).score) ? currScore : highScores.get(0).score;
		doubleBuffer.drawString("Highscore: " + highscore, d.width - (fontWidth + fm.stringWidth("Highscore: " + highscore)), fontHeight + 20);
		if (playing) {
			doubleBuffer.drawString("Lives: " + lives, fontWidth, d.height - 20);
			if (paused) {
				s = "Game Paused";
				doubleBuffer.drawString(s, (d.width - fm.stringWidth(s)) / 2, d.height / 4);
				doubleBuffer.drawString("Press 'H' for help", (d.width - fm.stringWidth("Press 'H' for help") - fontWidth) , d.height - 20);
			}
		}
		else if (!playing) {
			if (firstStart)
				s = "ASTEROIDS";
			else s = "Game Over";
			doubleBuffer.drawString(s, (d.width - fm.stringWidth(s)) / 2, d.height / 2);

			if (firstStart) s = "Press 'S' to start";
			else s = "Press 'S' to restart";
			doubleBuffer.drawString( s, (d.width - fm.stringWidth(s)) / 2, d.height / 2 + fontHeight);

			// Draw highscores
			doubleBuffer.drawString("Highscores", (d.width - fm.stringWidth("Highscores")) / 2, d.height / 2 + fontHeight + 50);
			for (i = 0; i < highScores.size(); i++)
				doubleBuffer.drawString("" + (i+1) + ". " + highScores.get(i).name + " : " + highScores.get(i).score, (d.width - fm.stringWidth(s)) / 2, d.height / 2 + fontHeight + 50 + fontHeight*(i+1));

			// Draw difficulty
			doubleBuffer.drawString("Difficulty: " + DIFFICULTY_NAMES[difficulty], fontWidth, d.height - 20);

			doubleBuffer.drawString("Press 'H' for help", (d.width - fm.stringWidth("Press 'H' for help") - fontWidth) , d.height - 20 * 2);
			doubleBuffer.drawString("Press 'Q' to quit", (d.width - fm.stringWidth("Press 'Q' to quit") - fontWidth) , d.height - 20);

		}

		// Draw final image on screen
		g.drawImage(gImage, 0, 0, this);
	}

	private void changeDifficulty() {
		Thread t = new Thread(new Runnable(){
			public void run() {
				String diff = (String) JOptionPane.showInputDialog(parent_frame, "Choose game difficulty", "Difficulty", JOptionPane.QUESTION_MESSAGE, null, DIFFICULTY_NAMES, DIFFICULTY_NAMES[difficulty]);

				if (diff == null || diff == "") diff = new String("Medium");

				switch (diff) {
				case "Easy":
					difficulty = 0; break;
				case "Medium":
					difficulty = 1; break;
				case "Hard":
					difficulty = 2; break;
				default:
					difficulty = 0; break;
				}
			}
		});
		t.start();
	}

	private void initShip() {
		player = new Ship();

		Thread test = new Thread(){
			public void run(){
				try {Thread.sleep(2000); }
				catch (InterruptedException e) { e.printStackTrace(); }
				player.setInvuln(false);
			}
		};
		test.start();
	}

	private void stopShip() {
		player.setAlive(false);
		if (lives > 0)
			lives--;

		if (lives <= 0) gameOver();

		if(playing){
			Thread sleepT = new Thread(){
				public void run(){
					try {Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }

					initShip();
				}
			};
			sleepT.start();
		}
	}

	private void updateBullets() {
		for (int i = 0; i < bullets_al.size(); i++) {
			boolean wrap = bullets_al.get(i).update();
			if (wrap) {
				bullets_al.remove(i);
				--i;
			}
		}
	}

	private void initAsteroids() {
		asteroids_al = new ArrayList<Asteroid>();
		for (int i = 0; i < MAX_ROCKS; i++) asteroids_al.add(new Asteroid());
	}

	private void asteroidExplosion(Asteroid a, int i) {
		SoundEngine.getInstance().playSound("asteroids_explosion.wav");
		int temp_size = a.getSize() - 1;
		double temp_x = a.getX();
		double temp_y = a.getY();
		pm.explosion(temp_x, temp_y, 20);
		if (temp_size > 0) {
			asteroids_al.add(new Asteroid(temp_size, temp_x, temp_y));
			asteroids_al.add(new Asteroid(temp_size, temp_x, temp_y));
		} else if (asteroids_al.size() == 1) {
			Thread sleepT = new Thread(){
				public void run(){
					try {Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }
					MAX_ROCKS += 4;
					initAsteroids();
				}
			};
			sleepT.start();
		}
	}

	private void updateAsteroids() {
		for (int i = 0; i < asteroids_al.size(); i++) {
			asteroids_al.get(i).update();

			if (asteroids_al.get(i).checkCollision(player) && player.isAlive() && !player.isInvuln()) {
				pm.explosion(player.getX(), player.getY(), 100);
				asteroidExplosion(asteroids_al.get(i), i);
				asteroids_al.remove(i);
				--i;
				stopShip();
				continue;
			}

			for (int j = 0; j < bullets_al.size(); j++) {
				if (asteroids_al.get(i).checkCollision(bullets_al.get(j))) {
					currScore += POINTS[Math.min(asteroids_al.get(i).getSize(), POINTS.length-1)];

					asteroidExplosion(asteroids_al.get(i), i);
					asteroids_al.remove(i);
					--i;

					bullets_al.remove(j);
					--j;

					break;
				}
			}
		}
	}


	private void gameOver() {
		playing = false;

		if (testing) return;

		// Get player name for scoreboard
		Thread t = new Thread(new Runnable() {
			public void run(){

				String name = (String) JOptionPane.showInputDialog(parent_frame, "Game over!\nWhat's your name?", "Player name", JOptionPane.QUESTION_MESSAGE, null, null, "Anonymous");

				if (name == null || name.length() == 0)
					name = "Anonymous";

				for (int i = 0; i < highScores.size(); i++) {
					if (currScore > highScores.get(i).score) {
						highScores.add(i, new HighScore(name, currScore));
						highScores.remove(highScores.size()-1);
						break;
					}
				}
			}
		});
		t.start();
	}

	private void showHelp() {
		boolean wasPaused = paused;
		paused = true;
		Thread t = new Thread(new Runnable(){
			public void run() {
		JOptionPane.showMessageDialog(parent_frame, ""
				+ "Help:\n\n"
				+ "The main objective of the game is to avoid the moving asteroids while also destroying them\n"
				+ "     - You can only move the ship forward or backwards and steer it to the left or right\n"
				+ "     - Normal and big asteroids transform into two smaller asteroids when shot\n"
				+ "     - The bigger the asteroid, the more points it gives when destroyed\n"
				+ "\n\n"
				+ "Controls:\n\n"
				+ "S: (Re)Start game\n"
				+ "ARROW KEYS: Steer and move ship\n"
				+ "SPACE: Shoot\n"
				+ "P: Pause game\n"
				+ "O: Change difficulty\n"
				+ "M: Toggle mute\n"
				+ "H: Show this screen\n"
				+ "Q: Quit the game");
		InputEngine.getInstance().clearBools();
		paused = wasPaused;
			}
		});
		t.start();
	}

	private void saveScores() {
		ObjectOutputStream os = null;
		try{
			os = new ObjectOutputStream(new FileOutputStream("highscores.data"));
			os.writeObject(this.highScores);
			os.close();
			System.out.println("Saved highscores");
		}
		catch (IOException e2) { e2.printStackTrace(); }
	}

	private void loadScores() {
		ObjectInputStream is = null;
		try{
			is = new ObjectInputStream(new FileInputStream("highscores.data"));
			try { this.highScores = (ArrayList<HighScore>) is.readObject();
			} catch (ClassNotFoundException e) { e.printStackTrace(); }
			is.close();
			System.out.println("Loaded Highscores\n");
		}
		catch (IOException e) {
			e.printStackTrace();
			System.out.println("No 'highscores.data' available, creating...");
			this.highScores = new ArrayList<HighScore>();
			for (int i = 0; i < 5; i++) this.highScores.add(new HighScore());
			saveScores();
		}
	}

	/*
	 * For testing purposes
	 */

	/**
	 * Initializes game parameters for testing purposes
	 * @param f Parent JFrame
	 */
	public void initTest(JFrame f) {
		testing = true;
		parent_frame = f;
		Dimension d = getSize();

		GameObject.width = d.width;
		GameObject.height = d.height;

		firstStart = false;
		loadScores();
		newGame();
		player.setAlive(true);

		loaded = true;
	}
	/**
	 * Removes all the asteroids
	 */
	public void removeAsteroids() {
		asteroids_al = new ArrayList<Asteroid>();
	}
	/**
	 * Adds an asteroid to the game
	 * @param x Asteroid's X position
	 * @param y Asteroid's Y position
	 * @param size Asteroid's size
	 */
	public void addAsteroid(double x, double y, int size) {
		Asteroid test = new Asteroid(size, x, y);
		test.setYspeed(0);
		test.setXspeed(0);
		asteroids_al.add(test);
	}

	/**
	 * @return How many lives left
	 */
	public int getLives() { return lives; }
	/**
	 * Sets lives left to the specified one
	 * @param l
	 */
	public void setLives(int l) {this.lives = l;}
	/**
	 * @return TRUE if game is currently paused FALSE if not
	 */
	public boolean gameIsOn() { return playing; }
	/**
	 * @return TRUE if game is currently paused FALSE if not
	 */
	public boolean gameIsPaused() { return paused; }
	/**
	 * @return Game's player (Ship)
	 */
	public Ship getPlayer() {return player; }
	/**
	 * @return Game's list of asteroids
	 */
	public ArrayList<Asteroid> getAsteroids() {return asteroids_al; }
	/**
	 * @return Game's list of bullets
	 */
	public ArrayList<Bullet> getBullets() {return bullets_al; }

}