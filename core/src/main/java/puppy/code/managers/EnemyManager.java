package puppy.code.managers;

import java.util.ArrayList;
import java.util.Iterator;
import puppy.code.entities.enemies.Enemy;

public class EnemyManager {
    private ArrayList<Enemy> activeEnemies;
    private WaveManager waveManager;
    private GameStateManager gameState;
    private int logCounter = 0; // Para controlar frecuencia de logs
    
    public EnemyManager() {
        this.activeEnemies = new ArrayList<>();
        this.waveManager = new WaveManager();
        this.gameState = GameStateManager.getInstance();
    }
    
    // Iniciar una nueva wave
    public void startWave(int round) {
        // Limpiar enemigos existentes
        activeEnemies.clear();
        
        // Generar nueva wave
        ArrayList<Enemy> waveEnemies = waveManager.startWave(round);
        activeEnemies.addAll(waveEnemies);
    }
    
    // Actualizar todos los enemigos activos
    public void update(float delta) {
        Iterator<Enemy> iterator = activeEnemies.iterator();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            
            // Actualizar el enemigo SIEMPRE (para que animaciones funcionen)
            enemy.update(delta);
            
            if (enemy.isDestroyed()) {
                // Para asteroides, verificar si termino la animacion de explosion
                if (enemy instanceof puppy.code.entities.enemies.MeteoriteEnemy) {
                    puppy.code.entities.enemies.MeteoriteEnemy asteroid = (puppy.code.entities.enemies.MeteoriteEnemy) enemy;
                    if (!asteroid.isExploding()) {
                        iterator.remove();
                    }
                } else {
                    // Otros tipos de enemigos se remueven inmediatamente
                    // El score y XP ya se otorgaron en CollisionSystem
                    iterator.remove();
                }
            }
        }
        
        // Verificar si la wave esta completa
        if (activeEnemies.isEmpty() && waveManager.isWaveActive()) {
            waveManager.endWave();
        }
    }
    
    // Renderizar todos los enemigos
    public void render(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        for (Enemy enemy : activeEnemies) {
            enemy.draw(batch);
        }
    }
    
    // Obtener lista de enemigos activos para colisiones
    public ArrayList<Enemy> getActiveEnemies() {
        return activeEnemies;
    }
    
    // Verificar si wave actual esta completa
    public boolean isWaveComplete() {
        return activeEnemies.isEmpty() && !waveManager.isWaveActive();
    }
    
    // Obtener informacion de wave actual
    public WaveConfiguration getCurrentWaveInfo() {
        return waveManager.getCurrentWaveInfo();
    }
    
    // Obtener numero de enemigos restantes
    public int getRemainingEnemyCount() {
        return activeEnemies.size();
    }
    
    // Limpiar todos los enemigos (para game over o reset)
    public void clearAllEnemies() {
        activeEnemies.clear();
        waveManager.endWave();
    }
}