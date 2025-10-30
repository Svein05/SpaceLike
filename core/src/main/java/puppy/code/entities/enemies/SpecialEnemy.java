package puppy.code.entities.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class SpecialEnemy extends Enemy {
    private Sprite sprite;

    public SpecialEnemy(float x, float y, Texture texture) {
        super(x, y, 64, 64, 3, 100f);
        sprite = new Sprite(texture);
        sprite.setPosition(x, y);
    }

    @Override
    public void update(float delta) {
        y -= speed * delta;
        sprite.setPosition(x, y);
        
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
        return 50;
    }

    @Override
    public int getXPValue() {
        return 15; // Los enemigos especiales dan 15 XP
    }
}