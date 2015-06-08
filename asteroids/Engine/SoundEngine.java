package asteroids.Engine;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

/**
 * Singleton class responsible for managin and playing sound files
 */
public class SoundEngine
{
	private static SoundEngine instance = null;

	private static boolean muted = false;

	private SoundEngine() {
	}

	/**
	 * Plays a sound
	 * @param filename Sound's filename
	 */
	public void playSound(String filename) {
		if (muted) return;
		try {
			java.net.URL url = this.getClass().getClassLoader().getResource(filename);
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
			Clip clip = AudioSystem.getClip();
			clip.addLineListener(new LineListener() {
				@Override
				public void update(LineEvent event) {
					if (event.getType() == LineEvent.Type.STOP)
						clip.close();
				}
			});
			clip.open(audioIn);
			clip.start();
		} catch (Exception e) {e.printStackTrace();}
	}

	/**
	 * Loops a sound
	 * @param filename Sound's filename
	 */
	public void loopSound(String filename) {
		if (muted) return;
		try {
			java.net.URL url = this.getClass().getClassLoader().getResource(filename);
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
			Clip clip = AudioSystem.getClip();
			clip.addLineListener(new LineListener() {

				@Override
				public void update(LineEvent event) {
					if (event.getType() == LineEvent.Type.STOP)
						clip.close();
				}
			});
			clip.open(audioIn);
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		} catch (Exception e) {e.printStackTrace();}
	}

	/**
	 * Toggles the muted state
	 */
	public void toggleMute() { muted = !muted; }

	/**
	 * Returns the Singleton instance
	 * NOTE: Creates one if it doesn't exist yet
	 * @return Singleton instance
	 */
	public synchronized static SoundEngine getInstance() {
		if(instance == null){
			instance = new SoundEngine();
		}
		return instance;
	}
}