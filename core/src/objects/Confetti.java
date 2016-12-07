//Drew Misicko
package objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.misicko.gdx.game1.Assets;

public class Confetti extends AbstractGameObject
{
	private TextureRegion regConfetti;
	
	public Confetti()
	{
		init();
	}
	private void init()
	{
		dimension.set(0.25f, 0.25f);
		
		regConfetti = Assets.instance.levelDecoration.confetti;
		
		// Set bounding box for collision detection
		bounds.set(0, 0, dimension.x, dimension.y);
		origin.set(dimension.x / 2, dimension.y / 2);
	}
	
	public void render(SpriteBatch batch)
	{
		TextureRegion reg = null;
		
		reg = regConfetti;
		batch.draw(reg.getTexture(), position.x - origin.x,
			position.y - origin.y, origin.x, origin.y, dimension.x,
			dimension.y, scale.x, scale.y, rotation, reg.getRegionX(),
			reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(),
			false, false);
	}
}
