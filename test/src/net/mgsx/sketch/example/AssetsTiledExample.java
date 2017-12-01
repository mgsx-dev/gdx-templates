package net.mgsx.sketch.example;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureAdapter;
import com.badlogic.gdx.maps.tiled.AtlasTmxMapLoader;
import com.badlogic.gdx.maps.tiled.AtlasTmxMapLoader.AtlasTiledMapLoaderParameters;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TmxMapLoader.Parameters;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class AssetsTiledExample extends ApplicationAdapter 
{
	public static final boolean useOriginal = false;
	public static final boolean filtering = true;
	public static final String mapName = "example2.tmx";
	
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 640;
		config.height = 480;
		new LwjglApplication(new AssetsTiledExample(), config);
	}
	
	private TiledMapRenderer mapRenderer;
	private TiledMap map;
	private OrthographicCamera camera;
	private Viewport viewport;

	public AssetsTiledExample() {
		super();
	}
	
	@Override
	public void create () {
		camera = new OrthographicCamera();
		
		if(useOriginal){
			Parameters params = new Parameters();
			params.textureMinFilter = filtering ? TextureFilter.Linear : TextureFilter.Nearest;
			params.textureMagFilter = filtering ? TextureFilter.Linear : TextureFilter.Nearest;
			map = new TmxMapLoader().load("assets-src/maps/" + mapName, params);
		}else{
			AtlasTiledMapLoaderParameters params = new AtlasTiledMapLoaderParameters();
			params.textureMinFilter = filtering ? TextureFilter.Linear : TextureFilter.Nearest;
			params.textureMagFilter = filtering ? TextureFilter.Linear : TextureFilter.Nearest;
			params.forceTextureFilters = true;
			map = new AtlasTmxMapLoader().load("assets/maps/" + mapName, params );
		}
		
		// XXX debug
		
		for(TiledMapTileSet ts : map.getTileSets()){
			Gdx.app.log("map", "Tileset : " + ts.getName() + " " + ts.size() + " tiles");
		}
		
		// XXX debug
		
		
		mapRenderer = new OrthogonalTiledMapRenderer(map, 1f / 2f);
		viewport = new ScreenViewport(camera);
		
		Gdx.input.setInputProcessor(
			new GestureDetector(new GestureAdapter() {
				@Override
				public boolean pan(float x, float y, float deltaX, float deltaY) {
					if(Gdx.input.isButtonPressed(Input.Buttons.RIGHT)){
						if(deltaX > 0)
							camera.zoom /= 1 + deltaX/Gdx.graphics.getWidth();
						else
							camera.zoom *= 1 - deltaX/Gdx.graphics.getWidth();
					}else{
						camera.position.add(-deltaX * camera.zoom, deltaY * camera.zoom, 0);
					}
					return true;
				}
				@Override
				public boolean zoom(float initialDistance, float distance) {
					camera.zoom = distance / initialDistance;
					return true;
				}
			})
		);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		camera.update();
		mapRenderer.setView(camera);
		mapRenderer.render();
	}
	
	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
	}
	
	@Override
	public void dispose () {
		// TODO dispose sketch
	}
}
