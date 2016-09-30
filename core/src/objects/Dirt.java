/*
 * Drew Misicko
 */

package objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.misicko.gdx.game1.Assets;

public class Dirt extends AbstractGameObject
{
	private TextureRegion dirtRegion;
	
	private int length;
	
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
}