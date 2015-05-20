package asteroids.GameObjects;

public class Ship extends GameObject{

	private final double SHIP_ANGLE_STEP = 0.01;
	private final double SHIP_SPEED_STEP = 0.01;
	private final double MAX_SHIP_SPEED  = 3;
	
	private boolean alive;

	public Ship()
	{
		super();
		objSprite.addPoint(0, -10);
		objSprite.addPoint(7, 10);
		objSprite.addPoint(-7, 10);
		alive = true;
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
		double speed = Math.sqrt(xspeed * xspeed + yspeed * yspeed);
		if (speed > MAX_SHIP_SPEED) {
			dx = MAX_SHIP_SPEED * -Math.sin(angle);
			dy = MAX_SHIP_SPEED *  Math.cos(angle);
		}
		xspeed += dx;
		yspeed += dy;
	}
	public void moveBack()
	{

		double dx = SHIP_SPEED_STEP * -Math.sin(angle);
		double dy = SHIP_SPEED_STEP *  Math.cos(angle);
		double speed = Math.sqrt(xspeed * xspeed + yspeed * yspeed);
		if (speed > MAX_SHIP_SPEED) {
			dx = MAX_SHIP_SPEED * -Math.sin(angle);
			dy = MAX_SHIP_SPEED *  Math.cos(angle);
		}
		xspeed -= dx;
		yspeed -= dy;
	}
	
	public boolean isAlive(){return alive;}
	public void setAlive(boolean alive){this.alive = alive;}
}
