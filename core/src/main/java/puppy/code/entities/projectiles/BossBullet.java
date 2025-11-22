package puppy.code.entities.projectiles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import puppy.code.entities.GameObject;

public class BossBullet extends GameObject {
    
    public float velocityX;
    public float velocityY;
    private boolean destroyed;
    private static final float RADIUS = 8f;
    private static final Color BULLET_COLOR = new Color(0.6f, 0.2f, 0.8f, 1f);
    
    public BossBullet(float x, float y, float velocityX, float velocityY) {
        super(x, y, RADIUS * 2, RADIUS * 2);
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.destroyed = false;
    }
    
    @Override
    public void update(float delta) {
        x += velocityX * delta;
        y += velocityY * delta;
        
        if (x < -RADIUS * 2 || x > 1920 + RADIUS * 2 || 
            y < -RADIUS * 2 || y > 1080 + RADIUS * 2) {
            destroyed = true;
        }
    }
    
    public void render(ShapeRenderer shapeRenderer) {
        if (!destroyed) {
            shapeRenderer.setColor(BULLET_COLOR);
            shapeRenderer.circle(x + RADIUS, y + RADIUS, RADIUS);
        }
    }
    
    @Override
    public void draw(SpriteBatch batch) {
        // Renderizado con ShapeRenderer en PantallaBoss
    }
    
    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
    
    public void destroy() {
        destroyed = true;
    }
    
    public boolean isDestroyed() {
        return destroyed;
    }
    
    public float getRadius() {
        return RADIUS;
    }
}
