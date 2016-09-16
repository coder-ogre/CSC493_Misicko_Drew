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

public class Assets implements Disposable, AssetErrorListener {
	public static final String TAG = Assets.class.getName();
	
	public static final Assets instance = new Assets();
	
	private AssetManager assetManager;
	
	public AssetPusheen pusheen;
	public AssetRock rock;
	public AssetGenericPowerup genericPowerup;
	public AssetCloud cloud;
	public AssetLava lava;
	public AssetMountain mountain;
	
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
		rock = new AssetRock(atlas);
		genericPowerup = new AssetGenericPowerup(atlas);
		cloud = new AssetCloud(atlas);
		lava = new AssetLava(atlas);
		mountain = new AssetMountain(atlas);
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
		public final AtlasRegion head;
		public AssetPusheen (TextureAtlas atlas) {
			head = atlas.findRegion("scooter_pusheen");
		}
	}
	
	public class AssetRock {
		public final AtlasRegion rock;
		
		public AssetRock (TextureAtlas atlas) {
			rock = atlas.findRegion("rock");
		}
	}
	
	
	
	public class AssetCloud {
		public final AtlasRegion cloud;
		
		public AssetCloud (TextureAtlas atlas) {
			cloud = atlas.findRegion("cloud");
		}
	}
	
	public class AssetMountain {
		public final AtlasRegion mountain;
		
		public AssetMountain (TextureAtlas atlas) {
			mountain = atlas.findRegion("mountain");
		}
	}
	
	public class AssetLava {
		public final AtlasRegion lava;
		
		public AssetLava (TextureAtlas atlas) {
			lava = atlas.findRegion("lava");
		}
	}
	
	public class AssetGenericPowerup {
		public final AtlasRegion genericPowerup;
		
		public AssetGenericPowerup (TextureAtlas atlas) {
			genericPowerup = atlas.findRegion("generic_powerup");
		}
	}
	
	public class AssetDirt {
		public final AtlasRegion dirt;
		
		public AssetDirt (TextureAtlas atlas) {
			dirt = atlas.findRegion("dirt");
		}
	}
	
	public class AssetLevelDecoration {
		public final AtlasRegion cloud;
		public final AtlasRegion mountain;
		public final AtlasRegion lava;
		
		public AssetLevelDecoration (TextureAtlas atlas) {
			cloud = atlas.findRegion("cloud");
			mountain = atlas.findRegion("mountain");
			lava =  atlas.findRegion("lava");
		}
	}
	
}
