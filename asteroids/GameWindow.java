package asteroids;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import asteroids.Engine.InputEngine;

public class GameWindow {
	JFrame window;

	public GameWindow(int w, int h, GamePanel gp) {
		window = new JFrame();
		window.setSize(w, h);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.getContentPane().setLayout(new BorderLayout(0, 0));
		window.setResizable(false);
		window.setLocationRelativeTo(null);
		
		window.getContentPane().add(InputEngine.getInstance());

		window.getContentPane().add(gp, BorderLayout.CENTER);
	}
	
	public void open() {
		window.setVisible(true);
	}
	public void close() {
		window.setVisible(false);
	}
	
	public JFrame getWindow() { return this.window; }
}
