/*
 * Drew Misicko
 */

package game;

import util.Constants;
import util.GamePreferences;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Align;

// added in chapter 8 for keeping track of lives
import com.badlogic.gdx.math.MathUtils;

// added in chapter 11 for box2d collision physics
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;

public class WorldRenderer 
{
	// moves field of vision around world map to follow player's actions.
	// orthographic means perspective does not change ostensible relative size
	private OrthographicCamera camera;
	// draws the objects
	private SpriteBatch batch;
	private WorldController worldController;
	
	private OrthographicCamera cameraGUI;
	
	// instance vars added in chapter 11 to do box2d physics
	private static final boolean DEBUG_DRAW_BOX2D_WORLD = false;
	private Box2DDebugRenderer b2debugRenderer;
	
	public WorldRenderer (WorldController worldController) { 
		this.worldController = worldController;
		init();
	}
	private void init () { 
		batch = new SpriteBatch();
		camera = new OrthographicCamera(Constants.VIEWPORT_WIDTH, 
				Constants.VIEWPORT_HEIGHT);
		camera.position.set(0, 0, 0);
		camera.update();
		
		cameraGUI = new OrthographicCamera(Constants.VIEWPORT_GUI_WIDTH,
											Constants.VIEWPORT_GUI_HEIGHT);
		cameraGUI.position.set(0, 0, 0);
		cameraGUI.setToOrtho(true); // flip y-axis
		cameraGUI.update();
		// added in chapter 11 for box2d physics
		b2debugRenderer = new Box2DDebugRenderer();
	}
	
	public void render () 
	{ 
		//renderTestObjects();
		renderWorld(batch);
		renderGui(batch);
	}
	
	/*private void renderTestObjects() {
		worldController.cameraHelper.applyTo(camera);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		for(Sprite sprite : worldController.testSprites) {
			sprite.draw(batch);
		}
		batch.end();
	}*/
	
   //passing in SpriteBatch here is optional, since it is a global variable
	private void renderWorld(SpriteBatch batch)
	{
		worldController.cameraHelper.applyTo(camera);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		worldController.level.render(batch);
		batch.end();
		//added in chapter 11 for box2d physics
		if(DEBUG_DRAW_BOX2D_WORLD)
		{
			b2debugRenderer.render(worldController.b2world,
				camera.combined);
		}
	}
	
	// renders the gui
	private void renderGui(SpriteBatch batch)
	{
		batch.setProjectionMatrix(cameraGUI.combined);
		batch.begin();
		// draw collected gold coins icon + text
		// (anchored to top left edge)
		renderGuiScore(batch);
		// draw collected feather icon (anchored to top left edge) from assignment 6
		renderGuiFeatherPowerup(batch);
		// draw extra lives icon + text (anchored to top right edge)
		renderGuiExtraLive(batch);
		// draw FPS text (anchored to bottom right edge)
		if(GamePreferences.instance.showFpsCounter)
			renderGuiFpsCounter(batch);
		// draw game over text
		renderGuiGameOverMessage(batch);
		batch.end();
	}
	
	// render the GUI score
	private void renderGuiScore(SpriteBatch batch)
	{
		float x = -15;
		float y = -15;
		
		// added in chapter 8
		float offsetX = 50;
		float offsetY = 50;
		if(worldController.scoreVisual < worldController.score)
		{
			long shakeAlpha = System.currentTimeMillis() % 360;
			float shakeDist = 1.5f;
			offsetX += MathUtils.sinDeg(shakeAlpha * 2.2f) * shakeDist;
			offsetY += MathUtils.sinDeg(shakeAlpha * 2.9f) * shakeDist;
		}
		
		/* the value in score visual is cast to an integer value to cut off the fraction.
		 * the resulting intermediate value will be the score that is shown in the GUI for the
		 * counting-up animation. To let the coin icon shake, we use a sine function with different
		 * factors as input angles to find the offset for the temporary displacement of the icon.
		 */
		batch.draw(Assets.instance.goldCoin.goldCoin,
			//x, y, 50, 50, 100, 100, 0.35f, -0.35f, 0);
			x, y, offsetX, offsetY, 100, 100, 0.35f, -0.35f, 0);
		Assets.instance.fonts.defaultBig.draw(batch,
			"" + worldController.scoreVisual,
			x + 75, y + 37);
	}
	
	// draws available extra lives, and darkens and makes see through used lives
	private void renderGuiExtraLive(SpriteBatch batch)
	{
		float x = cameraGUI.viewportWidth - 50 -
			Constants.LIVES_START * 50;
		float y = -15;
		for(int i = 0; i < Constants.LIVES_START; i++)
		{
			if(worldController.lives <= i)
				batch.setColor(0.5f, 0.5f, 0.5f, 0.5f);
			batch.draw(Assets.instance.bunny.head,
				x + i * 50, y, 50, 50, 120, 100, 0.35f, -0.35f, 0);
			batch.setColor(1, 1, 1, 1);
		}
		//this part added in chapter 8 for parallax mountains...
		if(worldController.lives >= 0
			&& worldController.livesVisual > worldController.lives)
		{
			int i = worldController.lives;
			float alphaColor = Math.max(0, worldController.livesVisual
				- worldController.lives - 0.5f);
			float alphaScale = 0.35f * (2 + worldController.lives
				- worldController.livesVisual) * 2;
			float alphaRotate = -45 * alphaColor;
			batch.setColor(1.0f, 0.7f, 0.7f, alphaColor);
			batch.draw(Assets.instance.bunny.head,
				x + i * 50, y, 50, 50, 120, 100, alphaScale, -alphaScale,
				alphaRotate);
			batch.setColor(1, 1, 1, 1);
		}
	}
	
	private void renderGuiFpsCounter(SpriteBatch batch)
	{
		float x = cameraGUI.viewportWidth - 55;
		float y = cameraGUI.viewportHeight - 15;
		int fps = Gdx.graphics.getFramesPerSecond();
		BitmapFont fpsFont = Assets.instance.fonts.defaultNormal;
		if(fps >= 45)
		{
			// 45 or more FPS show up in green
			fpsFont.setColor(0, 1, 0, 1);
		}
		else if(fps >= 30)
		{
			// 30 or more FPS show up in yellow
			fpsFont.setColor(1, 1, 0, 1);
		}
		else
		{
			// less than 30 FPS show up in red
			fpsFont.setColor(1, 0, 0, 1);
		}
		fpsFont.draw(batch, "FPS: " + fps, x, y);
		fpsFont.setColor(1, 1, 1, 1); // white
	}
	
	// renders game over message, from assignment 6
	private void renderGuiGameOverMessage(SpriteBatch batch)
	{
		float x = cameraGUI.viewportWidth / 2;
		float y = cameraGUI.viewportHeight / 2;
		if(worldController.isGameOver()) 
		{
			BitmapFont fontGameOver = Assets.instance.fonts.defaultBig;
			fontGameOver.setColor(1, 0.75f, 0.25f, 1);
			fontGameOver.draw(batch, "GAME OVER", x, y, 0,
				Align.center, false);//2compile, had to change this, as mentioned on stackoverflow and d2l
			fontGameOver.setColor(1, 1, 1, 1);
		}
	}
	
	//renders feather powerup HUD changes, from assignment 6
	private void renderGuiFeatherPowerup(SpriteBatch batch)
	{
		float x = -15;
		float y = 30;
		float timeLeftFeatherPowerup = worldController.level.bunnyHead.timeLeftFeatherPowerup;
		if(timeLeftFeatherPowerup > 0)
		{
			// Start icon fade in/out if the left power-up time
			// is less than 4 seconds.  The fade interfal is set
			// to 5 changes per second.
			if(timeLeftFeatherPowerup < 4)
			{
				if(((int)(timeLeftFeatherPowerup * 5) % 2) != 0)
				{
					batch.setColor(1, 1, 1, 0.5f);
				}
			}
			batch.draw(Assets.instance.feather.feather,
				x, y, 50, 50, 100, 100, 0.35f, -0.35f, 0);
			batch.setColor(1, 1, 1, 1);
			Assets.instance.fonts.defaultSmall.draw(batch,
				"" + (int)timeLeftFeatherPowerup, x + 60, y + 57);
		}
	}
	
	public void resize (int width, int height)
	{
		camera.viewportWidth = (Constants.VIEWPORT_HEIGHT / height) *
				width;
		camera.update();
		
		cameraGUI.viewportHeight = Constants.VIEWPORT_GUI_HEIGHT;
		cameraGUI.viewportWidth = (Constants.VIEWPORT_GUI_HEIGHT
				/ (float)height) * (float)width;
		cameraGUI.position.set(cameraGUI.viewportWidth / 2,
				cameraGUI.viewportHeight / 2, 0);
	}
	
	//the override threw an error, had to get rid of it
	// @Override
	public void dispose () {
		batch.dispose();
	}
}