// Drew Misicko
package util;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

// class to manage audio
public class AudioManager 
{
	public static final AudioManager instance = new AudioManager();
	
	private Music playingMusic;
	
	// singleton: prevent instantiation from other classes
	private AudioManager() {}
	
	// plays
	public void play (Sound sound)
	{
		play(sound, 1);
	}
	
	// plays
	public void play(Sound sound, float volume)
	{
		play(sound, volume, 1);
	}
	
	// plays
	public void play(Sound sound, float volume, float pitch)
	{
		play(sound, volume, pitch, 0);
	}
	
	// plays
	public void play(Sound sound, float volume, float pitch, float pan)
	{
		if(!GamePreferences.instance.sound) return;
		sound.play(GamePreferences.instance.volSound * volume,
			pitch, pan);
	}
	
	// plays
	public void play(Music music)
	{
		stopMusic();
		playingMusic = music;
		if(GamePreferences.instance.music);
		{
			music.setLooping(true);
			music.setVolume(GamePreferences.instance.volMusic);
			music.play();
		}
	}
	
	// Hammertime
	public void stopMusic()
	{
		if(playingMusic != null) playingMusic.stop();
	}
	
	// changes music settings when prompted to by a settings update
	public void onSettingsUpdated()
	{
		if(playingMusic == null) return;
		playingMusic.setVolume(GamePreferences.instance.volMusic);
		if(GamePreferences.instance.music)
		{
			if(!playingMusic.isPlaying()) playingMusic.play();
		}
		else
			playingMusic.pause();
	}
}
