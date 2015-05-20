package asteroids.Engine;

public class SoundEngine 
{
	private static SoundEngine instance = null;
	
	private static boolean muted = false;

	protected SoundEngine()
	{
		loadSounds();
	}
	
	private void loadSounds()
	{
		
	}
	
	public static void toggleMute() { muted = !muted; }

	public static SoundEngine getInstance()
	{
		if(instance == null){
			instance = new SoundEngine();
		}
		return instance;
	}
}