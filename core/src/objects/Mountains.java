/*
 * Drew Misicko
 */

package objects;

import game.Assets;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

// added in chapter 8 to add parallax scrolling
import com.badlogic.gdx.math.Vector2;

public class Mountains extends AbstractGameObject
{
	private TextureRegion regMountainLeft;
	private TextureRegion regMountainRight;
	
	private int length;
	
	public Mountains(int length)
	{
		this.length = length;
		init();
	}
	
	private void init()
	{
		dimension.set(10, 2);
		
		regMountainLeft = Assets.instance.levelDecoration.mountainLeft;
		regMountainRight = Assets.instance.levelDecoration.mountainRight;
		
		// shift mountain and extend length
		origin.x = -dimension.x * 2;
		length += dimension.x * 2;
	}
	
	//draws mountains
	// updated in chapter 8 to add parallax features
	private void drawMountain(SpriteBatch batch, float offsetX, float offsetY, float tintColor, float parallaxSpeedX)
	{
		TextureRegion reg = null;
		batch.setColor(tintColor, tintColor, tintColor, 1);
		float xRel = dimension.x * offsetX;
		float yRel = dimension.y * offsetY;
		
		// mountains span the whole level
		int mountainLength = 0;
		
		//added in chapter 8 to add parallax features
		mountainLength += MathUtils.ceil(
			length / (2 * dimension.x) * (1 - parallaxSpeedX));
		
		mountainLength += MathUtils.ceil(length / (2 * dimension.x));
		mountainLength += MathUtils.ceil(0.5f + offsetX);
		for(int i = 0; i < mountainLength; i++)
		{
			// mountainLeft, updated in chapter 8 for parallax
			reg = regMountainLeft;
			batch.draw(reg.getTexture(),
				origin.x + xRel + position.x * parallaxSpeedX,
				origin.y + yRel + position.y,
				origin.x, origin.y,
				dimension.x, dimension.y,
				scale.x, scale.y,
				rotation,
				reg.getRegionX(), reg.getRegionY(),
				reg.getRegionWidth(), reg.getRegionHeight(),
				false, false);
			
			/*batch.draw(reg.getTexture(), origin.x + xRel, position.y +
				origin.y + yRel, origin.x, origin.y, dimension.x, dimension.y,
				scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(),
				reg.getRegionWidth(), reg.getRegionHeight(), false, false);*/
			xRel += dimension.x;
			
			// mountain right, updated in chapter 8 for parallax
			reg = regMountainRight;
			batch.draw(reg.getTexture(),
				origin.x + xRel + position.x * parallaxSpeedX,
				origin.y + yRel + position.y,
				origin.x, origin.y,
				dimension.x, dimension.y,
				scale.x, scale.y,
				rotation,
				reg.getRegionX(), reg.getRegionY(),
				reg.getRegionWidth(), reg.getRegionHeight(),
				false, false);
			
			/*batch.draw(reg.getTexture(), origin.x + xRel, position.y +
				origin.y + yRel, origin.x, origin.y, dimension.x, dimension.y,
				scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(),
				reg.getRegionWidth(), reg.getRegionHeight(), false, false);*/
			xRel += dimension.x;
		}
		// reset color to white
		batch.setColor(1, 1, 1, 1);
	}
	
	@Override
	public void render(SpriteBatch batch) {
		// TODO Auto-generated method stub
		
		// distant mountains (dark gray)
		drawMountain(batch, 0.5f, 0.5f, 0.5f, 0.8f);
		// distant mountains (gray)
		drawMountain(batch, 0.25f, 0.25f, 0.7f, 0.5f);
		// distant mountains (light gray)
		drawMountain(batch, 0.0f, 0.0f, 0.9f, 0.3f);
	}

	// added in chapter 8 to update the scrolling position for parallax
	public void updateScrollPosition(Vector2 camPosition)
	{
		position.set(camPosition.x, position.y);
	}
}
