package puppy.code.entities.projectiles;

import puppy.code.entities.GameObject;

public abstract class Projectile extends GameObject {
    public float velocityX, velocityY;
    protected int damage;
    protected boolean destroyed;
    
    public Projectile(float x, float y, float width, float height, float velocityX, float velocityY, int damage) {
        super(x, y, width, height);
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.damage = damage;
        this.destroyed = false;
    }
    
    public boolean isDestroyed() { return destroyed; }
    public int getDamage() { return damage; }
    public void destroy() { destroyed = true; }
    
    public void reset(float x, float y, float velocityX, float velocityY) {
        this.x = x;
        this.y = y;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.destroyed = false;
    }
}