/*
 * Drew Misicko
 */

package objects;

import game.Assets;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

// added in chapter 12 for animation
import com.badlogic.gdx.math.MathUtils;

//class added in assignment 6 with the rest of the actors
public class GoldCoin extends AbstractGameObject
{
	//added in assignment 6
	private TextureRegion regGoldCoin;
	
	public boolean collected;
	//end of instance vars added in chapter 6
	
	//constructor added in assignment 6
	public GoldCoin()
	{
		init();
	}
	
	//init method added in assignment 6
	private void init()
	{
		dimension.set(0.5f, 0.5f);
		
		// added in chapter 12 for animation
		setAnimation(Assets.instance.goldCoin.animGoldCoin);
		stateTime = MathUtils.random(0.0f, 1.0f);
		//
		
		regGoldCoin = Assets.instance.goldCoin.goldCoin;
		
		// Set bounding box for collision detection
		bounds.set(0, 0, dimension.x, dimension.y);
		
		collected = false;
	}
	
	//code added in assignment 6 to render the gold coin
	public void render(SpriteBatch batch)
	{
		if(collected) return;
		
		TextureRegion reg = null;
		//reg = regGoldCoin;
		reg = animation.getKeyFrame(stateTime, true);
		batch.draw(reg.getTexture(), position.x, position.y,
			origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y,
			rotation, reg.getRegionX(), reg.getRegionY(),
			reg.getRegionWidth(), reg.getRegionHeight(), false, false);
	}
	
	//code added in assignment 6 to get the amount of collected coins
	public int getScore()
	{
		return 100; // 1 gold coin = 100 points
	}
	
	
}
