package puppy.code.entities.enemies;

public enum EnemyType {
    METEORITE("aGreyMedium4.png", 20, 1, 10, 5, "explosion.ogg"),
    BOSS("boss.png", 128, 20, 500, 100, "hurt.ogg"),
    SPECIAL("special.png", 64, 3, 50, 15, "explosion.ogg"),
    ENEMYSHIP1("Game/Enemys/EnemyShips/EnemyShip1.png", 48, 10, 75, 15, "explosion.ogg");
    
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