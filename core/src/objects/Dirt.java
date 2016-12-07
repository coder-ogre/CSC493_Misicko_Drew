/*
 * Drew Misicko
 */

package objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.misicko.gdx.game1.Assets;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Dirt extends AbstractGameObject
{

	private TextureRegion dirtRegion;
	private int length;
	private final float FLOAT_CYCLE_TIME = 2.0f;
	private final float FLOAT_AMPLITUDE = 0.25f;
	private float floatCycleTimeLeft;
	private boolean floatingDownwards;
	private Vector2 floatTargetPosition;

	public Dirt() 
	{
		init();
	}

	private void init () 
	{
		dimension.set(1, 1.5f);
		dirtRegion = Assets.instance.dirt.dirtRegion;

		// Start length of this rock
		setLength(1);

		//make sure that the floating mechanism is correctly initialized. The
		//starting value for the float direction is set to up
		floatingDownwards = false;
		floatCycleTimeLeft = MathUtils.random(0,FLOAT_CYCLE_TIME / 2);
		floatTargetPosition = null;
	}

	public void setLength (int length) 
	{
		this.length = length;

		// Update bounding box for collision detection
		bounds.set(0, 0, dimension.x * length, dimension.y);
	}

	public void increaseLength (int amount) 
	{
		setLength(length + amount);
	}


	@Override
	public void render(SpriteBatch batch) 
	{	
		TextureRegion reg = null;
		float relX = 0;
		float relY = 0;

		// Draw left edge
		reg = dirtRegion;
		relX -= dimension.x / 4;
		batch.draw(reg.getTexture(), position.x + relX, position.y +
				relY, origin.x, origin.y, dimension.x / 4, dimension.y,
				scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(),
				reg.getRegionWidth(), reg.getRegionHeight(), false, false);

		// Draw middle
		relX = 0;
		reg = dirtRegion;
		for (int i = 0; i < length; i++) 
		{
			batch.draw(reg.getTexture(), position.x + relX, position.y +
					relY, origin.x, origin.y, dimension.x, dimension.y,scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(),reg.getRegionWidth(), reg.getRegionHeight(), false, false);
			relX += dimension.x;
		}
		// Draw right edge
		reg = dirtRegion;
		batch.draw(reg.getTexture(),position.x + relX, position.y +
				relY, origin.x + dimension.x / 8, origin.y, dimension.x / 4,
				dimension.y, scale.x, scale.y, rotation, reg.getRegionX(),
				reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(),
				true, false);
	}

	@Override
	public void update (float deltaTime)
	{
		super.update(deltaTime);
		floatCycleTimeLeft -= deltaTime;
	
		//if (floatTargetPosition == null)
		
			//used to store the next target position, as shown here
			floatTargetPosition = new Vector2(position);
		
		if (floatCycleTimeLeft<= 0) 
		{
			floatCycleTimeLeft = FLOAT_CYCLE_TIME;
			floatingDownwards = !floatingDownwards;
			//floatTargetPosition.y += FLOAT_AMPLITUDE* (floatingDownwards ? -1 : 1);
			body.setLinearVelocity(0, FLOAT_AMPLITUDE * (floatingDownwards ? -1 : 1));
		}
		else
		{
			body.setLinearVelocity(body.getLinearVelocity().scl(0.98f));
		}
		position.lerp(floatTargetPosition, deltaTime);
	}
}



/*package objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.misicko.gdx.game1.Assets;

// added in chapter 8 to have rocks float on water
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Dirt extends AbstractGameObject
{
	private TextureRegion dirtRegion;
	
	private int length;
	
	// added in chapter 8 to have rocks float on water
	private final float FLOAT_CYCLE_TIME = 2.0f;
	private final float FLOAT_AMPLITUDE = 0.25f;
	private float floatCycleTimeLeft;
	private boolean floatingDownwards;
	private Vector2 floatTargetPosition;
	
	public Dirt()
	{
		init();
	}
	
	private void init()
	{
		dimension.set(1, 1.5f);
		
		dirtRegion = Assets.instance.dirt.dirtRegion;
		
		// Start length of this dirt
		setLength(1);
		
		// from chapter 8, to let rocks float
		floatingDownwards = false;
		floatCycleTimeLeft = MathUtils.random(0,
			FLOAT_CYCLE_TIME / 2);
		floatTargetPosition = null;
	}
	
	public void setLength(int length)
	{
		this.length = length;
		
		//this part added in assignment 6 to ensure bounding box size is adjusted when dirt length is changed
		bounds.set(0, 0, dimension.x * length, dimension.y);
		//end of code added from assignment 6
	}
	
	public void increaseLength(int amount)
	{
		setLength(length + amount);
	}
	
	@Override
	public void render(SpriteBatch batch) {
		// TODO Auto-generated method stub
		TextureRegion reg = null;
		
		float relX = 0;
		float relY = 0;
		
		// Draw left edge
		reg = dirtRegion;
		relX -= dimension.x / 4;
		batch.draw(reg.getTexture(), position.x + relX, position.y +
				relY, origin.x, origin.y, dimension.x / 4, dimension.y,
				scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(),
				reg.getRegionWidth(), reg.getRegionHeight(), false, false);
		
		// Draw middle
		relX = 0;
		reg = dirtRegion;
		for(int i = 0; i < length; i++)
		{
			batch.draw(reg.getTexture(), position.x + relX, position.y
				+ relY, origin.x, origin.y, dimension.x, dimension.y,
				scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(),
				reg.getRegionWidth(), reg.getRegionHeight(), false, false);
			relX += dimension.x;
		}
		
		// Draw right edge
		reg = dirtRegion;
		batch.draw(reg.getTexture(), position.x + relX, position.y +
			relY, origin.x + dimension.x / 8, origin.y, dimension.x / 4,
			dimension.y, scale.x, scale.y, rotation, reg.getRegionX(),
			reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(),
			true, false);
	}
	
	// chapter 8, allows rocks to float on water
	@Override
	public void update(float deltaTime)
	{
		super.update(deltaTime);
		
		floatCycleTimeLeft -= deltaTime;
		if(floatTargetPosition == null)
			floatTargetPosition = new Vector2(position);
		
		if(floatCycleTimeLeft <= 0)
		{
			floatCycleTimeLeft = FLOAT_CYCLE_TIME;
			floatingDownwards = !floatingDownwards;
			floatTargetPosition.y += FLOAT_AMPLITUDE
				* (floatingDownwards ? -1 : 1);
		}
		position.lerp(floatTargetPosition, deltaTime);
	}
}*/