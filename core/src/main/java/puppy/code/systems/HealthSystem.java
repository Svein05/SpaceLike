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
    private float currentHearts;
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
        
        float maxHeartsFloat = shipStats.getMaxHearts();
        maxHearts = (int) Math.ceil(maxHeartsFloat);
        hearts = new HeartState[maxHearts];
        currentHearts = maxHeartsFloat;
        
        updateHeartStates();
    }
    
    public void takeDamage() {
        takeDamage(0.5f);
    }
    
    public void takeDamage(float heartsLost) {
        if (currentHearts > 0) {
            currentHearts -= heartsLost;
            if (currentHearts < 0) {
                currentHearts = 0;
            }
            
            updateHeartStates();
        }
    }
    
    private void updateHeartStates() {
        for (int i = 0; i < maxHearts; i++) {
            float heartValue = currentHearts - i;
            
            if (heartValue >= 1.0f) {
                hearts[i] = HeartState.FULL;
            } else if (heartValue >= 0.5f) {
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
        return currentHearts <= 0;
    }
    
    public float getCurrentHealth() {
        return currentHearts;
    }
    
    public int getVidas() {
        return (int) Math.ceil(currentHearts);
    }
    
    public void setVidas(int vidas) {
        currentHearts = vidas;
        updateHeartStates();
    }
    
    public void setCurrentHealth(float hearts) {
        currentHearts = Math.min(hearts, shipStats.getMaxHearts());
        if (currentHearts < 0) {
            currentHearts = 0;
        }
        updateHeartStates();
    }
    
    public void fullHeal() {
        currentHearts = shipStats.getMaxHearts();
        updateHeartStates();
    }
    
    public void heal(int amount) {
        float heartsToHeal = amount / 2.0f;
        currentHearts = Math.min(currentHearts + heartsToHeal, shipStats.getMaxHearts());
        updateHeartStates();
    }
    
    public void refreshFromStats() {
        float newMaxHearts = shipStats.getMaxHearts();
        int newMaxHeartsInt = (int) Math.ceil(newMaxHearts);
        
        if (newMaxHeartsInt != maxHearts) {
            maxHearts = newMaxHeartsInt;
            HeartState[] newHearts = new HeartState[maxHearts];
            
            for (int i = 0; i < Math.min(hearts.length, newHearts.length); i++) {
                newHearts[i] = hearts[i];
            }
            
            for (int i = hearts.length; i < newHearts.length; i++) {
                newHearts[i] = HeartState.EMPTY;
            }
            
            hearts = newHearts;
        }
        
        float maxHeartsLimit = shipStats.getMaxHearts();
        if (currentHearts > maxHeartsLimit) {
            currentHearts = maxHeartsLimit;
        }
        
        updateHeartStates();
    }
}