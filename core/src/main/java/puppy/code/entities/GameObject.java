package puppy.code.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public abstract class GameObject {
    protected float x, y;
    protected float width, height;
    
    public GameObject(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public abstract void update(float delta);
    public abstract void draw(SpriteBatch batch);
    public abstract Rectangle getBounds();
    
    public float getX() { return x; }
    public float getY() { return y; }
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
}