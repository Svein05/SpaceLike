package puppy.code.entities.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Ball2 extends Enemy {
    private Sprite spr;
    public float velocityX, velocityY;

    public Ball2(int x, int y, int size, int xSpeed, int ySpeed, Texture tx) {
        super(x, y, size, size, 1, Math.max(Math.abs(xSpeed), Math.abs(ySpeed)));
        spr = new Sprite(tx);
        this.velocityX = xSpeed;
        this.velocityY = ySpeed;
        
        // Validar que borde de esfera no quede fuera
        if (x - size < 0) this.x = x + size;
        if (x + size > 1920) this.x = x - size;
        
        if (y - size < 0) this.y = y + size;
        if (y + size > 1080) this.y = y - size;
        
        spr.setPosition(this.x, this.y);
    }

    @Override
    public void update(float delta) {
        x += velocityX * delta * 60; // Convertir a frame-rate independiente
        y += velocityY * delta * 60;

        if (x + velocityX < 0 || x + velocityX + spr.getWidth() > 1920)
            velocityX *= -1;
        if (y + velocityY < 0 || y + velocityY + spr.getHeight() > 1080)
            velocityY *= -1;
            
        spr.setPosition(x, y);
    }

    @Override
    public void draw(SpriteBatch batch) {
        spr.draw(batch);
    }

    @Override
    public Rectangle getBounds() {
        return spr.getBoundingRectangle();
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
        return 10; // Ball2 (meteoritos) dan 10 puntos
    }

    @Override
    public int getXPValue() {
        return 5; // Ball2 (meteoritos) dan 5 XP
    }

    public void checkCollision(Ball2 b2) {
        if (spr.getBoundingRectangle().overlaps(b2.spr.getBoundingRectangle())) {
            // Rebote
            if (velocityX == 0) velocityX = velocityX + b2.velocityX / 2;
            if (b2.velocityX == 0) b2.velocityX = b2.velocityX + velocityX / 2;
            velocityX = -velocityX;
            b2.velocityX = -b2.velocityX;
            
            if (velocityY == 0) velocityY = velocityY + b2.velocityY / 2;
            if (b2.velocityY == 0) b2.velocityY = b2.velocityY + velocityY / 2;
            velocityY = -velocityY;
            b2.velocityY = -b2.velocityY;
        }
    }
}