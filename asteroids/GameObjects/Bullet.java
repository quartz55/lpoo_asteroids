package asteroids.GameObjects;

public class Bullet extends GameObject{

	public Bullet()
	{
		super();
		objSprite.addPoint(1, 1);
		objSprite.addPoint(1, -1);
		objSprite.addPoint(-1, 1);
		objSprite.addPoint(-1, -1);
	}

}
