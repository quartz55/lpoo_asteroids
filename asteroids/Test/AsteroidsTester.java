package asteroids.Test;

import static org.junit.Assert.*;

import org.junit.Test;

import asteroids.GamePanel;
import asteroids.GameWindow;
import asteroids.Engine.InputEngine;
import asteroids.GameObjects.GameObject;

public class AsteroidsTester {

	@Test
	public void TestMove() {

		GamePanel gp = new GamePanel();
		GameWindow gw = new GameWindow(100, 100, gp);

		gw.open();

		gp.initTest(gw.getWindow());

		gp.getPlayer().setAng(0);
		InputEngine.getInstance().clearBools();
		InputEngine.getInstance().addInput('m');
		InputEngine.getInstance().KEY_UP = true;
		InputEngine.getInstance().KEY_RIGHT = true;

		double playerY = gp.getPlayer().getY();
		double playerX = gp.getPlayer().getX();

		Thread t = new Thread(new Runnable() {
			public void run(){
				gp.run();
			}
		});
		t.start();

		// Wait for thread t to run
		while (!t.isAlive());

		try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }

		assertTrue(gp.getPlayer().getY() < playerY);
		assertTrue(gp.getPlayer().getX() > playerX);

		InputEngine.getInstance().clearBools();
		InputEngine.getInstance().KEY_DOWN = true;
		InputEngine.getInstance().KEY_LEFT = true;

		gp.getPlayer().setXspeed(0);
		gp.getPlayer().setYspeed(0);
		playerY = gp.getPlayer().getY();
		playerX = gp.getPlayer().getX();

		try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }

		assertTrue(gp.getPlayer().getY() > playerY);
		assertTrue(gp.getPlayer().getX() < playerX);

		InputEngine.getInstance().addInput('q');

		gw.close();
	}

	@Test
	public void TestShoot() {
		GamePanel gp = new GamePanel();
		GameWindow gw = new GameWindow(200, 200, gp);

		gw.open();

		gp.initTest(gw.getWindow());

		assertTrue(gp.getBullets().isEmpty());
		InputEngine.getInstance().clearBools();
		InputEngine.getInstance().KEY_SPACE = true;

		Thread t = new Thread(new Runnable() {
			public void run(){
				gp.run();
			}
		});
		t.start();

		while (!t.isAlive());


		try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }

		assertTrue(!gp.getBullets().isEmpty());

		InputEngine.getInstance().addInput('q');

		gw.close();
	}

	@Test
	public void TestWarpAround() {
		GamePanel gp = new GamePanel();
		GameWindow gw = new GameWindow(200, 200, gp);

		gw.open();

		gp.initTest(gw.getWindow());
		Thread t = new Thread(new Runnable() {
			public void run(){
				gp.run();
			}
		});
		t.start();

		// Wait for thread t to run
		while (!t.isAlive());

		gp.getAsteroids().clear();
		gp.getPlayer().setAng(0);
		gp.getPlayer().setY(-GameObject.height/2);
		InputEngine.getInstance().clearBools();
		InputEngine.getInstance().KEY_UP = true;

		double playerY = gp.getPlayer().getY();

		try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }

		assertTrue(playerY < gp.getPlayer().getY());

		InputEngine.getInstance().addInput('q');
	}

	@Test
	public void TestDestroyAsteroid() {

		GamePanel gp = new GamePanel();
		GameWindow gw = new GameWindow(300, 300, gp);

		gw.open();

		gp.initTest(gw.getWindow());

		InputEngine.getInstance().clearBools();
		InputEngine.getInstance().KEY_SPACE = true;
		gp.removeAsteroids();
		gp.addAsteroid(gp.getPlayer().getX(), gp.getPlayer().getY()-50, 3);
		gp.getPlayer().setAng(0);

		assertTrue(!gp.getAsteroids().isEmpty());

		int prevSize = gp.getAsteroids().size();

		Thread t = new Thread(new Runnable() {
			public void run(){
				gp.run();
			}
		});
		t.start();

		// Wait for thread t to run
		while (!t.isAlive());

		try { Thread.sleep(300); } catch (InterruptedException e) { e.printStackTrace(); }

		assertTrue(prevSize < gp.getAsteroids().size());

		InputEngine.getInstance().addInput('q');
	}

	@Test
	public void TestGameOver() {

		GamePanel gp = new GamePanel();
		GameWindow gw = new GameWindow(200, 200, gp);

		gw.open();

		gp.initTest(gw.getWindow());

		InputEngine.getInstance().clearBools();
		gp.removeAsteroids();
		gp.addAsteroid(gp.getPlayer().getX(), gp.getPlayer().getY(), 3);
		gp.getPlayer().setInvuln(false);
		gp.setLives(1);

		assertTrue(gp.getLives() > 0 && gp.gameIsOn());

		Thread t = new Thread(new Runnable() {
			public void run(){
				gp.run();
			}
		});
		t.start();

		// Wait for thread t to run
		while (!t.isAlive());

		try { Thread.sleep(200); } catch (InterruptedException e) { e.printStackTrace(); }

		assertTrue(gp.getLives() == 0 && !gp.gameIsOn());

		InputEngine.getInstance().addInput('q');
	}

	@Test
	public void TestPause() {

		GamePanel gp = new GamePanel();
		GameWindow gw = new GameWindow(100, 100, gp);

		gw.open();

		gp.initTest(gw.getWindow());

		gp.removeAsteroids();

		assertTrue(!gp.gameIsPaused() && gp.gameIsOn());

		InputEngine.getInstance().clearBools();
		InputEngine.getInstance().addInput('p');

		Thread t = new Thread(new Runnable() {
			public void run(){
				gp.run();
			}
		});
		t.start();

		// Wait for thread t to run
		while (!t.isAlive());

		try { Thread.sleep(200); } catch (InterruptedException e) { e.printStackTrace(); }

		assertTrue(gp.gameIsPaused() && gp.gameIsOn());

		InputEngine.getInstance().addInput('q');
	}
}
