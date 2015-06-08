package asteroids.GameObjects;

public class Asteroid extends GameObject{

	static final int    MIN_ROCK_SIDES =   6;
	static final int    MAX_ROCK_SIDES =  16;
	static final int    MIN_ROCK_SIZE  =  10;
	static final int    MAX_ROCK_SIZE  =  20;
	static final double MIN_ROCK_SPEED =  1.32;
	static final double MAX_ROCK_SPEED = 1.664;
	static final double MAX_ROCK_SPIN  = 0.0446;
	public static int MAX_SIZE_MULT  = 2;

	private int size;

	public Asteroid(int size, double x, double y)
	{
		super();
		
		this.size = size;
		
		generateAsteroid();
		
		this.x = x;
		this.y = y;

	}

	public Asteroid(int size)
	{
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

	public Asteroid()
	{
		super();
		
		this.size = (int) (Math.random()*(MAX_SIZE_MULT))+1;
		generateAsteroid();

		/* Set random position */
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

	private void generateAsteroid()
	{
		int s;
		double theta, r;
		int point_x, point_y;

		s = MIN_ROCK_SIDES + (int) (Math.random() * (MAX_ROCK_SIDES - MIN_ROCK_SIDES));
		for (int j = 0; j < s; j ++) {
			theta = 2 * Math.PI / s * j;
			r = (MIN_ROCK_SIZE + (int) (Math.random() * (MAX_ROCK_SIZE - MIN_ROCK_SIZE))) * this.size;
			point_x = (int) -Math.round(r * Math.sin(theta));
			point_y = (int)  Math.round(r * Math.cos(theta));
			objSprite.addPoint(point_x, point_y);
		}
		angle = 0.0;
		angSpeed = Math.random() * 2 * MAX_ROCK_SPIN - MAX_ROCK_SPIN;

		this.xspeed = Math.random() * MAX_ROCK_SPEED;
		if (Math.random() < 0.5)
			this.xspeed = -this.xspeed;
		yspeed = Math.random() * MAX_ROCK_SPEED;
		if (Math.random() < 0.5)
			this.yspeed = -this.yspeed;
	}

	public int getSize(){return size;}

}
