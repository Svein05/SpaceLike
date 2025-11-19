package puppy.code.entities.enemies.weapons;

import puppy.code.entities.enemies.Enemy;
import puppy.code.interfaces.EnemyWeapon;
import puppy.code.managers.ProjectileManager;

public class NoWeapon implements EnemyWeapon {
    @Override
    public void shoot(Enemy enemy, ProjectileManager projectileManager) {
    }

    @Override
    public void update(float delta) {
    }

    @Override
    public boolean canShoot() {
        return false;
    }

    @Override
    public float getFireRate() {
        return 0f;
    }
}
