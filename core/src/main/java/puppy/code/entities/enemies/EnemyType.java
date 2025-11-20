package puppy.code.entities.enemies;

public enum EnemyType {
    METEORITE("Game/Enemys/Asteroids/Asteroid.atlas", 20, 1, 10, 5, "Audio/SFX/Explosions/explosion.mp3"),
    ROKU("Game/Enemys/EnemyShips/Roku.png", 48, 10, 75, 15, "Audio/SFX/Explosions/Boom10.mp3"),
    CHARGER("Game/Enemys/EnemyShips/Charger.png", 48, 15, 50, 15, "Audio/SFX/Explosions/Boom10.mp3");
    
    private final String texturePath;
    private final float size;
    private final int health;
    private final int scoreValue;
    private final int xpValue;
    private final String destructionSound;
    
    EnemyType(String texturePath, float size, int health, int scoreValue, int xpValue, String destructionSound) {
        this.texturePath = texturePath;
        this.size = size;
        this.health = health;
        this.scoreValue = scoreValue;
        this.xpValue = xpValue;
        this.destructionSound = destructionSound;
    }
    
    public String getTexturePath() { return texturePath; }
    public float getSize() { return size; }
    public int getHealth() { return health; }
    public int getScoreValue() { return scoreValue; }
    public int getXpValue() { return xpValue; }
    public String getDestructionSound() { return destructionSound; }
}