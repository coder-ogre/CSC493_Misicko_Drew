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
import com.misicko.gdx.game1.CharacterSkin;
import com.misicko.gdx.game1.GamePreferences;

//menu screen with different options, and has a picture for background
public class MenuScreen extends AbstractGameScreen
{
	private static final String TAG = MenuScreen.class.getName();
	
	private Stage stage;
	private Skin skinCanyonBunny;
	private Skin skinLibgdx;
	
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
		skinLibgdx = new Skin
			(Gdx.files.internal(Constants.SKIN_LIBGDX_UI),
				new TextureAtlas(Constants.TEXTURE_ATLAS_LIBGDX_UI));
		
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
	
	// this method allows the Options Window to be opened. The settings are
	// loaded before the Options window is shown so that the widgets will always
	// be correctly initialized.
	private void onOptionsClicked()
	{
		loadSettings();
		btnMenuPlay.setVisible(false);
		btnMenuOptions.setVisible(false);
		winOptions.setVisible(true);
		
	}
	
	// this method builds a table containing the audio settings. first, a label showing
	// the text Audio in an orange color is added. Then, a checkbox (another label showing
	// the text Sound) and a slider are added in the next row for the sound settings. This
	// is also done for th emusic settings in the same way
	private Table buildOptWinAudioSettings()
	{
		Table tbl = new Table();
		// + Title: "Audio"
		tbl.pad(10, 10, 0, 10);
		tbl.add(new Label("Audio", skinLibgdx, "default-font",
			Color.ORANGE)).colspan(3);
		tbl.row();
		tbl.columnDefaults(0).padRight(10);
		tbl.columnDefaults(1).padRight(10);
		// + Checkbox, "Sound" label, sound volume slider
		chkSound = new CheckBox("", skinLibgdx);
		tbl.add(chkSound);
		tbl.add(new Label("Sound", skinLibgdx));
		sldSound = new Slider(0.0f, 1.0f, 0.1f, false, skinLibgdx);
		tbl.add(sldSound);
		tbl.row();
		// + Checkbox, "Music" label, music volume slider
		chkMusic = new CheckBox("", skinLibgdx);
		tbl.add(chkMusic);
		tbl.add(new Label("Music", skinLibgdx));
		sldMusic = new Slider(0.0f, 1.0f, 0.1f, false, skinLibgdx);
		tbl.add(sldMusic);
		tbl.row();
		return tbl;
	}
	
	// this method builds a table that contains the character skin selection via
	// a drop-down box and a preview image next to it. A ChangeListener method is 
	// added to the drop-down widget selCharSkin so that the setting and preview
	// image is updated by calling onCharSkinSelected() whenever a new selection occurs.
	private Table buildOptWinSkinSelection()
	{
		Table tbl = new Table();
		// + Title: "Character Skin"
		tbl.pad(10, 10, 0, 10);
		tbl.add(new Label("Character Skin", skinLibgdx,
			"default-font", Color.ORANGE)).colspan(2);
		tbl.row();
		// + Drop down box filled with skin items
		selCharSkin = new SelectBox<CharacterSkin>(skinLibgdx);
		
		selCharSkin.setItems(CharacterSkin.values());
		
		selCharSkin.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onCharSkinSelected(((SelectBox<CharacterSkin>)
					actor).getSelectedIndex());
			}
		});
		tbl.add(selCharSkin).width(120).padRight(20);
		// + Skin preview image
		imgCharSkin = new Image(Assets.instance.bunny.head);
		tbl.add(imgCharSkin).width(50).height(50);
		return tbl;
	}
	
	// this method builds  table that contains the debug settings. At
	// the moment, we only have one checkbox here that allows the player
	// to toggle and checks whether the FPS Counter is shown or not.
	private Table buildOptWinDebug()
	{
		Table tbl = new Table();
		// + Title: "Debug"
		tbl.pad(10, 10, 0, 10);
		tbl.add(new Label("Debug", skinLibgdx, "default-font",
			Color.RED)).colspan(3);
		tbl.row();
		tbl.columnDefaults(0).padRight(10);
		tbl.columnDefaults(1).padRight(10);
		// + Checkbox, "Show FPS Counter" label
		chkShowFpsCounter = new CheckBox("", skinLibgdx);
		tbl.add(new Label("Show FPS Counter", skinLibgdx));
		tbl.add(chkShowFpsCounter);
		tbl.row();
		return tbl;
	}
	
	// This method builds a table that contains a separator, and the Save and Cancel
	// buttons at the bottom of the Options Window. The Save and Cancel buttons use
	// ChangeListener, which will call onSaveClicked() and onCancelClicked() methods,
	// respectively, whenever a click is detected.
	private Table buildOptWinButtons()
	{
		Table tbl = new Table();
		
		// + Separator
		Label lbl = null;
		lbl = new Label("", skinLibgdx);
		lbl.setColor(0.75f, 0.75f, 0.75f, 1);
		lbl.setStyle(new LabelStyle(lbl.getStyle()));
		lbl.getStyle().background = skinLibgdx.newDrawable("white");
		tbl.add(lbl).colspan(2).height(1).width(220).pad(0, 0, 0, 1);
		tbl.row();
		lbl = new Label("", skinLibgdx);
		lbl.setColor(0.5f, 0.5f, 0.5f, 1);
		lbl.setStyle(new LabelStyle(lbl.getStyle()));
		lbl.getStyle().background = skinLibgdx.newDrawable("white");
		tbl.add(lbl).colspan(2).height(1).width(220).pad(0, 1, 5, 0);
		tbl.row();
		
		// + Save Button with event handler
		btnWinOptSave = new TextButton("Save", skinLibgdx);
		tbl.add(btnWinOptSave).padRight(30);
		btnWinOptSave.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onSaveClicked();
			}
		});
		
		// + Cancel Button with event handler
		btnWinOptCancel = new TextButton("Cancel", skinLibgdx);
		tbl.add(btnWinOptCancel);
		btnWinOptCancel.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onCancelClicked();
			}
		});
		
		return tbl;
	}
	
	// used to translate back and forth between the values stored in the widgets and the instance
	// of the GamePreferences class.
	private void loadSettings()
	{
		GamePreferences prefs = GamePreferences.instance;
		prefs.load();
		chkSound.setChecked(prefs.sound);
		sldSound.setValue(prefs.volSound);
		chkMusic.setChecked(prefs.music);
		sldMusic.setValue(prefs.volMusic);
		selCharSkin.setSelectedIndex(prefs.charSkin);
		onCharSkinSelected(prefs.charSkin);
		chkShowFpsCounter.setChecked(prefs.showFpsCounter);
	}
	
	// used to translate back and forth between the values stored in the widgets and the instance
	// of the GamePreferences class.
	private void saveSettings()
	{
		GamePreferences prefs = GamePreferences.instance;
		prefs.sound = chkSound.isChecked();
		prefs.volSound = sldSound.getValue();
		prefs.music = chkMusic.isChecked();
		prefs.volMusic = sldMusic.getValue();
		prefs.charSkin = selCharSkin.getSelectedIndex();
		prefs.showFpsCounter = chkShowFpsCounter.isChecked();
		prefs.save();
	}
	
	// contains code that we want to be exected at certain events. in particular,
	// it updates the preivew image.
	private void onCharSkinSelected(int index)
	{
		CharacterSkin skin = CharacterSkin.values()[index];
		imgCharSkin.setColor(skin.getColor());
	}
	
	// contains code that we want to be exected at certain events. in particular,
	// it saves the current settings of the Options window and swaps the Options
	// window for the menu controls.
	private void onSaveClicked()
	{
		saveSettings();
		onCancelClicked();
	}
	
	// contains code that we want to be expected at certain events. in particular,
	// it swaps the widgets, which also means that any changed settings will be discarded.
	// The visibility of the menu controls and the Options window is simply toggled by calling
	// setVisible() on the respective widgets.
	private void onCancelClicked()
	{
		btnMenuPlay.setVisible(true);
		btnMenuOptions.setVisible(true);
		winOptions.setVisible(false);
	}
	
	// This method contains the code that initializes the Options window. It builds each
	// part of the menu using the build methods that we just implemented before this one.
	// The Options window is set to an opacity value of 80 percent. This makes the window
	// appear slightly transparent, which adds a nice visual detail to it. The call of the
	// pack() method of the Window widget makes sure that TableLayout recalculates the
	// widget sizes and positions them so that all added widgets will correctly fit into the
	// window. After this, the window is moved to the bottom-right corner of the screen.
	private Table buildOptionsWindowLayer()
	{
		//Table layer = new Table();
		//return layer;
		winOptions = new Window("Options", skinLibgdx);
		// + Audio settings: Sound/Music CheckBox and Volume Slider
		winOptions.add(buildOptWinAudioSettings()).row();
		// + Character Skin: Selection Box ( White, Gray, Brown)
		winOptions.add(buildOptWinSkinSelection()).row();
		// + Debug: Show FPS Counter
		winOptions.add(buildOptWinDebug()).row();
		// + Separator and Buttons (Save, Cancel)
		winOptions.add(buildOptWinButtons()).pad(10, 0, 10, 0);
		
		// Make options window slightly transparent
		winOptions.setColor(1, 1, 1, 0.8f);
		// Hide options window by default
		winOptions.setVisible(false);
		if(debugEnabled) winOptions.debug();
		// Let TableLayout recalculate widget sizes and positions
		winOptions.pack();
		// Move options window to bottom right corner
		winOptions.setPosition
			(Constants.VIEWPORT_GUI_WIDTH - winOptions.getWidth() - 50,
				50);
		return winOptions;
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
		skinLibgdx.dispose();
	}
	
	@Override public void pause()
	{
		
	}
}
