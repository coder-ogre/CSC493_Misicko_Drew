/*
 * Drew Misicko
 */

package com.misicko.gdx.game1;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

//imports added from assignment 6


import objects.AbstractGameObject;
import objects.Clouds;
import objects.Dirt;
import objects.GenericPowerup;
import objects.LavaOverlay;
import objects.Mountains;
import objects.Pusheen;
import objects.SuperCookie;

public class Level 
{
	public static final String TAG = Level.class.getName();
	
	//instance vars from assignment 6
	public Pusheen pusheen;
	public Array<GenericPowerup> genericPowerups;
	public Array<SuperCookie> superCookies;
	//end of istance vars from assignment 6
	
	public enum BLOCK_TYPE
	{
		EMPTY(0, 0, 0), // black
		DIRT(0, 255, 0), // green
		PLAYER_SPAWNPOINT(255, 255, 255), // white
		ITEM_SUPERCOOKIE(255, 0, 255), // purple
		ITEM_GENERICPOWERUP(255, 255, 0); // yellow
		
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
	public Array<Dirt> dirts;
	
	// decoration
	public Clouds clouds;
	public Mountains mountains;
	public LavaOverlay lavaOverlay;
	
	public Level(String filename)
	{
		init(filename);
	}
	
	private void init(String filename)
	{
		// player character
		pusheen = null; // from assignment 6
		
		// objects
		dirts = new Array<Dirt>();
		
		genericPowerups = new Array<GenericPowerup>();//added from assignment 6
		superCookies = new Array<SuperCookie>(); // added from assignment 6
		
		// load image file that represents the level data
		Pixmap pixmap = new Pixmap(Gdx.files.internal(filename));
		// scan pixels from top-left to bottom-right
		int lastPixel = -1;for(int pixelY = 0; pixelY < pixmap.getHeight(); pixelY++)
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
				// dirt
				else if(BLOCK_TYPE.DIRT.sameColor(currentPixel))
				{
					if(lastPixel != currentPixel)
					{
						obj = new Dirt();
						float heightIncreaseFactor = 0.25f;/* 0.25f;*/
						offsetHeight = -2.5f;
						obj.position.set(pixelX, baseHeight * obj.dimension.y
						* heightIncreaseFactor + offsetHeight);
						dirts.add((Dirt)obj);
					}
					else
					{
						dirts.get(dirts.size - 1).increaseLength(1);
					}
				}
				// player spawn point
				else if(BLOCK_TYPE.PLAYER_SPAWNPOINT.sameColor(currentPixel))
				{
					//code added in assignment 6
					obj = new Pusheen();
					offsetHeight = -3.0f;
					obj.position.set(pixelX, baseHeight * obj.dimension.y + offsetHeight);
					pusheen = (Pusheen)obj;
					//end of code added in assignment 6
				}
				// superCOokie
				else if(BLOCK_TYPE.ITEM_SUPERCOOKIE.sameColor(currentPixel))
				{
					//code added in assignment 6
					obj = new SuperCookie();
					offsetHeight = -1.5f;
					obj.position.set(pixelX, baseHeight * obj.dimension.y + offsetHeight);
					superCookies.add((SuperCookie)obj);
					//end of code added in assignment 6
				}
				// genericPowerup
				else if(BLOCK_TYPE.ITEM_GENERICPOWERUP.sameColor(currentPixel))
				{
					//code added in assignment 6
					obj = new GenericPowerup();
					offsetHeight = -1.5f;
					obj.position.set(pixelX, baseHeight * obj.dimension.y + offsetHeight);
					genericPowerups.add((GenericPowerup)obj);
					//end of code added in chapter 6
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
		lavaOverlay = new LavaOverlay(pixmap.getWidth());
		lavaOverlay.position.set(0, -3.75f);
		
		// free memory
		pixmap.dispose();
		Gdx.app.debug(TAG, "level '" + filename + "' loaded");
	}
	
	public void render(SpriteBatch batch)
	{
		// Draw Mountains
		mountains.render(batch);
		
		// Draw dirt
		for(Dirt dirt : dirts)
			dirt.render(batch);
		
		// code added in assignment 6 for actor rendering
		//Draw generic powerups
		for(GenericPowerup genericPowerup : genericPowerups)
			genericPowerup.render(batch);
		// Draw superCookies
		for (SuperCookie superCookie: superCookies)
			superCookie.render(batch);
		// Draw Player Character
		pusheen.render(batch);
		//end of code added in assignment 6
		
		// Draw lava Overlay
		lavaOverlay.render(batch);
		
		// Draw Clouds
		clouds.render(batch);
	}
	
	//updates elements in level, from assignment 6
	public void update(float deltaTime)
	{
		pusheen.update(deltaTime);
		for(Dirt dirt : dirts)
			dirt.update(deltaTime);
		for(GenericPowerup genericPowerup : genericPowerups)
			genericPowerup.update(deltaTime);
		for(SuperCookie superCookie : superCookies)
			superCookie.update(deltaTime);
		clouds.update(deltaTime);
	}
}