/*
 * Drew Misicko
 */

package com.misicko.gdx.game1;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.MathUtils;

// this class is implemented as a singleton so we can call its load() and save()
// methods from virtually anywhere inside our project. the settings will be
// loaded from and saved to a preferences file defined in Constants.PREFERENCES
public class GamePreferences 
{
	public static final String TAG = GamePreferences.class.getName();
	
	public static final GamePreferences instance = new GamePreferences();
	
	public boolean sound;
	public boolean music;
	public float volSound;
	public float volMusic;
	public int charSkin;
	public boolean showFpsCounter;
	
	private Preferences prefs;
	
	// singleton: prevent instatniation from other classes
	private GamePreferences()
	{
		prefs = Gdx.app.getPreferences(Constants.PREFERENCES);
	}
	
	// the load method will always try its best to find a suitable and, more importantly,
	// valid value. This is achieved by supplying default values to the greater methods of
	// the Preferences class. For example, the call getFloat("volSound", 0.5f) will return
	// a value of 0.5f if there is no value found for the key named volSound. Before the 
	// value of the sound volume is finally stored, it is also passed to the clamp() utility
	// method to ensure that the value is within the allowed range of values, which is 0.0f
	// and 1.0f here.
	public void load() 
	{
		sound = prefs.getBoolean("sound", true);
		music = prefs.getBoolean("music", true);
		volSound = MathUtils.clamp(prefs.getFloat("volSound", 0.5f), 0.0f, 1.0f);
		volMusic = MathUtils.clamp(prefs.getFloat("volMusic", 0.5f),
			0.0f, 1.0f);
		charSkin = MathUtils.clamp(prefs.getInteger("charSkin", 0),
			0, 2);
		showFpsCounter = prefs.getBoolean("showFpsCounter", false);
	}
	
	// The save() method takes the current values of its public variables and puts them
	// into a map of the preferences file. Finally, flush() is called on the prefereces 
	// file to actually write the changed values into the file.
	public void save() 
	{
		prefs.putBoolean("sound", sound);
		prefs.putBoolean("music", music);
		prefs.putFloat("volSound", volSound);
		prefs.putFloat("volMusic", volMusic);
		prefs.putInteger("charSkin", charSkin);
		prefs.putBoolean("showFpsCounter", showFpsCounter);
		prefs.flush();
	}
}
