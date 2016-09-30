/*
 * Drew Misicko
 */

package com.misicko.gdx.game1;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.misicko.gdx.game1.Assets;
import objects.AbstractGameObject;

public class LavaOverlay extends AbstractGameObject
{
	private TextureRegion regLavaOverlay;
	private float length;
	
	public LavaOverlay (float length)
	{
		this.length = length;
		init();
	}
	
	private void init() {
		dimension.set(length * 10, 3);
		
		regLavaOverlay = Assets.instance.levelDecoration.lavaOverlay;
		
		origin.x = -dimension.x / 2;
	}

	@Override
	public void render(SpriteBatch batch) {
		// TODO Auto-generated method stub
		TextureRegion reg = null;
		reg = regLavaOverlay;
		batch.draw(reg.getTexture(), position.x + origin.x, position.y 
			+ origin. y, origin.x, origin.y, dimension.x, dimension.y, scale.x,
			scale.y, rotation, reg.getRegionX(), reg.getRegionY(),
			reg.getRegionWidth(), reg.getRegionHeight(), false, false);
	}
}