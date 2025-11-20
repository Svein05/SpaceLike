package puppy.code.stats;

import puppy.code.entities.projectiles.ProjectileType;

public class ShipStats {
    private static final float BASE_MAX_HEARTS = 3.0f;
    private static final float BASE_DEFENSE_MULTIPLIER = 1.0f;
    
    // Multiplicadores de upgrades
    private float healthUpgrade = 0.0f;
    private float fireRateMultiplier = 1.0f;
    private float projectileSpeedMultiplier = 1.0f;
    private float defenseMultiplier = 1.0f;
    private float damageMultiplier = 1.0f;
    private float homingPrecision = 0.0f;
    private boolean spinnerUnlocked = false;
    private int spinnerCount = 0;
    private float spinnerDamageMultiplier = 1.0f;
    private int bouncingBulletsLevel = 0;
    
    // === SALUD ===
    
    public float getMaxHearts() {
        return BASE_MAX_HEARTS + healthUpgrade;
    }
    
    public void addHealthUpgrade(float heartsToAdd) {
        healthUpgrade += heartsToAdd;
    }
    
    public float getHealthUpgrade() {
        return healthUpgrade;
    }
    
    // === VELOCIDAD DE DISPARO ===
    
    public float getEffectiveFireRate(ProjectileType projectileType) {
        float baseFireRate = projectileType.getFireRate();
        return baseFireRate / fireRateMultiplier;
    }
    
    public void addFireRateUpgrade(float percentIncrease) {
        fireRateMultiplier += percentIncrease;
    }
    
    public float getFireRateMultiplier() {
        return fireRateMultiplier;
    }
    
    // === VELOCIDAD DE PROYECTILES ===
    
    public float getEffectiveProjectileSpeed(ProjectileType projectileType) {
        float baseSpeed = projectileType.getDefaultSpeed();
        return baseSpeed * projectileSpeedMultiplier;
    }
    
    public void addProjectileSpeedUpgrade(float percentIncrease) {
        projectileSpeedMultiplier += percentIncrease;
    }
    
    public float getProjectileSpeedMultiplier() {
        return projectileSpeedMultiplier;
    }
    
    // === DEFENSA ===
    
    public float calculateDamageReceived(float incomingDamage) {
        return incomingDamage / defenseMultiplier;
    }
    
    public void addDefenseUpgrade(float percentIncrease) {
        defenseMultiplier += percentIncrease;
    }
    
    public float getDefenseMultiplier() {
        return defenseMultiplier;
    }
    
    // === DANO ===
    
    public float getDamageMultiplier() {
        return damageMultiplier;
    }
    
    public void addDamageUpgrade(float percentIncrease) {
        damageMultiplier += percentIncrease;
    }
    
    public float calculateDamage(float baseDamage) {
        return baseDamage * damageMultiplier;
    }
    
    // === TELEDIRIGIDO ===
    
    public float getHomingPrecision() {
        return homingPrecision;
    }
    
    public void addHomingUpgrade(float percentIncrease) {
        homingPrecision = Math.min(1.0f, homingPrecision + percentIncrease);
    }
    
    public void setHomingPrecision(float precision) {
        this.homingPrecision = Math.max(0.0f, Math.min(1.0f, precision));
    }
    
    // === SPINNER ===
    
    public boolean isSpinnerUnlocked() {
        return spinnerUnlocked;
    }
    
    public void unlockSpinner() {
        spinnerUnlocked = true;
        spinnerCount = 1;
    }
    
    public int getSpinnerCount() {
        return spinnerCount;
    }
    
    public void addSpinner() {
        if (spinnerCount < 10) {
            spinnerCount++;
        }
    }
    
    public void setSpinnerCount(int count) {
        this.spinnerCount = Math.min(count, 10);
    }
    
    public float getSpinnerDamageMultiplier() {
        return spinnerDamageMultiplier;
    }
    
    public void addSpinnerDamageUpgrade(float percentIncrease) {
        spinnerDamageMultiplier += percentIncrease;
    }
    
    public void setSpinnerDamageMultiplier(float multiplier) {
        this.spinnerDamageMultiplier = multiplier;
    }
    
    // === REBOTE DE BALAS ===
    
    public int getBouncingBulletsLevel() {
        return bouncingBulletsLevel;
    }
    
    public void addBouncingBulletsLevel() {
        bouncingBulletsLevel++;
    }
    
    public void setBouncingBulletsLevel(int level) {
        this.bouncingBulletsLevel = Math.max(0, level);
    }
    
    // === UTILIDADES ===
    
    public void resetToDefaults() {
        healthUpgrade = 0.0f;
        fireRateMultiplier = 1.0f;
        projectileSpeedMultiplier = 1.0f;
        defenseMultiplier = 1.0f;
        damageMultiplier = 1.0f;
        homingPrecision = 0.0f;
        spinnerUnlocked = false;
        spinnerCount = 0;
        spinnerDamageMultiplier = 1.0f;
        bouncingBulletsLevel = 0;
    }
    
    public String getStatsString() {
        return String.format(
            "ShipStats: Health=%.1f (+%.1f), FireRate=%.2fx, Speed=%.2fx, Defense=%.2fx, Damage=%.2fx",
            getMaxHearts(), healthUpgrade, 
            fireRateMultiplier, projectileSpeedMultiplier, defenseMultiplier, damageMultiplier
        );
    }
    
    // === VALORES BASE (GETTERS) ===
    
    public static float getBaseMaxHearts() {
        return BASE_MAX_HEARTS;
    }
    
    public static float getBaseDefenseMultiplier() {
        return BASE_DEFENSE_MULTIPLIER;
    }
}