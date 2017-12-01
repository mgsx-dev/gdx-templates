package net.mgsx.sketch.example;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
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
	private Image animImage;
	private float time;
	private TextureRegionDrawable drawable;
	private Animation<TextureRegion> anim;
	
	@Override
	public void create () {
		stage = new Stage(new ScreenViewport());
		Gdx.input.setInputProcessor(stage);
		skin = new Skin(Gdx.files.internal("assets/skins/hud-skin.json"));
		
		Table table = new Table(skin);
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
		
		TextureAtlas animAtlas = new TextureAtlas(Gdx.files.internal("assets/animations.atlas"));
		anim = new Animation<TextureRegion>(.1f, animAtlas.findRegions("anim"));
		
		subTable.add(animImage = new Image(anim.getKeyFrame(0))).row();
		drawable = new TextureRegionDrawable();
		
		Table root = new Table();
		root.setFillParent(true);
		root.add(table).expand().center();
		stage.addActor(root);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		time += Gdx.graphics.getDeltaTime();
		drawable.setRegion(anim.getKeyFrame(time, true));
		animImage.setDrawable(drawable);
		stage.act();
		stage.draw();
	}
	
	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}
	
}
