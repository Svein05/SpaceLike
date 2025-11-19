package puppy.code.entities.enemies.weapons;

import puppy.code.entities.enemies.Enemy;
import puppy.code.entities.projectiles.ProjectileType;
import puppy.code.interfaces.EnemyWeapon;
import puppy.code.managers.ProjectileManager;

public class BasicEnemyLaser implements EnemyWeapon {
    private float fireRate;
    private float timeSinceLastShot;
    private float projectileSpeed;

    public BasicEnemyLaser(float fireRate, float projectileSpeed) {
        this.fireRate = fireRate;
        this.projectileSpeed = projectileSpeed;
        this.timeSinceLastShot = 0f;
    }

    @Override
    public void shoot(Enemy enemy, ProjectileManager projectileManager) {
        if (canShoot()) {
            float centerX = enemy.getX() + enemy.getWidth() / 2;
            float centerY = enemy.getY();
            
            projectileManager.createProjectile(
                ProjectileType.BULLET,
                centerX,
                centerY,
                0,
                -projectileSpeed
            );
            
            timeSinceLastShot = 0f;
        }
    }

    @Override
    public void update(float delta) {
        timeSinceLastShot += delta;
    }

    @Override
    public boolean canShoot() {
        return timeSinceLastShot >= fireRate;
    }

    @Override
    public float getFireRate() {
        return fireRate;
    }
}
