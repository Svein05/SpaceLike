package puppy.code.entities.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import puppy.code.entities.Nave;

public class ChargerEnemy extends Enemy {
    private Sprite sprite;
    private Nave playerShip;
    
    private float damageFlashTimer;
    private boolean showDamageFlash;
    private static final float DAMAGE_FLASH_DURATION = 0.2f;
    
    private ChargerState state;
    private static final float WAITING_SPEED = 1.5f;
    private static final float CHARGING_SPEED = 8.0f;
    private static final float ALIGNMENT_THRESHOLD = 80f;
    
    public enum ChargerState {
        WAITING,
        CHARGING
    }
    
    public ChargerEnemy(float x, float y, Texture texture, int health, float speed, Nave playerShip) {
        super(x, y, texture.getWidth() * 1.5f, texture.getHeight() * 1.5f, health, speed);
        this.sprite = new Sprite(texture);
        this.sprite.setSize(width, height);
        this.playerShip = playerShip;
        this.state = ChargerState.WAITING;
    }

    @Override
    public void update(float delta) {
        float currentSpeed = (state == ChargerState.WAITING) ? WAITING_SPEED : CHARGING_SPEED;
        
        if (state == ChargerState.WAITING) {
            float playerCenterX = playerShip.getX() + playerShip.getWidth() / 2;
            float enemyCenterX = x + width / 2;
            float distanceX = Math.abs(playerCenterX - enemyCenterX);
            
            if (distanceX < ALIGNMENT_THRESHOLD && y > playerShip.getY()) {
                state = ChargerState.CHARGING;
                sprite.setColor(1f, 0.5f, 0.5f, 1f);
            }
        }
        
        y -= currentSpeed * delta * 60;
        
        if (y < -height - 50) {
            respawnFromTop();
        }
        
        updateWeapon(delta);
        
        if (showDamageFlash) {
            damageFlashTimer -= delta;
            if (damageFlashTimer <= 0) {
                showDamageFlash = false;
                if (state == ChargerState.CHARGING) {
                    sprite.setColor(1f, 0.5f, 0.5f, 1f);
                } else {
                    sprite.setColor(1f, 1f, 1f, 1f);
                }
            }
        }
    }
    
    private void respawnFromTop() {
        x = (float) (Math.random() * (1920 - width));
        y = 1080 + 50;
        state = ChargerState.WAITING;
        sprite.setColor(1f, 1f, 1f, 1f);
    }

    @Override
    public void draw(SpriteBatch batch) {
        sprite.setPosition(x, y);
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
            sprite.setColor(1f, 0.3f, 0.3f, 1f);
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
        return 15;
    }

    @Override
    public String getDestructionSound() {
        return "explosion.ogg";
    }
    
    public ChargerState getState() {
        return state;
    }
}
