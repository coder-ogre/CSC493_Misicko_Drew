/*
 * Drew Misicko
 */

package com.misicko.gdx.game1;

/*
 * In case access is lost, note that
 * Original name of repository was MisickoGDXGame1
 * but will have been changed to CSC493_Misicko_Drew
 * 
 */

import com.misicko.gdx.game1.Assets;

import screens.MenuScreen;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.Game;




// added in chapter 10 for audio
import util.AudioManager;
import util.Constants;
import util.GamePreferences;

public class MisickoGdxGame1 extends /*ApplicationAdapter*/ Game {
	
	@Override public void create () {
		GamePreferences instance = GamePreferences.instance;
		Preferences prefs = Gdx.app.getPreferences(Constants.PREFERENCES);
		
		// Set Libgdx log level to DEBUG
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		// Load assets
		AssetManager am;
		Assets.instance.init(am = new AssetManager());
		// Load preferences for audio settings and start playing music
		GamePreferences.instance.load();
		am.load("music/keith303_-_brand_new_highscore.mp3",
				Music.class);
		am.load("music/03_Ave_Maria.mp3", Music.class);
		if(prefs.getBoolean("music", true))
		{
			//am.load("music/keith303_-_brand_new_highscore.mp3",
			//		Music.class);
			AudioManager.instance.play1(Assets.instance.music.song01);
		}
		if(prefs.getBoolean("altMusic", true))
		{
			//am.load("music/03_Ave_Maria.mp3", Music.class);
			AudioManager.instance.play2(Assets.instance.music.song02);
		}
		// Start game at menu screen
		setScreen(new MenuScreen(this));
	}
}