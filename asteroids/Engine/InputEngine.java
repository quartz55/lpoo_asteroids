package asteroids.Engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JPanel;

/**
 * Singleton class responsible for the game input system
 */
public class InputEngine extends JPanel{

	private static InputEngine instance = null;

	public boolean KEY_UP;
	public boolean KEY_DOWN;
	public boolean KEY_LEFT;
	public boolean KEY_RIGHT;
	public boolean KEY_SPACE;

	private ArrayList<Character> inputQueue;

	private InputEngine() {
		KEY_UP = false;
		KEY_DOWN = false;
		KEY_LEFT = false;
		KEY_RIGHT = false;
		KEY_SPACE = false;

		inputQueue = new ArrayList<Character>();

		KeyListener listener = new MyListener();
		addKeyListener(listener);
		requestFocus();
		setFocusable(true);
	}

	/**
	 * Returns the Singleton instance
	 * NOTE: Creates one if it doesn't exist yet
	 * @return Singleton instance
	 */
	public synchronized static InputEngine getInstance()
	{
		if(instance == null){
			instance = new InputEngine();
		}
		return instance;
	}

	/**
	 * Changes all the key states to FALSE
	 */
	public void clearBools() {
		KEY_UP = false;
		KEY_DOWN = false;
		KEY_LEFT = false;
		KEY_RIGHT = false;
		KEY_SPACE = false;
	}

	/**
	 * Adds "artificial" input to the engine
	 * NOTE: Mostly used for testing purposes
	 * @param c Character code to add
	 */
	public void addInput(char c) {
		inputQueue.add(c);
	}

	/**
	 * Checks the current frame input queue for the character code
	 * @param c Character code to check
	 * @return TRUE if the code is present in the queue FALSE if not
	 */
	public boolean checkInput(char c) {
		for (int i = 0; i < inputQueue.size(); i++) {
			if (c == inputQueue.get(i)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Clears the engine's input queue
	 */
	public void clearInput() {
		inputQueue = new ArrayList<Character>();
	}

	public class MyListener implements KeyListener {
		@Override
		public void keyTyped(KeyEvent e) {
		}

		@Override
		public void keyPressed(KeyEvent e) {
			inputQueue.add(e.getKeyChar());
			if (e.getKeyCode() == KeyEvent.VK_LEFT)
				KEY_LEFT = true;
			if (e.getKeyCode() == KeyEvent.VK_RIGHT)
				KEY_RIGHT = true;
			if (e.getKeyCode() == KeyEvent.VK_UP)
				KEY_UP = true;
			if (e.getKeyCode() == KeyEvent.VK_DOWN)
				KEY_DOWN = true;
			if (e.getKeyCode() == KeyEvent.VK_SPACE)
				KEY_SPACE = true;
		}

		@Override
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_LEFT)
				KEY_LEFT = false;
			if (e.getKeyCode() == KeyEvent.VK_RIGHT)
				KEY_RIGHT = false;
			if (e.getKeyCode() == KeyEvent.VK_UP)
				KEY_UP = false;
			if (e.getKeyCode() == KeyEvent.VK_DOWN)
				KEY_DOWN = false;
			if (e.getKeyCode() == KeyEvent.VK_SPACE)
				KEY_SPACE = false;
		}
	};
}