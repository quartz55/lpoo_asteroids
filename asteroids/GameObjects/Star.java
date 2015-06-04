package asteroids.GameObjects;

public class Star extends GameObject{

	public Star(double x, double y)
	{
		super();
		objSprite.addPoint(0, 0);

		this.x = x;
		this.y = y;

		this.xspeed = Math.random() * 0.02 - 0.01;
		this.yspeed = Math.random() * 0.02 - 0.01;
	}
}