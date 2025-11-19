package puppy.code.managers;

import java.util.ArrayList;
import java.util.Iterator;
import puppy.code.entities.enemies.Enemy;

public class EnemyManager {
    private ArrayList<Enemy> activeEnemies;
    private WaveManager waveManager;
    
    public EnemyManager() {
        this.activeEnemies = new ArrayList<>();
        this.waveManager = new WaveManager();
    }
    
    public void startWave(int round) {
        activeEnemies.clear();
        
        ArrayList<Enemy> waveEnemies = waveManager.startWave(round);
        activeEnemies.addAll(waveEnemies);
    }
    
    public void update(float delta) {
        Iterator<Enemy> iterator = activeEnemies.iterator();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            
            enemy.update(delta);
            
            if (enemy.isDestroyed()) {
                if (enemy instanceof puppy.code.entities.enemies.MeteoriteEnemy) {
                    puppy.code.entities.enemies.MeteoriteEnemy asteroid = (puppy.code.entities.enemies.MeteoriteEnemy) enemy;
                    if (!asteroid.isExploding()) {
                        iterator.remove();
                    }
                } else {
                    iterator.remove();
                }
            }
        }
        
        if (activeEnemies.isEmpty() && waveManager.isWaveActive()) {
            waveManager.endWave();
        }
    }
    
    public void updateEnemyShooting(ProjectileManager projectileManager) {
        for (Enemy enemy : activeEnemies) {
            enemy.performShoot(projectileManager);
        }
    }
    
    public void render(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        for (Enemy enemy : activeEnemies) {
            enemy.draw(batch);
        }
    }
    
    public ArrayList<Enemy> getActiveEnemies() {
        return activeEnemies;
    }
    
    public boolean isWaveComplete() {
        return activeEnemies.isEmpty() && !waveManager.isWaveActive();
    }
    
    public WaveConfiguration getCurrentWaveInfo() {
        return waveManager.getCurrentWaveInfo();
    }
    
    public int getRemainingEnemyCount() {
        return activeEnemies.size();
    }
    
    public void clearAllEnemies() {
        activeEnemies.clear();
        waveManager.endWave();
    }
}