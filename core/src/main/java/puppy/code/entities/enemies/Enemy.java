package puppy.code.entities.enemies;

import puppy.code.entities.GameObject;

public abstract class Enemy extends GameObject {
    protected int health;
    protected float speed;
    protected boolean destroyed;
    
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
}