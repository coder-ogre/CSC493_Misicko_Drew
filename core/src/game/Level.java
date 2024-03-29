/*
 * Drew Misicko
 */

package game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

//imports added from assignment 6


import objects.AbstractGameObject;
import objects.BunnyHead;
import objects.Clouds;
import objects.Feather;
import objects.GoldCoin;
import objects.Mountains;
import objects.Rock;
import objects.WaterOverlay;

/* added in chapter 11 to add goal and carrots to level */
import objects.Carrot;
import objects.Goal;

public class Level 
{
	public static final String TAG = Level.class.getName();
	
	//instance vars from assignment 6
	public BunnyHead bunnyHead;
	public Array<GoldCoin> goldCoins;
	public Array<Feather> feathers;
	//end of istance vars from assignment 6
	
	/* instance variables added in chapter 11 to add goal and carrot */
	public Array<Carrot> carrots;
	public Goal goal;
	
	public enum BLOCK_TYPE
	{
		EMPTY(0, 0, 0), // black
		ROCK(0, 255, 0), // green
		PLAYER_SPAWNPOINT(255, 255, 255), // white
		ITEM_FEATHER(255, 0, 255), // purple
		ITEM_GOLD_COIN(255, 255, 0), // yellow
		GOAL(255, 0, 0); // red // added in chapter 11 to add goal
		
		private int color;
		
		private BLOCK_TYPE(int r, int g, int b)
		{
			color = r << 24 | g << 16 | b << 8 | 0xff;
		}
		
		public boolean sameColor(int color)
		{
			return this.color == color;
		}
		
		public int getColor()
		{
			return color;
		}
	}
	
	// objects
	public Array<Rock> rocks;
	
	// decoration
	public Clouds clouds;
	public Mountains mountains;
	public WaterOverlay waterOverlay;
	
	public Level(String filename)
	{
		init(filename);
	}
	
	private void init(String filename)
	{
		// player character
		bunnyHead = null; // from assignment 6
		
		// objects
		rocks = new Array<Rock>();
		
		goldCoins = new Array<GoldCoin>();//added from assignment 6
		feathers = new Array<Feather>(); // added from assignment 6
		
		carrots = new Array<Carrot>(); // added in chapter 11.
		
		// load image file that represents the level data
		Pixmap pixmap = new Pixmap(Gdx.files.internal(filename));
		// scan pixels from top-left to bottom-right
		int lastPixel = -1;
		for(int pixelY = 0; pixelY < pixmap.getHeight(); pixelY++)
		{
			for(int pixelX = 0; pixelX < pixmap.getWidth(); pixelX++)
			{
				AbstractGameObject obj = null;
				float offsetHeight = 0;
				// height grows from bottom to top
				float baseHeight = pixmap.getHeight() - pixelY;
				// get color of current pixel as 32-bit RGBA value
				int currentPixel = pixmap.getPixel(pixelX, pixelY);
				// find matching color value to identify block type at (x, y)
				// point and create the corresponding game object if there is
				// a match
				
				// empty space
				if(BLOCK_TYPE.EMPTY.sameColor(currentPixel))
				{
					// do nothing
				}
				// rock
				else if(BLOCK_TYPE.ROCK.sameColor(currentPixel))
				{
					if(lastPixel != currentPixel)
					{
						obj = new Rock();
						float heightIncreaseFactor = 0.25f;
						offsetHeight = -2.5f;
						obj.position.set(pixelX, baseHeight * obj.dimension.y
						* heightIncreaseFactor + offsetHeight);
						rocks.add((Rock)obj);
					}
					else
					{
						rocks.get(rocks.size - 1).increaseLength(1);
					}
				}
				// player spawn point
				else if(BLOCK_TYPE.PLAYER_SPAWNPOINT.sameColor(currentPixel))
				{
					//code added in assignment 6
					obj = new BunnyHead();
					offsetHeight = -3.0f;
					obj.position.set(pixelX, baseHeight * obj.dimension.y + offsetHeight);
					bunnyHead = (BunnyHead)obj;
					//end of code added in assignment 6
				}
				// feather
				else if(BLOCK_TYPE.ITEM_FEATHER.sameColor(currentPixel))
				{
					//code added in assignment 6
					obj = new Feather();
					offsetHeight = -1.5f;
					obj.position.set(pixelX, baseHeight * obj.dimension.y + offsetHeight);
					feathers.add((Feather)obj);
					//end of code added in assignment 6
				}
				// gold coin
				else if(BLOCK_TYPE.ITEM_GOLD_COIN.sameColor(currentPixel))
				{
					//code added in assignment 6
					obj = new GoldCoin();
					offsetHeight = -1.5f;
					obj.position.set(pixelX, baseHeight * obj.dimension.y + offsetHeight);
					goldCoins.add((GoldCoin)obj);
					//end of code added in chapter 6
				}
				// goal (added in chapter 11)
				else if (BLOCK_TYPE.GOAL.sameColor(currentPixel))
				{
					obj = new Goal();
					offsetHeight = -3.0f;
					obj.position.set(pixelX, baseHeight  + offsetHeight);
					goal = (Goal)obj;
				}
				//unknown object/pixel color
				else
				{
					int r = 0xff & (currentPixel >>> 24); // red color channel
					int g = 0xff & (currentPixel >>> 16); // green color channel
					int b = 0xff & (currentPixel >>> 8); // blue color channel
					int a = 0xff & currentPixel; // alpha channel
					Gdx.app.error(TAG, "Unknown object at x<" + pixelX + "> y<"
						+ pixelY + ">: r<" + r + "> g<" + g + "> b<" + b + "> a<" + a + ">");
				}
				lastPixel = currentPixel;
			}
		}
		
		// decoration
		clouds = new Clouds(pixmap.getWidth());
		clouds.position.set(0, 2);
		mountains = new Mountains(pixmap.getWidth());
		mountains.position.set(-1, -1);
		waterOverlay = new WaterOverlay(pixmap.getWidth());
		waterOverlay.position.set(0, -3.75f);
		
		// free memory
		pixmap.dispose();
		Gdx.app.debug(TAG, "level '" + filename + "' loaded");
	}
	
	public void render(SpriteBatch batch)
	{
		// Draw Mountains
		mountains.render(batch);
		
		// Draws the Goal (from chapter 11)
		goal.render(batch);
		
		// Draw Rocks
		for(Rock rock : rocks)
			rock.render(batch);
		
		// code added in assignment 6 for actor rendering
		//Draw Gold Coins
		for(GoldCoin goldCoin : goldCoins)
			goldCoin.render(batch);
		// Draw Feathers
		for (Feather feather: feathers)
			feather.render(batch);
		// Draws the carrots
		for(Carrot carrot : carrots)
			carrot.render(batch);
		// Draw Player Character
		bunnyHead.render(batch);
		//end of code added in assignment 6
		
		// Draw Water Overlay
		waterOverlay.render(batch);
		
		// Draw Clouds
		clouds.render(batch);
	}
	
	//updates elements in level, from assignment 6
	public void update(float deltaTime)
	{
		bunnyHead.update(deltaTime);
		for(Rock rock : rocks)
			rock.update(deltaTime);
		for(GoldCoin goldCoin : goldCoins)
			goldCoin.update(deltaTime);
		for(Feather feather : feathers)
			feather.update(deltaTime);
		for(Carrot carrot : carrots)
			carrot.update(deltaTime);
		clouds.update(deltaTime);
	}
}
