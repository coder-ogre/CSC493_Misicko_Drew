/*
 * Drew Misicko
 */

package util;

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
	public boolean altMusic;
	public float volSound;
	public float volMusic;
	public int charSkin;
	public boolean showFpsCounter;
	
	private Preferences prefs;
	public Preferences highScoreList;
	
	// singleton: prevent instatniation from other classes
	private GamePreferences()
	{
		prefs = Gdx.app.getPreferences(Constants.PREFERENCES);
		highScoreList = Gdx.app.getPreferences(Constants.HIGHSCORE_LIST);
		
		if(highScoreList.getInteger("1") == 0)
			
		{
			for(int i = 1; i < 11; i++)
			{
				highScoreList.putInteger(""+i, 0);
			}
		}
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
		altMusic = prefs.getBoolean("altMusic", true);
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
		prefs.putBoolean("altMusic", altMusic);
		prefs.putFloat("volSound", volSound);
		prefs.putFloat("volMusic", volMusic);
		prefs.putInteger("charSkin", charSkin);
		prefs.putBoolean("showFpsCounter", showFpsCounter);
		prefs.flush();
	}
	
	public void updateHighScores(int score)
	{
		//Get all scores
		int i = 0;
		int scores[] = new int[11];
		while(i < scores.length-1)
		{
			scores[i] = this.highScoreList.getInteger(""+i);
			i++;
		}
		scores[10] = score;
		
		boolean done = false;
		while(!done)
		{
			done = true;
			for(int j = 0; j < scores.length-1; j++)
			{
				if(scores[j] < scores[j+1])
				{
					int tmp = scores[j];
					scores[j] = scores[j+1];
					scores[j+1] = tmp;
					done = false;
				}
			}
		}
		//this.highScoreList.putInteger("" + 0, val)
		
		//Replace scores in highscores list
		for(i = 1; i < 11; i++)
		{
			this.highScoreList.putInteger(""+i, scores[i-1]);
		}
		this.highScoreList.flush();
	}
}
