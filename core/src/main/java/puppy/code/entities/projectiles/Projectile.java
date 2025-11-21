package puppy.code.entities.projectiles;

import puppy.code.entities.GameObject;
import java.util.HashSet;

public abstract class Projectile extends GameObject {
    public float velocityX, velocityY;
    protected int damage;
    protected int baseDamage;
    protected boolean destroyed;
    protected int remainingBounces;
    protected boolean isBouncing;
    protected HashSet<Object> hitEnemies;
    protected float baseSpeed;
    protected boolean speedBoosted;
    protected boolean damageReduced;
    protected float age;
    
    public Projectile(float x, float y, float width, float height, float velocityX, float velocityY, int damage) {
        super(x, y, width, height);
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.damage = damage;
        this.baseDamage = damage;
        this.destroyed = false;
        this.remainingBounces = 0;
        this.isBouncing = false;
        this.hitEnemies = new HashSet<>();
        this.baseSpeed = (float) Math.sqrt(velocityX * velocityX + velocityY * velocityY);
        this.speedBoosted = false;
        this.damageReduced = false;
        this.age = 0f;
    }
    
    public boolean isDestroyed() { return destroyed; }
    public int getDamage() { return damage; }
    public float getAge() { return age; }
    public void destroy() { destroyed = true; }
    
    public int getRemainingBounces() { return remainingBounces; }
    public void setRemainingBounces(int bounces) { this.remainingBounces = Math.max(0, bounces); }
    public boolean isBouncing() { return isBouncing; }
    public void setIsBouncing(boolean bouncing) { this.isBouncing = bouncing; }
    public HashSet<Object> getHitEnemies() { return hitEnemies; }
    public void addHitEnemy(Object enemy) { hitEnemies.add(enemy); }
    public boolean hasHitEnemy(Object enemy) { return hitEnemies.contains(enemy); }
    
    public void consumeBounce() {
        if (remainingBounces > 0) {
            remainingBounces--;
        }
    }
    
    public void redirectTo(float targetX, float targetY, float speed) {
        float dx = targetX - (x + width / 2);
        float dy = targetY - (y + height / 2);
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        
        if (distance > 0) {
            float bounceSpeed = speedBoosted ? speed : speed * 3.0f;
            if (!speedBoosted) {
                speedBoosted = true;
            }
            
            if (!damageReduced) {
                damage = Math.max(1, baseDamage / 2);
                damageReduced = true;
            }
            
            velocityX = (dx / distance) * bounceSpeed;
            velocityY = (dy / distance) * bounceSpeed;
        }
        isBouncing = true;
    }
    
    public void reset(float x, float y, float velocityX, float velocityY) {
        this.x = x;
        this.y = y;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.destroyed = false;
        this.remainingBounces = 0;
        this.isBouncing = false;
        this.hitEnemies.clear();
        this.baseSpeed = (float) Math.sqrt(velocityX * velocityX + velocityY * velocityY);
        this.speedBoosted = false;
        this.damage = baseDamage;
        this.damageReduced = false;
        this.age = 0f;
    }
}