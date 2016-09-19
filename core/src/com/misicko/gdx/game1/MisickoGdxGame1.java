/*
 * Drew Misicko
 */

package com.misicko.gdx.game1;

/*
 * Test change to Game2 branch
 */

/*
 * In case access is lost, note that
 * Original name of repository was MisickoGDXGame1
 * but will have been changed to CSC493_Misicko_Drew
 * 
 */

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

import com.badlogic.gdx.assets.AssetManager;
import com.misicko.gdx.game1.Assets;

public class MisickoGdxGame1 extends ApplicationAdapter {
	private static final String TAG =
			MisickoGdxGame1.class.getName();
	
	private WorldController worldController;
	private WorldRenderer worldRenderer;
	
	private boolean paused;
	
	
	@Override public void create () {
		// Set Libgdx log level to DEBUG
				Gdx.app.setLogLevel(Application.LOG_DEBUG);
				// Load assets
				Assets.instance.init(new AssetManager());
				// Initialize controller and renderer
				worldController = new WorldController();
				worldRenderer = new WorldRenderer(worldController);
				// Game world is active on start
				paused = false;
	}
	@Override public void render() {
		// Do not update game world when paused.
		if (!paused) {
			// Update game world by the time that has passed
			// since last rendered frame
			worldController.update(Gdx.graphics.getDeltaTime());
		}
		// Sets the clear screen color to: Cornflower Blue
		Gdx.gl.glClearColor(0x64/255.0f, 0x95/255.0f, 0xed/255.0f,
				0xff/255.0f);
		// Clears the screen
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		// Render game world to screen
		worldRenderer.render();
	}
	@Override public void resize (int width, int height) {
		worldRenderer.resize(width, height);
	}
	@Override public void pause () { 
		paused = true;
	}
	@Override public void resume () { 
		paused = false;
	}
	@Override public void dispose () { 
		worldRenderer.dispose();
		Assets.instance.dispose();
	}
}

/*
package com.misicko.gdx.game1;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MisickoGdxGame1 extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(img, 0, 0);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
}
*/