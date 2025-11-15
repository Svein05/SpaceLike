package puppy.code.stats;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class UpgradeOption {
    private UpgradeType type;
    private Rectangle bounds;
    private boolean hovered;
    private static final float PADDING = 20;
    private static final float BORDER_WIDTH = 3;
    
    public UpgradeOption(UpgradeType type, float x, float y, float width, float height) {
        this.type = type;
        this.bounds = new Rectangle(x, y, width, height);
        this.hovered = false;
    }
    
    public void renderBackground(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (hovered) {
            shapeRenderer.setColor(0.3f, 0.3f, 0.5f, 0.9f);
        } else {
            shapeRenderer.setColor(0.2f, 0.2f, 0.3f, 0.8f);
        }
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        shapeRenderer.end();
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        if (hovered) {
            shapeRenderer.setColor(Color.CYAN);
        } else {
            shapeRenderer.setColor(Color.WHITE);
        }
        for (int i = 0; i < BORDER_WIDTH; i++) {
            shapeRenderer.rect(bounds.x + i, bounds.y + i, 
                             bounds.width - i * 2, bounds.height - i * 2);
        }
        shapeRenderer.end();
    }
    
    public void renderText(SpriteBatch batch, BitmapFont font) {
        font.getData().setScale(2f);
        font.setColor(Color.YELLOW);
        font.draw(batch, type.getDisplayName(), 
                 bounds.x + PADDING, 
                 bounds.y + bounds.height - PADDING);
        
        font.getData().setScale(1.2f);
        font.setColor(Color.WHITE);
        font.draw(batch, type.getDescription(), 
                 bounds.x + PADDING, 
                 bounds.y + bounds.height / 2);
    }
    
    public boolean contains(float x, float y) {
        return bounds.contains(x, y);
    }
    
    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }
    
    public boolean isHovered() {
        return hovered;
    }
    
    public UpgradeType getType() {
        return type;
    }
    
    public Rectangle getBounds() {
        return bounds;
    }
}
