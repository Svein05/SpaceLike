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
    
    // Factory method para crear enemigos basado en tipo
    public Enemy spawnEnemy(EnemyType type, float x, float y, float velocityX, float velocityY) {
        return spawnEnemy(type, x, y, velocityX, velocityY, 1); // Round 1 por defecto
    }
    
    // Factory method con round especifico para vida escalable
    public Enemy spawnEnemy(EnemyType type, float x, float y, float velocityX, float velocityY, int round) {
        Texture texture = resourceManager.getTexture(type.getTexturePath());
        
        switch (type) {
            case METEORITE:
                return new MeteoriteEnemy((int)x, (int)y, (int)type.getSize(), 
                                        (int)velocityX, (int)velocityY, texture, round);
            case BOSS:
                return new BossEnemy(x, y, texture);
            case SPECIAL:
                return new SpecialEnemy(x, y, texture);
            default:
                return new MeteoriteEnemy((int)x, (int)y, (int)type.getSize(), 
                                        (int)velocityX, (int)velocityY, texture, round);
        }
    }
    
    // Spawn con posicion random
    public Enemy spawnEnemyRandom(EnemyType type, int velX, int velY) {
        return spawnEnemyRandom(type, velX, velY, 1); // Round 1 por defecto
    }
    
    // Spawn con posicion random y round especifico
    public Enemy spawnEnemyRandom(EnemyType type, int velX, int velY, int round) {
        // Spawn desde arriba con posicion X aleatoria
        float x = random.nextInt(1920 - 100); // Dejar margen de 100px a los lados
        float y = 1080 + 50; // Aparecer 50px arriba de la pantalla
        
        // Movimiento diagonal: principalmente hacia abajo pero con inclinacion aleatoria
        float velocityX = (random.nextFloat() - 0.5f) * 4f; // Entre -2 y +2 para inclinacion
        float velocityY = -(velY + random.nextFloat() * 2f); // Siempre hacia abajo, velocidad variable
        
        return spawnEnemy(type, x, y, velocityX, velocityY, round);
    }
    
    // Spawn multiple enemigos del mismo tipo
    public void spawnMultiple(EnemyType type, int count, int velX, int velY, 
                             java.util.ArrayList<Enemy> enemies) {
        spawnMultiple(type, count, velX, velY, enemies, 1); // Round 1 por defecto
    }
    
    // Spawn multiple enemigos del mismo tipo con round especifico
    public void spawnMultiple(EnemyType type, int count, int velX, int velY, 
                             java.util.ArrayList<Enemy> enemies, int round) {
        for (int i = 0; i < count; i++) {
            Enemy enemy = spawnEnemyRandom(type, velX, velY, round);
            enemies.add(enemy);
        }
    }
}