package asteroids.GameObjects;

/**
 * Class to represent a Bullet in the game world
 * NOTE: Extends GameObject
 */
public class Bullet extends GameObject{

	public static final double BULLET_SPEED = 6;

	/**
	 * Default constructor
	 * Creates a bullet in the specified position and sets the direction to the one the player's ship is pointing at
	 * @param x Bullet's X position
	 * @param y Bullet's Y position
	 * @param plAng Player's ship angle
	 */
	public Bullet(double x, double y, double plAng)
	{
		super();
		objSprite.addPoint(1, 1);
		objSprite.addPoint(1, -1);
		objSprite.addPoint(-1, 1);
		objSprite.addPoint(-1, -1);

		this.x = x;
		this.y = y;

		double temp_xspeed = BULLET_SPEED * -Math.sin(plAng);
		double temp_yspeed = BULLET_SPEED * Math.cos(plAng);
		this.xspeed = temp_xspeed;
		this.yspeed = temp_yspeed;
	}

}
