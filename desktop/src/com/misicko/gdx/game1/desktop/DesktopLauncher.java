package com.misicko.gdx.game1.desktop;
/*
 * Drew Misicko
 */

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;
import com.misicko.gdx.game1.MisickoGdxGame1;
//test changes
public class DesktopLauncher {
	private static boolean rebuildAtlas = false;
	private static boolean drawDebugOutline = false;
	public static void main (String[] arg) {
		if(rebuildAtlas)
		{
			Settings settings = new Settings();
			settings.maxWidth = 1024;
			settings.maxHeight = 1024;
			//settings.duplicatePadding = false;
			settings.debug = drawDebugOutline;
			TexturePacker.process(settings, "assetsraw/images", "../core/assets/images", "mygame.pack");
			TexturePacker.process(settings, "assetsraw/images-ui",
				".CanyonBunny-android/assets/images-ui",
				"canyonbunny-ui.pack");
				
		}
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "myGame";
		config.width = 800;
		config.height = 480;
		new LwjglApplication(new MisickoGdxGame1(), config);
	}
}
