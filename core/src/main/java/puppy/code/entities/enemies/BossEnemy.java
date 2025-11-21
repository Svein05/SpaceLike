package puppy.code.entities.enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import puppy.code.entities.GameObject;

public class BossEnemy extends GameObject {
    
    public enum BossState {
        ENTERING,
        PHASE_1,
        PHASE_2,
        PHASE_3,
        DYING
    }
    
    private Animation<TextureRegion> animation;
    private float stateTime;
    private int health;
    private int maxHealth;
    private boolean destroyed;
    private BossState currentState;
    
    // Efecto de daño
    private boolean isHurt;
    private float hurtTimer;
    private static final float HURT_DURATION = 0.1f;
    
    // Entrada cinematica
    private float targetY;
    private static final float ENTRY_SPEED = 100f;
    
    public BossEnemy(float x, float y, TextureAtlas atlas, int health) {
        super((1920 - 700) / 2f, 1080f, 700, 750);
        this.health = health;
        this.maxHealth = health;
        this.destroyed = false;
        this.stateTime = 0f;
        this.isHurt = false;
        this.hurtTimer = 0f;
        this.currentState = BossState.ENTERING;
        this.targetY = (1080 - 750) / 2f;        

        if (atlas != null) {
            Array<TextureAtlas.AtlasRegion> frames = atlas.getRegions();
            animation = new Animation<>(0.1f, frames, Animation.PlayMode.LOOP);
        }
    }
    
    @Override
    public void update(float delta) {
        stateTime += delta;
        
        switch (currentState) {
            case ENTERING:
                updateEntering(delta);
                break;
            case PHASE_1:
            case PHASE_2:
            case PHASE_3:
                updateCombat(delta);
                break;
            case DYING:
                updateDying(delta);
                break;
        }
        
        if (isHurt) {
            hurtTimer += delta;
            if (hurtTimer >= HURT_DURATION) {
                isHurt = false;
                hurtTimer = 0f;
            }
        }
    }
    
    private void updateEntering(float delta) {
        if (y > targetY) {
            y -= ENTRY_SPEED * delta;
            
            if (y <= targetY) {
                y = targetY;
                currentState = BossState.PHASE_1;
            }
        }
    }
    
    private void updateCombat(float delta) {
        // Verificar transiciones de fase segun HP
        float healthPercent = (float)health / maxHealth;
        
        if (healthPercent <= 0.33f && currentState != BossState.PHASE_3) {
            currentState = BossState.PHASE_3;
            System.out.println("[BOSS] Transicion a PHASE_3 (" + health + "/" + maxHealth + " HP)");
        } else if (healthPercent <= 0.66f && currentState == BossState.PHASE_1) {
            currentState = BossState.PHASE_2;
            System.out.println("[BOSS] Transicion a PHASE_2 (" + health + "/" + maxHealth + " HP)");
        }
    }
    
    private void updateDying(float delta) {
        // TODO: Secuencia de muerte
    }
    
    @Override
    public void draw(SpriteBatch batch) {
        if (!destroyed && animation != null) {
            TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
            
            Color originalColor = batch.getColor().cpy();
            if (isHurt) {
                batch.setColor(1f, 0.3f, 0.3f, 1f);
            }
            
            batch.draw(currentFrame, x, y, width, height);
            
            if (isHurt) {
                batch.setColor(originalColor);
            }
        }
    }
    
    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
    
    public void takeDamage(int damage) {
        if (!destroyed) {
            health -= damage;
            isHurt = true;
            hurtTimer = 0f;
            
            if (health <= 0) {
                health = 0;
                destroyed = true;
            }
        }
    }
    
    public boolean isDestroyed() {
        return destroyed;
    }
    
    public int getHealth() {
        return health;
    }
    
    public int getMaxHealth() {
        return maxHealth;
    }
    
    public BossState getCurrentState() {
        return currentState;
    }
    
    public boolean isEntering() {
        return currentState == BossState.ENTERING;
    }
    
    public boolean inCombat() {
        return currentState == BossState.PHASE_1 || 
               currentState == BossState.PHASE_2 || 
               currentState == BossState.PHASE_3;
    }
}
