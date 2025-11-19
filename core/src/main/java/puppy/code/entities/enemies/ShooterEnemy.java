package puppy.code.entities.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class ShooterEnemy extends Enemy {
    private Sprite sprite;
    private int maxHealth;
    private float damageFlashTimer;
    private boolean showDamageFlash;
    private static final float DAMAGE_FLASH_DURATION = 0.2f;

    public ShooterEnemy(float x, float y, Texture texture, int health, float speed) {
        super(x, y, 48, 48, health, speed);
        this.sprite = new Sprite(texture);
        this.sprite.setPosition(x, y);
        this.sprite.setSize(width, height);
        this.maxHealth = health;
        this.damageFlashTimer = 0f;
        this.showDamageFlash = false;
    }

    @Override
    public void update(float delta) {
        if (movementBehavior != null) {
            movementBehavior.move(this, delta);
        }
        
        updateWeapon(delta);
        
        sprite.setPosition(x, y);
        
        if (showDamageFlash) {
            damageFlashTimer -= delta;
            if (damageFlashTimer <= 0) {
                showDamageFlash = false;
                sprite.setColor(1f, 1f, 1f, 1f);
            }
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    @Override
    public void takeDamage(int damage) {
        health -= damage;
        
        if (health > 0) {
            showDamageFlash = true;
            damageFlashTimer = DAMAGE_FLASH_DURATION;
            
            float healthPercentage = (float) health / maxHealth;
            sprite.setColor(1f, healthPercentage, healthPercentage, 1f);
        } else {
            destroyed = true;
        }
    }

    @Override
    public int getScoreValue() {
        return 50;
    }

    @Override
    public int getXPValue() {
        return 10;
    }

    @Override
    public String getDestructionSound() {
        return "explosion.ogg";
    }
}
