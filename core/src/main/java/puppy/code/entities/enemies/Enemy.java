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
    public abstract int getXPValue(); // XP que otorga al ser derrotado
    public abstract String getDestructionSound(); // Sonido especifico de destruccion
    
    public boolean isDestroyed() { return destroyed; }
    public int getHealth() { return health; }
}