package puppy.code.entities.enemies.weapons;

import puppy.code.entities.enemies.ChargerEnemy;
import puppy.code.entities.enemies.Enemy;
import puppy.code.entities.projectiles.ProjectileType;
import puppy.code.interfaces.EnemyWeapon;
import puppy.code.managers.ProjectileManager;

public class ChargerWeapon implements EnemyWeapon {
    private float fireRate;
    private float timeSinceLastShot;
    private float projectileSpeed;
    
    public ChargerWeapon(float fireRate, float projectileSpeed) {
        this.fireRate = fireRate;
        this.projectileSpeed = projectileSpeed;
        this.timeSinceLastShot = 0;
    }
    
    @Override
    public void shoot(Enemy enemy, ProjectileManager projectileManager) {
        if (!(enemy instanceof ChargerEnemy)) {
            return;
        }
        
        ChargerEnemy charger = (ChargerEnemy) enemy;
        
        if (charger.getState() == ChargerEnemy.ChargerState.CHARGING && canShoot()) {
            float centerX = enemy.getX() + enemy.getWidth() / 2;
            float centerY = enemy.getY() + enemy.getHeight() / 2;
            
            projectileManager.createProjectile(
                ProjectileType.ENEMY_BALL,
                centerX - 8,
                centerY - 8,
                projectileSpeed,
                0
            );
            
            projectileManager.createProjectile(
                ProjectileType.ENEMY_BALL,
                centerX - 8,
                centerY - 8,
                -projectileSpeed,
                0
            );
            
            timeSinceLastShot = 0;
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
