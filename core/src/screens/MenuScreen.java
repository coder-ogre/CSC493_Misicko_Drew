/*
 * Drew Misicko
 */

package screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.misicko.gdx.game1.Assets;
import com.misicko.gdx.game1.Constants;

//menu screen with different options, and has a picture for background
public class MenuScreen extends AbstractGameScreen
{
	private static final String TAG = MenuScreen.class.getName();
	
	private Stage stage;
	private Skin skinCanyonBunny;
	
	// menu
	private Image imgBackground;
	private Image imgLogo;
	private Image imgInfo;
	private Image imgCoins;
	private Image imgBunny;
	private Button btnMenuPlay;
	private Button btnMenuOptions;
	
	// options
	private Window winOptions;
	private TextButton btnWinOptSave;
	private TextButton btnWinOptCancel;
	private CheckBox chkSound;
	private Slider sldSound;
	private CheckBox chkMusic;
	private Slider sldMusic;
	private SelectBox<CharacterSkin> selCharSkin;
	private Image imgCharSkin;
	private CheckBox chkShowFpsCounter;
	
	// debug
	private final float DEBUG_REBUILD_INTERVAL = 5.0f;
	private boolean debugEnabled = false;
	private float debugRebuildStage;
	
	public MenuScreen (Game game) {
		super(game);
	}
	
	
	@Override
	public void render(float deltaTime) {
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		//if(Gdx.input.isTouched())
		//	game.setScreen(new GameScreen(game));
		
		// updates and renders stage
		if(debugEnabled)
		{
			debugRebuildStage -= deltaTime;
			if(debugRebuildStage <= 0)
			{
				debugRebuildStage = DEBUG_REBUILD_INTERVAL;
				rebuildStage();
			}
			stage.act(deltaTime);
			stage.draw();
			stage.setDebugAll(true); // may show debug lines.. may have to be disabled in future
		}
	}
	
	private void rebuildStage()
	{
		skinCanyonBunny = new Skin(
			Gdx.files.internal(Constants.SKIN_CANYONBUNNY_UI),
			new TextureAtlas(Constants.TEXTURE_ATLAS_UI));
		
		// build all layers
		Table layerBackground = buildBackgroundLayer();
		Table layerObjects = buildObjectsLayer();
		Table layerLogos = buildLogosLayer();
		Table layerControls = buildControlsLayer();
		Table layerOptionsWindow = buildOptionsWindowLayer();
		
		// assemble stage for menu screen
		stage.clear();
		Stack stack = new Stack();
		stage.addActor(stack);
		stack.setSize(Constants.VIEWPORT_GUI_WIDTH,
			Constants.VIEWPORT_GUI_HEIGHT);
		stack.add(layerBackground);
		stack.add(layerObjects);
		stack.add(layerLogos);
		stack.add(layerControls);
		stage.addActor(layerOptionsWindow);
	}
	
	//draws backgorund image for menu.  the image is referenced using the background name that we defined
	// in the skin file canyonbunny-ui.json.  if you change the size of the screen, the stage will adjust
	// accordingly along with the background layer and its Image widget
	private Table buildBackgroundLayer()
	{
		Table layer = new Table();
		// + Background
		imgBackground = new Image(skinCanyonBunny, "background");
		layer.add(imgBackground);
		return layer;
	}
	
	// makes image of some coins and a huge bunny head, which are both drawn on
	// top of the background layer.  the positions of each actor are explicitly
	// set to certain coordinates by calling setPosition() on each actor.
	private Table buildObjectsLayer()
	{
		Table layer = new Table();
		// + Coins
		imgCoins = new Image(skinCanyonBunny, "coins");
		layer.addActor(imgCoins);
		imgCoins.setPosition(135, 80);
		// + Bunny
		imgBunny = new Image(skinCanyonBunny, "bunny");
		layer.addActor(imgBunny);
		imgBunny.setPosition(355, 40);
		return layer;
	}
	
	// the logos layer is anchored in the top-left corner of the screen.  after this,
	// an image logo is added to the table followed by a call of the row() and expandY()
	// methods.  Every time you call add() on a Table widget, TableLayout will add a new
	// column, which means the widget grows in a horizontal direction. So, if you want to
	// start a new row, you can tell TableLayout about this by calling row().  The expandY()
	// method expands the empty space in a vertical direction.  The expansion is done by
	// shifting the widgets to the bound of the cell.  After this, more image information
	// is added to the table, which is literally pushed down to the bottom edge due to
	// the cal of expandY().  Lastly, there is a call to layer.debug(), which is the way
	// to tell TableLayout the object it should draw debug visuals for.
	private Table buildLogosLayer()
	{
		Table layer = new Table();
		layer.left().top();
		// + Game Logo
		imgLogo = new Image(skinCanyonBunny, "logo");
		layer.add(imgLogo);
		layer.row().expandY();
		// + Info Logos
		imgInfo = new Image(skinCanyonBunny, "info");
		layer.add(imgInfo).bottom();
		if(debugEnabled) layer.debug();
		return layer;
	}
	
	// the controls layer is anchored in the bottom-right corner of the screen.  A new button
	// widget is added using the Play style. Next, a new ChangeListener is added to this
	// button to define the action to be executed when the button is clicked on. After this,
	// a new row is stared in which the second button widget is added using the Options style.
	// Each event handler calls a separate method to make it easier for us to maintain the code
	// of the layer and the code to handle events.
	private Table buildControlsLayer()
	{
		Table layer = new Table();
		layer.right().bottom();
		// + Play Button
		btnMenuPlay = new Button(skinCanyonBunny, "play");
		layer.add(btnMenuPlay);
		btnMenuPlay.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onPlayClicked();
			}
		});
		// + Options Button
		btnMenuOptions = new Button(skinCanyonBunny, "options");
		layer.add(btnMenuOptions);
		btnMenuOptions.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onOptionsClicked();
			}
		});
		if(debugEnabled) layer.debug();
		return layer;
	}
	
	// The onPlayClicked() method will switch to the game screen
	private void onPlayClicked() 
	{
		game.setScreen(new GameScreen(game));
	}
	
	// the onOptionsClicked() method is intentionally left empty for the moment
	private void onOptionsClicked()
	{
		
	}
	
	private Table buildOptionsWindowLayer()
	{
		Table layer = new Table();
		return layer;
	}
	
	// sets the viewport size of the stage
	@Override public void resize(int width, int height) 
	{
		stage.getViewport().update(width, height, true);
	}
	
	// called when teh screen is shown.  initializes stage, sets it as LibGDX's current input processor
	// so that the stage will receive all future inputs, and finally, the stage is rebuilt.
	@Override public void show() 
	{
		stage = new Stage(new StretchViewport(Constants.VIEWPORT_GUI_WIDTH,
			Constants.VIEWPORT_GUI_HEIGHT));
		Gdx.input.setInputProcessor(stage);
		rebuildStage();
	}
	
	// frees the allocated resouces when the screen is hidden.
	@Override public void hide() 
	{
		stage.dispose();
		skinCanyonBunny.dispose();
	}
	
	@Override public void pause()
	{
		
	}
}
