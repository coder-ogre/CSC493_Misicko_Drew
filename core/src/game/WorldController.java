/*
 * Drew Misicko
 */

package game;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.Game; 

import screens.MenuScreen;
import util.AudioManager;
import util.CameraHelper;
import util.Constants;

//imports added in assignment 6
import com.badlogic.gdx.math.Rectangle;

import objects.BunnyHead;
import objects.Feather;
import objects.GoldCoin;
import objects.Rock;
import objects.BunnyHead.JUMP_STATE;

// imports added in chapter 11 for box2d collision physics
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import objects.Carrot;

// chapter 11, used to implement disposable
import com.badlogic.gdx.utils.Disposable;

public class WorldController  extends InputAdapter implements Disposable{
	private Game game;
	
	public CameraHelper cameraHelper;
	
	public Level level;
	public int lives;
	public int score;
	
	//added in chapter 8 to show that a player has lost any lives or not
	public float livesVisual;
	//added in chapter 8 to show that a player's score
	public float scoreVisual;
	
	
	
	//instance vars from assignment 6
		//Rectangles for collision detection
		private Rectangle r1 = new Rectangle();
		private Rectangle r2 = new Rectangle();
		private float timeLeftGameOverDelay; // time system waits after game ends
	//end of instance vars for assignment 6
		
		// instance variables for adding collision physics with box2d for the carrots
		private boolean goalReached;
		public World b2world;
		
		/* method for adding collision physics with Box2d (chapter 11) */
		private void initPhysics()
		{
			if(b2world != null) b2world.dispose();
			b2world = new World(new Vector2(0, -9.81f), true);
			// Rocks
			Vector2 origin = new Vector2();
			for(Rock rock : level.rocks) 
			{
				BodyDef bodyDef = new BodyDef();
				bodyDef.type = BodyType.KinematicBody;
				bodyDef.position.set(rock.position);
					Body body = b2world.createBody(bodyDef);
				rock.body = body;
				PolygonShape polygonShape = new PolygonShape();
				origin.x = rock.bounds.width / 2.0f;
				origin.y = rock.bounds.height / 2.0f;
				polygonShape.setAsBox(rock.bounds.width / 2.0f,
					rock.bounds.height / 2.0f, origin, 0);
				FixtureDef fixtureDef = new FixtureDef();
				fixtureDef.shape = polygonShape;
				body.createFixture(fixtureDef);
				polygonShape.dispose();
			}
		}
		
		/*
		 * code added in chapter 11 to spawn carrots out of thin air
		 */
		private void spawnCarrots(Vector2 pos, int numCarrots, float radius)
		{
			float carrotShapeScale = 0.5f;
			// create carrots with box2d body and fixture
			for(int i = 0; i < numCarrots; i++)
			{
				Carrot carrot = new Carrot();
				// calculate random spawn position, rotation, and scale
				float x = MathUtils.random(-radius, radius);
				float y = MathUtils.random(5.0f, 15.0f);
				float rotation = MathUtils.random(0.0f, 360.0f) * MathUtils.degreesToRadians;
				float carrotScale = MathUtils.random(0.5f, 1.5f);
				carrot.scale.set(carrotScale, carrotScale);
				// create box2d body for carrot with start position
				// and angle of rotation
				BodyDef bodyDef = new BodyDef();
				bodyDef.position.set(pos);
				bodyDef.position.add(x, y);
				bodyDef.angle = rotation;
					Body body = b2world.createBody(bodyDef);
				body.setType(BodyType.DynamicBody);
				carrot.body = body;
					// create rectangular shape for carrot to allow
					// interactions (collisions) with other objects
				PolygonShape polygonShape = new PolygonShape();
				float halfWidth = carrot.bounds.width / 2.0f * carrotScale;
				float halfHeight = carrot.bounds.height /2.0f * carrotScale;
				polygonShape.setAsBox(halfWidth * carrotShapeScale,
					halfHeight * carrotShapeScale);
				// set physics attributes
				FixtureDef fixtureDef = new FixtureDef();
				fixtureDef.shape = polygonShape;
				fixtureDef.density = 50;
				fixtureDef.restitution = 0.5f;
				fixtureDef.friction = 0.5f;
				body.createFixture(fixtureDef);
				polygonShape.dispose();
					// finally, add new carrot to list for updating/rendering
				level.carrots.add(carrot);
			}
		}
		
		/*
		 * code from chapter 11 to provide carrot dropping behavior when bunny reaches goal
		 */
		private void onCollisionBunnyWithGoal()
		{
			goalReached = true;
			timeLeftGameOverDelay = Constants.TIME_DELAY_GAME_FINISHED;
			Vector2 centerPosBunnyHead =
				new Vector2(level.bunnyHead.position);
			centerPosBunnyHead.x += level.bunnyHead.bounds.width;
			spawnCarrots(centerPosBunnyHead, Constants.CARROTS_SPAWN_MAX,
				Constants.CARROTS_SPAWN_RADIUS);
		}
		
		//switches back to the menu screen
		private void backToMenu ()
		{
			// switch to menu screen
			game.setScreen(new MenuScreen(game));
		}
		
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
			// added in chapter 10 to make coin collecting sound
			AudioManager.instance.play(Assets.instance.sounds.pickupCoin);
			score += goldCoin.getScore();
			Gdx.app.log(TAG, "Gold coin collected");
		};
		
		//specifies what happens when Bunny collides with feather, from assignment 6
		private void onCollisionBunnyWithFeather(Feather feather) 
		{
			feather.collected = true;
			// added in chapter 10 to make feather collecting sound
			AudioManager.instance.play(Assets.instance.sounds.pickupFeather);
			score += feather.getScore();
			level.bunnyHead.setFeatherPowerup(true);
			Gdx.app.log(TAG,  "Feather collected");
		};
	
	private void initLevel()
	{
		score = 0;
		
		// added in chapter 8 to keep track of score
		scoreVisual = score;
		
		goalReached = false;// sets goal to has-not-been-reached by default
		
		level = new Level(Constants.LEVEL_01);
		cameraHelper.setTarget(level.bunnyHead);//added in assignment 6 to follow bunny head actor
		initPhysics(); // initatiates box2d physics in level
	}
	
	private static final String TAG =
			WorldController.class.getName();
	
	// public Sprite[] testSprites;
	// public int selectedSprite;
	
	public WorldController (Game game) {
		this.game = game;
		init();
	}
	
	
	// initiates control of objects
	private void init () { 
		Gdx.input.setInputProcessor(this);
		cameraHelper = new CameraHelper();
		//initTestObjects();
		lives = Constants.LIVES_START;
		
		// updated in chapter 8 to keep track of lost lives
		livesVisual = lives;
		timeLeftGameOverDelay = 0;
		
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
		if(isGameOver() || goalReached)
		{
			timeLeftGameOverDelay -= deltaTime;
			if(timeLeftGameOverDelay < 0)
				//init();
				backToMenu();
		}
		else
		{
			handleInputGame(deltaTime); //invokes the handleInputGame method to update positions, from assingment 6
		}
		
		//updateTestObjects(deltaTime);
		level.update(deltaTime);// added from assignment 6 to invoke level update
		testCollisions(); //invokes assignment 6 method to test collisions
		b2world.step(deltaTime, 8, 3); // added in chapter 11 to check the box2d world
		cameraHelper.update(deltaTime);
		if(!isGameOver() && isPlayerInWater())
		{
			AudioManager.instance.play(Assets.instance.sounds.liveLost);
			lives--;
			if(isGameOver())
				timeLeftGameOverDelay = Constants.TIME_DELAY_GAME_OVER;
			else
				initLevel();
		}
		//added in chapter 8 to allow for parallax scrolling of mountains
		level.mountains.updateScrollPosition
			(cameraHelper.getPosition());
		//added in chapter 8 to keep track of lives
		if(livesVisual > lives)
			livesVisual = Math.max(lives,  livesVisual - 1 * deltaTime);
		//added in chapter 8 to keep track of score
		if(scoreVisual < score)
			scoreVisual = Math.min(score,  scoreVisual + 250 * deltaTime);
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
		
		// Back to Menu
		else if (keycode == Keys.ESCAPE || keycode == Keys.BACK)
		{
			backToMenu();
		}
		
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
		
		// Test Collision: Bunny Head <-> Goal
		if(!goalReached)
		{
			r2.set(level.goal.bounds);
			r2.x += level.goal.position.x;
			r2.y += level.goal.position.y;
		}
		if(r1.overlaps(r2))
			onCollisionBunnyWithGoal();
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

	@Override // added in chapter 11 to free the physics world
	public void dispose() {
		// TODO Auto-generated method stub
		if(b2world != null)
			b2world.dispose();
	}
	
}
