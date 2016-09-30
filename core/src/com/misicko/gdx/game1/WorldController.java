/*
 * Drew Misicko
 */

package com.misicko.gdx.game1;

import objects.Rock;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.misicko.gdx.game1.CameraHelper;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

//imports added in assignment 6
import com.badlogic.gdx.math.Rectangle;
import objects.BunnyHead;
import objects.BunnyHead.JUMP_STATE;
import objects.Feather;
import objects.GoldCoin;
import objects.Rock;
//end of imports from assignment 6

public class WorldController  extends InputAdapter{
	public CameraHelper cameraHelper;
	
	public Level level;
	public int lives;
	public int score;
	
	//instance vars from assignment 6
		//Rectangles for collision detection
		private Rectangle r1 = new Rectangle();
		private Rectangle r2 = new Rectangle();
		private float timeLeftGameOverDelay; // time system waits after game ends
	//end of instance vars for assignment 6
		
		//specifies what happens when Bunny collides with rock, from assignment 6
		private void onCollisionBunnyHeadWithRock(Rock rock)
		{
			BunnyHead bunnyHead = level.bunnyHead;
			float heightDifference = Math.abs(bunnyHead.position.y - ( rock.position.y + rock.bounds.height));
			if ( heightDifference > 0.25f) {
				boolean hitRightEdge = bunnyHead.position.x > (
					rock.position.x + rock.bounds.width / 2.0f);
				if(hitRightEdge) {
					bunnyHead.position.x = rock.position.x + rock.bounds.width;
				}
				else
				{
					bunnyHead.position.x = rock.position.x - bunnyHead.bounds.width;
				}
				return;
			}
			
			switch(bunnyHead.jumpState)
			{
			case GROUNDED:
				break;
			case FALLING:
			case JUMP_FALLING:bunnyHead.position.y = rock.position.y + bunnyHead.bounds.height + bunnyHead.origin.y;
			bunnyHead.jumpState = JUMP_STATE.GROUNDED;
			break;
			case JUMP_RISING:
				bunnyHead.position.y = rock.position.y + bunnyHead.bounds.height + bunnyHead.origin.y;
				break;
			}
		}
		
		//specifies what happens when Bunny COllides with gold coin, from assignment 6
		private void onCollisionBunnyWithGoldCoin(GoldCoin goldCoin) 
		{
			goldCoin.collected = true;
			score += goldCoin.getScore();
			Gdx.app.log(TAG, "Gold coin collected");
		};
		
		//specifies what happens when Bunny collides with feather, from assignment 6
		private void onCollisionBunnyWithFeather(Feather feather) 
		{
			feather.collected = true;
			score += feather.getScore();
			level.bunnyHead.setFeatherPowerup(true);
			Gdx.app.log(TAG,  "Feather collected");
		};
	//end of instance vars from assignment 6
	
	private void initLevel()
	{
		score = 0;
		level = new Level(Constants.LEVEL_01);
		cameraHelper.setTarget(level.bunnyHead);//added in assignment 6 to follow bunny head actor
	}
	
	private static final String TAG =
			WorldController.class.getName();
	
	// public Sprite[] testSprites;
	// public int selectedSprite;
	
	public WorldController () {
		init();
	}
	
	
	// initiates control of objects
	private void init () { 
		Gdx.input.setInputProcessor(this);
		cameraHelper = new CameraHelper();
		//initTestObjects();
		lives = Constants.LIVES_START;
		initLevel();
	}
	
	/*private void initTestObjects() {
		// Create new array for 5 sprites
		testSprites = new Sprite[5];
		/*
		// Create empty POT-sized Pixmap with 8 bit RGBA pixel data
		int width = 32;
		int height = 32;
		Pixmap pixmap = createProceduralPixmap(width, height);
		// Create a new texture from pixmap data
		Texture texture = new Texture(pixmap);
		*//*
		
		//Create a list of texture regions
		Array<TextureRegion> regions = new Array<TextureRegion>();
		regions.add(Assets.instance.bunny.head);
		regions.add(Assets.instance.feather.feather);
		regions.add(Assets.instance.goldCoin.goldCoin);
		
		// Create new sprites using the just created texture
		for (int i = 0; i < testSprites.length; i++) {
			Sprite spr = new Sprite(/*texture*//*regions.random());
			// Define sprite size to be 1m x 1m in game world
			spr.setSize(1, 1);
			// Set origin to sprite's center
			spr.setOrigin(spr.getWidth() / 2.0f, 
				spr.getHeight() / 2.0f);
			// Calculate random position for sprite
			float randomX = MathUtils.random(-2.0f, 2.0f);
			float randomY = MathUtils.random(-2.0f, 2.0f);
			spr.setPosition(randomX, randomY);
			// Put new sprite into array
			testSprites[i] = spr;
		}
		// Set first sprite as selected one
		selectedSprite = 0;
	}*/
	
	private Pixmap createProceduralPixmap (int width, int height) {
		Pixmap pixmap = new Pixmap(width, height, Format.RGBA8888);
		// Fill square with red color at 50% opacity
		pixmap.setColor(1, 0, 0, 0.5f);
		pixmap.fill();
		// Draw a yellow-colored X shape on square
		pixmap.setColor(1, 1, 0, 1);
		pixmap.drawLine(0, 0, width, height);
		pixmap.drawLine(width, 0 ,0, height);
		// Draw a cyan-colored border around square
		pixmap.setColor(0, 1, 1, 1);
		pixmap.drawRectangle(0, 0, width, height);
		return pixmap;
	}
	
	public void update (float deltaTime) { 
		handleDebugInput(deltaTime);
		if(isGameOver())
		{
			timeLeftGameOverDelay -= deltaTime;
			if(timeLeftGameOverDelay < 0)
				init();
		}
		else
		{
			handleInputGame(deltaTime); //invokes the handleInputGame method to update positions, from assingment 6
		}
		
		//updateTestObjects(deltaTime);
		level.update(deltaTime);// added from assignment 6 to invoke level update
		testCollisions(); //invokes assignment 6 method to test collisions
		cameraHelper.update(deltaTime);
		if(!isGameOver() && isPlayerInWater())
		{
			lives--;
			if(isGameOver())
				timeLeftGameOverDelay = Constants.TIME_DELAY_GAME_OVER;
			else
				initLevel();
		}
	}
	
	private void handleDebugInput (float deltaTime) {
		if (Gdx.app.getType() != ApplicationType.Desktop) return;
		
		//added to ensure camera follows bunny from assignment 6
		if(!cameraHelper.hasTarget(level.bunnyHead))
		{
			float camMoveSpeed = 5*deltaTime;
			float camMoveSpeedAccelerationFactor = 5;
			if(Gdx.input.isKeyPressed(Keys.SHIFT_LEFT))
				camMoveSpeed *= camMoveSpeedAccelerationFactor;
			if(Gdx.input.isKeyPressed(Keys.LEFT))
				moveCamera(-camMoveSpeed, 0);
			if(Gdx.input.isKeyPressed(Keys.RIGHT))
				moveCamera(camMoveSpeed, 0);
			if(Gdx.input.isKeyPressed(Keys.UP))
				moveCamera(0, camMoveSpeed);
			if(Gdx.input.isKeyPressed(Keys.DOWN))
				moveCamera(0, -camMoveSpeed);
			if(Gdx.input.isKeyPressed(Keys.BACKSPACE))
				cameraHelper.setPosition(0,  0);
		}
		
		// Selected Sprite Controls
		/*float sprMoveSpeed = 5 * deltaTime;
		if (Gdx.input.isKeyPressed(Keys.A)) moveSelectedSprite(
				-sprMoveSpeed, 0);
		if (Gdx.input.isKeyPressed(Keys.D))
			moveSelectedSprite(sprMoveSpeed, 0);
		if (Gdx.input.isKeyPressed(Keys.W)) moveSelectedSprite(0, 
				sprMoveSpeed);
		if (Gdx.input.isKeyPressed(Keys.S)) moveSelectedSprite(0,
				-sprMoveSpeed);*/
		
		//Camera Controls (move)
		float camMoveSpeed = 5 * deltaTime;
		float camMoveSpeedAccelerationFactor = 5;
		if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) camMoveSpeed *=
				camMoveSpeedAccelerationFactor;
		if (Gdx.input.isKeyPressed(Keys.LEFT)) moveCamera(-camMoveSpeed,
				0);
		if (Gdx.input.isKeyPressed(Keys.RIGHT)) moveCamera(camMoveSpeed,
				0);
		if (Gdx.input.isKeyPressed(Keys.UP)) moveCamera(0, camMoveSpeed);
		if (Gdx.input.isKeyPressed(Keys.UP)) moveCamera(0, 
				-camMoveSpeed);
		if (Gdx.input.isKeyPressed(Keys.BACKSPACE))
				cameraHelper.setPosition(0, 0);
		
		// Camera Controls (zoom)
		float camZoomSpeed = 1 * deltaTime;
		float camZoomSpeedAccelerationFactor = 5;
		if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) camZoomSpeed *= 
				camZoomSpeedAccelerationFactor;
		if (Gdx.input.isKeyPressed(Keys.COMMA))
			cameraHelper.addZoom(camZoomSpeed);
		if (Gdx.input.isKeyPressed(Keys.PERIOD)) cameraHelper.addZoom(
				-camZoomSpeed);
		if (Gdx.input.isKeyPressed(Keys.SLASH)) cameraHelper.setZoom(1);
	}
	
	public void moveCamera(float x, float y) {
		x += cameraHelper.getPosition().x;
		y += cameraHelper.getPosition().y;
		cameraHelper.setPosition(x, y);
	}
	
	/*private void moveSelectedSprite (float x, float y) {
		testSprites[selectedSprite].translate(x, y);
	}*/
	
	@Override
	public boolean keyUp (int keycode) {
		// Reset game world
		if (keycode == Keys.R) {
			init();
			Gdx.app.debug(TAG, "Game world resetted");
		}
		//code added to toggle the camerafollow from assignment 6
		//Toggle camera follow
		else if (keycode == Keys.ENTER)
		{
			cameraHelper.setTarget(cameraHelper.hasTarget() ? null: level.bunnyHead);
			Gdx.app.debug(TAG, "Camera follow enabled: " + cameraHelper.hasTarget());
		}
		
		
		// Select next sprite
		/*else if (keycode == Keys.SPACE) {
			selectedSprite = (selectedSprite + 1) % testSprites.length;
			// Update camera's target to follow the currently
			// selected sprite
			if (cameraHelper.hasTarget()) {
				cameraHelper.setTarget(testSprites[selectedSprite]);
			}
			Gdx.app.debug(TAG, "Sprite #" + selectedSprite + " selected");
		}*/
		
		// Toggle camera follow
		/*else if (keycode == Keys.ENTER) {
			cameraHelper.setTarget(cameraHelper.hasTarget() ? null :
				testSprites[selectedSprite]);
			Gdx.app.debug(TAG, "Camera follow enabled: " +
				cameraHelper.hasTarget());
		}*/
		
		return false;
	}
	
	/*private void updateTestObjects(float deltaTime) {
		// Get current rotation from selected sprite
		float rotation = testSprites[selectedSprite].getRotation();
		// Rotate sprite by 90 degrees per second
		rotation += 90 * deltaTime;
		// Wrap around at 360 degrees
		rotation %= 360;
		// Set new rotation value to selected sprite
		testSprites[selectedSprite].setRotation(rotation);
	}*/
	
	//method for testing collisions from assignment 6
	private void testCollisions()
	{
		r1.set(level.bunnyHead.position.x, level.bunnyHead.position.y,
			level.bunnyHead.bounds.width, level.bunnyHead.bounds.height);
		
		//Test collision: Bunny Head <-> Rocks
		for(Rock rock : level.rocks) {
			r2.set(rock.position.x, rock.position.y, rock.bounds.width,
			rock.bounds.height);
			if(!r1.overlaps(r2)) continue;
			onCollisionBunnyHeadWithRock(rock);
			// IMPORTANT: must do all collisions for valid
			// edge testing on rocks.
		}
		
		// Test collision: Bunny Head <-> GOld Coins
		for(GoldCoin goldCoin : level.goldCoins){
			if(goldCoin.collected) continue;
			r2.set(goldCoin.position.x, goldCoin.position.y,
				goldCoin.bounds.width, goldCoin.bounds.height);
			if(!r1.overlaps(r2)) continue;
			onCollisionBunnyWithGoldCoin(goldCoin);
			break;
		}
		
		//Test collision: Bunny Head <-> Feathers
		for(Feather feather : level.feathers)
		{
			if(feather.collected) continue;
			r2.set(feather.position.x, feather.position.y,
					feather.bounds.width, feather.bounds.height);
			if(!r1.overlaps(r2)) continue;
			onCollisionBunnyWithFeather(feather);
			break;
		}
	}
	
	//code added from assignment 6 to handle input for bunny in-game
	private void handleInputGame(float deltaTime)
	{
		if(cameraHelper.hasTarget(level.bunnyHead)) {
			// Player Movement
			if(Gdx.input.isKeyPressed(Keys.LEFT))
			{
				level.bunnyHead.velocity.x = -level.bunnyHead.terminalVelocity.x;
			}
			else if(Gdx.input.isKeyPressed(Keys.RIGHT))
			{
				level.bunnyHead.velocity.x = level.bunnyHead.terminalVelocity.x;
			}
			else
			{
				// Execute auto-forward movement on non-desktop platform
				if(Gdx.app.getType() != ApplicationType.Desktop)
				{
					level.bunnyHead.velocity.x = level.bunnyHead.terminalVelocity.x;
				}
			}
			
			// Bunny Jump
			if (Gdx.input.isTouched() || Gdx.input.isKeyPressed(Keys.SPACE)) {
				level.bunnyHead.setJumping(true);
			}
			else
			{
				level.bunnyHead.setJumping(false);
			}
		}
	}
	
	//decides whether the game is over or not, from assignment 6
	public boolean isGameOver()
	{
		return lives < 0;
	}
	
	//decides whether player is in water or not, from assignemtn 6
	public boolean isPlayerInWater()
	{
		return level.bunnyHead.position.y < -5;
	}
	
}

/*package com.misicko.gdx.game1;

import objects.Rock;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.misicko.gdx.game1.CameraHelper;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class WorldController  extends InputAdapter{
	public CameraHelper cameraHelper;
	
	public Level level;
	public int lives;
	public int score;
	
	private void initLevel()
	{
		score = 0;
		level = new Level(Constants.LEVEL_01);
	}
	
	private static final String TAG =
			WorldController.class.getName();
	
	// public Sprite[] testSprites;
	// public int selectedSprite;
	
	public WorldController () {
		init();
	}
	
	
	// initiates control of objects
	private void init () { 
		Gdx.input.setInputProcessor(this);
		cameraHelper = new CameraHelper();
		//initTestObjects();
		lives = Constants.LIVES_START;
		initLevel();
	}
	
	private Pixmap createProceduralPixmap (int width, int height) {
		Pixmap pixmap = new Pixmap(width, height, Format.RGBA8888);
		// Fill square with red color at 50% opacity
		pixmap.setColor(1, 0, 0, 0.5f);
		pixmap.fill();
		// Draw a yellow-colored X shape on square
		pixmap.setColor(1, 1, 0, 1);
		pixmap.drawLine(0, 0, width, height);
		pixmap.drawLine(width, 0 ,0, height);
		// Draw a cyan-colored border around square
		pixmap.setColor(0, 1, 1, 1);
		pixmap.drawRectangle(0, 0, width, height);
		return pixmap;
	}
	
	public void update (float deltaTime) { 
		handleDebugInput(deltaTime);
		//updateTestObjects(deltaTime);
		cameraHelper.update(deltaTime);
	}
	
	private void handleDebugInput (float deltaTime) {
		if (Gdx.app.getType() != ApplicationType.Desktop) return;
		
		float camMoveSpeed = 5 * deltaTime;
		float camMoveSpeedAccelerationFactor = 5;
		if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) camMoveSpeed *=
				camMoveSpeedAccelerationFactor;
		if (Gdx.input.isKeyPressed(Keys.LEFT)) moveCamera(-camMoveSpeed,
				0);
		if (Gdx.input.isKeyPressed(Keys.RIGHT)) moveCamera(camMoveSpeed,
				0);
		if (Gdx.input.isKeyPressed(Keys.UP)) moveCamera(0, camMoveSpeed);
		if (Gdx.input.isKeyPressed(Keys.UP)) moveCamera(0, 
				-camMoveSpeed);
		if (Gdx.input.isKeyPressed(Keys.BACKSPACE))
				cameraHelper.setPosition(0, 0);
		
		// Camera Controls (zoom)
		float camZoomSpeed = 1 * deltaTime;
		float camZoomSpeedAccelerationFactor = 5;
		if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) camZoomSpeed *= 
				camZoomSpeedAccelerationFactor;
		if (Gdx.input.isKeyPressed(Keys.COMMA))
			cameraHelper.addZoom(camZoomSpeed);
		if (Gdx.input.isKeyPressed(Keys.PERIOD)) cameraHelper.addZoom(
				-camZoomSpeed);
		if (Gdx.input.isKeyPressed(Keys.SLASH)) cameraHelper.setZoom(1);
	}
	
	public void moveCamera(float x, float y) {
		x += cameraHelper.getPosition().x;
		y += cameraHelper.getPosition().y;
		cameraHelper.setPosition(x, y);
	}
	
	
	
	@Override
	public boolean keyUp (int keycode) {
		// Reset game world
		if (keycode == Keys.R) {
			init();
			Gdx.app.debug(TAG, "Game world resetted");
		}
		return false;
	}
	
}*/
