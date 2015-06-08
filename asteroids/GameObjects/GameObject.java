package asteroids.GameObjects;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Base class for the game objects (Like Ship and Asteroid)
 * It is extended by all the other objects
 */
public class GameObject
{
	public static int width, height; // Size of the game panel (used to check if an object is out of bounds)

	protected double angle;
	protected double angSpeed;
	protected double x, y;
	protected double xspeed, yspeed;
	protected double friction = 1;
	protected Sprite objSprite;

	/**
	 * Default constructor
	 * Sets everything to 0
	 */
	public GameObject()
	{
		this.angle = 0;
		this.angSpeed = 0;
		this.x = this.y = 0;
		this.xspeed = this.yspeed = 0;
		this.objSprite = new Sprite();
	}

	/**
	 * Updates all the data in the game object
	 *   - Moves object
	 *   - Rotates object
	 *   - Applies friction
	 * @return TRUE if object went out of bounds FALSE if not
	 */
	public boolean update()
	{
		boolean wrapped;

		this.angle += this.angSpeed;
		if (this.angle < 0)
			this.angle += 2 * Math.PI;
		if (this.angle > 2 * Math.PI)
			this.angle -= 2 * Math.PI;

		wrapped = false;

		this.xspeed *= this.friction;
		this.x += this.xspeed;

		if (this.x < -width / 2) {
			this.x += width;
			wrapped = true;
		}
		if (this.x > width / 2) {
			this.x -= width;
			wrapped = true;
		}

		this.yspeed *= this.friction;
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

	/**
	 * Draws the Sprite of the game object to the specified buffer
	 * @param buffer Buffer to draw to
	 * @param fill Fill color for Sprite
	 * @param outline Outline color for Sprite
	 */
	public void draw(Graphics buffer, Color fill, Color outline)
	{
		this.objSprite.draw(x, y, angle, width, height, fill, outline, buffer);
	}

	/**
	 * Draws the Sprite of the game object with the provided alpha to the specified buffer
	 * NOTE: Default color is BLACK for fill and WHITE for outline
	 * @param buffer Buffer to draw to
	 * @param alpha Sprite alpha
	 */
	public void draw(Graphics buffer, float alpha)
	{
		draw(buffer, new Color(0, 0, 0, alpha), new Color(1, 1, 1, alpha));
	}

	/**
	 * Draws the Sprite of the game object to the specified buffer
	 * NOTE: Default color is BLACK for fill and WHITE for outline
	 * @param buffer
	 */
	public void draw(Graphics buffer)
	{
		draw(buffer, Color.BLACK, Color.WHITE);
	}

	/**
	 * Checks if two game objects are colliding with each other
	 * @param obj Game object to check with
	 * @return TRUE if colliding FALSE if not
	 */
	public boolean checkCollision(GameObject obj)
	{
		return this.objSprite.isColliding(obj.getObjSprite());
	}

	/**
	 * @return Game object's sprite
	 */
	public Sprite getObjSprite(){ return this.objSprite;}

	/**
	 * @return Game object's X position
	 */
	public double getX(){return x;}
	/**
	 * @return Game object's Y position
	 */
	public double getY(){return y;}
	/**
	 * Sets the current game object's Y position to the specified one
	 * @param Y
	 */
	public void setY(double Y){this.y = Y;}
	/**
	 * @return Game object's current angle
	 */
	public double getAng(){return angle;}
	/**
	 * Sets the current game object's angle to the specified one
	 * @param ang
	 */
	public void setAng(double ang){this.angle = ang;}
	/**
	 * @return Game object's X velocity
	 */
	public double getXspeed(){return xspeed;}
	/**
	 * Sets the current game object's X velocity to the specified one
	 * @param xsp
	 */
	public void setXspeed(double xsp){this.xspeed = xsp;}
	/**
	 * @return Game object's Y velocity
	 */
	public double getYspeed(){return yspeed;}
	/**
	 * Sets the current game object's Y velocity to the specified one
	 * @param ysp
	 */
	public void setYspeed(double ysp){this.yspeed = ysp;}
}
