package asteroids.GameObjects;

import java.awt.Graphics;

/**
 * Class to represent a Particle in the game world
 * NOTE: Extends GameObject
 */
public class Particle extends GameObject{
	int lifeTime = 0;
	float alpha = 1;
	Thread lifeThread;

	/**
	 * Default constructor
	 * @param x Particle's X position
	 * @param y Particle's Y position
	 * @param xspeed Particle's X velocity
	 * @param yspeed Particle's X velocity
	 * @param size Particle's size
	 * @param lifeT Particle's life time
	 * @param friction Particle's friction
	 */
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

	/**
	 * Overrides the game object's update method in order to remove the particle if it goes off screen
	 */
	@Override
	public boolean update() {
		if (super.update()){
			lifeTime = 0;
			alpha = 0;
			return true;
		}
		return false;
	}

	/**
	 * Overrides the game object's draw method in order to draw with alpha
	 */
	@Override
	public void draw(Graphics buffer) {
		super.draw(buffer, alpha);
	}

	/**
	 * @return TRUE if particle's lifetime is greater than 0 FALSE if not
	 */
	public boolean isAlive() { return lifeTime > 0; }
}
