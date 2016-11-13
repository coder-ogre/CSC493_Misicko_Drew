/*
 * Drew Misicko
 */

package com.misicko.gdx.game1;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

//imports added in assignment 6
import com.badlogic.gdx.math.Rectangle;

// import added in chapter 10 for audio
import util.AudioManager;

import objects.Dirt;
import objects.GenericPowerup;
import objects.Pusheen;
import objects.SuperCookie;
import objects.Pusheen.JUMP_STATE;
//end of imports from assignment 6
import util.CameraHelper;
import util.Constants;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import objects.AbstractGameObject;
import util.CollisionHandler;

public class WorldController  extends InputAdapter implements Disposable
{
	public CameraHelper cameraHelper;
	
	private Game game;
	
	public Level level;
	public int lives;
	//code in chapter 8 to have visual on lives GUI
	public float livesVisual;
	public int score;
	public float scoreVisual;
	
	//instance vars from assignment 6
		//Rectangles for collision detection
		private Rectangle r1 = new Rectangle();
		private Rectangle r2 = new Rectangle();
		private float timeLeftGameOverDelay; // time system waits after game ends
	//end of instance vars for assignment 6
		
		// box2d
		public World myWorld;
		public Array<AbstractGameObject> objectsToRemove;
		
		//specifies what happens when Pusheen collides with the ground, from assignment 6
		private void onCollisionPusheenWithGround(Dirt dirt)
		{
			Pusheen pusheen = level.pusheen;
			float heightDifference = Math.abs(pusheen.position.y - ( dirt.position.y + dirt.bounds.height));
			if ( heightDifference > 0.25f) {
				boolean hitRightEdge = pusheen.position.x > (
					dirt.position.x + dirt.bounds.width / 2.0f);
				if(hitRightEdge) {
					pusheen.position.x = dirt.position.x + dirt.bounds.width;
				}
				else
				{
					pusheen.position.x = dirt.position.x - pusheen.bounds.width;
				}
				return;
			}
			
			switch(pusheen.jumpState)
			{
			case GROUNDED:
				break;
			case FALLING:
			case JUMP_FALLING:pusheen.position.y = dirt.position.y + dirt.bounds.height + dirt.origin.y;
			pusheen.jumpState = JUMP_STATE.GROUNDED;
			break;
			case JUMP_RISING:
				pusheen.position.y = dirt.position.y + pusheen.bounds.height + pusheen.origin.y;
				break;
			}
		}
		
		//specifies what happens when Pusheen COllides with a genericPowerup, from assignment 6
		private void onCollisionPusheenWithGenericPowerup(GenericPowerup genericPowerup) 
		{
			genericPowerup.collected = true;
			score += genericPowerup.getScore();
			Gdx.app.log(TAG, "Generic powerup collected");
		};
		
		//specifies what happens when Pusheen collides with the superCookie, from assignment 6
		private void onCollisionPusheenWithSuperCookie(SuperCookie superCookie) 
		{
			superCookie.collected = true;
			score += superCookie.getScore();
			level.pusheen.setSuperCookie(true);
			Gdx.app.log(TAG,  "SuperCookie collected");
		};
	//end of instance vars from assignment 6
	
	private void initLevel()
	{
		score = 0;
		scoreVisual = score;// from chapter 8
		level = new Level(Constants.LEVEL_01);
		cameraHelper.setTarget(level.pusheen);//added in assignment 6 to follow Pusheen actor
		initPhysics();
	}
	
	// initiates physics
	private void initPhysics()
	{
		if (myWorld != null)
			myWorld.dispose();
		myWorld = new World(new Vector2(0, -9.81f), true);
		myWorld.setContactListener(new CollisionHandler(this));  // Not in the book
		Vector2 origin = new Vector2();
		
		//for rocks 
		for (Dirt dirt : level.dirts)
		{
			BodyDef bodyDef = new BodyDef();
			bodyDef.position.set(dirt.position);
			bodyDef.type = BodyType.KinematicBody;
			Body body = myWorld.createBody(bodyDef);
			//body.setType(BodyType.DynamicBody);
			body.setUserData(dirt);
			dirt.body = body;
			PolygonShape polygonShape = new PolygonShape();
			origin.x = dirt.bounds.width / 2.0f;
			origin.y = rock.bounds.height / 2.0f;
			polygonShape.setAsBox(rock.bounds.width / 2.0f, (rock.bounds.height-0.04f) / 4.0f, origin, 0);
			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.shape = polygonShape;
			body.createFixture(fixtureDef);
			polygonShape.dispose();
	}
	
	private static final String TAG =
			WorldController.class.getName();
	
	public WorldController (Game game) {
		this.game = game;
		init();
	}
	
	// initiates control of objects
	private void init () 
	{ 
		objectsToRemove = new Array<AbstractGameObject>();
		Gdx.input.setInputProcessor(this);
		cameraHelper = new CameraHelper();
		//initTestObjects();
		lives = Constants.LIVES_START;
		// from chapter 8, for initialization to lives GUI
		livesVisual = lives;
		timeLeftGameOverDelay = 0;
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
		if(!isGameOver() && isPlayerInLava())
		{
			AudioManager.instance.play(Assets.instance.sounds.liveLost);
			lives--;
			if(isGameOver())
				timeLeftGameOverDelay = Constants.TIME_DELAY_GAME_OVER;
			else
				initLevel();
		}
		//added in chapter 8 to have parallax mountains
		level.mountains.updateScrollPOsition(
			cameraHelper.getPosition());
		//added in chapter 8 to update lives GUI
		if(livesVisual > lives)
			livesVisual = Math.max(lives, livesVisual - 1 * deltaTime);
		// from chapter 8 to update visual score
		if(scoreVisual < score)
			scoreVisual = Math.min(scoreVisual, scoreVisual
				+ 250 * deltaTime);
	}
	
	private void handleDebugInput (float deltaTime) {
		if (Gdx.app.getType() != ApplicationType.Desktop) return;
		
		//added to ensure camera follows Pusheen from assignment 6
		if(!cameraHelper.hasTarget(level.pusheen))
		{
			float camMoveSpeed = 5*deltaTime;
			float camMoveSpeedAccelerationFactor = 5;
			if(Gdx.input.isKeyPressed(Keys.SHIFT_LEFT))
				camMoveSpeed *= camMoveSpeedAccelerationFactor;
			if(Gdx.input.isKeyPressed(Keys.LEFT))
				if(level.pusheen.hasSuperCookie)
				{
					moveCamera((-camMoveSpeed*2), 0);
				}
				else
				{
					moveCamera(-camMoveSpeed, 0);
				}
			if(Gdx.input.isKeyPressed(Keys.RIGHT))
				if(level.pusheen.hasSuperCookie)
				{
					moveCamera((camMoveSpeed*2), 0);
				}
				else
				{
					moveCamera(camMoveSpeed, 0);
				}
			if(Gdx.input.isKeyPressed(Keys.UP))
				moveCamera(0, camMoveSpeed);
			if(Gdx.input.isKeyPressed(Keys.DOWN))
				moveCamera(0, -camMoveSpeed);
			if(Gdx.input.isKeyPressed(Keys.BACKSPACE))
				cameraHelper.setPosition(0,  0);
		}
		
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
			cameraHelper.setTarget(cameraHelper.hasTarget() ? null: level.pusheen);
			Gdx.app.debug(TAG, "Camera follow enabled: " + cameraHelper.hasTarget());
		}
		return false;
	}
	
	//method for testing collisions from assignment 6
	private void testCollisions()
	{
		r1.set(level.pusheen.position.x, level.pusheen.position.y,
			level.pusheen.bounds.width, level.pusheen.bounds.height);
		
		//Test collision: Pusheen <-> Ground
		for(Dirt dirt : level.dirts) {
			r2.set(dirt.position.x, dirt.position.y, dirt.bounds.width,
				dirt.bounds.height);
			if(!r1.overlaps(r2)) continue;
			onCollisionPusheenWithGround(dirt);
			// IMPORTANT: must do all collisions for valid
			// edge testing on dirt.
		}
		
		// Test collision: Pusheen <-> genericPowerup
		for(GenericPowerup genericPowerup : level.genericPowerups){
			if(genericPowerup.collected) continue;
			r2.set(genericPowerup.position.x, genericPowerup.position.y,
				genericPowerup.bounds.width, genericPowerup.bounds.height);
			if(!r1.overlaps(r2)) continue;
			onCollisionPusheenWithGenericPowerup(genericPowerup);
			break;
		}
		
		//Test collision: Pusheen <-> superCookie
		for(SuperCookie superCookie : level.superCookies)
		{
			if(superCookie.collected) continue;
			r2.set(superCookie.position.x, superCookie.position.y,
				superCookie.bounds.width, superCookie.bounds.height);
			if(!r1.overlaps(r2)) continue;
			onCollisionPusheenWithSuperCookie(superCookie);
			break;
		}
	}
	
	//code added from assignment 6 to handle input for Pusheen in-game
	private void handleInputGame(float deltaTime)
	{
		if(cameraHelper.hasTarget(level.pusheen)) {
			// Player Movement
			if(Gdx.input.isKeyPressed(Keys.LEFT))
			{
				level.pusheen.velocity.x = -level.pusheen.terminalVelocity.x;
			}
			else if(Gdx.input.isKeyPressed(Keys.RIGHT))
			{
				level.pusheen.velocity.x = level.pusheen.terminalVelocity.x;
			}
			else
			{
				// Execute auto-forward movement on non-desktop platform
				if(Gdx.app.getType() != ApplicationType.Desktop)
				{
					level.pusheen.velocity.x = level.pusheen.terminalVelocity.x;
				}
			}
			
			// Pusheen
			if (Gdx.input.isTouched() || Gdx.input.isKeyPressed(Keys.SPACE)) {
				level.pusheen.setJumping(true);
			}
			else
			{
				level.pusheen.setJumping(false);
			}
		}
	}
	
	//decides whether the game is over or not, from assignment 6
	public boolean isGameOver()
	{
		return lives < 0;
	}
	
	//decides whether player is in water or not, from assignemtn 6
	public boolean isPlayerInLava()
	{
		return level.pusheen.position.y < -5;
	}
	
}
