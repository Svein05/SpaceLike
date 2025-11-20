package puppy.code.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import puppy.code.entities.enemies.Enemy;
import puppy.code.entities.enemies.EnemyType;
import puppy.code.entities.enemies.MeteoriteEnemy;
import puppy.code.entities.Nave;
import puppy.code.entities.enemies.factories.EnemyFactory;
import puppy.code.entities.enemies.factories.MeteoriteEnemyFactory;
import puppy.code.entities.enemies.factories.RokuEnemyFactory;
import puppy.code.entities.enemies.factories.ChargerEnemyFactory;

public class EnemySpawner {
    private Random random;
    private Map<EnemyType, EnemyFactory> factories;
    
    public EnemySpawner() {
        this.random = new Random();
        this.factories = new HashMap<>();
        
        initializeFactories();
    }
    
    private void initializeFactories() {
        factories.put(EnemyType.METEORITE, new MeteoriteEnemyFactory());
        factories.put(EnemyType.ROKU, new RokuEnemyFactory());
    }
    
    public Enemy spawnEnemy(EnemyType type, float x, float y, float velocityX, float velocityY) {
        return spawnEnemy(type, x, y, velocityX, velocityY, 1);
    }
    
    public Enemy spawnEnemy(EnemyType type, float x, float y, float velocityX, float velocityY, int round) {
        EnemyFactory factory = factories.get(type);
        
        if (factory != null) {
            return factory.createCompleteEnemy(x, y, velocityX, velocityY, round);
        }
        
        return new MeteoriteEnemy((int)x, (int)y, (int)type.getSize(), 
                                (int)velocityX, (int)velocityY, round);
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
    
    public void registerFactory(EnemyType type, EnemyFactory factory) {
        factories.put(type, factory);
    }
    
    public EnemyFactory getFactory(EnemyType type) {
        return factories.get(type);
    }
    
    public void registerChargerFactory(Nave playerShip) {
        if (!factories.containsKey(EnemyType.CHARGER)) {
            factories.put(EnemyType.CHARGER, new ChargerEnemyFactory(playerShip));
        }
    }
}