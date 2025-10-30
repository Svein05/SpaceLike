package puppy.code.entities.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class BossEnemy extends Enemy {
    private Sprite sprite;
    private float attackTimer;
    private float attackCooldown = 2.0f;

    public BossEnemy(float x, float y, Texture texture) {
        super(x, y, 128, 128, 20, 50f);
        sprite = new Sprite(texture);
        sprite.setPosition(x, y);
        attackTimer = 0;
    }

    @Override
    public void update(float delta) {
        y -= speed * delta;
        sprite.setPosition(x, y);
        
        attackTimer += delta;
        if (attackTimer >= attackCooldown) {
            // Aqui se implementaria el ataque del boss
            attackTimer = 0;
        }
        
        if (y < -height) {
            destroyed = true;
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }

    @Override
    public Rectangle getBounds() {
        return sprite.getBoundingRectangle();
    }

    @Override
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            destroyed = true;
        }
    }

    @Override
    public int getScoreValue() {
        return 500;
    }

    @Override
    public int getXPValue() {
        return 100; // Los boss dan 100 XP
    }
}