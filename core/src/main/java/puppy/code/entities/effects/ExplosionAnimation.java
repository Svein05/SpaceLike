package puppy.code.entities.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class ExplosionAnimation {
    private TextureRegion[] explosionFrames;
    private float currentTime;
    private int currentFrame;
    private boolean finished;
    private float x, y;
    private float width, height;
    
    private static final float FRAME_DURATION = 0.15f; // Duracion de cada frame
    
    public ExplosionAnimation(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        // Hacer la explosion mas grande que el asteroide original
        this.width = width * 1.5f;
        this.height = height * 1.5f;
        // Centrar la explosion
        this.x = x - (this.width - width) / 2;
        this.y = y - (this.height - height) / 2;
        
        this.currentTime = 0;
        this.currentFrame = 0;
        this.finished = false;
        
        loadExplosionFrames();
    }
    
    private void loadExplosionFrames() {
        try {
            TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("Game/Enemys/Asteroids/Asteroid.atlas"));
            explosionFrames = new TextureRegion[3];
            explosionFrames[0] = atlas.findRegion("Asteroid - Exploted 1");
            explosionFrames[1] = atlas.findRegion("Asteroid - Exploted 2");
            explosionFrames[2] = atlas.findRegion("Asteroid - Exploted Final");
        } catch (Exception e) {
            System.err.println("Error cargando atlas de explosion: " + e.getMessage());
            finished = true;
        }
    }
    
    public void update(float delta) {
        if (finished || explosionFrames == null) return;
        
        currentTime += delta;
        
        if (currentTime >= FRAME_DURATION) {
            currentFrame++;
            currentTime = 0;
            
            if (currentFrame >= explosionFrames.length) {
                finished = true;
            }
        }
    }
    
    public void render(SpriteBatch batch) {
        if (finished || explosionFrames == null || currentFrame >= explosionFrames.length) return;
        
        TextureRegion currentRegion = explosionFrames[currentFrame];
        if (currentRegion != null) {
            batch.draw(currentRegion, x, y, width, height);
        }
    }
    
    public boolean isFinished() {
        return finished;
    }
    
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
    }
}