/*
 * Drew Misicko
 */

package objects;

//beginning of imports added in assignment 6
import util.Constants;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.misicko.gdx.game1.Assets;

import util.CharacterSkin;
import util.GamePreferences;


// from chapter 8, to add particle effects
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;

// imports added in chapter 10 for audio
import com.badlogic.gdx.math.MathUtils;
import util.AudioManager;

//class added in assignment 6 with rest of the actors
public class Pusheen extends AbstractGameObject
{
	//instance vars added in assignment 6S
	public static final String TAG = Pusheen.class.getName();
	
	private final float JUMP_TIME_MAX = 0.6f;// changed from .3f to make pusheen jump twice as high
	private final float JUMP_TIME_MIN = 0.2f;//changed from .1f to make pusheen jump twice as high
	private final float JUMP_TIME_OFFSET_FLYING = JUMP_TIME_MAX - 0.018f;
	
	public enum VIEW_DIRECTION { LEFT, RIGHT }
	
	public enum JUMP_STATE
	{
		GROUNDED, FALLING, JUMP_RISING, JUMP_FALLING
	}
	
	private TextureRegion regHead;
	
	public VIEW_DIRECTION viewDirection;
	public float timeJumping;
	public JUMP_STATE jumpState;
	public boolean hasSuperCookie;
	public float timeLeftForSuperCookie;
	//end of instance vars from assignment 6
	
	// from chapter 8, for particles
	public ParticleEffect dustParticles = new ParticleEffect();
	
	//constructor
	public Pusheen()
	{
		init();
	}
	
	//initiates, used in constructor, from assignment 6
	public void init() 
	{
		dimension.set(1, 1);
		regHead = Assets.instance.pusheen.pusheenRegion;
		// Center image on game object
		origin.set(dimension.x / 2, dimension.y / 2);
		// Bounding box for collision detection
		bounds.set(0, 0, dimension.x, dimension.y);
		// Set physics values
		terminalVelocity.set(3.0f, 4.0f);
		friction.set(12.0f, 0.0f);
		acceleration.set(0.0f, -25.0f);
		// View direction
		viewDirection = VIEW_DIRECTION.RIGHT;
		// Jump state
		jumpState = JUMP_STATE.FALLING;
		timeJumping = 0;
		// Power-ups
		hasSuperCookie = false;
		timeLeftForSuperCookie = 0;
		// Particles
		dustParticles.load(Gdx.files.internal("particles/dust.pfx"),
			Gdx.files.internal("particles"));
	};
	
	//sets the jumping state, from assignment 6
	public void setJumping(boolean jumpKeyPressed)
	{
		switch(jumpState)
		{
		case GROUNDED: // Character is standing on a platform
			if(jumpKeyPressed)
			{
				// make jumping sound
				AudioManager.instance.play(Assets.instance.sounds.jump);
				// Start counting jump time from the beginning
				timeJumping = 0;
				jumpState = JUMP_STATE.JUMP_RISING;
			}
			break;
		case JUMP_RISING: // Rising in the air
			if(!jumpKeyPressed)
			{
				jumpState = JUMP_STATE.JUMP_FALLING;
			}
			break;
		case FALLING: //Falling down
		case JUMP_FALLING: // Falling down after jump
			if(jumpKeyPressed && hasSuperCookie)
			{
				// plays enhanced jump sound
				AudioManager.instance.play(
					Assets.instance.sounds.jumpWithSuperCookie, 1,
					MathUtils.random(1.0f, 1.1f));
				timeJumping = JUMP_TIME_OFFSET_FLYING;
				jumpState = JUMP_STATE.JUMP_RISING;
			}
			break;
		}
	};
	
	//sets superCookie from assignment 6
	public void setSuperCookie(boolean pickedUp) 
	{
		hasSuperCookie = pickedUp;
		if(pickedUp)
		{
			timeLeftForSuperCookie = 
				Constants.ITEM_SUPERCOOKIE_DURATION;
		}
		terminalVelocity.set(6.0f, 8.0f);//superCookie increases speed. (and still make him fly, because that's cool)
	};
	
	//returns whether or not Pusheen has superCookie
	public boolean hasSuperCookie()
	{
		return hasSuperCookie && timeLeftForSuperCookie > 0;
	}

	//overrides the render method to handle the drawing of images for Pusheen
	@Override
	public void render(SpriteBatch batch)
	{
		// TODO Auto-generated method stub
		TextureRegion reg = null;
		
		// Apply Skin Color
		batch.setColor(
			CharacterSkin.values()[GamePreferences.instance.charSkin].getColor());
		
		// Set special color when game object has a superCookie
		if(hasSuperCookie)
		{
			batch.setColor(1.0f, 0.8f, 0.0f, 1.0f);
		}
		// Draw image
		reg = regHead;
		batch.draw(reg.getTexture(), position.x, position.y, origin.x,
			origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation,
			reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(),
			reg.getRegionHeight(), viewDirection == VIEW_DIRECTION.LEFT,
			false);
		
		// Reset color to white
		batch.setColor(1, 1, 1, 1);
		
		// Draw Particles, from chapter 8
		dustParticles.draw(batch);
	};
	
	//overrides the update method to take care of jumps and superCookie status, from assignment 6
	@Override
	public void update(float deltaTime)
	{
		super.update(deltaTime);
		if(velocity.x != 0)
		{
			viewDirection = velocity.x < 0 ? VIEW_DIRECTION.LEFT : VIEW_DIRECTION.RIGHT;
		}
		if(timeLeftForSuperCookie > 0)
		{
			timeLeftForSuperCookie -= deltaTime;
			if(timeLeftForSuperCookie < 0)
			{
				// disable powerup
				timeLeftForSuperCookie = 0;
				setSuperCookie(false);
				terminalVelocity.set(3.0f, 4.0f);
			}
		}
		// added from chapter 8 to update particles
		dustParticles.update(deltaTime);
	}
	
	//overrides the updateMotionY method to handle elevation changes, from assignment 6
	@Override
	protected void updateMotionY(float deltaTime)
	{
		switch(jumpState)
		{
			case GROUNDED:
				jumpState = JUMP_STATE.FALLING;
				if(velocity.x != 0)
				{
					dustParticles.setPosition(position.x + dimension.x / 2,
						position.y);
					dustParticles.start();
				}
				break;
			case JUMP_RISING:
				//keep track of jump time
				timeJumping += deltaTime;
				// Jump time left?
				if(timeJumping <= JUMP_TIME_MAX)
				{
					// Still jumping
					velocity.y = terminalVelocity.y;
				}
				break;
			case FALLING:
				break;
			case JUMP_FALLING:
				// Add delta times to track jump time
				timeJumping += deltaTime;
				// Jump to minimal height if jump key was pressed too short
				if(timeJumping > 0 && timeJumping <= JUMP_TIME_MIN)
				{
					// Still jumping
					velocity.y = terminalVelocity.y;
				}
		}
		if(jumpState != JUMP_STATE.GROUNDED)
		{
			dustParticles.allowCompletion(); // added in chapt 8 to allow dust to become invisible
			super.updateMotionY(deltaTime);
		}
	}
}
