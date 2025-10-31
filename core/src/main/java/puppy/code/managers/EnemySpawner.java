package puppy.code.managers;

import java.util.Random;
import com.badlogic.gdx.graphics.Texture;
import puppy.code.entities.enemies.Enemy;
import puppy.code.entities.enemies.EnemyType;
import puppy.code.entities.enemies.MeteoriteEnemy;
import puppy.code.entities.enemies.BossEnemy;
import puppy.code.entities.enemies.SpecialEnemy;

public class EnemySpawner {
    private Random random;
    private ResourceManager resourceManager;
    
    public EnemySpawner() {
        this.random = new Random();
        this.resourceManager = ResourceManager.getInstance();
    }
    
    public Enemy spawnEnemy(EnemyType type, float x, float y, float velocityX, float velocityY) {
        return spawnEnemy(type, x, y, velocityX, velocityY, 1);
    }
    
    public Enemy spawnEnemy(EnemyType type, float x, float y, float velocityX, float velocityY, int round) {
        switch (type) {
            case METEORITE:
                return new MeteoriteEnemy((int)x, (int)y, (int)type.getSize(), 
                                        (int)velocityX, (int)velocityY, round);
            case BOSS:
                Texture bossTexture = resourceManager.getTexture(type.getTexturePath());
                return new BossEnemy(x, y, bossTexture);
            case SPECIAL:
                Texture specialTexture = resourceManager.getTexture(type.getTexturePath());
                return new SpecialEnemy(x, y, specialTexture);
            default:
                return new MeteoriteEnemy((int)x, (int)y, (int)type.getSize(), 
                                        (int)velocityX, (int)velocityY, round);
        }
    }
    
    public Enemy spawnEnemyRandom(EnemyType type, int velX, int velY) {
        return spawnEnemyRandom(type, velX, velY, 1);
    }
    
    public Enemy spawnEnemyRandom(EnemyType type, int velX, int velY, int round) {
        float x = random.nextInt(1920 - 100);
        float y = 1080 + 50;
        
        float velocityX = (random.nextFloat() - 0.5f) * 4f;
        float velocityY = -(velY + random.nextFloat() * 2f);
        
        return spawnEnemy(type, x, y, velocityX, velocityY, round);
    }
    
    public void spawnMultiple(EnemyType type, int count, int velX, int velY, 
                             java.util.ArrayList<Enemy> enemies) {
        spawnMultiple(type, count, velX, velY, enemies, 1);
    }
    
    public void spawnMultiple(EnemyType type, int count, int velX, int velY, 
                             java.util.ArrayList<Enemy> enemies, int round) {
        for (int i = 0; i < count; i++) {
            Enemy enemy = spawnEnemyRandom(type, velX, velY, round);
            enemies.add(enemy);
        }
    }
}