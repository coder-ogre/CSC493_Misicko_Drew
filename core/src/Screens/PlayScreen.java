package Screens;

import Scenes.Hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.misicko.gdx.game1.MarioBros;

public class PlayScreen implements Screen{
	private MarioBros game;
	Texture texture;
	private OrthographicCamera gameCam;
	private Viewport gamePort;
	private Hud hud;
	
	public PlayScreen(MarioBros game) {
		this.game = game;
		//texture = new Texture("../core/assets/badlogic.jpg");
		gameCam = new OrthographicCamera();
		//gamePort = new StretchViewport(800, 480, gameCam);  //this one stretches the aspect ratio
		//gamePort = new ScreenViewport(gameCam);   // this one doesn't stretch aspect ratio to fit screen, so if you stretch screen you can see more stuff
		gamePort = new FitViewport(MarioBros.V_WIDTH, MarioBros.V_HEIGHT, gameCam); //this one preserves the aspect ratio, and adds black bars to the side with extra space
		hud = new Hud(game.batch);
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		/*game.batch.setProjectionMatrix(gameCam.combined);
		game.batch.begin();
		game.batch.draw(texture, 0, 0);
		game.batch.end();*/
		game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
		hud.stage.draw();		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		gamePort.update(width, height);
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
