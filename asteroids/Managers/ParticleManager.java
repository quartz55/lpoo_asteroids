package asteroids.Managers;

import java.awt.Graphics;
import java.util.ArrayList;

import asteroids.GameObjects.Particle;

public class ParticleManager {
	ArrayList<Particle> particles = new ArrayList<Particle>();

	public void update() {
		for (int i = 0; i < particles.size(); i++) {
			if (particles.get(i).isAlive())
				particles.get(i).update();
			else particles.remove(i--);
		}
	}

	public void draw(Graphics buffer) {
		for (int i = 0; i < particles.size(); i++)
			particles.get(i).draw(buffer);
	}

	public void addParticle(double x, double y, double xspeed, double yspeed, int size) {
		for (int i = 0; i < size; i++)
			particles.add(new Particle(x, y, xspeed, yspeed, 1, 500, 1));
	}

	public void explosion(double x, double y, int size) {
		for (int i = 0; i < size; i++)
			particles.add(new Particle(x, y, Math.random()*3 - 1.5, Math.random()*3 - 1.5, 1, 1000, 0.995));
	}
}