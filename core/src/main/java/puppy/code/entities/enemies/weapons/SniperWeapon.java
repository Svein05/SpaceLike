package puppy.code.entities.enemies.weapons;

import com.badlogic.gdx.math.Vector2;
import puppy.code.entities.enemies.Enemy;
import puppy.code.entities.enemies.SniperEnemy;
import puppy.code.entities.projectiles.SniperProjectile;
import puppy.code.interfaces.EnemyWeapon;
import puppy.code.managers.ProjectileManager;

public class SniperWeapon implements EnemyWeapon {
    private float cooldownTimer;
    private float fireRate;
    private static final float PROJECTILE_SPEED = 400f;

    public SniperWeapon(float fireRate) {
        this.fireRate = fireRate;
        this.cooldownTimer = 0f;
    }

    @Override
    public void shoot(Enemy enemy, ProjectileManager projectileManager) {
        if (!(enemy instanceof SniperEnemy)) {
            return;
        }
        
        SniperEnemy sniper = (SniperEnemy) enemy;
        
        if (sniper.getState() == SniperEnemy.SniperState.SHOOTING && canShoot()) {
            Vector2 direction = sniper.getAimDirection();
            
            if (direction.len() > 0) {
            float startX = enemy.getX() + enemy.getWidth() / 2 - 8;
            float startY = enemy.getY() - 8;
            
            System.out.println("[SNIPER SHOOT] Nave pos: (" + enemy.getX() + ", " + enemy.getY() + ") Size: " + enemy.getWidth() + "x" + enemy.getHeight());
            System.out.println("[SNIPER SHOOT] Proyectil origen: (" + startX + ", " + startY + ")");                SniperProjectile projectile = new SniperProjectile(
                    startX, 
                    startY, 
                    direction.cpy(), 
                    PROJECTILE_SPEED
                );
                
                projectileManager.getActiveProjectiles().add(projectile);
                cooldownTimer = fireRate;
            }
        }
    }

    @Override
    public void update(float delta) {
        if (cooldownTimer > 0) {
            cooldownTimer -= delta;
        }
    }

    @Override
    public boolean canShoot() {
        return cooldownTimer <= 0;
    }

    @Override
    public float getFireRate() {
        return fireRate;
    }
}
