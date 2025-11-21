package puppy.code.managers;

import puppy.code.entities.bonus.BonusAutocannon;
import puppy.code.entities.bonus.BonusShield;
import puppy.code.entities.bonus.BonusOmniShot;
import puppy.code.entities.bonus.BonusHeal;
import puppy.code.entities.bonus.BonusType;
import puppy.code.interfaces.Bonus;
import java.util.Random;
import com.badlogic.gdx.audio.Sound;

public class BonusManager {
    private Bonus activeBonus;
    private BonusType activeBonusType;
    private BonusType lastSpawnedBonusType;
    private BonusState currentState;
    private float timer;
    private Random random;
    private ResourceManager resourceManager;
    
    private static final float SPAWN_INTERVAL = 15f;
    private static final float PENALTY_INTERVAL = 30f;
    private static final float BONUS_DURATION = 10f;
    
    private static final float SCREEN_WIDTH = 1920f;
    private static final float SPAWN_HEIGHT = 1080f + 50f;
    
    public enum BonusState {
        WAITING_TO_SPAWN,
        FALLING,
        EFFECT_ACTIVE
    }
    
    public BonusManager() {
        random = new Random();
        currentState = BonusState.WAITING_TO_SPAWN;
        timer = SPAWN_INTERVAL;
        resourceManager = ResourceManager.getInstance();
    }
    
    public void update(float delta) {
        timer -= delta;
        
        switch (currentState) {
            case WAITING_TO_SPAWN:
                if (timer <= 0) {
                    spawnBonus();
                    currentState = BonusState.FALLING;
                }
                break;
                
            case FALLING:
                if (activeBonus != null) {
                    activeBonus.update(delta);
                    
                    if (activeBonus.isOffScreen()) {
                        bonusLost();
                    }
                }
                break;
                
            case EFFECT_ACTIVE:
                if (activeBonus != null) {
                    activeBonus.update(delta);
                }
                if (timer <= 0) {
                    effectExpired();
                }
                break;
        }
    }
    
    private void spawnBonus() {
        float randomX = random.nextFloat() * (SCREEN_WIDTH - 44f);
        
        BonusType[] types = BonusType.values();
        
        if (types.length > 1) {
            BonusType newBonusType;
            do {
                newBonusType = types[random.nextInt(types.length)];
            } while (newBonusType == lastSpawnedBonusType);
            activeBonusType = newBonusType;
        } else {
            activeBonusType = types[0];
        }
        
        lastSpawnedBonusType = activeBonusType;
        
        switch (activeBonusType) {
            case AUTOCANNON:
                activeBonus = new BonusAutocannon(randomX, SPAWN_HEIGHT);
                break;
            case SHIELD:
                activeBonus = new BonusShield(randomX, SPAWN_HEIGHT);
                break;
            case OMNISHOT:
                activeBonus = new BonusOmniShot(randomX, SPAWN_HEIGHT);
                break;
            case HEAL:
                activeBonus = new BonusHeal(randomX, SPAWN_HEIGHT);
                break;
        }
        
        timer = 0f;
    }
    
    public void bonusCollected(puppy.code.entities.Nave nave) {
        if (currentState == BonusState.FALLING && activeBonus != null) {
            activeBonus.collect();
            
            playBonusSound(activeBonusType);
            
            if (activeBonusType != BonusType.HEAL) {
                activeBonus.activateEffect();
                currentState = BonusState.EFFECT_ACTIVE;
                timer = BONUS_DURATION;
            } else {
                if (activeBonus instanceof BonusHeal) {
                    BonusHeal healBonus = (BonusHeal) activeBonus;
                    nave.getHealthSystem().heal(healBonus.getHealAmount());
                }
                activeBonus.dispose();
                activeBonus = null;
                activeBonusType = null;
                currentState = BonusState.WAITING_TO_SPAWN;
                timer = SPAWN_INTERVAL;
            }
        }
    }
    
    public void applyBonusToShip(puppy.code.entities.Nave nave) {
        if (activeBonusType == BonusType.HEAL) {
            return;
        }
        
        if (currentState == BonusState.EFFECT_ACTIVE && activeBonus != null) {
            switch (activeBonusType) {
                case AUTOCANNON:
                    if (activeBonus instanceof BonusAutocannon) {
                        nave.setBonusShootingBehavior((BonusAutocannon) activeBonus);
                    }
                    break;
                case SHIELD:
                    nave.setInvincible(true);
                    break;
                case OMNISHOT:
                    if (activeBonus instanceof BonusOmniShot) {
                        nave.setBonusShootingBehavior((BonusOmniShot) activeBonus);
                    }
                    break;
                case HEAL:
                    break;
            }
        }
    }
    
    public void removeBonusFromShip(puppy.code.entities.Nave nave) {
        if (activeBonusType != null) {
            switch (activeBonusType) {
                case AUTOCANNON:
                    nave.setBonusShootingBehavior(null);
                    break;
                case SHIELD:
                    nave.setInvincible(false);
                    break;
                case OMNISHOT:
                    nave.setBonusShootingBehavior(null);
                    break;
                case HEAL:
                    break;
            }
        }
    }
    
    private void bonusLost() {
        if (activeBonus != null) {
            activeBonus.dispose();
            activeBonus = null;
        }
        activeBonusType = null;
        currentState = BonusState.WAITING_TO_SPAWN;
        timer = PENALTY_INTERVAL;
    }
    
    private void effectExpired() {
        if (activeBonus != null) {
            activeBonus.deactivateEffect();
            activeBonus.dispose();
            activeBonus = null;
        }
        activeBonusType = null;
        currentState = BonusState.WAITING_TO_SPAWN;
        timer = SPAWN_INTERVAL;
    }
    
    public void updateShipBehavior(puppy.code.entities.Nave nave) {
        if (currentState == BonusState.EFFECT_ACTIVE && activeBonus != null && activeBonusType != null) {
            switch (activeBonusType) {
                case AUTOCANNON:
                    if (nave.getShootingBehavior() != activeBonus) {
                        applyBonusToShip(nave);
                    }
                    break;
                case SHIELD:
                    if (!nave.isInvincible()) {
                        applyBonusToShip(nave);
                    }
                    break;
                case OMNISHOT:
                    if (nave.getShootingBehavior() != activeBonus) {
                        applyBonusToShip(nave);
                    }
                    break;
                case HEAL:
                    break;
            }
        } else {
            if (nave.getShootingBehavior() != null) {
                nave.setBonusShootingBehavior(null);
            }
        }
    }
    
    public Bonus getActiveBonus() {
        return activeBonus;
    }
    
    public BonusType getActiveBonusType() {
        return activeBonusType;
    }
    
    public boolean isBonusEffectActive() {
        return currentState == BonusState.EFFECT_ACTIVE;
    }
    
    public BonusState getCurrentState() {
        return currentState;
    }
    
    public float getRemainingTime() {
        return timer;
    }
    
    public void saveState() {
    }
    
    public void forceExpireBonus(puppy.code.entities.Nave nave) {
        if (currentState == BonusState.EFFECT_ACTIVE) {
            removeBonusFromShip(nave);
            effectExpired();
        }
    }
    
    private void playBonusSound(BonusType bonusType) {
        try {
            Sound bonusSound;
            switch (bonusType) {
                case HEAL:
                    bonusSound = resourceManager.getSound("Audio/SFX/Player/PowerUp6.mp3");
                    break;
                case SHIELD:
                    bonusSound = resourceManager.getSound("Audio/SFX/Player/PowerUp34.mp3");
                    break;
                case AUTOCANNON:
                case OMNISHOT:
                default:
                    bonusSound = resourceManager.getSound("Audio/SFX/Player/powerUp.mp3");
                    break;
            }
            bonusSound.play(0.7f);
        } catch (Exception e) {
            System.out.println("Error reproduciendo sonido de bonus: " + e.getMessage());
        }
    }
    
    public void dispose() {
        if (activeBonus != null) {
            activeBonus.dispose();
        }
    }
}