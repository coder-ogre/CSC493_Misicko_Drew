/*
 * Drew Misicko
 */

package com.misicko.gdx.game1;

import util.Constants;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;

// code added in chapter 10 for sounds and music
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;


public class Assets implements Disposable, AssetErrorListener 
{
	public static final String TAG = Assets.class.getName();
	
	public static final Assets instance = new Assets();
	
	private AssetManager assetManager;
	
	public AssetPusheen pusheen;
	public AssetDirt dirt;
	public AssetGenericPowerup genericPowerup;
	public AssetSuperCookie superCookie;
	public AssetInstaDeathCookie instaDeathCookie;
	public AssetLevelDecoration levelDecoration;
	
	public AssetSounds sounds;
	public AssetMusic music;
	
	// code added in chapter 10 to provide assets for sounds
	public class AssetSounds
	{
		public final Sound jump;
		public final Sound jumpWithSuperCookie;
		public final Sound pickupGenericPowerup;
		public final Sound pickupSuperCookie;
		public final Sound liveLost;
		public AssetSounds(AssetManager am)
		{
			jump = am.get("sounds/myjump.wav", Sound.class);
			jumpWithSuperCookie = am.get("sounds/myjump_with_superCookie.wav",
				Sound.class);
			pickupGenericPowerup = am.get("sounds/mypickup_genericPowerup.wav", Sound.class);
			pickupSuperCookie = am.get("sounds/mypickup_superCookie.wav", Sound.class);
			liveLost = am.get("sounds/mylive_lost.wav", Sound.class);
		}
	}
	
	// code added in chapter 10 to provide assets for music
	public class AssetMusic
	{
		public final Music song01;
		
		public AssetMusic(AssetManager am)
		{
			song01 = am.get("music/keith303_-_brand_new_highscore.mp3",
				Music.class);
		}
	}
	
	// singleton: prevent instantiation from other classes
	private Assets () 
	{
		
	}
	
	public AssetFonts fonts;
	public class AssetFonts
	{
		public final BitmapFont defaultSmall;
		public final BitmapFont defaultNormal;
		public final BitmapFont defaultBig;
		
		public AssetFonts()
		{
			// create three fonts using Libgdx's 15px bitmap font
			defaultSmall = new BitmapFont(
				Gdx.files.internal("images/arial-15.fnt"), true);
			defaultNormal = new BitmapFont(
				Gdx.files.internal("images/arial-15.fnt"), true);
			defaultBig = new BitmapFont(
					Gdx.files.internal("images/arial-15.fnt"), true);
			// set font sizes
			defaultSmall.getData().setScale(0.75f);
			defaultNormal.getData().setScale(1.0f);
			defaultBig.getData().setScale(2.0f);
			// enable linear texture filtering for smooth fonts
			defaultSmall.getRegion().getTexture().setFilter(
				TextureFilter.Linear, TextureFilter.Linear);
			defaultNormal.getRegion().getTexture().setFilter(
					TextureFilter.Linear, TextureFilter.Linear);
			defaultBig.getRegion().getTexture().setFilter(
					TextureFilter.Linear, TextureFilter.Linear);
		}
	}
	
	// this method initiates and loads the various assets
	public void init (AssetManager assetManager) 
	{
		this.assetManager = assetManager;
		// set asset manager error handler
		assetManager.setErrorListener(this);
		// load texture atlas
		assetManager.load(Constants.TEXTURE_ATLAS_OBJECTS, TextureAtlas.class);
		// load sounds
		assetManager.load("sounds/myjump.wav", Sound.class);
		assetManager.load("sounds/myjump_with_superCookie.wav", Sound.class);
		assetManager.load("sounds/mypickup_genericPowerup.wav", Sound.class);
		assetManager.load("sounds/mypickup_superCookie.wav", Sound.class);
		assetManager.load("sounds/mylive_lost.wav", Sound.class);
		// load music
		assetManager.load("music/keith303_-_brand_new_highscore.mp3",
			Music.class);
		// start loading assets and wait until finished
		assetManager.finishLoading();
		Gdx.app.debug(TAG, "# of assets loaded: " + assetManager.getAssetNames().size);
		for (String a : assetManager.getAssetNames())
			Gdx.app.debug(TAG, "asset: " + a);
	
		TextureAtlas atlas = 
				assetManager.get(Constants.TEXTURE_ATLAS_OBJECTS);
		
		// enable texture filtering for pixel smoothing
		for (Texture t : atlas.getTextures())
			t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			
		// create game resource objects
		fonts = new AssetFonts();
		pusheen = new AssetPusheen(atlas);
		dirt = new AssetDirt(atlas);
		genericPowerup = new AssetGenericPowerup(atlas);
		superCookie = new AssetSuperCookie(atlas);
		instaDeathCookie = new AssetInstaDeathCookie(atlas);
		levelDecoration = new AssetLevelDecoration(atlas);
		// code added in chapter 10 to instantiate sounds and music
		sounds = new AssetSounds(assetManager);
		music = new AssetMusic(assetManager);
	}
	
	@Override
	public void dispose () {
		assetManager.dispose();
		
		fonts.defaultSmall.dispose();
		fonts.defaultNormal.dispose();
		fonts.defaultBig.dispose();
	}
	
	//for some reason I again had to remove the override for this method to work.
	//@Override
	public void error (String filename, Class type,
			Throwable throwable) {
		Gdx.app.error(TAG, "Couldn't load asset '"
				+ filename + "'", (Exception) throwable);
	}

	@Override
	public void error(AssetDescriptor asset, Throwable throwable) {
		// TODO Auto-generated method stub
		Gdx.app.error(TAG, "Couldn't load asset '" +
				asset.fileName + "'", (Exception) throwable);
	}
	
	// defines the pusheen with respect to the atlas
	public class AssetPusheen {
		public final AtlasRegion pusheenRegion;
		
		// constructor. finds region of atlas for the image
		public AssetPusheen (TextureAtlas atlas) {
			pusheenRegion = atlas.findRegion("scooterPusheen");
		}
	}
	
	public class AssetDirt {
		public final AtlasRegion dirtRegion;
		
		public AssetDirt (TextureAtlas atlas) {
			dirtRegion = atlas.findRegion("dirt");
		}
	}
	
	public class AssetGenericPowerup {
		public final AtlasRegion genericPowerupRegion;
		
		public AssetGenericPowerup (TextureAtlas atlas) {
			genericPowerupRegion = atlas.findRegion("genericPowerup");
		}
	}
	
	
	
	public class AssetSuperCookie {
		public final AtlasRegion superCookieRegion;
		
		public AssetSuperCookie (TextureAtlas atlas) {
			superCookieRegion = atlas.findRegion("superCookie");
		}
	}
	
	public class AssetInstaDeathCookie {
		public final AtlasRegion instaDeathCookieRegion;
		
		public AssetInstaDeathCookie (TextureAtlas atlas) {
			instaDeathCookieRegion = atlas.findRegion("instaDeathCookie");
		}
	}
	
	// these images do not affect gameplay.  they only change the way the world looks
	public class AssetLevelDecoration {
		public final AtlasRegion cloud01;
		public final AtlasRegion cloud02;
		public final AtlasRegion cloud03;
		public final AtlasRegion mountainLeft;
		public final AtlasRegion mountainRight;
		public final AtlasRegion lavaOverlay;
		
		public AssetLevelDecoration (TextureAtlas atlas) {
			cloud01 = atlas.findRegion("cloud01");
			cloud02 = atlas.findRegion("cloud02");
			cloud03 = atlas.findRegion("cloud03");
			mountainLeft = atlas.findRegion("mountain_left");
			mountainRight =	 atlas.findRegion("mountain_right");
			lavaOverlay = atlas.findRegion("lavaOverlay");
		}
	}
}
