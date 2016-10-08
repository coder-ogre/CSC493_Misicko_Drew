/*
 * Drew Misicko
 */

package objects;

//beginning of imports added in assignment 6
import game.Assets;
import util.Constants;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
//end of imports added in assignment 6

import com.misicko.gdx.game1.CharacterSkin;
import com.misicko.gdx.game1.GamePreferences;

//class added in assignment 6 with rest of the actors
public class BunnyHead extends AbstractGameObject
{
	//instance vars added in assignment 6
	public static final String TAG = BunnyHead.class.getName();
	
	private final float JUMP_TIME_MAX = 0.3f;
	private final float JUMP_TIME_MIN = 0.1f;
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
	public boolean hasFeatherPowerup;
	public float timeLeftFeatherPowerup;
	//end of instance vars from assignment 6
	
	//constructor
	public BunnyHead()
	{
		init();
	}
	
	//initiates, used in constructor, from assignment 6
	public void init() 
	{
		dimension.set(1, 1);
		regHead = Assets.instance.bunny.head;
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
		hasFeatherPowerup = false;
		timeLeftFeatherPowerup = 0;
	};
	
	//sets the jumping state, from assignment 6
	public void setJumping(boolean jumpKeyPressed)
	{
		switch(jumpState)
		{
		case GROUNDED: // Character is standing on a platform
			if(jumpKeyPressed)
			{
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
			if(jumpKeyPressed && hasFeatherPowerup)
			{
				timeJumping = JUMP_TIME_OFFSET_FLYING;
				jumpState = JUMP_STATE.JUMP_RISING;
			}
			break;
		}
	};
	
	//sets feather powerup from assignment 6
	public void setFeatherPowerup(boolean pickedUp) 
	{
		hasFeatherPowerup = pickedUp;
		if(pickedUp)
		{
			timeLeftFeatherPowerup = 
				Constants.ITEM_FEATHER_POWERUP_DURATION;
		}
	};
	
	//returns whether or not bunny has feather powerup
	public boolean hasFeatherPowerup()
	{
		return hasFeatherPowerup && timeLeftFeatherPowerup > 0;
	}

	//overrides the render method to handle the drawing of images for bunnyhead
	@Override
	public void render(SpriteBatch batch)
	{
		// TODO Auto-generated method stub
		TextureRegion reg = null;
		
		// Apply Skin Color
		batch.setColor(
			CharacterSkin.values()[GamePreferences.instance.charSkin]
			.getColor());
		
		// Set special color when game object has a feather power-up
		if(hasFeatherPowerup)
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
		
	};
	
	//overrides the update method to take care of jumps and feather status, from assignment 6
	@Override
	public void update(float deltaTime)
	{
		super.update(deltaTime);
		if(velocity.x != 0)
		{
			viewDirection = velocity.x < 0 ? VIEW_DIRECTION.LEFT : VIEW_DIRECTION.RIGHT;
		}
		if(timeLeftFeatherPowerup > 0)
		{
			timeLeftFeatherPowerup -= deltaTime;
			if(timeLeftFeatherPowerup < 0)
			{
				// disable powerup
				timeLeftFeatherPowerup = 0;
				setFeatherPowerup(false);
			}
		}
	}
	
	//overrides the updateMotionY method to handle elevation changes, from assignment 6
	@Override
	protected void updateMotionY(float deltaTime)
	{
		switch(jumpState)
		{
			case GROUNDED:
				jumpState = JUMP_STATE.FALLING;
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
			super.updateMotionY(deltaTime);
	}
}
