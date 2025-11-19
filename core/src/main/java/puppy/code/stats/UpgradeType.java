package puppy.code.stats;

public enum UpgradeType {
    HEALTH("Vida Extra", "Aumenta vida maxima en 1 corazon completo", 1.0f),
    DAMAGE("Poder de Fuego", "Aumenta el dano global en 20%", 0.20f),
    FIRE_RATE("Cadencia Rapida", "Aumenta velocidad de disparo en 25%", 0.25f),
    HOMING("Teledirigido", "Mejora tracking de proyectiles en 10%", 0.10f),
    SPINNER_UNLOCK("Spinner Defense", "Desbloquea bolas orbitales que danan enemigos y destruyen proyectiles", 1.0f),
    SPINNER_COUNT("Spinner +1", "Agrega 1 bola orbitante (Max: 10)", 1.0f),
    SPINNER_DAMAGE("Spinner Dano", "Aumenta dano de spinners en 20%", 0.20f);
    
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
