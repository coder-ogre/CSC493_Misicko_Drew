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
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.Game;

public class MisickoGdxGame1 extends /*ApplicationAdapter*/ Game {
	
	@Override public void create () {
		// Set Libgdx log level to DEBUG
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		// Load assets
		Assets.instance.init(new AssetManager());
		// Start game at menu screen
		setScreen(new MenuScreen(this));
	}
}