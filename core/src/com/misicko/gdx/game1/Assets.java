/*
 * Drew Misicko
 */

package com.misicko.gdx.game1;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Disposable;
import com.misicko.gdx.game1.Constants;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;

//this is the main class that deals with assets
public class Assets implements Disposable, AssetErrorListener {
	public static final String TAG = Assets.class.getName();
	
	public static final Assets instance = new Assets();
	
	private AssetManager assetManager;
	
	public AssetPusheen pusheen;
	//public AssetDirt dirt;
	public AssetGenericPowerup genericPowerup;
	public AssetFlyCookie flyCookie;
	public AssetInstaDeathCookie instaDeathCookie;
	public AssetLevelDecoration levelDecoration;
	
	// singleton: preent instantiation from other classes
	private Assets () {}
	
	public void init (AssetManager assetManager) {
		this.assetManager = assetManager;
		// set asset manager error handler
		assetManager.setErrorListener(this);
		// load texture atlas
		assetManager.load(Constants.TEXTURE_ATLAS_OBJECTS, TextureAtlas.class);
		// start loading assets and wait until finished
		assetManager.finishLoading();
		Gdx.app.debug(TAG, "# of assets loaded: " + assetManager.getAssetNames().size);
		for (String a : assetManager.getAssetNames())
			Gdx.app.debug(TAG, "asset: " + a);
	
		TextureAtlas atlas = 
				assetManager.get(Constants.TEXTURE_ATLAS_OBJECTS);
		
		// enable texture filtering for pixel smoothing
		for (Texture t : atlas.getTextures()) {
			t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
	}
	
	
	// create game resource objects
	pusheen = new AssetPusheen(atlas);
	//dirt = new AssetDirt(atlas);
	genericPowerup = new AssetGenericPowerup(atlas);
	flyCookie = new AssetFlyCookie(atlas);
	instaDeathCookie = new AssetInstaDeathCookie(atlas);
	levelDecoration = new AssetLevelDecoration(atlas);
	}
	
	@Override
	public void dispose () {
		assetManager.dispose();
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
	
	public class AssetPusheen {
		public final AtlasRegion pusheenRegion;
		public AssetPusheen (TextureAtlas atlas) {
			pusheenRegion = atlas.findRegion("scooterPusheen");
		}
	}
	
	/*public class AssetDirt {
		public final AtlasRegion dirtRegion;
		
		public AssetDirt (TextureAtlas atlas) {
			dirtRegion = atlas.findRegion("dirt");
		}
	}*/
	
	public class AssetGenericPowerup {
		public final AtlasRegion genericPowerupRegion;
		
		public AssetGenericPowerup (TextureAtlas atlas) {
			genericPowerupRegion = atlas.findRegion("genericPowerup");
		}
	}
	
	public class AssetFlyCookie {
		public final AtlasRegion flyCookieRegion;
		
		public AssetFlyCookie (TextureAtlas atlas) {
			flyCookieRegion = atlas.findRegion("fly_cookie");
		}
	}
	
	public class AssetInstaDeathCookie {
		public final AtlasRegion instaDeathCookieRegion;
		
		public AssetInstaDeathCookie (TextureAtlas atlas) {
			instaDeathCookieRegion = atlas.findRegion("instaDeathCookie");
		}
	}
	
	public class AssetLevelDecoration {
		public final AtlasRegion cloud01;
		public final AtlasRegion cloud02;
		public final AtlasRegion cloud03;
		public final AtlasRegion mountainLeft;
		public final AtlasRegion mountainRight;
		public final AtlasRegion waterOverlay;
		
		public AssetLevelDecoration (TextureAtlas atlas) {
			cloud01 = atlas.findRegion("cloud01");
			cloud02 = atlas.findRegion("cloud02");
			cloud03 = atlas.findRegion("cloud03");
			mountainLeft = atlas.findRegion("mountain_left");
			mountainRight =	 atlas.findRegion("mountain_right");
			waterOverlay = atlas.findRegion("water_overlay");
		}
	}
	
}
