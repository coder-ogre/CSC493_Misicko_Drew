// Drew Misicko
package util;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

// manages audio
public class AudioManager 
{
	// because singleton...
	public static final AudioManager instance = new AudioManager();
	
	private Music playingMusic1;
	private Music playingMusic2;
	
	// singleton: prevent instantiation from other lcasses
	private AudioManager()
	{ 
		
	}
	
	//plays
	public void play (Sound sound)
	{
		play(sound, 1);
	}
	
	// plays sound
	public void play(Sound sound, float volume)
	{
		play(sound, volume, 1);
	}
	
	// plays sound
	public void play(Sound sound, float volume, float pitch)
	{
		play(sound, volume, pitch, 0);
	}
	
	// plays sound
	public void play(Sound sound, float volume, float pitch, float pan)
	{
		if(!GamePreferences.instance.sound) return;
		sound.play(GamePreferences.instance.volSound * volume, pitch, pan);
	}
	
	// plays music
	public void play1(Music music)
	{
		stopMusic1();
		playingMusic1 = music;
		if(GamePreferences.instance.music)
		{
			music.setLooping(true);
			music.setVolume(GamePreferences.instance.volMusic);
			music.play();
		}
	}
	
	// plays music
		public void play2(Music music)
		{
			stopMusic2();
			playingMusic2 = music;
			if(GamePreferences.instance.altMusic)
			{
				music.setLooping(true);
				music.setVolume(GamePreferences.instance.volMusic);
				music.play();
			}
		}
	
		// stops the music
		//public void stopMusic()
		//{
		//	if(playingMusic1 != null) playingMusic1.stop();
		//}
		
	// stops the music
	public void stopMusic1()
	{
		if(playingMusic1 != null) playingMusic1.stop();
	}
	
	// stops the music
		public void stopMusic2()
		{
			if(playingMusic2 != null) playingMusic2.stop();
		}
	
	// allows audio settings to be updated
	public void onSettingsUpdated1()
	{
		if(playingMusic1 == null) return;
		playingMusic1.setVolume(GamePreferences.instance.volMusic);
		if(GamePreferences.instance.music)
		{
			if(!playingMusic1.isPlaying()) playingMusic1.play();
		}
		else
			playingMusic1.pause();		
	}
	
	// allows audio settings to be updated
		public void onSettingsUpdated2()
		{			
			if(playingMusic2 == null) return;
			playingMusic2.setVolume(GamePreferences.instance.volMusic);
			if(GamePreferences.instance.altMusic)
			{
				if(!playingMusic2.isPlaying()) playingMusic2.play();
			}
			else
				playingMusic2.pause();
		}
}