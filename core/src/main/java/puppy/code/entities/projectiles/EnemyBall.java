package puppy.code.entities.projectiles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class EnemyBall extends Projectile {
    private static final float BALL_RADIUS = 8f;
    private ShapeRenderer shapeRenderer;
    private Color ballColor;

    public EnemyBall(float x, float y, float velocityX, float velocityY) {
        super(x, y, BALL_RADIUS * 2, BALL_RADIUS * 2, velocityX, velocityY, 1);
        this.shapeRenderer = new ShapeRenderer();
        this.ballColor = new Color(1f, 0.3f, 0.1f, 1f);
    }

    @Override
    public void update(float delta) {
        x += velocityX * delta * 60;
        y += velocityY * delta * 60;

        if (y < -height || y > 1080 + height || x < -width || x > 1920 + width) {
            destroy();
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        // TODO: Reemplazar ShapeRenderer con textura de sprite cuando este disponible
        batch.end();
        
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(ballColor);
        shapeRenderer.circle(x + BALL_RADIUS, y + BALL_RADIUS, BALL_RADIUS);
        shapeRenderer.end();
        
        batch.begin();
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    @Override
    public int getDamage() {
        return 1;
    }

    public void dispose() {
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
    }
}
