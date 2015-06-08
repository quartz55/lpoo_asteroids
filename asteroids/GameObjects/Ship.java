package asteroids.GameObjects;

import java.awt.Graphics;

/**
 * Class to represent a Ship in the game world
 * NOTE: Extends GameObject
 */
public class Ship extends GameObject{

	private final double SHIP_ANGLE_STEP = 0.06;
	private final double SHIP_SPEED_STEP = 0.12;

	private boolean alive;
	private boolean invuln;

	/**
	 * Default constructor
	 */
	public Ship() {
		super();
		objSprite.addPoint(0, -10);
		objSprite.addPoint(7, 10);
		objSprite.addPoint(-7, 10);
		alive = true;
		invuln = true;
		this.friction = 0.9975;
	}

	/**
	 * Overrides the game object's draw method to make the ship flicker when invulnerable
	 */
	@Override
	public void draw(Graphics buffer) {
		if (invuln)
			super.draw(buffer, (Math.random() > 0.5 ? (float) 0.1 : 1));
		else super.draw(buffer);
	}

	/**
	 * Rotates the ship left based on the angular speed
	 */
	public void rotLeft() {
		angle += SHIP_ANGLE_STEP;
		if (angle > 2 * Math.PI)
			angle -= 2 * Math.PI;
	}
	/**
	 * Rotates the ship right based on the angular speed
	 */
	public void rotRight() {
		angle -= SHIP_ANGLE_STEP;
		if (angle < 0)
			angle += 2 * Math.PI;
	}
	/**
	 * Moves the ship forward based on the current angle
	 */
	public void moveFwd() {
		double dx = SHIP_SPEED_STEP * -Math.sin(angle);
		double dy = SHIP_SPEED_STEP *  Math.cos(angle);
		xspeed += dx;
		yspeed += dy;
	}
	/**
	 * Moves the ship backward based on the current angle
	 */
	public void moveBack() {
		double dx = SHIP_SPEED_STEP * -Math.sin(angle);
		double dy = SHIP_SPEED_STEP *  Math.cos(angle);
		xspeed -= dx;
		yspeed -= dy;
	}

	/**
	 * @return TRUE if ship is alive FALSE if not
	 */
	public boolean isAlive(){return alive;}
	/**
	 * Sets the ship's alive value to the specified one
	 * @param alive New alive value
	 */
	public void setAlive(boolean alive){this.alive = alive;}
	/**
	 * @return TRUE if the ship is invulnerable FALSE if not
	 */
	public boolean isInvuln(){return invuln;}
	/**
	 * Sets the ship's invulnerability value to the specified one
	 * @param invuln New invuln value
	 */
	public void setInvuln(boolean invuln){this.invuln = invuln;}
}
