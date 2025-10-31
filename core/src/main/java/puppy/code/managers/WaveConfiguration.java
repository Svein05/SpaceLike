package puppy.code.managers;

import java.util.ArrayList;
import puppy.code.entities.enemies.EnemyType;

public class WaveConfiguration {
    private int round;
    private ArrayList<EnemySpawnData> enemySpawns;
    private int baseVelocityX;
    private int baseVelocityY;
    
    public WaveConfiguration(int round) {
        this.round = round;
        this.enemySpawns = new ArrayList<>();
        this.baseVelocityX = 1;
        this.baseVelocityY = 1;
    }
    
    public void addEnemySpawn(EnemyType type, int count) {
        enemySpawns.add(new EnemySpawnData(type, count));
    }
    
    public int getRound() { return round; }
    public ArrayList<EnemySpawnData> getEnemySpawns() { return enemySpawns; }
    public int getBaseVelocityX() { return baseVelocityX; }
    public int getBaseVelocityY() { return baseVelocityY; }
    
    public void setBaseVelocity(int velX, int velY) {
        this.baseVelocityX = velX;
        this.baseVelocityY = velY;
    }
    
    public static class EnemySpawnData {
        public final EnemyType type;
        public final int count;
        
        public EnemySpawnData(EnemyType type, int count) {
            this.type = type;
            this.count = count;
        }
    }
}