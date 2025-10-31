package puppy.code.weapons;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import puppy.code.interfaces.Weapon;
import puppy.code.managers.ProjectileManager;

public class BasicCannon implements Weapon {
    private float fireRate;
    private float lastFireTime;
    private int damage;
    
    public BasicCannon() {
        this.fireRate = puppy.code.entities.projectiles.ProjectileType.BULLET.getFireRate();
        this.damage = 1;
        this.lastFireTime = 0;
    }
    
    @Override
    public boolean fire(float x, float y, ProjectileManager projectileManager) {
        if (canFire()) {
            projectileManager.createProjectile(puppy.code.entities.projectiles.ProjectileType.BULLET, x, y, 0, 
                puppy.code.entities.projectiles.ProjectileType.BULLET.getDefaultSpeed());
            lastFireTime = 0;
            return true;
        }
        return false;
    }
    
    @Override
    public void update(float delta) {
        lastFireTime += delta;
    }
    
    @Override
    public void draw(SpriteBatch batch) {}
    
    @Override
    public boolean canFire() {
        return lastFireTime >= fireRate;
    }
    
    @Override
    public String getName() {
        return "Basic Cannon";
    }
    
    @Override
    public int getDamage() {
        return damage;
    }
    
    @Override
    public float getFireRate() {
        return fireRate;
    }
}