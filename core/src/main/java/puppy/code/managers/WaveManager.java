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
        // Round 1: Pocos asteroides para empezar
        WaveConfiguration wave1 = new WaveConfiguration(1);
        wave1.addEnemySpawn(EnemyType.METEORITE, 3);
        wave1.setBaseVelocity(2, 3);
        waveConfigurations.put(1, wave1);
        
        // Round 2: Incremento moderado
        WaveConfiguration wave2 = new WaveConfiguration(2);
        wave2.addEnemySpawn(EnemyType.METEORITE, 5);
        wave2.setBaseVelocity(2, 3);
        waveConfigurations.put(2, wave2);
        
        // Round 3: Mas asteroides
        WaveConfiguration wave3 = new WaveConfiguration(3);
        wave3.addEnemySpawn(EnemyType.METEORITE, 7);
        wave3.setBaseVelocity(3, 4);
        waveConfigurations.put(3, wave3);
        
        // Round 4: Incremento significativo
        WaveConfiguration wave4 = new WaveConfiguration(4);
        wave4.addEnemySpawn(EnemyType.METEORITE, 10);
        wave4.setBaseVelocity(3, 4);
        waveConfigurations.put(4, wave4);
        
        // Round 5+: Configuracion escalada dinamicamente
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
                round // Pasar el round para vida escalable
            );
        }
        
        return waveEnemies;
    }
    
    private WaveConfiguration getWaveConfiguration(int round) {
        WaveConfiguration config = waveConfigurations.get(round);
        
        if (config == null) {
            // Configuracion dinamica para rounds altos
            config = createDynamicWaveConfiguration(round);
        }
        
        return config;
    }
    
    private WaveConfiguration createDynamicWaveConfiguration(int round) {
        WaveConfiguration config = new WaveConfiguration(round);
        
        // Escalado progresivo pero razonable de asteroides
        // Formula: 3 base + 2 por ronda, maximo 25 para evitar sobrecarga
        int meteoriteCount = Math.min(3 + (round * 2), 25);
        
        // Eliminar otros tipos de enemigos por ahora, solo asteroides
        config.addEnemySpawn(EnemyType.METEORITE, meteoriteCount);
        
        // Velocidad base escalada moderadamente
        // Formula: 2-4 velocidad base, incremento cada 3 rondas
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
    
    // Metodo para obtener info de la wave actual
    public WaveConfiguration getCurrentWaveInfo() {
        return getWaveConfiguration(currentRound);
    }
}