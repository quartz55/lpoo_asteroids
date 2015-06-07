package asteroids.GameObjects;

import java.awt.Color;
import java.awt.Graphics;
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

	public void draw(double x, double y, double angle, int width, int height, Color fill, Color outline, Graphics buffer) 
	{
		this.sprite = new Polygon();
		for (int i = 0; i < this.base.npoints; i++)
			this.sprite.addPoint((int) Math.round(this.base.xpoints[i] * Math.cos(angle) + this.base.ypoints[i] * Math.sin(angle)) + (int) Math.round(x) + width / 2,
					(int) Math.round(this.base.ypoints[i] * Math.cos(angle) - this.base.xpoints[i] * Math.sin(angle)) + (int) Math.round(y) + height / 2);


		buffer.setColor(fill);
		buffer.fillPolygon(sprite);
		buffer.setColor(outline);
		buffer.drawPolygon(sprite);
		buffer.drawLine(sprite.xpoints[sprite.npoints - 1], sprite.ypoints[sprite.npoints - 1],
				sprite.xpoints[0], sprite.ypoints[0]);

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
