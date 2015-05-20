package asteroids;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import java.applet.Applet;
import java.applet.AudioClip;

import asteroids.GameObjects.GameObject;
import asteroids.GameObjects.Ship;

/******************************************************************************
  The AsteroidsSprite class defines a game object, including it's shape,
  position, movement and rotation. It also can detemine if two objects collide.
 ******************************************************************************/

class Sprite_bk {

	// Fields:

	static int width;          // Dimensions of the graphics area.
	static int height;

	Polygon shape;             // Base sprite shape, centered at the origin (0,0).
	boolean active;            // Active flag.
	double  angle;             // Current angle of rotation.
	double  angSpeed;        // Amount to change the rotation angle.
	double  x, y;              // Current position on screen.
	double  xspeed, yspeed;    // Amount to change the screen position.
	Polygon sprite;            // Final location and shape of sprite after
	// applying rotation and translation to get screen
	// position. Used for drawing on the screen and in
	// detecting collisions.

	// Constructors:

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

	// Methods:

	public boolean update() {

		boolean wrapped;

		// Update the rotation and position of the sprite based on the delta
		// values. If the sprite moves off the edge of the screen, it is wrapped
		// around to the other side and TRUE is returned.

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

		// Render the sprite's shape and location by rotating it's base shape and
		// moving it to it's proper screen position.

		this.sprite = new Polygon();
		for (int i = 0; i < this.shape.npoints; i++)
			this.sprite.addPoint((int) Math.round(this.shape.xpoints[i] * Math.cos(this.angle) + this.shape.ypoints[i] * Math.sin(this.angle)) + (int) Math.round(this.x) + width / 2,
					(int) Math.round(this.shape.ypoints[i] * Math.cos(this.angle) - this.shape.xpoints[i] * Math.sin(this.angle)) + (int) Math.round(this.y) + height / 2);
	}

	public boolean isColliding(Sprite_bk s) {

		int i;

		// Determine if one sprite overlaps with another, i.e., if any vertice
		// of one sprite lands inside the other.

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

	static final int MAX_SHOTS =  8;          // Maximum number of sprites
	static final int MAX_ROCKS =  8;          // for photons, asteroids and
	static final int MAX_SCRAP = 40;          // explosions.

	static final int SCRAP_COUNT  = 2 * FPS;  // Timer counter starting values
	static final int HYPER_COUNT  = 3 * FPS;  // calculated using number of
	static final int STORM_PAUSE  = 2 * FPS;

	static final int    MIN_ROCK_SIDES =   6; // Ranges for asteroid shape, size
	static final int    MAX_ROCK_SIDES =  16; // speed and rotation.
	static final int    MIN_ROCK_SIZE  =  20;
	static final int    MAX_ROCK_SIZE  =  40;
	static final double MIN_ROCK_SPEED =  40.0 / FPS;
	static final double MAX_ROCK_SPEED = 240.0 / FPS;
	static final double MAX_ROCK_SPIN  = Math.PI / FPS;

	static final int MAX_SHIPS = 3;           // Starting number of ships for
	// each game.

	// Ship's rotation and acceleration rates and maximum speed.

	static final double SHIP_ANGLE_STEP = Math.PI / FPS;
	static final double SHIP_SPEED_STEP = 15.0 / FPS;
	static final double MAX_SHIP_SPEED  = 1.25 * MAX_ROCK_SPEED;

	static final int FIRE_DELAY = 50;         // Minimum number of milliseconds
	// required between photon shots.

	// Probablility of flying saucer firing a missle during any given frame
	// (other conditions must be met).

	static final double MISSLE_PROBABILITY = 0.45 / FPS;

	static final int BIG_POINTS    =  25;     // Points scored for shooting
	static final int SMALL_POINTS  =  50;     // various objects.
	static final int UFO_POINTS    = 250;
	static final int MISSLE_POINTS = 500;

	// Number of points the must be scored to earn a new ship or to cause the
	// flying saucer to appear.

	static final int NEW_SHIP_POINTS = 5000;
	static final int NEW_UFO_POINTS  = 2750;

	// Background stars.

	int     numStars;
	Point[] stars;

	// Game data.

	int score;
	int highScore;
	int newShipScore;
	int newUfoScore;

	// Flags for game state and options.

	boolean loaded = false;
	boolean paused;
	boolean playing;
	boolean sound;
	boolean detail;

	// Key flags.

	boolean left  = false;
	boolean right = false;
	boolean up    = false;
	boolean down  = false;

	// Sprite objects.

	Sprite_bk   ship;
	Sprite_bk   fwdThruster, revThruster;
	Sprite_bk   missle;
	Sprite_bk[] photons    = new Sprite_bk[MAX_SHOTS];
	Sprite_bk[] asteroids  = new Sprite_bk[MAX_ROCKS];
	Sprite_bk[] explosions = new Sprite_bk[MAX_SCRAP];

	// Ship data.

	int lives;           // Number of ships left in game, including current one.
	int shipCounter;     // Timer counter for ship explosion.

	// Photon data.

	int   photonIndex;    // Index to next available photon sprite.
	long  photonTime;     // Time value used to keep firing rate constant.

	// Asteroid data.

	boolean[] asteroidIsSmall = new boolean[MAX_ROCKS];    // Asteroid size flag.
	int       asteroidsCounter;                            // Break-time counter.
	double    asteroidsSpeed;                              // Asteroid speed.
	int       asteroidsLeft;                               // Number of active asteroids.

	// Explosion data.

	int[] explosionCounter = new int[MAX_SCRAP];  // Time counters for explosions.
	int   explosionIndex;                         // Next available explosion sprite.

	// Sound clips.

	AudioClip crashSound;
	AudioClip explosionSound;
	AudioClip fireSound;
	AudioClip missleSound;
	AudioClip saucerSound;
	AudioClip thrustersSound;
	AudioClip warpSound;

	// Flags for looping sound clips.

	boolean thrustersPlaying;
	boolean saucerPlaying;
	boolean misslePlaying;

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

	public void init() {

		Dimension d = getSize();
		int i;

		// Display copyright information.

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

		// Create shape for the ship sprite.

		player = new Ship();

		ship = new Sprite_bk();
		ship.shape.addPoint(0, -10);
		ship.shape.addPoint(7, 10);
		ship.shape.addPoint(-7, 10);

		// Create shapes for the ship thrusters.

		fwdThruster = new Sprite_bk();
		fwdThruster.shape.addPoint(0, 12);
		fwdThruster.shape.addPoint(-3, 16);
		fwdThruster.shape.addPoint(0, 26);
		fwdThruster.shape.addPoint(3, 16);
		revThruster = new Sprite_bk();
		revThruster.shape.addPoint(-2, 12);
		revThruster.shape.addPoint(-4, 14);
		revThruster.shape.addPoint(-2, 20);
		revThruster.shape.addPoint(0, 14);
		revThruster.shape.addPoint(2, 12);
		revThruster.shape.addPoint(4, 14);
		revThruster.shape.addPoint(2, 20);
		revThruster.shape.addPoint(0, 14);

		// Create shape for each photon sprites.

		for (i = 0; i < MAX_SHOTS; i++) {
			photons[i] = new Sprite_bk();
			photons[i].shape.addPoint(1, 1);
			photons[i].shape.addPoint(1, -1);
			photons[i].shape.addPoint(-1, 1);
			photons[i].shape.addPoint(-1, -1);
		}

		// Create asteroid sprites.

		for (i = 0; i < MAX_ROCKS; i++)
			asteroids[i] = new Sprite_bk();

		// Create explosion sprites.

		for (i = 0; i < MAX_SCRAP; i++)
			explosions[i] = new Sprite_bk();

		// Initialize game data and put us in 'game over' mode.

		highScore = 0;
		sound = true;
		detail = true;
		initGame();
		endGame();
	}

	public void initGame() {

		// Initialize game data and sprites.

		score = 0;
		lives = MAX_SHIPS;
		asteroidsSpeed = MIN_ROCK_SPEED;
		newShipScore = NEW_SHIP_POINTS;
		newUfoScore = NEW_UFO_POINTS;
		initShip();
		initPhotons();
		initAsteroids();
		initExplosions();
		playing = true;
		paused = false;
		photonTime = System.currentTimeMillis();
	}

	public void endGame() {

		// Stop ship, flying saucer, guided missle and associated sounds.

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
			loadSounds();
			loaded = true;
			loadThread.stop();
		}

		// This is the main loop.

		while (Thread.currentThread() == loopThread) {

			if (!paused) {

				// Move and process all sprites.

				updateShip();
				updatePhotons();
				updateAsteroids();
				updateExplosions();

				if(player.isAlive()){
					if(left) player.rotLeft();
					else if(right) player.rotRight();
					if(up) player.moveFwd();
					else if(down) player.moveBack();
					player.update();
					player.draw();
				}

				// Check the score and advance high score, add a new ship or start the
				// flying saucer as necessary.

				if (score > highScore)
					highScore = score;
				if (score > newShipScore) {
					newShipScore += NEW_SHIP_POINTS;
					lives++;
				}

				// If all asteroids have been destroyed create a new batch.

				if (asteroidsLeft <= 0)
					if (--asteroidsCounter <= 0)
						initAsteroids();
			}

			// Update the screen and set the timer for the next loop.

			repaint();
			try {
				startTime += DELAY;
				Thread.sleep(Math.max(0, startTime - System.currentTimeMillis()));
			}
			catch (InterruptedException e) {
				break;
			}
		}
	}

	public void loadSounds() {

		// Load all sound clips by playing and immediately stopping them. Update
		// counter and total for display.

		try {
			crashSound     = getAudioClip(new URL(getCodeBase(), "crash.au"));
			clipTotal++;
			explosionSound = getAudioClip(new URL(getCodeBase(), "explosion.au"));
			clipTotal++;
			fireSound      = getAudioClip(new URL(getCodeBase(), "fire.au"));
			clipTotal++;
			missleSound    = getAudioClip(new URL(getCodeBase(), "missle.au"));
			clipTotal++;
			saucerSound    = getAudioClip(new URL(getCodeBase(), "saucer.au"));
			clipTotal++;
			thrustersSound = getAudioClip(new URL(getCodeBase(), "thrusters.au"));
			clipTotal++;
			warpSound      = getAudioClip(new URL(getCodeBase(), "warp.au"));
			clipTotal++;
		}
		catch (MalformedURLException e) {}

		try {
			crashSound.play();     crashSound.stop();     clipsLoaded++;
			repaint(); Thread.currentThread().sleep(DELAY);
			explosionSound.play(); explosionSound.stop(); clipsLoaded++;
			repaint(); Thread.currentThread().sleep(DELAY);
			fireSound.play();      fireSound.stop();      clipsLoaded++;
			repaint(); Thread.currentThread().sleep(DELAY);
			missleSound.play();    missleSound.stop();    clipsLoaded++;
			repaint(); Thread.currentThread().sleep(DELAY);
			saucerSound.play();    saucerSound.stop();    clipsLoaded++;
			repaint(); Thread.currentThread().sleep(DELAY);
			thrustersSound.play(); thrustersSound.stop(); clipsLoaded++;
			repaint(); Thread.currentThread().sleep(DELAY);
			warpSound.play();      warpSound.stop();      clipsLoaded++;
			repaint(); Thread.currentThread().sleep(DELAY);
		}
		catch (InterruptedException e) {}
	}

	public void initShip() {

		// Reset the ship sprite at the center of the screen.

		player = new Ship();

		ship.active = true;
		ship.angle = 0.0;
		ship.angSpeed = 0.0;
		ship.x = 0.0;
		ship.y = 0.0;
		ship.xspeed = 0.0;
		ship.yspeed = 0.0;
		ship.draw();

		// Initialize thruster sprites.

		// fwdThruster.x = ship.x;
		// fwdThruster.y = ship.y;
		// fwdThruster.angle = ship.angle;
		// fwdThruster.draw();
		// revThruster.x = ship.x;
		// revThruster.y = ship.y;
		// revThruster.angle = ship.angle;
		// revThruster.draw();

		if (loaded)
			thrustersSound.stop();
		thrustersPlaying = false;
	}

	public void updateShip() {

		double dx, dy, speed;

		if (!playing)
			return;

		// Rotate the ship if left or right cursor key is down.

		if (left) {
			ship.angle += SHIP_ANGLE_STEP;
			if (ship.angle > 2 * Math.PI)
				ship.angle -= 2 * Math.PI;
		}
		if (right) {
			ship.angle -= SHIP_ANGLE_STEP;
			if (ship.angle < 0)
				ship.angle += 2 * Math.PI;
		}

		// Fire thrusters if up or down cursor key is down.

		dx = SHIP_SPEED_STEP * -Math.sin(ship.angle);
		dy = SHIP_SPEED_STEP *  Math.cos(ship.angle);
		if (up) {
			ship.xspeed += dx;
			ship.yspeed += dy;
		}
		if (down) {
			ship.xspeed -= dx;
			ship.yspeed -= dy;
		}

		// Don't let ship go past the speed limit.

		if (up || down) {
			speed = Math.sqrt(ship.xspeed * ship.xspeed + ship.yspeed * ship.yspeed);
			if (speed > MAX_SHIP_SPEED) {
				dx = MAX_SHIP_SPEED * -Math.sin(ship.angle);
				dy = MAX_SHIP_SPEED *  Math.cos(ship.angle);
				if (up)
					ship.xspeed = dx;
				else
					ship.xspeed = -dx;
				if (up)
					ship.yspeed = dy;
				else
					ship.yspeed = -dy;
			}
		}

		if (ship.active) {
			ship.update();
			ship.draw();

			// Update the thruster sprites to match the ship sprite.
			/*
      fwdThruster.x = ship.x;
      fwdThruster.y = ship.y;
      fwdThruster.angle = ship.angle;
      fwdThruster.draw();
      revThruster.x = ship.x;
      revThruster.y = ship.y;
      revThruster.angle = ship.angle;
      revThruster.draw();*/
		}

		// Ship is exploding, advance the countdown or create a new ship if it is
		// (This gives the player time to move the ship if it is in imminent
		// danger.) If that was the last ship, end the game.

		else
			if (--shipCounter <= 0)
				if (lives > 0) {
					initShip();
				}
				else
					endGame();
	}

	public void stopShip() {

		ship.active = false;
		player.setAlive(false);
		shipCounter = SCRAP_COUNT;
		if (lives > 0)
			lives--;
		if (loaded)
			thrustersSound.stop();
		thrustersPlaying = false;
	}

	public void initPhotons() {

		int i;

		for (i = 0; i < MAX_SHOTS; i++)
			photons[i].active = false;
		photonIndex = 0;
	}

	public void updatePhotons() {

		int i;

		// Move any active photons. Stop it when its counter has expired.

		for (i = 0; i < MAX_SHOTS; i++)
			if (photons[i].active) {
				if (!photons[i].update())
					photons[i].draw();
				else
					photons[i].active = false;
			}
	}

	public void initAsteroids() {

		int i, j;
		int s;
		double theta, r;
		int x, y;

		// Create random shapes, positions and movements for each asteroid.

		for (i = 0; i < MAX_ROCKS; i++) {

			// Create a jagged shape for the asteroid and give it a random rotation.

			asteroids[i].shape = new Polygon();
			s = MIN_ROCK_SIDES + (int) (Math.random() * (MAX_ROCK_SIDES - MIN_ROCK_SIDES));
			for (j = 0; j < s; j ++) {
				theta = 2 * Math.PI / s * j;
				r = MIN_ROCK_SIZE + (int) (Math.random() * (MAX_ROCK_SIZE - MIN_ROCK_SIZE));
				x = (int) -Math.round(r * Math.sin(theta));
				y = (int)  Math.round(r * Math.cos(theta));
				asteroids[i].shape.addPoint(x, y);
			}
			asteroids[i].active = true;
			asteroids[i].angle = 0.0;
			asteroids[i].angSpeed = Math.random() * 2 * MAX_ROCK_SPIN - MAX_ROCK_SPIN;

			// Place the asteroid at one edge of the screen.

			if (Math.random() < 0.5) {
				asteroids[i].x = -Sprite_bk.width / 2;
				if (Math.random() < 0.5)
					asteroids[i].x = Sprite_bk.width / 2;
				asteroids[i].y = Math.random() * Sprite_bk.height;
			}
			else {
				asteroids[i].x = Math.random() * Sprite_bk.width;
				asteroids[i].y = -Sprite_bk.height / 2;
				if (Math.random() < 0.5)
					asteroids[i].y = Sprite_bk.height / 2;
			}

			// Set a random motion for the asteroid.

			asteroids[i].xspeed = Math.random() * asteroidsSpeed;
			if (Math.random() < 0.5)
				asteroids[i].xspeed = -asteroids[i].xspeed;
			asteroids[i].yspeed = Math.random() * asteroidsSpeed;
			if (Math.random() < 0.5)
				asteroids[i].yspeed = -asteroids[i].yspeed;

			asteroids[i].draw();
			asteroidIsSmall[i] = false;
		}

		asteroidsCounter = STORM_PAUSE;
		asteroidsLeft = MAX_ROCKS;
		if (asteroidsSpeed < MAX_ROCK_SPEED)
			asteroidsSpeed += 0.5;
	}

	public void initSmallAsteroids(int n) {

		int count;
		int i, j;
		int s;
		double tempX, tempY;
		double theta, r;
		int x, y;

		// Create one or two smaller asteroids from a larger one using inactive
		// asteroids. The new asteroids will be placed in the same position as the
		// old one but will have a new, smaller shape and new, randomly generated
		// movements.

		count = 0;
		i = 0;
		tempX = asteroids[n].x;
		tempY = asteroids[n].y;
		do {
			if (!asteroids[i].active) {
				asteroids[i].shape = new Polygon();
				s = MIN_ROCK_SIDES + (int) (Math.random() * (MAX_ROCK_SIDES - MIN_ROCK_SIDES));
				for (j = 0; j < s; j ++) {
					theta = 2 * Math.PI / s * j;
					r = (MIN_ROCK_SIZE + (int) (Math.random() * (MAX_ROCK_SIZE - MIN_ROCK_SIZE))) / 2;
					x = (int) -Math.round(r * Math.sin(theta));
					y = (int)  Math.round(r * Math.cos(theta));
					asteroids[i].shape.addPoint(x, y);
				}
				asteroids[i].active = true;
				asteroids[i].angle = 0.0;
				asteroids[i].angSpeed = Math.random() * 2 * MAX_ROCK_SPIN - MAX_ROCK_SPIN;
				asteroids[i].x = tempX;
				asteroids[i].y = tempY;
				asteroids[i].xspeed = Math.random() * 2 * asteroidsSpeed - asteroidsSpeed;
				asteroids[i].yspeed = Math.random() * 2 * asteroidsSpeed - asteroidsSpeed;
				asteroids[i].draw();
				asteroidIsSmall[i] = true;
				count++;
				asteroidsLeft++;
			}
			i++;
		} while (i < MAX_ROCKS && count < 2);
	}

	public void updateAsteroids() {

		int i, j;

		// Move any active asteroids and check for collisions.

		for (i = 0; i < MAX_ROCKS; i++)
			if (asteroids[i].active) {
				asteroids[i].update();
				asteroids[i].draw();

				// If hit by photon, kill asteroid and advance score. If asteroid is
				// large, make some smaller ones to replace it.

				for (j = 0; j < MAX_SHOTS; j++)
					if (photons[j].active && asteroids[i].active && asteroids[i].isColliding(photons[j])) {
						asteroidsLeft--;
						asteroids[i].active = false;
						photons[j].active = false;
						if (sound)
							explosionSound.play();
						explode(asteroids[i]);
						if (!asteroidIsSmall[i]) {
							score += BIG_POINTS;
							initSmallAsteroids(i);
						}
						else
							score += SMALL_POINTS;
					}


				if (player.isAlive() &&
						asteroids[i].active && asteroids[i].isColliding(ship)) {
					if (sound)
						crashSound.play();
					explode(ship);
					stopShip();
				}
			}
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

	public void explode(Sprite_bk s) {

		int c, i, j;
		int cx, cy;

		// Create sprites for explosion animation. The each individual line segment
		// of the given sprite is used to create a new sprite that will move
		// outward  from the sprite's original position with a random rotation.

		s.draw();
		c = 2;
		if (detail || s.sprite.npoints < 6)
			c = 1;
		for (i = 0; i < s.sprite.npoints; i += c) {
			explosionIndex++;
			if (explosionIndex >= MAX_SCRAP)
				explosionIndex = 0;
			explosions[explosionIndex].active = true;
			explosions[explosionIndex].shape = new Polygon();
			j = i + 1;
			if (j >= s.sprite.npoints)
				j -= s.sprite.npoints;
			cx = (int) ((s.shape.xpoints[i] + s.shape.xpoints[j]) / 2);
			cy = (int) ((s.shape.ypoints[i] + s.shape.ypoints[j]) / 2);
			explosions[explosionIndex].shape.addPoint(
					s.shape.xpoints[i] - cx,
					s.shape.ypoints[i] - cy);
			explosions[explosionIndex].shape.addPoint(
					s.shape.xpoints[j] - cx,
					s.shape.ypoints[j] - cy);
			explosions[explosionIndex].x = s.x + cx;
			explosions[explosionIndex].y = s.y + cy;
			explosions[explosionIndex].angle = s.angle;
			explosions[explosionIndex].angSpeed = 4 * (Math.random() * 2 * MAX_ROCK_SPIN - MAX_ROCK_SPIN);
			explosions[explosionIndex].xspeed = (Math.random() * 2 * MAX_ROCK_SPEED - MAX_ROCK_SPEED + s.xspeed) / 2;
			explosions[explosionIndex].yspeed = (Math.random() * 2 * MAX_ROCK_SPEED - MAX_ROCK_SPEED + s.yspeed) / 2;
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

		if ((up || down) && player.isAlive() && !thrustersPlaying) {
			if (sound && !paused)
				thrustersSound.loop();
			thrustersPlaying = true;
		}

		// Spacebar: fire a photon and start its counter.

		if (e.getKeyChar() == ' ' && player.isAlive()) {
			if (sound & !paused)
				fireSound.play();
			photonTime = System.currentTimeMillis();
			photonIndex++;
			if (photonIndex >= MAX_SHOTS)
				photonIndex = 0;
			photons[photonIndex].active = true;
			// photons[photonIndex].x = ship.x;
			// photons[photonIndex].y = ship.y;
			// photons[photonIndex].xspeed = 2 * MAX_ROCK_SPEED * -Math.sin(ship.angle);
			// photons[photonIndex].yspeed = 2 * MAX_ROCK_SPEED *  Math.cos(ship.angle);
			photons[photonIndex].x = player.getX();
			photons[photonIndex].y = player.getY();
			photons[photonIndex].xspeed = 2 * MAX_ROCK_SPEED * -Math.sin(player.getAng());
			photons[photonIndex].yspeed = 2 * MAX_ROCK_SPEED *  Math.cos(player.getAng());
		}

		// Allow upper or lower case characters for remaining keys.

		c = Character.toLowerCase(e.getKeyChar());

		// 'P' key: toggle pause mode and start or stop any active looping sound
		// clips.

		if (c == 'p') {
			if (paused) {
				if (sound && misslePlaying)
					missleSound.loop();
				if (sound && saucerPlaying)
					saucerSound.loop();
				if (sound && thrustersPlaying)
					thrustersSound.loop();
			}
			else {
				if (misslePlaying)
					missleSound.stop();
				if (saucerPlaying)
					saucerSound.stop();
				if (thrustersPlaying)
					thrustersSound.stop();
			}
			paused = !paused;
		}

		// 'M' key: toggle sound on or off and stop any looping sound clips.

		if (c == 'm' && loaded) {
			if (sound) {
				crashSound.stop();
				explosionSound.stop();
				fireSound.stop();
				missleSound.stop();
				saucerSound.stop();
				thrustersSound.stop();
				warpSound.stop();
			}
			else {
				if (misslePlaying && !paused)
					missleSound.loop();
				if (saucerPlaying && !paused)
					saucerSound.loop();
				if (thrustersPlaying && !paused)
					thrustersSound.loop();
			}
			sound = !sound;
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

		if (!up && !down && thrustersPlaying) {
			thrustersSound.stop();
			thrustersPlaying = false;
		}
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
		if (detail) {
			doubleBuffer.setColor(Color.white);
			for (i = 0; i < numStars; i++)
				doubleBuffer.drawLine(stars[i].x, stars[i].y, stars[i].x, stars[i].y);
		}

		// Draw photon bullets.

		doubleBuffer.setColor(Color.white);
		for (i = 0; i < MAX_SHOTS; i++)
			if (photons[i].active)
				doubleBuffer.drawPolygon(photons[i].sprite);

		// Draw the asteroids.

		for (i = 0; i < MAX_ROCKS; i++)
			if (asteroids[i].active) {
				if (detail) {
					doubleBuffer.setColor(Color.black);
					doubleBuffer.fillPolygon(asteroids[i].sprite);
				}
				doubleBuffer.setColor(Color.white);
				doubleBuffer.drawPolygon(asteroids[i].sprite);
				doubleBuffer.drawLine(asteroids[i].sprite.xpoints[asteroids[i].sprite.npoints - 1], asteroids[i].sprite.ypoints[asteroids[i].sprite.npoints - 1],
						asteroids[i].sprite.xpoints[0], asteroids[i].sprite.ypoints[0]);
			}

		// Draw the flying saucer.


		if (player.isAlive()) {
			doubleBuffer.setColor(Color.black);
			// offGraphics.fillPolygon(ship.sprite);
			doubleBuffer.fillPolygon(player.getObjSprite().getSprite());
			doubleBuffer.setColor(Color.white);
			// offGraphics.drawPolygon(ship.sprite);
			// offGraphics.drawLine(ship.sprite.xpoints[ship.sprite.npoints - 1], ship.sprite.ypoints[ship.sprite.npoints - 1],
			// ship.sprite.xpoints[0], ship.sprite.ypoints[0]);
			doubleBuffer.drawPolygon(player.getObjSprite().getSprite());
			doubleBuffer.drawLine(player.getObjSprite().getSprite().xpoints[player.getObjSprite().getSprite().npoints - 1], player.getObjSprite().getSprite().ypoints[ship.sprite.npoints - 1],
					player.getObjSprite().getSprite().xpoints[0], player.getObjSprite().getSprite().ypoints[0]);

			// Draw thruster exhaust if thrusters are on. Do it randomly to get a
			// flicker effect.

			/*if (!paused && detail && Math.random() < 0.5) {
        if (up) {
          offGraphics.drawPolygon(fwdThruster.sprite);
          offGraphics.drawLine(fwdThruster.sprite.xpoints[fwdThruster.sprite.npoints - 1], fwdThruster.sprite.ypoints[fwdThruster.sprite.npoints - 1],
                               fwdThruster.sprite.xpoints[0], fwdThruster.sprite.ypoints[0]);
        }
        if (down) {
          offGraphics.drawPolygon(revThruster.sprite);
          offGraphics.drawLine(revThruster.sprite.xpoints[revThruster.sprite.npoints - 1], revThruster.sprite.ypoints[revThruster.sprite.npoints - 1],
                               revThruster.sprite.xpoints[0], revThruster.sprite.ypoints[0]);
        }
      }*/
		}

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
		if (!sound) {
			s = "Mute";
			doubleBuffer.drawString(s, d.width - (fontWidth + fm.stringWidth(s)), d.height - fontHeight);
		}

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
