package puppy.code.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import puppy.code.stats.ShipStats;

public class HealthSystem {
    
    public enum HeartState {
        FULL,
        HALF,
        EMPTY
    }
    
    private TextureRegion heartFull;
    private TextureRegion heartHalf;
    private TextureRegion heartEmpty;
    
    private HeartState[] hearts;
    private int maxHearts;
    private float currentHealth;
    private ShipStats shipStats;
    
    public HealthSystem(ShipStats shipStats) {
        this.shipStats = shipStats;
        
        TextureAtlas heartAtlas = new TextureAtlas(Gdx.files.internal("UI/Heart/HeartsUwU.atlas"));
        
        heartFull = heartAtlas.findRegion("HeartFull");
        heartHalf = heartAtlas.findRegion("HeartMid");     
        heartEmpty = heartAtlas.findRegion("Heart0");
        
        if (heartHalf != null) {
            heartHalf.flip(true, false);
        }
        
        if (heartFull == null || heartHalf == null || heartEmpty == null) {
            throw new RuntimeException("No se pudieron cargar los sprites de corazones desde el atlas.");
        }
        
        // Usar maxHealth de shipStats
        int maxHealthPoints = shipStats.getMaxHealth();
        maxHearts = (maxHealthPoints + 1) / 2;
        hearts = new HeartState[maxHearts];
        currentHealth = maxHealthPoints;
        
        for (int i = 0; i < maxHearts; i++) {
            hearts[i] = HeartState.FULL;
        }
    }
    
    public void takeDamage() {
        takeDamage(1.0f);
    }
    
    public void takeDamage(float baseDamage) {
        if (currentHealth > 0) {
            float actualDamage = shipStats.calculateDamageReceived(baseDamage);
            currentHealth -= actualDamage;
            if (currentHealth < 0) {
                currentHealth = 0;
            }
            
            updateHeartStates();
        }
    }
    
    private void updateHeartStates() {
        for (int i = 0; i < maxHearts; i++) {
            float heartHealth = currentHealth - (i * 2);
            
            if (heartHealth >= 2) {
                hearts[i] = HeartState.FULL;
            } else if (heartHealth >= 1) {
                hearts[i] = HeartState.HALF;
            } else {
                hearts[i] = HeartState.EMPTY;
            }
        }
    }
    
    public void render(SpriteBatch batch, float x, float y) {
        float heartWidth = 32;
        float heartHeight = 32;
        float heartSpacing = 40;
        
        for (int i = 0; i < maxHearts; i++) {
            TextureRegion heartTexture;
            
            switch (hearts[i]) {
                case FULL:
                    heartTexture = heartFull;
                    break;
                case HALF:
                    heartTexture = heartHalf;
                    break;
                case EMPTY:
                default:
                    heartTexture = heartEmpty;
                    break;
            }
            
            batch.draw(heartTexture, x + (i * heartSpacing), y, heartWidth, heartHeight);
        }
    }
    
    public boolean isDead() {
        return currentHealth <= 0;
    }
    
    public float getCurrentHealth() {
        return currentHealth;
    }
    
    public int getVidas() {
        return (int) Math.ceil(currentHealth / 2.0f);
    }
    
    public void setVidas(int vidas) {
        currentHealth = vidas * 2;
        updateHeartStates();
    }
    
    public void fullHeal() {
        currentHealth = shipStats.getMaxHealth();
        updateHeartStates();
    }
    
    public void refreshFromStats() {
        int newMaxHealthPoints = shipStats.getMaxHealth();
        int newMaxHearts = (newMaxHealthPoints + 1) / 2;
        
        if (newMaxHearts != maxHearts) {
            maxHearts = newMaxHearts;
            HeartState[] newHearts = new HeartState[maxHearts];
            
            for (int i = 0; i < Math.min(hearts.length, newHearts.length); i++) {
                newHearts[i] = hearts[i];
            }
            
            for (int i = hearts.length; i < newHearts.length; i++) {
                newHearts[i] = HeartState.FULL;
            }
            
            hearts = newHearts;
        }
        
        float maxHealth = shipStats.getMaxHealth();
        if (currentHealth > maxHealth) {
            currentHealth = maxHealth;
        }
        
        updateHeartStates();
    }
}