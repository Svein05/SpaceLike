package puppy.code.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import puppy.code.managers.ResourceManager;

public class TutorialSystem {
    private TextureAtlas keyboardAtlas;
    private float alpha = 0.5f;
    private boolean fadingOut = false;
    
    private static final float KEY_SIZE = 56f;
    private static final float KEY_SPACING = 10f;
    private static final float FADE_SPEED = 2.0f;
    
    public TutorialSystem() {
        ResourceManager rm = ResourceManager.getInstance();
        keyboardAtlas = rm.getAtlas("UI/Keyboard/Keyboard.atlas");
    }
    
    public void update(float delta) {
        if (fadingOut) {
            alpha -= FADE_SPEED * delta;
            if (alpha < 0) alpha = 0;
        }
    }
    
    public void render(SpriteBatch batch) {
        if (alpha <= 0) return;
        
        float centerX = 1920 / 2f;
        float centerY = 1080 / 2f;
        
        float startX = centerX - (KEY_SIZE * 3.5f + KEY_SPACING * 2.5f);
        float startY = centerY;
        
        batch.setColor(1, 1, 1, alpha);
        
        boolean wPressed = Gdx.input.isKeyPressed(Input.Keys.W);
        boolean aPressed = Gdx.input.isKeyPressed(Input.Keys.A);
        boolean sPressed = Gdx.input.isKeyPressed(Input.Keys.S);
        boolean dPressed = Gdx.input.isKeyPressed(Input.Keys.D);
        boolean spacePressed = Gdx.input.isKeyPressed(Input.Keys.SPACE);
        boolean shiftPressed = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT);
        
        drawKey(batch, "W", wPressed, startX + KEY_SIZE + KEY_SPACING, startY + KEY_SIZE + KEY_SPACING);
        drawKey(batch, "A", aPressed, startX, startY);
        drawKey(batch, "S", sPressed, startX + KEY_SIZE + KEY_SPACING, startY);
        drawKey(batch, "D", dPressed, startX + (KEY_SIZE + KEY_SPACING) * 2, startY);
        
        float shiftX = startX + (KEY_SIZE + KEY_SPACING) * 3 + KEY_SPACING;
        float shiftY = startY + (KEY_SIZE / 2);
        drawKey(batch, "SHIFT", shiftPressed, shiftX, shiftY);
        
        float wasdWidth = (KEY_SIZE + KEY_SPACING) * 3;
        float shiftWidth = KEY_SIZE * 2;
        float totalWidth = wasdWidth + KEY_SPACING + shiftWidth;
        float spaceX = startX + (totalWidth / 2) - KEY_SIZE;
        float spaceY = startY - KEY_SIZE - KEY_SPACING * 2;
        drawKey(batch, "SPACE", spacePressed, spaceX, spaceY);
        
        batch.setColor(1, 1, 1, 1);
    }
    
    private void drawKey(SpriteBatch batch, String key, boolean pressed, float x, float y) {
        String regionName = (pressed ? "Press" : "NoPress") + key;
        TextureRegion region = keyboardAtlas.findRegion(regionName);
        
        if (region != null) {
            float width = key.equals("SPACE") || key.equals("SHIFT") ? KEY_SIZE * 2 : KEY_SIZE;
            batch.draw(region, x, y, width, KEY_SIZE);
        }
    }
    
    public void startFadeOut() {
        fadingOut = true;
    }
    
    public boolean isVisible() {
        return alpha > 0;
    }
    
    public void dispose() {
    }
}
