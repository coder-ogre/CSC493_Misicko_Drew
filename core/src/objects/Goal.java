/*
 * Drew Misicko, carrot class
 * class created in chapter 11 assignment
 */

package objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import game.Assets;

public class Goal extends AbstractGameObject
{
	private TextureRegion regGoal;// holds the region on the texture pack for the goal
	
	public Goal()
	{
		init();
	}
	
	// initiates goal
	private void init()
	{
		dimension.set(3.0f, 3.0f);
		regGoal = Assets.instance.levelDecoration.goal;
		
		// Set bounding box for collision detection
		bounds.set(1, Float.MIN_VALUE, 10, Float.MAX_VALUE);
		origin.set(dimension.x / 2.0f, 0.0f);
	}
	
	/* added in chapter 10 to set the region and bounds of carrot object*/
	/* bounds are set to be extremely tall, so that no matter the y position, the goal is touched */
	public void render(SpriteBatch batch)
	{
		TextureRegion reg = null;
		reg = regGoal;
		batch.draw(reg.getTexture(), position.x - origin.x,
			position.y - origin.y, origin.x, origin.y, dimension.x,
			dimension.y, scale.x, scale.y, rotation,
			reg.getRegionX(), reg.getRegionY(),
			reg.getRegionWidth(), reg.getRegionHeight(),
			false, false);
	}
}
