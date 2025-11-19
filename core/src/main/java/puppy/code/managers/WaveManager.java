package puppy.code.managers;

import java.util.ArrayList;
import puppy.code.entities.enemies.Enemy;
import puppy.code.entities.enemies.EnemyType;

public class WaveManager {
    private final EnemySpawner enemySpawner;
    private int currentRound;
    private boolean waveActive;
    
    public WaveManager() {
        this.enemySpawner = new EnemySpawner();
        this.currentRound = 1;
        this.waveActive = false;
    }
    
    public ArrayList<Enemy> startWave(int round) {
        this.currentRound = round;
        this.waveActive = true;
        
        WaveConfiguration config = getWaveConfiguration(round);
        ArrayList<Enemy> waveEnemies = new ArrayList<>();
        
        for (WaveConfiguration.EnemySpawnData spawnData : config.getEnemySpawns()) {
            enemySpawner.spawnMultiple(
                spawnData.type, 
                spawnData.count,
                config.getBaseVelocityX(),
                config.getBaseVelocityY(),
                waveEnemies,
                round
            );
        }
        
        return waveEnemies;
    }
    
    private WaveConfiguration getWaveConfiguration(int round) {
        return createDynamicWaveConfiguration(round);
    }
    
    private WaveConfiguration createDynamicWaveConfiguration(int round) {
        WaveConfiguration config = new WaveConfiguration(round);
        
        int meteoriteCount = Math.min(3 + ((round - 1) * 2), 15);
        config.addEnemySpawn(EnemyType.METEORITE, meteoriteCount);
        
        if (round >= 2) {
            int enemyShip1Count = calculateEnemyShip1Count(round);
            config.addEnemySpawn(EnemyType.ENEMYSHIP1, enemyShip1Count);
        }
        
        int baseVelX = Math.min(2 + (round / 3), 4);
        int baseVelY = Math.min(3 + (round / 3), 5);
        config.setBaseVelocity(baseVelX, baseVelY);
        
        return config;
    }
    
    private int calculateEnemyShip1Count(int round) {
        double x = round - 2;
        double count = -0.15 * x * x + 2.5 * x + 1;
        
        int result = (int) Math.round(count);
        return Math.max(1, Math.min(result, 8));
    }
    
    public void endWave() {
        this.waveActive = false;
    }
    
    public boolean isWaveActive() {
        return waveActive;
    }
    
    public int getCurrentRound() {
        return currentRound;
    }
    
    public WaveConfiguration getCurrentWaveInfo() {
        return getWaveConfiguration(currentRound);
    }
}