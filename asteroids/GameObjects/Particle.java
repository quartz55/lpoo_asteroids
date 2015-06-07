package asteroids.GameObjects;

import java.awt.Color;
import java.awt.Graphics;

public class Particle extends GameObject{
	int lifeTime = 0;
	float alpha = 1;
	Thread lifeThread;

	public Particle(double x, double y, double xspeed, double yspeed, int size, int lifeT, double friction) {
		super();
		objSprite.addPoint(-1, -1);
		objSprite.addPoint(1, -1);
		objSprite.addPoint(-1, 1);
		objSprite.addPoint(1, 1);
		
		this.x = x;
		this.y = y;
		this.xspeed = xspeed;
		this.yspeed = yspeed;
		this.friction = friction;
		this.lifeTime = lifeT;

		lifeThread = new Thread(){
			public void run(){
				float alphaStep = (float) (1.0 / lifeT * 100);

				while (lifeTime > 0) {
					try {Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
					lifeTime -= 100;
					alpha -= alphaStep;
				}
				lifeTime = 0;
				alpha = 0;
			}
		};
		lifeThread.start();
	}
	
	@Override
	public boolean update() {
		if (super.update()){
			lifeTime = 0;
			alpha = 0;
			return true;
		}
		return false;
	}

	@Override
	public void draw(Graphics buffer) {
		super.draw(buffer, alpha);
	}
	
	public boolean isAlive() { return lifeTime > 0; }
}
