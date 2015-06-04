package asteroids;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.applet.Applet;

import javax.swing.JPanel;

import asteroids.GameObjects.Asteroid;
import asteroids.GameObjects.Bullet;
import asteroids.GameObjects.GameObject;
import asteroids.GameObjects.Ship;
import asteroids.GameObjects.Star;

public class GamePanel extends JPanel implements KeyListener {
	// Constants
	static final int DELAY = 8;

	static int MAX_ROCKS = 8;

	static final int MAX_SHIPS = 1;

	static final int[] POINTS = {0, 25, 50, 100, 200};

	// Game data.
	int score;
	int highScore;

	// Flags for game state and options.
	boolean done = false;
	boolean paused;
	boolean playing;

	// Key flags.
	boolean left = false;
	boolean right = false;
	boolean up = false;
	boolean down = false;

	// Ship data.
	int lives;

	// Off screen image.
	Dimension offDimension;
	Image offImage;
	Graphics doubleBuffer;

	// Data for the screen font.
	Font font = new Font("Arial", Font.BOLD, 12);
	FontMetrics fm = getFontMetrics(font);
	int fontWidth = fm.getMaxAdvance();
	int fontHeight = fm.getHeight();

	// Game Objects
	Ship player;
	ArrayList<Asteroid> asteroids_al;
	ArrayList<Bullet> bullets_al;
	ArrayList<Star> stars_al;

	public void init() {
		Dimension d = getSize();

		addKeyListener(this);
		requestFocus();

		GameObject.width = d.width;
		GameObject.height = d.height;

		highScore = 0;
		initGame();
		endGame();
		
		run();
	}

	public void initGame() {
		score = 0;
		lives = MAX_SHIPS;
		initShip();
		initPhotons();
		initAsteroids();

		stars_al = new ArrayList<Star>();
		for (int i = 0; i < 100; i++) {
			Dimension d = getSize();
			double x = Math.random()*d.width - d.width/2;
			double y = Math.random()*d.height - d.height/2;
			stars_al.add(new Star(x, y));
		}

		playing = true;
		paused = false;
	}

	public void endGame() {
		playing = false;
		stopShip();
	}

	public void run()
	{
		long startTime = System.currentTimeMillis();

		// This is the main loop.
		while (!done) {
			if (!paused) {
				for (int i = 0; i < stars_al.size(); i++) stars_al.get(i).update();
				updateBullets();
				updateAsteroids();

				if (player.isAlive()) {
					if (left)
						player.rotLeft();
					else if (right)
						player.rotRight();
					if (up)
						player.moveFwd();
					else if (down)
						player.moveBack();

					player.update();
				}

				if (score > highScore)
					highScore = score;
			}

			repaint();

			try {
				long currTime = System.currentTimeMillis();
				startTime += DELAY;

				long nextFrame = Math.max(0, startTime - currTime);
				Thread.sleep(nextFrame);
			} catch (InterruptedException e) { done = true; }
		}
	}

	public void initShip() {
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

	public void stopShip() {
		player.setAlive(false);
		if (lives > 0)
			lives--;

		if (lives <= 0) playing = false;

		if(playing){
			Thread test = new Thread(){
				public void run(){
					try {Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }

					initShip();
				}
			};
			test.start();
		}
	}

	public void initPhotons() {
		bullets_al = new ArrayList<Bullet>();
	}

	public void updateBullets() {
		for (int i = 0; i < bullets_al.size(); i++) {
			boolean wrap = bullets_al.get(i).update();
			if (wrap) {
				bullets_al.remove(i);
				--i;
			}
		}
	}

	public void initAsteroids() {
		asteroids_al = new ArrayList<Asteroid>();
		for (int i = 0; i < MAX_ROCKS; i++) asteroids_al.add(new Asteroid());
	}

	private void asteroidExplosion(Asteroid a, int i)
	{
		int temp_size = a.getSize() - 1;
		double temp_x = a.getX();
		double temp_y = a.getY();
		if (temp_size > 0) {
			asteroids_al.add(new Asteroid(temp_size, temp_x, temp_y));
			asteroids_al.add(new Asteroid(temp_size, temp_x, temp_y));
		} else if (asteroids_al.size() == 1) {
			Thread test = new Thread(){
				public void run(){
					try {Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }
					MAX_ROCKS += 4;
					initAsteroids();
				}
			};
			test.start();
		}
	}

	public void updateAsteroids() {
		for (int i = 0; i < asteroids_al.size(); i++) {
			asteroids_al.get(i).update();

			if (asteroids_al.get(i).checkCollision(player) && player.isAlive() && !player.isInvuln()) {
				asteroidExplosion(asteroids_al.get(i), i);
				asteroids_al.remove(i);
				--i;
				stopShip();
				continue;
			}

			for (int j = 0; j < bullets_al.size(); j++) {
				try{
					if (asteroids_al.get(i).checkCollision(bullets_al.get(j))) {
						score += POINTS[Math.min(asteroids_al.get(i).getSize(), POINTS.length)];

						asteroidExplosion(asteroids_al.get(i), i);
						asteroids_al.remove(i);
						--i;

						bullets_al.remove(j);
						--j;

						break;
					}
				} catch (Exception e) { e.printStackTrace(); }
			}
		}
	}

	public void keyPressed(KeyEvent e) {
		char c;
		c = Character.toLowerCase(e.getKeyChar());

		if (e.getKeyCode() == KeyEvent.VK_LEFT)
			left = true;
		if (e.getKeyCode() == KeyEvent.VK_RIGHT)
			right = true;
		if (e.getKeyCode() == KeyEvent.VK_UP)
			up = true;
		if (e.getKeyCode() == KeyEvent.VK_DOWN)
			down = true;

		if (c == ' ' && player.isAlive()) {
			Bullet temp = new Bullet(player.getX(), player.getY(), player.getAng());
			bullets_al.add(temp);
		}


		if (c == 's' && !playing)
			initGame();
		if (c == 'p')
			paused = !paused;
		if (c == 'q')
			done = true;
	}

	public void keyReleased(KeyEvent e) {
		// Check if any cursor keys where released and set flags.

		if (e.getKeyCode() == KeyEvent.VK_LEFT)
			left = false;
		if (e.getKeyCode() == KeyEvent.VK_RIGHT)
			right = false;
		if (e.getKeyCode() == KeyEvent.VK_UP)
			up = false;
		if (e.getKeyCode() == KeyEvent.VK_DOWN)
			down = false;
	}

	public void keyTyped(KeyEvent e) {}

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

		doubleBuffer.setColor(Color.black);
		doubleBuffer.fillRect(0, 0, d.width, d.height);
		
		for (i = 0; i < stars_al.size(); i++) stars_al.get(i).draw(doubleBuffer);

		// Draw photon bullets.
		doubleBuffer.setColor(Color.white);
		for (i = 0; i < bullets_al.size(); i++) bullets_al.get(i).draw(doubleBuffer);

		// Draw the asteroids.
		for (i = 0; i < asteroids_al.size(); i++) asteroids_al.get(i).draw(doubleBuffer);

		if (player.isAlive())
			player.draw(doubleBuffer);

		// Display status and messages.

		doubleBuffer.setFont(font);
		doubleBuffer.setColor(Color.white);

		doubleBuffer.drawString("Score: " + score, fontWidth, fontHeight);
		doubleBuffer.drawString("Ships: " + lives, fontWidth, d.height - fontHeight);
		doubleBuffer.drawString("High: " + highScore, d.width - (fontWidth + fm.stringWidth("High: " + highScore)), fontHeight);

		if (!playing) {
			s = "Game Over";
			doubleBuffer.drawString(s, (d.width - fm.stringWidth(s)) / 2, d.height / 4);
			s = "'S' to Start";
			doubleBuffer.drawString(
					s, (d.width - fm.stringWidth(s)) / 2, d.height / 4 + fontHeight);
		} else if (paused) {
			s = "Game Paused";
			doubleBuffer.drawString(s, (d.width - fm.stringWidth(s)) / 2, d.height / 4);
		}

		g.drawImage(offImage, 0, 0, this);
	}
}
