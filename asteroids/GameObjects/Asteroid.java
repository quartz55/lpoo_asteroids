package asteroids.GameObjects;

/**
 * Class to represent an Asteroid in the game world
 * NOTE: Extends GameObject
 */
public class Asteroid extends GameObject{

	static final int    MIN_SIDES     =  6;
	static final int    MAX_SIDES     = 16;
	static final int    MIN_SIZE      = 10;
	static final int    MAX_SIZE      = 20;
	static final double MIN_SPEED     = 1.32;
	static final double MAX_SPEED     = 1.664;
	static final double MAX_SPIN      = 0.0446;
	public static int   MAX_SIZE_MULT = 2;

	private int size;

	/**
	 * Default constructor
	 *    - Generates an asteroid with a random size between 1 and MAX_SIZE_MULT
	 *    - Places the asteroid in a random position around the edges of the screen
	 *    - Attributes a random X and Y velocity as well as angular velocity
	 */
	public Asteroid() {
		this((int) (Math.random()*(MAX_SIZE_MULT))+1);
	}

	/**
	 * Size constructor
	 * Generates an asteroid with the specified size
	 * @param size
	 */
	public Asteroid(int size) {
		super();

		this.size = size;

		generateAsteroid();

		if (Math.random() < 0.5) {
			x = -GameObject.width / 2;
			if (Math.random() < 0.5)
				x = GameObject.width / 2;
			this.y = Math.random() * GameObject.height;
		}
		else {
			this.x = Math.random() * GameObject.width;
			y = -GameObject.height / 2;
			if (Math.random() < 0.5)
				y = GameObject.height / 2;
		}
	}

	/**
	 * Specific constructor
	 * Generates an asteroid with the specified size and X and Y positions
	 * @param size
	 * @param x
	 * @param y
	 */
	public Asteroid(int size, double x, double y) {
		super();

		this.size = size;

		generateAsteroid();

		this.x = x;
		this.y = y;

	}

	private void generateAsteroid()
	{
		int s;
		double theta, r;
		int point_x, point_y;

		s = MIN_SIDES + (int) (Math.random() * (MAX_SIDES - MIN_SIDES));
		for (int j = 0; j < s; j ++) {
			theta = 2 * Math.PI / s * j;
			r = (MIN_SIZE + (int) (Math.random() * (MAX_SIZE - MIN_SIZE))) * this.size;
			point_x = (int) -Math.round(r * Math.sin(theta));
			point_y = (int)  Math.round(r * Math.cos(theta));
			objSprite.addPoint(point_x, point_y);
		}
		angle = 0.0;
		angSpeed = Math.random() * 2 * MAX_SPIN - MAX_SPIN;

		this.xspeed = Math.random() * MAX_SPEED;
		if (Math.random() < 0.5)
			this.xspeed = -this.xspeed;
		yspeed = Math.random() * MAX_SPEED;
		if (Math.random() < 0.5)
			this.yspeed = -this.yspeed;
	}

	/**
	 * @return Current asteroid size
	 */
	public int getSize(){return size;}

}
