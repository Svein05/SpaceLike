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
    
    // Phase 1: Sistema de patrones
    private float patternTimer;
    private float patternCooldown;
    private BossPatternType currentPattern;
    private float patternRotation;
    private int shotsFired;
    private float cooldownMultiplier;
    private float rotationSpeedMultiplier;
    private boolean inBurstMode;
    private float burstTimer;
    private float nextBurstTime;
    
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
        
        this.patternTimer = 0f;
        this.patternCooldown = 3f;
        this.currentPattern = null;
        this.patternRotation = 0f;
        this.shotsFired = 0;
        this.cooldownMultiplier = 1.0f;
        this.rotationSpeedMultiplier = 1.0f;
        this.inBurstMode = false;
        this.burstTimer = 0f;
        this.nextBurstTime = (float)(Math.random() * 10 + 5);        

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
        float healthPercent = (float)health / maxHealth;
        
        if (healthPercent <= 0.33f && currentState != BossState.PHASE_3) {
            currentState = BossState.PHASE_3;
            System.out.println("[BOSS] Transicion a PHASE_3 (" + health + "/" + maxHealth + " HP)");
        } else if (healthPercent <= 0.66f && currentState == BossState.PHASE_1) {
            currentState = BossState.PHASE_2;
            System.out.println("[BOSS] Transicion a PHASE_2 (" + health + "/" + maxHealth + " HP)");
        }
        
        if (currentState == BossState.PHASE_1) {
            burstTimer += delta;
            
            if (!inBurstMode && burstTimer >= nextBurstTime) {
                inBurstMode = true;
                burstTimer = 0f;
            }
            
            if (inBurstMode && burstTimer >= 3f) {
                inBurstMode = false;
                burstTimer = 0f;
                nextBurstTime = (float)(Math.random() * 10 + 5);
            }
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
    
    public float getPatternTimer() {
        return patternTimer;
    }
    
    public void updatePatternTimer(float delta) {
        patternTimer += delta;
    }
    
    public void resetPatternTimer() {
        patternTimer = 0f;
    }
    
    public float getPatternCooldown() {
        return patternCooldown;
    }
    
    public BossPatternType getCurrentPattern() {
        return currentPattern;
    }
    
    public void setCurrentPattern(BossPatternType pattern) {
        this.currentPattern = pattern;
    }
    
    public float getPatternRotation() {
        return patternRotation;
    }
    
    public void incrementPatternRotation(float increment) {
        patternRotation += increment * getEffectiveRotationSpeed();
        if (patternRotation >= 360f) {
            patternRotation -= 360f;
        }
    }
    
    public void onShotFired() {
        shotsFired++;
        cooldownMultiplier *= 0.99f;
        
        if (shotsFired % 5 == 0) {
            rotationSpeedMultiplier *= 1.01f;
        }
    }
    
    public float getEffectiveCooldown() {
        float baseCooldown = patternCooldown * cooldownMultiplier;
        
        if (currentState == BossState.PHASE_1) {
            float healthPercent = (float)health / maxHealth;
            float phase1Percent = (healthPercent - 0.66f) / 0.34f;
            phase1Percent = Math.max(0f, Math.min(1f, phase1Percent));
            
            float hpMultiplier = 1.0f - (1.0f - phase1Percent) * 0.5f;
            baseCooldown *= hpMultiplier;
            
            if (inBurstMode) {
                baseCooldown *= 0.33f;
            }
        }
        
        return baseCooldown;
    }
    
    public float getEffectiveRotationSpeed() {
        float baseSpeed = rotationSpeedMultiplier;
        
        if (currentState == BossState.PHASE_1) {
            float healthPercent = (float)health / maxHealth;
            float phase1Percent = (healthPercent - 0.66f) / 0.34f;
            phase1Percent = Math.max(0f, Math.min(1f, phase1Percent));
            
            float hpMultiplier = 1.0f + (1.0f - phase1Percent) * 0.2f;
            baseSpeed *= hpMultiplier;
        }
        
        return baseSpeed;
    }
    
    public float getCenterX() {
        return x + width / 2f;
    }
    
    public float getCenterY() {
        return y + height / 2f;
    }
}
