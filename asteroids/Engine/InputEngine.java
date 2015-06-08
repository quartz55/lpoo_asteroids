package asteroids.Engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JPanel;

@SuppressWarnings("serial")
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

	public static InputEngine getInstance()
	{
		if(instance == null){
			instance = new InputEngine();
		}
		return instance;
	}
	
	public void clearBools() {
		KEY_UP = false;
		KEY_DOWN = false;
		KEY_LEFT = false;
		KEY_RIGHT = false;
		KEY_SPACE = false;
	}
	
	public void addInput(char c) {
		inputQueue.add(c);
	}

	public boolean checkInput(char c) {
		for (int i = 0; i < inputQueue.size(); i++) {
			if (c == inputQueue.get(i)) {
				return true;
			}
		}
		return false;
	}
	
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