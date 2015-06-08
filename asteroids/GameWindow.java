package asteroids;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import asteroids.Engine.InputEngine;

/**
 * Class responsible for creating a window with a game panel
 */
public class GameWindow {
	JFrame window;

	/**
	 * Default constructor
	 * Creates a window with the specified size and adds the game panel to the pane
	 * @param w Width of the window
	 * @param h Height of the window
	 * @param gp Game panel to add
	 */
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

	/**
	 * Sets the window visibility to true
	 */
	public void open() {
		window.setVisible(true);
	}
	/**
	 * Sets the window visibility to false
	 */
	public void close() {
		window.setVisible(false);
	}

	/**
	 * @return Current JFrame in use
	 */
	public JFrame getWindow() { return this.window; }
}
