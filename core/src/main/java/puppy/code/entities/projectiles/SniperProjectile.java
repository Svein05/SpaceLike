package puppy.code.entities.projectiles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class SniperProjectile extends Projectile {
    private Vector2 direction;
    private float speed;
    private ShapeRenderer shapeRenderer;
    private static final float PROJECTILE_RADIUS = 8f;

    public SniperProjectile(float x, float y, Vector2 direction, float speed) {
        super(x, y, PROJECTILE_RADIUS * 2, PROJECTILE_RADIUS * 2, 
              direction.x * speed, direction.y * speed, 1);
        this.direction = direction.nor();
        this.speed = speed;
        this.shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void update(float delta) {
        x += direction.x * speed * delta;
        y += direction.y * speed * delta;
        
        if (x < -PROJECTILE_RADIUS || x > 1920 + PROJECTILE_RADIUS || 
            y < -PROJECTILE_RADIUS || y > 1080 + PROJECTILE_RADIUS) {
            destroyed = true;
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.end();
        
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(1f, 0f, 0f, 1f);
        shapeRenderer.circle(x + PROJECTILE_RADIUS, y + PROJECTILE_RADIUS, PROJECTILE_RADIUS);
        shapeRenderer.end();
        
        batch.begin();
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, PROJECTILE_RADIUS * 2, PROJECTILE_RADIUS * 2);
    }
    
    public void dispose() {
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
    }
}
