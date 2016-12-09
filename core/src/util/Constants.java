/*
 * Drew Misicko
 */


package util;

public class Constants {
	// Visible game world is 5 meters wide
	public static final float VIEWPORT_WIDTH = 5.0f;
	
	// Visible game world is 5 meters tall
	public static final float VIEWPORT_HEIGHT = 5.0f;
	
	// GUI Width
	public static final float VIEWPORT_GUI_WIDTH = 800.0f;
	
	// GUI Height
	public static final float VIEWPORT_GUI_HEIGHT = 480.0f;
	
	// Location of description file for texture atlas
	public static final String TEXTURE_ATLAS_OBJECTS = "images/mygame.pack.atlas";
	
	// Location of image file for level 01
	public static final String LEVEL_01 = "levels/level-01.png";
	
	// Location of image file for level 02
	public static final String LEVEL_02 = "levels/level-02.png";
	
	// Amount of extra lives at level start
	public static final int LIVES_START = 3;
	
	//Duration of feather power-up in seconds
	public static final float ITEM_SUPERCOOKIE_DURATION = 9;
	
	// Delay after game over
	public static final float TIME_DELAY_GAME_OVER = 3;
	
	// Menu UI stuff
	public static final String TEXTURE_ATLAS_UI = 
		"images-ui/canyonbunny-ui.pack.atlas";
	public static final String TEXTURE_ATLAS_LIBGDX_UI =
		"images-ui/uiskin.atlas";
	// Location of description file for skins
	public static final String SKIN_LIBGDX_UI =
		"images-ui/uiskin.json";
	public static final String SKIN_SCOOTERPUSHEEN_UI =
		"images-ui/canyonbunny-ui.json";
	//File location for preferences
	 public static final String PREFERENCES = "canyonbunny.prefs";
	 
	 // number of confetti to spawn
	 public static final int CONFETTI_SPAWN_MAX = 50;
	 
	 // Spawn radius for confetti
	 public static final float CARROTS_SPAWN_RADIUS = 3.5f;
	 
	 // Delay after game finished
	 public static final float TIME_DELAY_GAME_FINISHED = 6;
	 
	 public static final String HIGHSCORE_LIST = "highScoreList.prefs";
}