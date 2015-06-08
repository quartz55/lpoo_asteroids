package asteroids.GameObjects;

/**
 * Class to represent a Star in the game world
 * NOTE: Extends GameObject
 */
public class Star extends GameObject{

	/**
	 * Default constructor
	 * Creates a star in the specified position
	 * @param x Star's X position
	 * @param y Star's Y position
	 */
	public Star(double x, double y)
	{
		super();
		objSprite.addPoint(0, 0);

		this.x = x;
		this.y = y;

		this.xspeed = Math.random() * 0.04 - 0.02;
		this.yspeed = Math.random() * 0.04 - 0.02;
	}
}