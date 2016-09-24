/*
 * Drew Misicko
 */
package objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.misicko.gdx.game1.Assets;

public class Rock extends AbstractGameObject
{
	private TextureRegion rockRegion;
	
	private int length;
	
	public Rock()
	{
		init();
	}
	
	private void init()
	{
		dimension.set(1, 1.5f);
		
		rockRegion = Assets.instance.rock.rockRegion;
		
		// Start length of this rock
		setLength(1);
	}
	
	public void setLength(int length)
	{
		this.length = length;
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
		reg = rockRegion;
		relX -= dimension.x / 4;
		batch.draw(reg.getTexture(), position.x + relX, position.y +
				relY, origin.x, origin.y, dimension.x / 4, dimension.y,
				scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(),
				reg.getRegionWidth(), reg.getRegionHeight(), false, false);
		
		// Draw middle
		relX = 0;
		reg = rockRegion;
		for(int i = 0; i < length; i++)
		{
			batch.draw(reg.getTexture(), position.x + relX, position.y
				+ relY, origin.x, origin.y, dimension.x, dimension.y,
				scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(),
				reg.getRegionWidth(), reg.getRegionHeight(), false, false);
			relX += dimension.x;
		}
		
		// Draw right edge
		reg = rockRegion;
		batch.draw(reg.getTexture(), position.x + relX, position.y +
			relY, origin.x + dimension.x / 8, origin.y, dimension.x / 4,
			dimension.y, scale.x, scale.y, rotation, reg.getRegionX(),
			reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(),
			true, false);
	}
}
