/*
 * Drew Misicko
 */

package objects;

//imports added in assignment 6
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.misicko.gdx.game1.Assets;

//class added in assignment 6 with rest of actors
public class Feather extends AbstractGameObject
{
	//beginning of instance variables added in assignment 6
	private TextureRegion regFeather;
	
	public boolean collected;
	//end of instance variables added in assignment 6
	
	// code to construct the Feather, added in assignment 6
	public Feather()
	{
		init();
	}
	
	// defines the init() method used in the constructor, added in assignment 6
	private void init()
	{
		dimension.set(0.5f, 0.5f);
		
		regFeather = Assets.instance.feather.feather;
		
		// Set bounding box for collision detection
		
		bounds.set(0, 0, dimension.x, dimension.y);
		
		collected = false;
	}
	
	//renders the feather, added in assignment 6
	public void render(SpriteBatch batch)
	{
		if(collected) return;
		
		TextureRegion reg = null;
		reg = regFeather;
		batch.draw(reg.getTexture(), position.x, position.y,
			origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y,
			rotation, reg.getRegionX(), reg.getRegionY(),
			reg.getRegionWidth(), reg.getRegionHeight(), false, false);
	}
	
	//gets score, added in assignment 6
	public int getScore()
	{
		return 250; //1 feather = 250 points
	}
}