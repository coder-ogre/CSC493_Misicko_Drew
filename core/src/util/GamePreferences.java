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
			}// initializes high score list
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
		boolean sorted = false;
		int highScores[] = new int[11];
		for(int i = 0; i < highScores.length - 1; i++)
		{
			highScores[i] = this.highScoreList.getInteger(""+i);// import existing high scores
			i++;
		}
		highScores[10] = score;// assuming scores imported already sorted, the lowest score (position 10) gets knocked down.
		
		do
		{
			sorted = true;
			for(int i = 0; i < highScores.length - 1; i++)
			{
				if(highScores[i] < highScores[i + 1])
				{
					int tmp = highScores[i];
					highScores[i] = highScores[i + 1];
					highScores[i + 1] = tmp;
					sorted = false;
				}
			}
		}while(!sorted);// sort new high score list to reposition the new score
		
		for(int i = 1; i < 11; i++)
		{
			this.highScoreList.putInteger(""+i, highScores[i - 1]);
		}//export the new high score list to the prefs to save them
		this.highScoreList.flush();
	}
}
