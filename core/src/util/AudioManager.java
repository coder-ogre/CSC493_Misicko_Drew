// Drew Misicko
package util;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

// manages audio
public class AudioManager 
{
	// because singleton...
	public static final AudioManager instance = new AudioManager();
	
	private Music playingMusic;
	
	// singleton: prevent instantiation from other lcasses
	private AudioManager() { }
	
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
	public void play(Music music)
	{
		stopMusic();
		playingMusic = music;
		if(GamePreferences.instance.music)
		{
			music.setLooping(true);
			music.setVolume(GamePreferences.instance.volMusic);
			music.play();
		}
	}
	
	// stops the music
	public void stopMusic()
	{
		if(playingMusic != null) playingMusic.stop();
	}
	
	// allows audio settings to be updated
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