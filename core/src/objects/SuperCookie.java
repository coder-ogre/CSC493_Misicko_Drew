/*
 * Drew Misicko
 */

package objects;

//imports added in assignment 6
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.misicko.gdx.game1.Assets;

//class added in assignment 6 with rest of actors
public class SuperCookie extends AbstractGameObject
{
	//beginning of instance variables added in assignment 6
	private TextureRegion superCookieRegion;
	
	public boolean collected;
	//end of instance variables added in assignment 6
	
	// code to construct the superCookie, added in assignment 6
	public SuperCookie()
	{
		init();
	}
	
	// defines the init() method used in the constructor, added in assignment 6
	private void init()
	{
		dimension.set(0.5f, 0.5f);
		
		superCookieRegion = Assets.instance.superCookie.superCookieRegion;
		
		// Set bounding box for collision detection
		
		bounds.set(0, 0, dimension.x, dimension.y);
		
		collected = false;
	}
	
	//renders the superCookie, added in assignment 6
	public void render(SpriteBatch batch)
	{
		if(collected) return;
		
		TextureRegion reg = null;
		reg = superCookieRegion;
		batch.draw(reg.getTexture(), position.x, position.y,
			origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y,
			rotation, reg.getRegionX(), reg.getRegionY(),
			reg.getRegionWidth(), reg.getRegionHeight(), false, false);
	}
	
	//gets score, added in assignment 6
	public int getScore()
	{
		return 250; //1 superCookie = 250 points
	}
}