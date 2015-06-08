package asteroids;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import asteroids.Engine.InputEngine;

public class main {
	public static void main(String[] args)
	{
		GamePanel gp = new GamePanel();
		GameWindow gw = new GameWindow(800, 640, gp);
		
		gw.open();

		gp.start(gw.getWindow());
		
		gw.close();

		System.exit(0);
	}
}
