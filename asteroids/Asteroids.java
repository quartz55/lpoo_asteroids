package asteroids;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.applet.Applet;

import asteroids.GameObjects.Asteroid;
import asteroids.GameObjects.Bullet;
import asteroids.GameObjects.GameObject;
import asteroids.GameObjects.Ship;

/******************************************************************************
  The AsteroidsSprite class defines a game object, including it's shape,
  position, movement and rotation. It also can detemine if two objects collide.
 ******************************************************************************/

class Sprite_bk {

	// Fields:

	static int width;
	static int height;

	Polygon shape;    
	boolean active;
	double  angle;
	double  angSpeed;
	double  x, y;
	double  xspeed, yspeed;
	Polygon sprite;

	public Sprite_bk() {
		this.shape = new Polygon();
		this.active = false;
		this.angle = 0.0;
		this.angSpeed = 0.0;
		this.x = 0.0;
		this.y = 0.0;
		this.xspeed = 0.0;
		this.yspeed = 0.0;
		this.sprite = new Polygon();
	}

	public boolean update() {

		boolean wrapped;

		this.angle += this.angSpeed;
		if (this.angle < 0)
			this.angle += 2 * Math.PI;
		if (this.angle > 2 * Math.PI)
			this.angle -= 2 * Math.PI;
		wrapped = false;
		this.x += this.xspeed;
		if (this.x < -width / 2) {
			this.x += width;
			wrapped = true;
		}
		if (this.x > width / 2) {
			this.x -= width;
			wrapped = true;
		}
		this.y -= this.yspeed;
		if (this.y < -height / 2) {
			this.y += height;
			wrapped = true;
		}
		if (this.y > height / 2) {
			this.y -= height;
			wrapped = true;
		}

		return wrapped;
	}

	public void draw() {

		this.sprite = new Polygon();
		for (int i = 0; i < this.shape.npoints; i++)
			this.sprite.addPoint((int) Math.round(this.shape.xpoints[i] * Math.cos(this.angle) + this.shape.ypoints[i] * Math.sin(this.angle)) + (int) Math.round(this.x) + width / 2,
					(int) Math.round(this.shape.ypoints[i] * Math.cos(this.angle) - this.shape.xpoints[i] * Math.sin(this.angle)) + (int) Math.round(this.y) + height / 2);
	}

	public boolean isColliding(Sprite_bk s) {

		int i;

		for (i = 0; i < s.sprite.npoints; i++)
			if (this.sprite.contains(s.sprite.xpoints[i], s.sprite.ypoints[i]))
				return true;
		for (i = 0; i < this.sprite.npoints; i++)
			if (s.sprite.contains(this.sprite.xpoints[i], this.sprite.ypoints[i]))
				return true;
		return false;
	}
}

/******************************************************************************
  Main applet code.
 ******************************************************************************/

public class Asteroids extends Applet implements Runnable, KeyListener {

	// Thread control variables.

	Thread loadThread;
	Thread loopThread;

	// Constants

	static final int DELAY = 8;
	static final int FPS   = Math.round(1000 / DELAY);

	static final int MAX_SHOTS =  8; 
	static final int MAX_ROCKS =  8;
	static final int MAX_SCRAP = 40;

	static final int SCRAP_COUNT  = 2 * FPS;
	static final int HYPER_COUNT  = 3 * FPS;
	static final int STORM_PAUSE  = 2 * FPS;

	static final int    MIN_ROCK_SIDES =   6;
	static final int    MAX_ROCK_SIDES =  16;
	static final int    MIN_ROCK_SIZE  =  20;
	static final int    MAX_ROCK_SIZE  =  40;
	static final double MIN_ROCK_SPEED =  40.0 / FPS;
	static final double MAX_ROCK_SPEED = 240.0 / FPS;
	static final double MAX_ROCK_SPIN  = Math.PI / FPS;

	static final int MAX_SHIPS = 3;

	static final double SHIP_ANGLE_STEP = 0.01;
	static final double SHIP_SPEED_STEP = 0.01;
	static final double MAX_SHIP_SPEED  = 3;

	static final int FIRE_DELAY = 50; 

	static final double MISSLE_PROBABILITY = 0.45 / FPS;

	static final int BIG_POINTS    =  25;
	static final int SMALL_POINTS  =  50;
	static final int MISSLE_POINTS = 500;

	static final int NEW_SHIP_POINTS = 5000;

	// Background stars.

	int     numStars;
	Point[] stars;

	// Game data.

	int score;
	int highScore;
	int newShipScore;

	// Flags for game state and options.

	boolean loaded = false;
	boolean paused;
	boolean playing;

	// Key flags.

	boolean left  = false;
	boolean right = false;
	boolean up    = false;
	boolean down  = false;

	// Sprite objects.

	Sprite_bk[] explosions = new Sprite_bk[MAX_SCRAP];

	// Ship data.

	int lives;        
	int shipCounter;

	// Photon data.

	int   photonIndex;
	long  photonTime;

	// Asteroid data.

	boolean[] asteroidIsSmall = new boolean[MAX_ROCKS];
	int       asteroidsCounter;
	double    asteroidsSpeed;
	int       asteroidsLeft;

	// Explosion data.

	int[] explosionCounter = new int[MAX_SCRAP];
	int   explosionIndex;

	// Counter and total used to track the loading of the sound clips.

	int clipTotal   = 0;
	int clipsLoaded = 0;

	// Off screen image.

	Dimension offDimension;
	Image     offImage;
	Graphics  doubleBuffer;

	// Data for the screen font.

	Font font      = new Font("Arial", Font.BOLD, 12);
	FontMetrics fm = getFontMetrics(font);
	int fontWidth  = fm.getMaxAdvance();
	int fontHeight = fm.getHeight();

	Ship player;
	ArrayList<Asteroid> asteroids_al;
	ArrayList<Bullet> bullets_al;

	public void init() {

		Dimension d = getSize();
		int i;

		// Set up key event handling and set focus to applet window.

		addKeyListener(this);
		requestFocus();

		// Save the screen size.

		GameObject.width = d.width;
		GameObject.height = d.height;

		Sprite_bk.width = d.width;
		Sprite_bk.height = d.height;

		// Generate the starry background.

		numStars = Sprite_bk.width * Sprite_bk.height / 5000;
		stars = new Point[numStars];
		for (i = 0; i < numStars; i++)
			stars[i] = new Point((int) (Math.random() * Sprite_bk.width), (int) (Math.random() * Sprite_bk.height));

		// Create explosion sprites.

		for (i = 0; i < MAX_SCRAP; i++)
			explosions[i] = new Sprite_bk();

		// Initialize game data and put us in 'game over' mode.

		highScore = 0;
		initGame();
		endGame();
	}

	public void initGame() {

		// Initialize game data and sprites.

		score = 0;
		lives = MAX_SHIPS;
		asteroidsSpeed = MIN_ROCK_SPEED;
		newShipScore = NEW_SHIP_POINTS;
		initShip();
		initPhotons();
		initAsteroids();
		initExplosions();
		playing = true;
		paused = false;
		photonTime = System.currentTimeMillis();
	}

	public void endGame() {

		playing = false;
		stopShip();
	}

	public void start() {

		if (loopThread == null) {
			loopThread = new Thread(this);
			loopThread.start();
		}
		if (!loaded && loadThread == null) {
			loadThread = new Thread(this);
			loadThread.start();
		}
	}

	public void stop() {

		if (loopThread != null) {
			loopThread.stop();
			loopThread = null;
		}
		if (loadThread != null) {
			loadThread.stop();
			loadThread = null;
		}
	}

	public void run() {

		int i, j;
		long startTime;

		// Lower this thread's priority and get the current time.

		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		startTime = System.currentTimeMillis();

		// Run thread for loading sounds.

		if (!loaded && Thread.currentThread() == loadThread) {
			loaded = true;
			loadThread.stop();
		}

		// This is the main loop.

		while (Thread.currentThread() == loopThread) {

			if (!paused) {

				updateBullets();
				updateAsteroids();
				updateExplosions();

				if(player.isAlive()){
					if(left) player.rotLeft();
					else if(right) player.rotRight();
					if(up) player.moveFwd();
					else if(down) player.moveBack();
					player.update();
				}

				if (score > highScore)
					highScore = score;
				if (score > newShipScore) {
					newShipScore += NEW_SHIP_POINTS;
					lives++;
				}

				// If all asteroids have been destroyed create a new batch.

				/*if (asteroidsLeft <= 0)
					if (--asteroidsCounter <= 0)
						initAsteroids();*/
			}

			// Update the screen and set the timer for the next loop.

			repaint();
			try {
				startTime += DELAY;
				Thread.sleep(Math.max(0, startTime - System.currentTimeMillis()));
			}
			catch (InterruptedException e) { break; }
		}
	}

	public void initShip() {

		// Reset the ship sprite at the center of the screen.

		player = new Ship();
	}

	public void stopShip() {

		player.setAlive(false);
		shipCounter = SCRAP_COUNT;
		if (lives > 0)
			lives--;
	}

	public void initPhotons() {

		bullets_al = new ArrayList<Bullet>();
	}

	public void updateBullets() {

		for(int i = 0; i < bullets_al.size(); i++)
		{
			boolean wrap = bullets_al.get(i).update();
			if(wrap)
			{
				bullets_al.remove(i);
				--i;
			}
		}
	}

	public void initAsteroids() {

		asteroids_al = new ArrayList<Asteroid>();
		for(int i = 0; i < MAX_ROCKS; i++)
			asteroids_al.add(new Asteroid());

		/*
		asteroidsCounter = STORM_PAUSE;
		asteroidsLeft = MAX_ROCKS;
		if (asteroidsSpeed < MAX_ROCK_SPEED)
			asteroidsSpeed += 0.5;
		 */
	}

	private void asteroidExplosion(Asteroid a, int i)
	{
		int temp_size = a.getSize() - 1;
		double temp_x = a.getX();
		double temp_y = a.getY();
		explode(a);
		asteroids_al.remove(i);
		if(temp_size > 0)
		{
			asteroids_al.add(new Asteroid(temp_size, temp_x, temp_y));
			asteroids_al.add(new Asteroid(temp_size, temp_x, temp_y));
		}
	}

	public void updateAsteroids() {

		for(int i = 0; i < asteroids_al.size(); i++)
		{
			asteroids_al.get(i).update();
			if(asteroids_al.get(i).checkCollision(player))
			{
				asteroidExplosion(asteroids_al.get(i), i);
				--i;
				--lives;
				continue;
			}
			for(int j = 0; j < bullets_al.size(); j++)
			{
				if(asteroids_al.get(i).checkCollision(bullets_al.get(j)))
				{
					asteroidExplosion(asteroids_al.get(i), i);
					bullets_al.remove(j);
					--i;
					break;
				}
			}

		}

		/*
						asteroids[i].active && asteroids[i].isColliding(ship)) {
					if (sound)
						crashSound.play();
					explode(ship);
					stopShip();
				}*/
	}

	public void initExplosions() {

		int i;

		for (i = 0; i < MAX_SCRAP; i++) {
			explosions[i].shape = new Polygon();
			explosions[i].active = false;
			explosionCounter[i] = 0;
		}
		explosionIndex = 0;
	}

	public void explode(GameObject o) {

		int c, i, j;
		int cx, cy;

		// Create sprites for explosion animation. The each individual line segment
		// of the given sprite is used to create a new sprite that will move
		// outward  from the sprite's original position with a random rotation.

		c = 1;
		for (i = 0; i < o.getObjSprite().getSprite().npoints; i += c) {
			explosionIndex++;
			if (explosionIndex >= MAX_SCRAP)
				explosionIndex = 0;
			explosions[explosionIndex].active = true;
			explosions[explosionIndex].shape = new Polygon();
			j = i + 1;
			if (j >= o.getObjSprite().getSprite().npoints)
				j -= o.getObjSprite().getSprite().npoints;
			cx = (int) ((o.getObjSprite().getSprite().xpoints[i] + o.getObjSprite().getSprite().xpoints[j]) / 2);
			cy = (int) ((o.getObjSprite().getSprite().ypoints[i] + o.getObjSprite().getSprite().ypoints[j]) / 2);
			explosions[explosionIndex].shape.addPoint(
					o.getObjSprite().getSprite().xpoints[i] - cx,
					o.getObjSprite().getSprite().ypoints[i] - cy);
			explosions[explosionIndex].shape.addPoint(
					o.getObjSprite().getSprite().xpoints[j] - cx,
					o.getObjSprite().getSprite().ypoints[j] - cy);
			explosions[explosionIndex].x = o.getX();
			explosions[explosionIndex].y = o.getY();
			explosions[explosionIndex].angle = o.getAng();
			explosions[explosionIndex].angSpeed = 4 * (Math.random() * 2 * MAX_ROCK_SPIN - MAX_ROCK_SPIN);
			explosions[explosionIndex].xspeed = (Math.random() * 2 * MAX_ROCK_SPEED - MAX_ROCK_SPEED + o.getXspeed()) / 2;
			explosions[explosionIndex].yspeed = (Math.random() * 2 * MAX_ROCK_SPEED - MAX_ROCK_SPEED + o.getYspeed()) / 2;
			explosionCounter[explosionIndex] = SCRAP_COUNT;
		}
	}

	public void updateExplosions() {

		int i;

		// Move any active explosion debris. Stop explosion when its counter has
		// expired.

		for (i = 0; i < MAX_SCRAP; i++)
			if (explosions[i].active) {
				explosions[i].update();
				explosions[i].draw();
				if (--explosionCounter[i] < 0)
					explosions[i].active = false;
			}
	}

	public void keyPressed(KeyEvent e) {

		char c;

		// Check if any cursor keys have been pressed and set flags.

		if (e.getKeyCode() == KeyEvent.VK_LEFT)
			left = true;
		if (e.getKeyCode() == KeyEvent.VK_RIGHT)
			right = true;
		if (e.getKeyCode() == KeyEvent.VK_UP)
			up = true;
		if (e.getKeyCode() == KeyEvent.VK_DOWN)
			down = true;

		// Spacebar: fire a photon and start its counter.

		if (e.getKeyChar() == ' ' && player.isAlive()) {

			Bullet temp = new Bullet(player.getX(), player.getY(), player.getAng());
			bullets_al.add(temp);

			/*
			photonTime = System.currentTimeMillis();
			photonIndex++;
			if (photonIndex >= MAX_SHOTS)
				photonIndex = 0;
			photons[photonIndex].active = true;
			photons[photonIndex].x = player.getX();
			photons[photonIndex].y = player.getY();
			photons[photonIndex].xspeed = 2 * MAX_ROCK_SPEED * -Math.sin(player.getAng());
			photons[photonIndex].yspeed = 2 * MAX_ROCK_SPEED *  Math.cos(player.getAng());
			 */
		}

		// Allow upper or lower case characters for remaining keys.

		c = Character.toLowerCase(e.getKeyChar());

		// 'P' key: toggle pause mode and start or stop any active looping sound
		// clips.

		if (c == 'p') {
			paused = !paused;
		}

		// 'S' key: start the game, if not already in progress.

		if (c == 's' && loaded && !playing)
			initGame();

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

	public void update(Graphics g) {

		paint(g);
	}

	public void paint(Graphics g) {

		Dimension d = getSize();
		int i;
		int c;
		String s;
		int w, h;
		int x, y;

		// Create the off screen graphics context, if no good one exists.

		if (doubleBuffer == null || d.width != offDimension.width || d.height != offDimension.height) {
			offDimension = d;
			offImage = createImage(d.width, d.height);
			doubleBuffer = offImage.getGraphics();
		}

		// Fill in background and stars.

		doubleBuffer.setColor(Color.black);
		doubleBuffer.fillRect(0, 0, d.width, d.height);
		doubleBuffer.setColor(Color.white);
		for (i = 0; i < numStars; i++)
			doubleBuffer.drawLine(stars[i].x, stars[i].y, stars[i].x, stars[i].y);

		// Draw photon bullets.

		doubleBuffer.setColor(Color.white);
		for(i = 0; i < bullets_al.size(); i++)
			bullets_al.get(i).draw(doubleBuffer);

		// Draw the asteroids.

		for(i = 0; i < asteroids_al.size(); i++)
			asteroids_al.get(i).draw(doubleBuffer);


		if (player.isAlive())
			player.draw(doubleBuffer);

		// Draw any explosion debris, counters are used to fade color to black.

		for (i = 0; i < MAX_SCRAP; i++)
			if (explosions[i].active) {
				c = (255 / SCRAP_COUNT) * explosionCounter [i];
				doubleBuffer.setColor(new Color(c, c, c));
				doubleBuffer.drawPolygon(explosions[i].sprite);
			}

		// Display status and messages.

		doubleBuffer.setFont(font);
		doubleBuffer.setColor(Color.white);

		doubleBuffer.drawString("Score: " + score, fontWidth, fontHeight);
		doubleBuffer.drawString("Ships: " + lives, fontWidth, d.height - fontHeight);
		s = "High: " + highScore;
		doubleBuffer.drawString(s, d.width - (fontWidth + fm.stringWidth(s)), fontHeight);

		if (!playing) {
			if (!loaded) {
				s = "Loading sounds...";
				w = 4 * fontWidth + fm.stringWidth(s);
				h = fontHeight;
				x = (d.width - w) / 2;
				y = 3 * d.height / 4 - fm.getMaxAscent();
				doubleBuffer.setColor(Color.black);
				doubleBuffer.fillRect(x, y, w, h);
				doubleBuffer.setColor(Color.gray);
				if (clipTotal > 0)
					doubleBuffer.fillRect(x, y, (int) (w * clipsLoaded / clipTotal), h);
				doubleBuffer.setColor(Color.white);
				doubleBuffer.drawRect(x, y, w, h);
				doubleBuffer.drawString(s, x + 2 * fontWidth, y + fm.getMaxAscent());
			}
			else {
				s = "Game Over";
				doubleBuffer.drawString(s, (d.width - fm.stringWidth(s)) / 2, d.height / 4);
				s = "'S' to Start";
				doubleBuffer.drawString(s, (d.width - fm.stringWidth(s)) / 2, d.height / 4 + fontHeight);
			}
		}
		else if (paused) {
			s = "Game Paused";
			doubleBuffer.drawString(s, (d.width - fm.stringWidth(s)) / 2, d.height / 4);
		}

		// Copy the off screen buffer to the screen.

		g.drawImage(offImage, 0, 0, this);
	}
}
