package asteroids.GameObjects;

public class Bullet extends GameObject{
	
	public static final double BULLET_SPEED = 3;

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
