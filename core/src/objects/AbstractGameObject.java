/*
 * Drew Misicko
 */


public abstract class AbstractGameObject
{
	public Vector2 position;
	public Vector2 dimension;
	public Vector2 origin;
	public Vector2 scale;
	public float rotation;
	
	//stuff added chapter 6 assignment 6
	public Vector2 velocity;
	public Vector2 terminalVelocity;
	public Vector2 friction;
	
	public Vector2 acceleration;
	public Rectangle bounds;
	//end of instance variables from assignment 6 chapter 6
	
	public AbstractGameObject()
	{
		position = new Vector2();
		dimension = new Vector2(1, 1);
		origin = new Vector2();
		scale = new Vector2(1, 1);
		rotation = 0;
		
		//code added in assignment 6 to add actors
		velocity = new Vector2();
		terminalVelocity = new Vector2(1, 1);
		friction = new Vector2();
		acceleration = new Vector2();
		bounds = new Rectangle();
		//end of snippet from assignment 6 for adding actors
	}
	
	//method added in chapter 6 to add actor features by allowing motion to update
	protected void updateMotionX(float deltaTime)
	{
		if(velocity.x != 0)
		{
			// Apply friction
			if(velocity.x > 0) {
				velocity.x = Math.max(velocity.x - friction.x * deltaTime, 0);
			}
			else
			{
				velocity.x = Math.min(velocity.x + friction.x * deltaTime, 0);
			}
		}
		//Apply acceleration
		velocity.x += acceleration.x *deltaTime;
		// Make sure that the object's velocity does not exceed the
		// positive or negative terminal velocity
		velocity.x = MathUtils.clamp(velocity.x, -terminalVelocity.x, terminalVelocity.x);
	}
	
	//method added in chapter 6 to add actor features by allowing motion to update
	protected void updateMotionY(float deltaTime)
	{
		if(velocity.y != 0)
		{
			// Apply friction
			if(velocity.y > 0)
			{
				velocity.y = Math.max(velocity.y - friction.y * deltaTime, 0);
			}
			else
			{
				velocity.y = Math.min(velocity.y + friction.y * deltaTime, 0);
			}
		}
		//Apply acceleration
		velocity.y += acceleration.y *deltaTime;
		// Make sure that the object's velocity does not exceed the
		// positive or negative terminal velocity
		velocity.y = MathUtils.clamp(velocity.y, -terminalVelocity.y, terminalVelocity.y);
	}
	
	public void update(float deltaTime)
	{
		//code to update game actor status added in assignment 6
		updateMotionX(deltaTime);
		updateMotionY(deltaTime);
		// Move to new postion
		position.x += velocity.x * deltaTime;
		position.y += velocity.y * deltaTime;		
		//end of code added in assignment 6
	}
	
	public abstract void render(SpriteBatch batch);
}

/*package objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;

public abstract class AbstractGameObject
{
	public Vector2 position;
	public Vector2 dimension;
	public Vector2 origin;
	public Vector2 scale;
	public float rotation;
	
	public AbstractGameObject()
	{
		position = new Vector2();
		dimension = new Vector2(1, 1);
		origin = new Vector2();
		scale = new Vector2(1, 1);
		rotation = 0;
	}
	
	public void update(float deltaTime)
	{
		
	}
	
	public abstract void render(SpriteBatch batch);
}*/
