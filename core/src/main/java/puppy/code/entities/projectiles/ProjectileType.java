package puppy.code.entities.projectiles;

public enum ProjectileType {
    BULLET("Rocket2.png", 10, 300, 5, 0.5f, "Audio/SFX/Weapons/Shoot19-_6_.mp3"),
    ENEMY_BALL("", 16, 300, 1, 1.0f, "pop-sound.mp3");  // Mantener pop-sound para enemigos
    
    private final String texturePath;
    private final float width;
    private final float defaultSpeed;
    private final int damage;
    private final float fireRate;
    private final String soundPath;
    
    ProjectileType(String texturePath, float width, float defaultSpeed, int damage, float fireRate, String soundPath) {
        this.texturePath = texturePath;
        this.width = width;
        this.defaultSpeed = defaultSpeed;
        this.damage = damage;
        this.fireRate = fireRate;
        this.soundPath = soundPath;
    }
    
    public String getTexturePath() { return texturePath; }
    public float getWidth() { return width; }
    public float getDefaultSpeed() { return defaultSpeed; }
    public int getDamage() { return damage; }
    public float getFireRate() { return fireRate; }
    public String getSoundPath() { return soundPath; }
}