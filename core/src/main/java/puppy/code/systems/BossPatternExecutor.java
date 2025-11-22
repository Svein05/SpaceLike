package puppy.code.systems;

import com.badlogic.gdx.utils.Array;
import puppy.code.entities.enemies.BossEnemy;
import puppy.code.entities.enemies.BossPatternType;
import puppy.code.entities.projectiles.BossBullet;

public class BossPatternExecutor {
    
    private Array<BossBullet> bossBullets;
    private static final float BULLET_SPEED = 250f;
    
    public BossPatternExecutor() {
        this.bossBullets = new Array<>();
    }
    
    public void executePattern(BossEnemy boss, BossPatternType pattern) {
        switch (pattern) {
            case RADIAL_ROTATING:
                executeRadialRotating(boss);
                break;
            case SPIRAL:
                executeSpiral(boss);
                break;
            case WAVE:
                executeWave(boss);
                break;
            case RANDOM_BURST:
                executeRandomBurst(boss);
                break;
        }
    }
    
    private void executeRadialRotating(BossEnemy boss) {
        float centerX = boss.getCenterX();
        float centerY = boss.getCenterY();
        int bulletCount = 12;
        float angleOffset = boss.getPatternRotation();
        
        for (int i = 0; i < bulletCount; i++) {
            float angle = (360f / bulletCount) * i + angleOffset;
            float radians = (float) Math.toRadians(angle);
            
            float velocityX = (float) Math.cos(radians) * BULLET_SPEED;
            float velocityY = (float) Math.sin(radians) * BULLET_SPEED;
            
            BossBullet bullet = new BossBullet(centerX, centerY, velocityX, velocityY);
            bossBullets.add(bullet);
        }
        
        boss.incrementPatternRotation(15f);
    }
    
    private void executeSpiral(BossEnemy boss) {
        // TODO: Etapa futura
    }
    
    private void executeWave(BossEnemy boss) {
        // TODO: Etapa futura
    }
    
    private void executeRandomBurst(BossEnemy boss) {
        // TODO: Etapa futura
    }
    
    public void update(float delta) {
        for (int i = bossBullets.size - 1; i >= 0; i--) {
            BossBullet bullet = bossBullets.get(i);
            bullet.update(delta);
            
            if (bullet.isDestroyed()) {
                bossBullets.removeIndex(i);
            }
        }
    }
    
    public Array<BossBullet> getBossBullets() {
        return bossBullets;
    }
    
    public void clear() {
        bossBullets.clear();
    }
}
