package puppy.code.entities.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class MeteoriteEnemy extends Enemy {
    private Sprite sprite;
    private int xSpeed;
    private int ySpeed;

    public MeteoriteEnemy(int x, int y, int size, int xSpeed, int ySpeed, Texture texture) {
        super(x, y, size, size, 1, Math.max(Math.abs(xSpeed), Math.abs(ySpeed)));
        
        sprite = new Sprite(texture);
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        
        if (x - size < 0) this.x = x + size;
        if (x + size > 1920) this.x = x - size;
        if (y - size < 0) this.y = y + size;
        if (y + size > 1080) this.y = y - size;
        
        sprite.setPosition(this.x, this.y);
    }

    @Override
    public void update(float delta) {
        x += xSpeed;
        y += ySpeed;

        if (x + xSpeed < 0 || x + xSpeed + sprite.getWidth() > 1920)
            xSpeed *= -1;
        if (y + ySpeed < 0 || y + ySpeed + sprite.getHeight() > 1080)
            ySpeed *= -1;
            
        sprite.setPosition(x, y);
    }

    @Override
    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }

    @Override
    public Rectangle getBounds() {
        return sprite.getBoundingRectangle();
    }

    @Override
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            destroyed = true;
        }
    }

    @Override
    public int getScoreValue() {
        return 10;
    }

    @Override
    public int getXPValue() {
        return 5; // Los meteoritos dan 5 XP
    }
    
    public void checkCollision(MeteoriteEnemy other) {
        if (sprite.getBoundingRectangle().overlaps(other.sprite.getBoundingRectangle())) {
            if (xSpeed == 0) xSpeed = other.xSpeed / 2;
            if (other.xSpeed == 0) other.xSpeed = xSpeed / 2;
            xSpeed = -xSpeed;
            other.xSpeed = -other.xSpeed;
            
            if (ySpeed == 0) ySpeed = other.ySpeed / 2;
            if (other.ySpeed == 0) other.ySpeed = ySpeed / 2;
            ySpeed = -ySpeed;
            other.ySpeed = -other.ySpeed;
        }
    }
    
    public int getXSpeed() { return xSpeed; }
    public void setXSpeed(int xSpeed) { this.xSpeed = xSpeed; }
    public int getYSpeed() { return ySpeed; }
    public void setYSpeed(int ySpeed) { this.ySpeed = ySpeed; }
}