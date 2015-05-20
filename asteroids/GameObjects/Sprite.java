package asteroids.GameObjects;

import java.awt.Polygon;

public class Sprite 
{
	private Polygon base;
	private Polygon sprite;	

	public Sprite()
	{
		this.base = new Polygon();
		this.sprite = new Polygon();
	}

	public void draw(double x, double y, double angle, int width, int height) 
	{
		this.sprite = new Polygon();
		for (int i = 0; i < this.base.npoints; i++)
			this.sprite.addPoint((int) Math.round(this.base.xpoints[i] * Math.cos(angle) + this.base.ypoints[i] * Math.sin(angle)) + (int) Math.round(x) + width / 2,
					(int) Math.round(this.base.ypoints[i] * Math.cos(angle) - this.base.xpoints[i] * Math.sin(angle)) + (int) Math.round(y) + height / 2);
	}

	public boolean isColliding(Sprite s) 
	{
		for (int i = 0; i < s.sprite.npoints; i++)
			if (this.sprite.contains(s.sprite.xpoints[i], s.sprite.ypoints[i]))
				return true;
		for (int i = 0; i < this.sprite.npoints; i++)
			if (s.sprite.contains(this.sprite.xpoints[i], this.sprite.ypoints[i]))
				return true;
		return false;
	}
	
	public Polygon getSprite(){ return sprite; }
	
	public void addPoint(int x, int y)
	{
		this.base.addPoint(x, y);
	}
}
