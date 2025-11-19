package puppy.code.entities.enemies;

import puppy.code.entities.GameObject;
import puppy.code.interfaces.EnemyWeapon;
import puppy.code.interfaces.MovementBehavior;
import puppy.code.managers.ProjectileManager;

public abstract class Enemy extends GameObject {
    protected int health;
    protected float speed;
    protected boolean destroyed;
    
    protected EnemyWeapon weapon;
    protected MovementBehavior movementBehavior;
    
    public Enemy(float x, float y, float width, float height, int health, float speed) {
        super(x, y, width, height);
        this.health = health;
        this.speed = speed;
        this.destroyed = false;
    }
    
    public abstract void takeDamage(int damage);
    public abstract int getScoreValue();
    public abstract int getXPValue();
    public abstract String getDestructionSound();
    
    public boolean isDestroyed() { return destroyed; }
    public int getHealth() { return health; }
    
    public void setWeapon(EnemyWeapon weapon) {
        this.weapon = weapon;
    }
    
    public void setMovementBehavior(MovementBehavior behavior) {
        this.movementBehavior = behavior;
    }
    
    public EnemyWeapon getWeapon() {
        return weapon;
    }
    
    public MovementBehavior getMovementBehavior() {
        return movementBehavior;
    }
    
    public void performShoot(ProjectileManager projectileManager) {
        if (weapon != null && weapon.canShoot()) {
            weapon.shoot(this, projectileManager);
        }
    }
    
    public void updateWeapon(float delta) {
        if (weapon != null) {
            weapon.update(delta);
        }
    }
}