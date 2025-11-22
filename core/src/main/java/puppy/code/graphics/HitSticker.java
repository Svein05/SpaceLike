package puppy.code.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class HitSticker {
    
    private float timer;
    private float rotation;
    private static final float DURATION = 0.5f;
    
    public HitSticker() {
        this.timer = 0f;
        this.rotation = (float)(Math.random() * 360);
    }
    
    public void update(float delta) {
        timer += delta;
    }
    
    public boolean isExpired() {
        return timer >= DURATION;
    }
    
    public void render(SpriteBatch batch, Texture hitTexture, float x, float y, float width, float height) {
        batch.draw(hitTexture,
            x, y,
            width / 2f, height / 2f,
            width, height,
            1f, 1f,
            rotation,
            0, 0,
            hitTexture.getWidth(), hitTexture.getHeight(),
            false, false);
    }
}
