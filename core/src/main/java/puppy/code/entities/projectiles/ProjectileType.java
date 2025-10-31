package puppy.code.entities.projectiles;

public enum ProjectileType {
    BULLET("Rocket2.png", 10, 300, 1, "pop-sound.mp3");
    // TODO: Añadir nuevos tipos cuando tengas los assets:
    // LASER("laser.png", 4, 800, 2, "laser-shot.ogg"),
    // MISSILE("missile.png", 15, 250, 3, "missile-launch.ogg"),
    // PLASMA("plasma.png", 12, 600, 4, "plasma-shot.ogg");
    
    private final String texturePath;
    private final float width;
    private final float defaultSpeed;
    private final int damage;
    private final String soundPath;
    
    ProjectileType(String texturePath, float width, float defaultSpeed, int damage, String soundPath) {
        this.texturePath = texturePath;
        this.width = width;
        this.defaultSpeed = defaultSpeed;
        this.damage = damage;
        this.soundPath = soundPath;
    }
    
    public String getTexturePath() { return texturePath; }
    public float getWidth() { return width; }
    public float getDefaultSpeed() { return defaultSpeed; }
    public int getDamage() { return damage; }
    public String getSoundPath() { return soundPath; }
}