/*
 * Drew Misicko
 */

package objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.misicko.gdx.game1.Assets;

//class added in assignment 6 with the rest of the actors
public class GenericPowerup extends AbstractGameObject
{
	//added in assignment 6
	private TextureRegion regGoldCoin;
	
	public boolean collected;
	//end of instance vars added in chapter 6
	
	//constructor added in assignment 6
	public GenericPowerup()
	{
		init();
	}
	
	//init method added in assignment 6
	private void init()
	{
		dimension.set(0.5f, 0.5f);
		
		regGoldCoin = Assets.instance.genericPowerup.genericPowerupRegion;
		
		// Set bounding box for collision detection
		bounds.set(0, 0, dimension.x, dimension.y);
		
		collected = false;
	}
	
	//code added in assignment 6 to render the genericPowerup
	public void render(SpriteBatch batch)
	{
		if(collected) return;
		
		TextureRegion reg = null;
		reg = regGoldCoin;
		batch.draw(reg.getTexture(), position.x, position.y,
			origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y,
			rotation, reg.getRegionX(), reg.getRegionY(),
			reg.getRegionWidth(), reg.getRegionHeight(), false, false);
	}
	
	//code added in assignment 6 to get the amount of collected genericPowerups
	public int getScore()
	{
		return 100; // 1 genericPowerup = 100 points
	}
}
