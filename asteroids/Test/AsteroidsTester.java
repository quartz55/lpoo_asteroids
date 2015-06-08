package asteroids.Test;

import static org.junit.Assert.*;

import org.junit.Test;

import asteroids.GamePanel;
import asteroids.Engine.InputEngine;
import asteroids.GameObjects.GameObject;

public class AsteroidsTester {
	
	@Test
	public void TestMove() {
		GamePanel gp = new GamePanel();
		
		gp.initTest(0, 0);
		Thread t = new Thread(new Runnable() {
			public void run(){
				gp.run();
			}
		});
		t.start();

		// Wait for thread t to run
		while (!t.isAlive());
		

		gp.player.setAng(0);
		InputEngine.getInstance().KEY_UP = true;
		InputEngine.getInstance().KEY_RIGHT = true;

		double playerY = gp.player.getY();
		double playerX = gp.player.getX();

		try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }

		assertTrue(playerY > gp.player.getY());
		assertTrue(playerX < gp.player.getX());

		InputEngine.getInstance().addInput('q');
	}

	@Test
	public void TestShoot() {
		GamePanel gp = new GamePanel();
		
		gp.initTest(200, 200);
		Thread t = new Thread(new Runnable() {
			public void run(){
				gp.run();
			}
		});
		t.start();

		while (!t.isAlive());

		assertTrue(gp.bullets_al.isEmpty());

		InputEngine.getInstance().KEY_SPACE = true;
		System.out.println(gp.bullets_al.size());

		try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }

		System.out.println(gp.bullets_al.size());
		assertTrue(!gp.bullets_al.isEmpty());

		InputEngine.getInstance().addInput('q');
	}

	@Test
	public void TestWarpAround() {
		GamePanel gp = new GamePanel();
		
		gp.initTest(100, 100);
		Thread t = new Thread(new Runnable() {
			public void run(){
				gp.run();
			}
		});
		t.start();

		// Wait for thread t to run
		while (!t.isAlive());
		
		gp.asteroids_al.clear();
		gp.player.setAng(0);
		gp.player.setY(-GameObject.height/2);
		InputEngine.getInstance().KEY_UP = true;

		double playerY = gp.player.getY();
		System.out.println(playerY);

		try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }

		System.out.println(gp.player.getY());
		assertTrue(playerY < gp.player.getY());

		InputEngine.getInstance().addInput('q');
	}

	@Test
	public void TestDestroyAsteroid() {
		GamePanel gp = new GamePanel();
		
		gp.initTest(400, 400);
		Thread t = new Thread(new Runnable() {
			public void run(){
				gp.run();
			}
		});
		t.start();

		// Wait for thread t to run
		while (!t.isAlive());
		
		InputEngine.getInstance().KEY_SPACE = true;
		InputEngine.getInstance().KEY_RIGHT = true;

		int prevSize = gp.asteroids_al.size();
		System.out.println(prevSize);

		try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }

		System.out.println(gp.asteroids_al.size());
		assertTrue(prevSize < gp.asteroids_al.size());

		InputEngine.getInstance().addInput('q');
	}

}
