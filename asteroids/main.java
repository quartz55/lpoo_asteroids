package asteroids;

import java.awt.BorderLayout;

import javax.swing.JFrame;

public class main {
	public static void main(String[] args)
	{
		JFrame window = new JFrame();
		window.setSize(800, 640);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.getContentPane().setLayout(new BorderLayout(0, 0));
		window.setResizable(false);
		window.setLocationRelativeTo(null);

		GamePanel gp = new GamePanel();
		window.getContentPane().add(gp, BorderLayout.CENTER);

		window.setVisible(true);

		gp.init();
		
		window.setVisible(false);
		System.exit(0);
	}
}
