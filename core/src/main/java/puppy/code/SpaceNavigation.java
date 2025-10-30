package puppy.code;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import puppy.code.screens.PantallaMenu;
import puppy.code.managers.ResourceManager;
import puppy.code.managers.GameStateManager;

public class SpaceNavigation extends Game {
    private SpriteBatch batch;
    private BitmapFont font;
    private ResourceManager resourceManager;
    private GameStateManager gameStateManager;

    public void create() {
        // Inicializar managers
        resourceManager = ResourceManager.getInstance();
        gameStateManager = GameStateManager.getInstance();
        
        batch = new SpriteBatch();
        font = resourceManager.getDefaultFont();
        
        // Precargar recursos comunes
        resourceManager.preloadCommonResources();
        
        Screen ss = new PantallaMenu(this);
        this.setScreen(ss);
    }

	public void render() {
		super.render(); // important!
	}

	public void dispose() {
		batch.dispose();
		resourceManager.dispose();
	}

	public SpriteBatch getBatch() {
		return batch;
	}

	public BitmapFont getFont() {
		return font;
	}

	public int getHighScore() {
		return gameStateManager.getHighScore();
	}

	public void setHighScore(int highScore) {
		gameStateManager.setHighScore(highScore);
	}
	
	public ResourceManager getResourceManager() {
		return resourceManager;
	}
	
	

}