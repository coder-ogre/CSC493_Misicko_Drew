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
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import objects.Confetti;


//imports added in assignment 6
import com.badlogic.gdx.math.Rectangle;




import screens.MenuScreen;
// import added in chapter 10 for audio
import util.AudioManager;
import objects.Dirt;
import objects.GenericPowerup;
import objects.Pusheen;
import objects.Pusheen.JUMP_STATE;
import objects.SuperCookie;
//import objects.Pusheen.JUMP_STATE;
import objects.Pusheen.VIEW_DIRECTION;
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
	int whichLevel = 0;
	int scoreSavedFromLevel1 = 0;
	
	public float timeHeld;
	
	//instance vars from assignment 6
	//Rectangles for collision detection
	private Rectangle r1 = new Rectangle();
	private Rectangle r2 = new Rectangle();
	private float timeLeftGameOverDelay; // time system waits after game ends
	//end of instance vars for assignment 6
	
	private boolean goalReached;
	
	// box2d
	public World myWorld;
	public Array<AbstractGameObject> objectsToRemove;
	public boolean canJump = true;
	public double airTime;
	
	private void spawnConfetti(Vector2 pos, int numConfetti, float radius)
	{
		float confettiShapeScale = 0.5f;
			// create carrots with box2d body and fixture
		for(int i = 0; i < numConfetti; i++)
		{
			Confetti confetti = new Confetti();
			// calculate random spawn position
			float x = MathUtils.random(-radius, radius);
			float y = MathUtils.random(5.0f, 15.0f);
			float rotation = MathUtils.random(0.0f, 360.0f) * MathUtils.degreesToRadians;
			float confettiScale = MathUtils.random(0.5f, 1.5f);
			confetti.scale.set(confettiScale, confettiScale);
				// create box2d body for confetti with start position
				// and angle of rotation
			BodyDef bodyDef = new BodyDef();
			bodyDef.position.set(pos);
			bodyDef.position.add(x, y);
			bodyDef.angle = rotation;
				Body body = myWorld.createBody(bodyDef);
			body.setType(BodyType.DynamicBody);
			confetti.body = body;
				// create rectangular shape for confetti to allow
				// interactions (collisions) with other objects
			PolygonShape polygonShape = new PolygonShape();
			float halfWidth = confetti.bounds.width / 2.0f * confettiScale;
			float halfHeight = confetti.bounds.height / 2.0f * confettiScale;
			polygonShape.setAsBox(halfWidth * confettiShapeScale, halfHeight * confettiShapeScale);
				// set physics attributes
			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.shape = polygonShape;
			fixtureDef.density = 50;
			fixtureDef.restitution = 0.5f;
			fixtureDef.friction = 0.5f;
			body.createFixture(fixtureDef);
			polygonShape.dispose();
				// finally, add new carrot to list for updating/rendering
			level.confetti.add(confetti);
		}
	}
	
	private void onCollisionPusheenWithGoal()
	{
		goalReached = true;
		timeLeftGameOverDelay = Constants.TIME_DELAY_GAME_FINISHED;
		Vector2 centerPosPusheen = new Vector2(level.pusheen.position);
		centerPosPusheen.x += level.pusheen.bounds.width;
		spawnConfetti(centerPosPusheen, Constants.CONFETTI_SPAWN_MAX, Constants.CARROTS_SPAWN_RADIUS);
	}
	
	//specifies what happens when Pusheen collides with the ground, from assignment 6
	private void onCollisionPusheenWithGround(Dirt dirt)
	{
		Pusheen pusheen = level.pusheen;
		level.pusheen.stopFinalParticles();
		float heightDifference = Math.abs(pusheen.position.y - ( dirt.position.y + dirt.bounds.height));
		if ( heightDifference > 0.25f) {
			boolean hitRightEdge = pusheen.position.x > (
				dirt.position.x + dirt.bounds.width / 2.0f);
			return;
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
		
		
		Vector2 centerPosPusheen = new Vector2(level.pusheen.position);
		centerPosPusheen.x += level.pusheen.bounds.width;
		spawnConfetti(centerPosPusheen, Constants.CONFETTI_SPAWN_MAX, Constants.CARROTS_SPAWN_RADIUS);
	};
	//end of instance vars from assignment 6
	
	private void initLevel1()
	{
		whichLevel = 1;
		score = 0;
		scoreSavedFromLevel1 = 0;
		scoreVisual = score;// from chapter 8
		goalReached = false;
		level = new Level(Constants.LEVEL_01);
		cameraHelper.setTarget(level.pusheen);//added in assignment 6 to follow Pusheen actor
		initPhysics();
	}
	
	private void initLevel2()
	{
		//score = 0;
		whichLevel = 2;
		scoreVisual = score;// from chapter 8
		goalReached = false;
		level = new Level(Constants.LEVEL_02);
		cameraHelper.setTarget(level.pusheen);//added in assignment 6 to follow Pusheen actor
		initPhysics();
	}
	
	// initiates physics
	private void initPhysics()
	{
		if (myWorld != null)
			myWorld.dispose();
		myWorld = new World(new Vector2(0, -9.81f), true);
		myWorld.setContactListener(new CollisionHandler(this));
		Vector2 origin = new Vector2();
		
		// when pusheen collides with dirt
		for (Dirt dirt : level.dirts)
		{
			BodyDef bodyDef = new BodyDef();
			bodyDef.type = BodyType.KinematicBody;
			bodyDef.position.set(dirt.position);
			Body body = myWorld.createBody(bodyDef);
			body.setUserData(dirt);
			dirt.body = body;
			PolygonShape polygonShape = new PolygonShape();
			origin.x = dirt.bounds.width / 2.0f;
			origin.y = dirt.bounds.height / 2.0f;
			polygonShape.setAsBox(dirt.bounds.width / 1.9f, (dirt.bounds.height-0.04f) / 1.95f, origin, 0);
			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.shape = polygonShape;
			body.createFixture(fixtureDef);
			polygonShape.dispose();
			onCollisionPusheenWithGround(dirt);
			//level.pusheen.grounded = true;
			level.pusheen.jumpState = JUMP_STATE.GROUNDED;
		}
		
		// when pusheen collides with genericPowerup
		for (GenericPowerup genericPowerup : level.genericPowerups)
		{
			BodyDef bodyDef = new BodyDef();
			bodyDef.position.set(genericPowerup.position);
			bodyDef.type = BodyType.KinematicBody;
			Body body = myWorld.createBody(bodyDef);
			body.setUserData(genericPowerup);
			genericPowerup.body = body;
			PolygonShape polygonShape = new PolygonShape();
			origin.x = genericPowerup.bounds.width / 2.0f;
			origin.y = genericPowerup.bounds.height / 2.0f;
			polygonShape.setAsBox(genericPowerup.bounds.width / 2.0f, (genericPowerup.bounds.height-0.04f) / 4.0f, origin, 0);
			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.isSensor = true;
			fixtureDef.shape = polygonShape;
			body.createFixture(fixtureDef);
			polygonShape.dispose();
		}
		
		// when pusheen collides with superCookies
		for (SuperCookie superCookie : level.superCookies)
		{
			BodyDef bodyDef = new BodyDef();
			bodyDef.position.set(superCookie.position);
			bodyDef.type = BodyType.KinematicBody;
			Body body = myWorld.createBody(bodyDef);
			body.setUserData(superCookie);
			superCookie.body = body;
			PolygonShape polygonShape = new PolygonShape();
			origin.x = superCookie.bounds.width / 2.0f;
			origin.y = superCookie.bounds.height / 2.0f;
			polygonShape.setAsBox(superCookie.bounds.width / 2.0f, (superCookie.bounds.height-0.04f) / 4.0f, origin, 0);
			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.isSensor = true;
			fixtureDef.shape = polygonShape;
			body.createFixture(fixtureDef);
			polygonShape.dispose();
		}

		// pusheen's physics
		Pusheen pusheen = level.pusheen;
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(pusheen.position);
		bodyDef.fixedRotation = true;

		Body body = myWorld.createBody(bodyDef);
		body.setType(BodyType.DynamicBody);
		body.setGravityScale(9.8f);
		body.setUserData(pusheen);
		pusheen.body = body;

		PolygonShape polygonShape = new PolygonShape();
		origin.x = (pusheen.bounds.width) / 2.0f;
		origin.y = (pusheen.bounds.height) / 2.0f;
		polygonShape.setAsBox((pusheen.bounds.width-0.7f) / 2.0f, (pusheen.bounds.height-0.15f) / 2.0f, origin, 0);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = polygonShape;
		body.createFixture(fixtureDef);
		polygonShape.dispose();
	}
	
	private static final String TAG =
			WorldController.class.getName();
	
	public WorldController (Game game) {
		this.game = game;
		init1();
	}
	
	// initiates control of objects
	private void init1() 
	{ 
		objectsToRemove = new Array<AbstractGameObject>();
		Gdx.input.setInputProcessor(this);
		cameraHelper = new CameraHelper();
		//initTestObjects();
		lives = Constants.LIVES_START;
		// from chapter 8, for initialization to lives GUI
		livesVisual = lives;
		timeLeftGameOverDelay = 0;
		initLevel1();
	}
	
	// initiates control of objects
		private void init2() 
		{ 
			objectsToRemove = new Array<AbstractGameObject>();
			Gdx.input.setInputProcessor(this);
			cameraHelper = new CameraHelper();
			//initTestObjects();
			lives = Constants.LIVES_START;
			// from chapter 8, for initialization to lives GUI
			livesVisual = lives;
			timeLeftGameOverDelay = 0;
			initLevel2();
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
	
	public void resetJump()
	{
		timeHeld = 0.0f;
	}
	
	public void update (float deltaTime) { 
		handleDebugInput(deltaTime);
		if(objectsToRemove.size > 0)
		{
			for(AbstractGameObject obj : objectsToRemove)
			{
				if(obj instanceof GenericPowerup)
				{
					int index = level.genericPowerups.indexOf((GenericPowerup) obj, true);
					if(index != -1)
					{
						level.genericPowerups.removeIndex(index);
						myWorld.destroyBody(obj.body);
					}
				}
			}
			objectsToRemove.removeRange(0,  objectsToRemove.size - 1);
		}
		if(isGameOver())
		{
			timeLeftGameOverDelay -= deltaTime;
			if(timeLeftGameOverDelay < 0)
				backToMenu();
		}
		else if(goalReached && whichLevel == 1)
		{
			timeLeftGameOverDelay -= deltaTime;
			if(timeLeftGameOverDelay < 0)
				init2();
		}
		else if(goalReached && whichLevel == 2)
		{
			timeLeftGameOverDelay -= deltaTime;
			if(timeLeftGameOverDelay < 0)
				backToMenu();
		}
		else
		{
			handleInputGame(deltaTime); //invokes the handleInputGame method to update positions, from assignment 6
		}
		
		if(MathUtils.random(0.0f, 2.0f) < deltaTime)
		{
			Vector2 centerPos = new Vector2(level.pusheen.position);
			centerPos.x += level.pusheen.bounds.width;
		}
		
		myWorld.step(deltaTime,  8, 3); // tells box2d world to update
		
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
			{
				if(whichLevel == 1)
				{
					score = 0;
					initLevel1();
				}
				else if(whichLevel == 2)
				{
					initLevel2();
					score = scoreSavedFromLevel1;
				}
			}
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
	
	private void backToMenu ()
	{
		// switch to menu screen
		game.setScreen(new MenuScreen(game));
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
		if (Gdx.input.isKeyPressed(Keys.LEFT)) moveCamera(-camMoveSpeed, 0);
		if (Gdx.input.isKeyPressed(Keys.RIGHT)) moveCamera(camMoveSpeed, 0);
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
			init1();
			Gdx.app.debug(TAG, "Game world resetted");
		}
		//code added to toggle the camera follow from assignment 6
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
			// IMPORTANT: must do all collisions for valid edge testing on dirt
			
			//Test collision: Pusheen <-> goal
			if(!goalReached)
			{
				r2.set(level.goal.bounds);
				r2.x += level.goal.position.x;
				r2.y += level.goal.position.y;
				if(r1.overlaps(r2))
					onCollisionPusheenWithGoal();
			}
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
				if(level.pusheen.hasSuperCookie())
				{
					level.pusheen.body.setLinearVelocity(-level.pusheen.terminalVelocity.x*2, level.pusheen.velocity.y);
					level.pusheen.startDustParticles();
				}
				else
				{
					level.pusheen.body.setLinearVelocity(-level.pusheen.terminalVelocity.x, level.pusheen.velocity.y);
					level.pusheen.startDustParticles();
				}
				
				level.pusheen.viewDirection = VIEW_DIRECTION.LEFT;
			}
			else if(Gdx.input.isKeyPressed(Keys.RIGHT))
			{
				if(level.pusheen.hasSuperCookie())
				{
					level.pusheen.body.setLinearVelocity(level.pusheen.terminalVelocity.x*2, level.pusheen.velocity.y);
					level.pusheen.startDustParticles();
				}
				else
				{
					level.pusheen.body.setLinearVelocity(level.pusheen.terminalVelocity.x, level.pusheen.velocity.y);
					level.pusheen.startDustParticles();
				}
				level.pusheen.viewDirection = VIEW_DIRECTION.RIGHT;
			}
			else
			{
				// Execute auto-forward movement on non-desktop platform
				if(Gdx.app.getType() != ApplicationType.Desktop)
				{
					level.pusheen.velocity.x = level.pusheen.terminalVelocity.x;
					level.pusheen.body.setLinearVelocity(level.pusheen.body.getLinearVelocity().x, level.pusheen.terminalVelocity.y);
				}
			}
			
			// Pusheen jumping
			if (Gdx.input.isTouched() || Gdx.input.isKeyPressed(Keys.SPACE)) 
			{
				if (!(level.pusheen.grounded == false && level.pusheen.jumping == false) && (Gdx.input.isTouched() || Gdx.input.isKeyPressed(Keys.SPACE)))
				{
					Vector2 vec = level.pusheen.body.getLinearVelocity();
					if (timeHeld < 0.5)
					{		
						if(level.pusheen.hasSuperCookie())
						{
							level.pusheen.body.applyLinearImpulse(0.0f, 1000.0f, level.pusheen.body.getPosition().x, level.pusheen.body.getPosition().y, true);
							level.pusheen.body.setLinearVelocity(vec.x, level.pusheen.terminalVelocity.y);
							level.pusheen.startFinalParticles();
						}
						else
						{
							level.pusheen.body.setLinearVelocity(vec.x, level.pusheen.terminalVelocity.y);
							level.pusheen.startFinalParticles();
						}
						level.pusheen.position.set(level.pusheen.body.getPosition());
						timeHeld += deltaTime;
					}
					else
					{
						level.pusheen.jumping = false;
					}
				}
			}
		}
	}
	
	// gets things ready to be removed
	public void flagForRemoval(AbstractGameObject obj)
	{
		objectsToRemove.add(obj);
	}
	
	//decides whether the game is over or not, from assignment 6
	public boolean isGameOver()
	{
		return lives < 0;
	}
	
	//decides whether player is in water or not, from assignment 6
	public boolean isPlayerInLava()
	{
		return level.pusheen.position.y < -5;
	}
	
	// garbage collection
	@Override
	public void dispose()
	{
		if(myWorld != null)
			myWorld.dispose();
	}
}

