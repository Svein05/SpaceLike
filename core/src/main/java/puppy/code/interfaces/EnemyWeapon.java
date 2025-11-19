package puppy.code.interfaces;

import puppy.code.entities.enemies.Enemy;
import puppy.code.managers.ProjectileManager;

public interface EnemyWeapon {
    void shoot(Enemy enemy, ProjectileManager projectileManager);
    void update(float delta);
    boolean canShoot();
    float getFireRate();
}
