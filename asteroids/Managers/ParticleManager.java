package asteroids.Managers;

import java.awt.Graphics;
import java.util.ArrayList;

import asteroids.GameObjects.Particle;

/**
 * Class responsible for managing all the particles in the game world
 */
public class ParticleManager {
	ArrayList<Particle> particles = new ArrayList<Particle>();

	/**
	 * Calls the update method for all particles in the list
	 */
	public void update() {
		for (int i = 0; i < particles.size(); i++) {
			if (particles.get(i).isAlive())
				particles.get(i).update();
			else particles.remove(i--);
		}
	}

	/**
	 * Calls the draw method for all particles in the list
	 * @param buffer Buffer to draw to
	 */
	public void draw(Graphics buffer) {
		for (int i = 0; i < particles.size(); i++)
			particles.get(i).draw(buffer);
	}

	/**
	 * Adds a particle with the specified parameters to the list
	 * @param x Particle's X position
	 * @param y Particle's Y position
	 * @param xspeed Particle's X velocity
	 * @param yspeed Particle's X velocity
	 * @param size Particle's size
	 */
	public void addParticle(double x, double y, double xspeed, double yspeed, int size) {
		for (int i = 0; i < size; i++)
			particles.add(new Particle(x, y, xspeed, yspeed, 1, 500, 1));
	}

	/**
	 * Creates an explosion effect
	 * @param x Explosion's center X position
	 * @param y Explosion's center Y position
	 * @param size Number of particles used in the effect
	 */
	public void explosion(double x, double y, int size) {
		for (int i = 0; i < size; i++)
			particles.add(new Particle(x, y, Math.random()*3 - 1.5, Math.random()*3 - 1.5, 1, 1000, 0.995));
	}
}