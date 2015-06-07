package asteroids.GameObjects;

import java.awt.Graphics;

public class Ship extends GameObject{

	private final double SHIP_ANGLE_STEP = 0.03;
	private final double SHIP_SPEED_STEP = 0.06;

	private boolean alive;
	private boolean invuln;

	public Ship()
	{
		super();
		objSprite.addPoint(0, -10);
		objSprite.addPoint(7, 10);
		objSprite.addPoint(-7, 10);
		alive = true;
		invuln = true;
		this.friction = 0.995;
	}

	@Override
	public void draw(Graphics buffer) {
		if (invuln)
			super.draw(buffer, (float) 0.5);
		else super.draw(buffer);
	}

	public void rotLeft()
	{
		angle += SHIP_ANGLE_STEP;
		if (angle > 2 * Math.PI)
			angle -= 2 * Math.PI;
	}
	public void rotRight()
	{
		angle -= SHIP_ANGLE_STEP;
		if (angle < 0)
			angle += 2 * Math.PI;
	}
	public void moveFwd()
	{
		double dx = SHIP_SPEED_STEP * -Math.sin(angle);
		double dy = SHIP_SPEED_STEP *  Math.cos(angle);
		xspeed += dx;
		yspeed += dy;
	}
	public void moveBack()
	{
		double dx = SHIP_SPEED_STEP * -Math.sin(angle);
		double dy = SHIP_SPEED_STEP *  Math.cos(angle);
		xspeed -= dx;
		yspeed -= dy;
	}

	public boolean isAlive(){return alive;}
	public void setAlive(boolean alive){this.alive = alive;}

	public boolean isInvuln(){return invuln;}
	public void setInvuln(boolean invuln){this.invuln = invuln;}
}
