package puppy.code.entities.enemies.weapons;

import puppy.code.entities.enemies.Enemy;
import puppy.code.entities.projectiles.ProjectileType;
import puppy.code.interfaces.EnemyWeapon;
import puppy.code.managers.ProjectileManager;

public class EnemyBallWeapon implements EnemyWeapon {
    private float fireRate;
    private float timeSinceLastShot;
    private float projectileSpeed;

    public EnemyBallWeapon(float fireRate, float projectileSpeed) {
        this.fireRate = fireRate;
        this.projectileSpeed = projectileSpeed;
        this.timeSinceLastShot = 0f;
    }

    @Override
    public void shoot(Enemy enemy, ProjectileManager projectileManager) {
        if (canShoot()) {
            float centerX = enemy.getX() + enemy.getWidth() / 2;
            float bottomY = enemy.getY() - 16;
            
            projectileManager.createProjectile(
                ProjectileType.ENEMY_BALL,
                centerX - 8,
                bottomY,
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
