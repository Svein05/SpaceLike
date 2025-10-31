package puppy.code.weapons;

import puppy.code.interfaces.Weapon;
import puppy.code.managers.ProjectileManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class LaserWeapon implements Weapon {
    private float fireRate;
    private float lastFireTime;

    public LaserWeapon() {
        this.fireRate = 0.1f;
        this.lastFireTime = 0;
    }

    @Override
    public boolean fire(float x, float y, ProjectileManager projectileManager) {
        if (canFire()) {
            projectileManager.createBullet(x, y, 0, 800);
            lastFireTime = 0;
            return true;
        }
        return false;
    }

    @Override
    public boolean canFire() {
        return lastFireTime >= fireRate;
    }

    @Override
    public void update(float delta) {
        lastFireTime += delta;
    }

    @Override
    public void draw(SpriteBatch batch) {}

    @Override
    public String getName() {
        return "Laser";
    }

    @Override
    public int getDamage() {
        return 2;
    }

    @Override
    public float getFireRate() {
        return fireRate;
    }
}