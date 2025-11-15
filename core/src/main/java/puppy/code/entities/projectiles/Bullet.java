package puppy.code.entities.projectiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import puppy.code.entities.enemies.MeteoriteEnemy;

public class Bullet extends Projectile {
    private Sprite spr;
    
    public Bullet(float x, float y, float xSpeed, float ySpeed, Texture tx) {
        super(x, y, 8, 16, xSpeed, ySpeed, ProjectileType.BULLET.getDamage());
        spr = new Sprite(tx);
        spr.setPosition(x, y);
    }
    
    @Override
    public void reset(float x, float y, float velocityX, float velocityY) {
        super.reset(x, y, velocityX, velocityY);
        spr.setPosition(x, y);
    }
    
    @Override
    public void update(float delta) {
        x += velocityX * delta;
        y += velocityY * delta;
        spr.setPosition(x, y);
        
        // Solo destruir si sale completamente de la pantalla
        if (x < -width || x > Gdx.graphics.getWidth() + width) {
            destroyed = true;
        }
        if (y < -height || y > Gdx.graphics.getHeight() + height) {
            destroyed = true;
        }
    }
    
    @Override
    public void draw(SpriteBatch batch) {
        spr.draw(batch);
    }
    
    @Override
    public Rectangle getBounds() {
        return spr.getBoundingRectangle();
    }
    
    public boolean checkCollision(MeteoriteEnemy b2) {
        if (spr.getBoundingRectangle().overlaps(b2.getBounds())) {
            this.destroyed = true;
            return true;
        }
        return false;
    }
}