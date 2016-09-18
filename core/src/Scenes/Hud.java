package Scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.misicko.gdx.game1.MarioBros;

public class Hud {
	public Stage stage;
	private Viewport viewport;
	
	private Integer worldTimer;
	private float timeCount;
	private Integer score;
	private Table table;	
	
	public Hud(SpriteBatch sb) {
		worldTimer = 300;
		timeCount = 0;
		score = 0;
		
		viewport = new FitViewport(MarioBros.V_WIDTH, MarioBros.V_HEIGHT, new OrthographicCamera());
		stage = new Stage(viewport, sb);
		                                               // 3 digits long, 
		Label countdownLabel = new Label(String.format("%03d", worldTimer), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
		Label scoreLabel = new Label(String.format("%06d", score), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
		//just text label, so don't need format
		Label timeLabel = new Label("TIME", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
		//will need to modify this levelLabel to be dynamic when more than 1 levle is created
		Label levelLabel = new Label("1-1", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
		Label worldLabel = new Label("WORLD", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
		Label marioLabel = new Label("MARIO", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
		
		//table will organize position of labels
		table = new Table();
		table.top();//puts it at the top
		table.setFillParent(true);//table is the size of the stage now
		
		table.add(marioLabel).expandX().padTop(10);
		table.add(worldLabel).expandX().padTop(10);
		table.add(timeLabel).expandX().padTop(10);
		table.row();
		table.add(scoreLabel).expandX();
		table.add(levelLabel).expandX();
		//table.add(countdownLabel).expand();
		table.add(countdownLabel).expandX();
		
		stage.addActor(table);
	}
}
