package puppy.code.stats;

import puppy.code.entities.projectiles.ProjectileType;

// Maneja las estadisticas modificables de la nave del jugador.
public class ShipStats {
    private static final int BASE_MAX_HEALTH = 6;
    private static final float BASE_DEFENSE_MULTIPLIER = 1.0f;
    
    // Multiplicadores de upgrades
    private float healthMultiplier = 1.0f;
    private float fireRateMultiplier = 1.0f;
    private float projectileSpeedMultiplier = 1.0f;
    private float defenseMultiplier = 1.0f;
    private float damageMultiplier = 1.0f;
    
    // === SALUD ===
    
    public int getMaxHealth() {
        return Math.round(BASE_MAX_HEALTH * healthMultiplier);
    }
    
    public void addHealthUpgrade(float percentIncrease) {
        healthMultiplier += percentIncrease;
    }
    
    public float getHealthMultiplier() {
        return healthMultiplier;
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
    
    // === UTILIDADES ===
    
    public void resetToDefaults() {
        healthMultiplier = 1.0f;
        fireRateMultiplier = 1.0f;
        projectileSpeedMultiplier = 1.0f;
        defenseMultiplier = 1.0f;
        damageMultiplier = 1.0f;
    }
    
    public String getStatsString() {
        return String.format(
            "ShipStats: Health=%.2fx (Max: %d), FireRate=%.2fx, Speed=%.2fx, Defense=%.2fx, Damage=%.2fx",
            healthMultiplier, getMaxHealth(), 
            fireRateMultiplier, projectileSpeedMultiplier, defenseMultiplier, damageMultiplier
        );
    }
    
    // === VALORES BASE (GETTERS) ===
    
    public static int getBaseMaxHealth() {
        return BASE_MAX_HEALTH;
    }
    
    public static float getBaseDefenseMultiplier() {
        return BASE_DEFENSE_MULTIPLIER;
    }
}