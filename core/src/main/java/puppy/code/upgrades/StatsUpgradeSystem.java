package puppy.code.upgrades;

public class StatsUpgradeSystem {
    private float healthMultiplier = 1.0f;
    private float damageMultiplier = 1.0f;
    private float fireRateMultiplier = 1.0f;
    
    public void addHealthUpgrade(float percentIncrease) {
        healthMultiplier += percentIncrease;
    }
    
    public void addDamageUpgrade(float percentIncrease) {
        damageMultiplier += percentIncrease;
    }
    
    public void addFireRateUpgrade(float percentIncrease) {
        fireRateMultiplier += percentIncrease;
    }
    
    public float getHealthMultiplier() {
        return healthMultiplier;
    }
    
    public float getDamageMultiplier() {
        return damageMultiplier;
    }
    
    public float getFireRateMultiplier() {
        return fireRateMultiplier;
    }
    
    public void setHealthMultiplier(float multiplier) {
        this.healthMultiplier = multiplier;
    }
    
    public void setDamageMultiplier(float multiplier) {
        this.damageMultiplier = multiplier;
    }
    
    public void setFireRateMultiplier(float multiplier) {
        this.fireRateMultiplier = multiplier;
    }
    
    public void reset() {
        healthMultiplier = 1.0f;
        damageMultiplier = 1.0f;
        fireRateMultiplier = 1.0f;
    }
}
