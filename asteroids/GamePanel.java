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

public class GamePanel extends JPanel{
	static final int MS_PER_FRAME = 8;

	static final int MAX_ROCKS_DEFAULT = 8;
	static int MAX_ROCKS = 8;

	static final int MAX_SHIPS = 1;
	static final long FIRE_DELAY = 200;

	static final int[] DIFFICULTY_LEVELS = {2, 3, 5};
	static final String[] DIFFICULTY_NAMES = {"Easy", "Medium", "Hard"};
	static final int[] POINTS = {0, 25, 50, 75, 150};

	// Game data.
	int score;
	ArrayList<HighScore> highScores;
	int difficulty = 2;

	// Flags for game state and options.
	boolean firstStart = true;
	boolean done = false;
	boolean paused;
	boolean playing;
	boolean loaded = false;

	// Ship data.
	int lives;
	boolean canFire = true;

	// Off screen image.
	Dimension offDimension;
	Image offImage;
	Graphics doubleBuffer;


	// Game Objects
	Ship player;
	ArrayList<Asteroid> asteroids_al;
	ArrayList<Bullet> bullets_al;
	ArrayList<Star> stars_al;

	ParticleManager pm;

	public void init() {
		Dimension d = getSize();

		GameObject.width = d.width;
		GameObject.height = d.height;

		loadScores();

		initGame();
		player.setAlive(false);

		changeDifficulty();
		
		loaded = true;

		run();
		
		saveScores();
	}

	private void initGame() {
		score = 0;
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

		if (!firstStart)
			playing = true;
		paused = false;
	}
	
	private void gameOver() {
		playing = false;

		JFrame frame = new JFrame("Player name");
		String name = JOptionPane.showInputDialog(frame, "Game over!\nWhat's your name?");

		if (name == null || name.length() == 0)
			name = "John Doe";

		for (int i = 0; i < highScores.size(); i++) {
			if (score > highScores.get(i).score) {
				highScores.add(i, new HighScore(name, score));
				highScores.remove(highScores.size()-1);
				break;
			}
		}
	}

	private void run() {
		long startTime = System.currentTimeMillis();

		// Main game loop
		while (!done) {
			getInput();
			if (!paused) {
				for (int i = 0; i < stars_al.size(); i++) stars_al.get(i).update();
				pm.update();
				updateBullets();
				updateAsteroids();
				if (player.isAlive()) player.update();
			}

			repaint();

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
						1);
				player.moveFwd();
			}
			else if (InputEngine.getInstance().KEY_DOWN) {
				pm.addParticle(
						player.getX(),
						player.getY(),
						-Math.sin(player.getAng()+Math.random()*1 - 0.5),
						Math.cos(player.getAng()+Math.random()*1 - 0.5), 
						1);
				player.moveBack();
			}
			if (InputEngine.getInstance().KEY_SPACE) {
				if (canFire) {
					canFire = false;
					bullets_al.add(new Bullet(player.getX(), player.getY(), player.getAng()));

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
			initGame();
		}
		if (InputEngine.getInstance().checkInput('p'))
			if (playing) paused = !paused;
		if (InputEngine.getInstance().checkInput('q'))
			done = true;

		InputEngine.getInstance().clearInput();
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
					score += POINTS[Math.min(asteroids_al.get(i).getSize(), POINTS.length-1)];

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

	public void paintComponent(java.awt.Graphics g){
		super.paintComponent(g);
		Dimension d = getSize();
		int i;
		String s;

		if (doubleBuffer == null || d.width != offDimension.width || d.height != offDimension.height) {
			offDimension = d;
			offImage = createImage(d.width, d.height);
			doubleBuffer = offImage.getGraphics();
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

		doubleBuffer.drawString("Score: " + score, fontWidth, fontHeight + 20);
		
		int highscore = (score > highScores.get(0).score) ? score : highScores.get(0).score;
		doubleBuffer.drawString("Highscore: " + highscore, d.width - (fontWidth + fm.stringWidth("Highscore: " + highscore)), fontHeight + 20);
		if (playing) {
			doubleBuffer.drawString("Lives: " + lives, fontWidth, d.height - 20);
			if (paused) {
				s = "Game Paused";
				doubleBuffer.drawString(s, (d.width - fm.stringWidth(s)) / 2, d.height / 4);
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
			doubleBuffer.drawString("Highscores", (d.width - fm.stringWidth(s)) / 2, d.height / 2 + fontHeight + 50);
			for (i = 0; i < highScores.size(); i++)
				doubleBuffer.drawString("" + (i+1) + ". " + highScores.get(i).name + " : " + highScores.get(i).score, (d.width - fm.stringWidth(s)) / 2, d.height / 2 + fontHeight + 50 + fontHeight*(i+1));
			
			// Draw difficulty
			doubleBuffer.drawString("Difficulty: " + DIFFICULTY_NAMES[difficulty], fontWidth, d.height - 20);

		}

		// Display buffer
		g.drawImage(offImage, 0, 0, this);
	}
	
	private void changeDifficulty() {
		JFrame frame = new JFrame("Difficulty");
		String diff = (String) JOptionPane.showInputDialog(frame, "Choose game difficulty", "Difficulty", JOptionPane.QUESTION_MESSAGE, null, DIFFICULTY_NAMES, DIFFICULTY_NAMES[0]);

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

}