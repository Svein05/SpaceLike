package puppy.code.stats;

public enum UpgradeType {
    HEALTH("Vida Extra", "Aumenta vida maxima en medio corazon", 0.5f),
    DEFENSE("Defensa Mejorada", "Reduce el dano de colisiones en 25%", 0.25f),
    DAMAGE("Poder de Fuego", "Aumenta el dano global en 20%", 0.20f),
    FIRE_RATE("Cadencia Rapida", "Aumenta velocidad de disparo en 25%", 0.25f);
    
    private final String displayName;
    private final String description;
    private final float value;
    
    UpgradeType(String displayName, String description, float value) {
        this.displayName = displayName;
        this.description = description;
        this.value = value;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public float getValue() {
        return value;
    }
}
