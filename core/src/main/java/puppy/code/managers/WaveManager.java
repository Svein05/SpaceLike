package puppy.code.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import puppy.code.entities.enemies.Enemy;
import puppy.code.entities.enemies.EnemyType;

public class WaveManager {
    private final EnemySpawner enemySpawner;
    private final Map<Integer, WaveConfiguration> waveConfigurations;
    private int currentRound;
    private boolean waveActive;
    
    public WaveManager() {
        this.enemySpawner = new EnemySpawner();
        this.waveConfigurations = new HashMap<>();
        this.currentRound = 1;
        this.waveActive = false;
        
        initializeWaveConfigurations();
    }
    
    private void initializeWaveConfigurations() {
        WaveConfiguration wave1 = new WaveConfiguration(1);
        wave1.addEnemySpawn(EnemyType.METEORITE, 3);
        wave1.setBaseVelocity(2, 3);
        waveConfigurations.put(1, wave1);
        
        WaveConfiguration wave2 = new WaveConfiguration(2);
        wave2.addEnemySpawn(EnemyType.METEORITE, 5);
        wave2.setBaseVelocity(2, 3);
        waveConfigurations.put(2, wave2);
        
        WaveConfiguration wave3 = new WaveConfiguration(3);
        wave3.addEnemySpawn(EnemyType.METEORITE, 7);
        wave3.setBaseVelocity(3, 4);
        waveConfigurations.put(3, wave3);
        
        WaveConfiguration wave4 = new WaveConfiguration(4);
        wave4.addEnemySpawn(EnemyType.METEORITE, 10);
        wave4.setBaseVelocity(3, 4);
        waveConfigurations.put(4, wave4);
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
        WaveConfiguration config = waveConfigurations.get(round);
        
        if (config == null) {
            config = createDynamicWaveConfiguration(round);
        }
        
        return config;
    }
    
    private WaveConfiguration createDynamicWaveConfiguration(int round) {
        WaveConfiguration config = new WaveConfiguration(round);
        
        int meteoriteCount = Math.min(3 + (round * 2), 25);
        
        config.addEnemySpawn(EnemyType.METEORITE, meteoriteCount);
        
        int baseVelX = Math.min(2 + (round / 3), 4);
        int baseVelY = Math.min(3 + (round / 3), 5);
        config.setBaseVelocity(baseVelX, baseVelY);
        
        return config;
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