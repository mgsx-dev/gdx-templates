package net.mgsx.sketch.example;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class AssetsSkinExample extends ApplicationAdapter 
{
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 640;
		config.height = 480;
		new LwjglApplication(new AssetsSkinExample(), config);
	}
	
	private Stage stage;
	private Skin skin;
	
	@Override
	public void create () {
		stage = new Stage(new ScreenViewport());
		Gdx.input.setInputProcessor(stage);
		skin = new Skin(Gdx.files.internal("assets/skins/hud-skin.json"));
		Table table = new Table(skin);
		table.setFillParent(true);
		table.add("Hello LD40!").row();
		table.add(new TextButton("Button", skin)).row();
		table.add(new Image(skin.getDrawable("noise"))).row();
		table.add(new TextButton("Button", skin, "blue")).row();
		
		Table subTable = new Table(skin);
		subTable.setBackground("panel");
		table.add(subTable).row();
		
		subTable.add("This is table").fill().row();
		subTable.add("And this is content !").fill().row();
		
		subTable.add(new Image(new Texture(Gdx.files.internal("assets/bg.png")))).size(100, 100).row();
		
		stage.addActor(table);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act();
		stage.draw();
	}
	
	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height);
	}
	
}
