package puppy.code.entities.projectiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import puppy.code.entities.enemies.MeteoriteEnemy;
import puppy.code.entities.enemies.Enemy;
import java.util.ArrayList;

public class Bullet extends Projectile {
    private Sprite spr;
    private float lifeTime;
    private static final float TRACKING_DELAY = 0.2f;
    private float homingPrecision;
    private boolean homingEnabled;
    private ArrayList<Enemy> enemies;
    private Enemy targetEnemy;
    
    public Bullet(float x, float y, float xSpeed, float ySpeed, Texture tx) {
        super(x, y, 8, 16, xSpeed, ySpeed, ProjectileType.BULLET.getDamage());
        spr = new Sprite(tx);
        spr.setPosition(x, y);
        this.lifeTime = 0f;
        this.homingPrecision = 0f;
        this.homingEnabled = false;
        this.enemies = null;
        this.targetEnemy = null;
    }
    
    @Override
    public void reset(float x, float y, float velocityX, float velocityY) {
        super.reset(x, y, velocityX, velocityY);
        spr.setPosition(x, y);
        spr.setRotation(0);
        this.lifeTime = 0f;
        this.targetEnemy = null;
    }
    
    @Override
    public void update(float delta) {
        lifeTime += delta;
        
        if (isBouncing && targetEnemy != null && !targetEnemy.isDestroyed()) {
            applyHomingToBounce(targetEnemy, delta);
        } else if (homingEnabled && homingPrecision > 0 && lifeTime >= TRACKING_DELAY && enemies != null) {
            if (targetEnemy == null || targetEnemy.isDestroyed()) {
                targetEnemy = findClosestEnemy();
            }
            
            if (targetEnemy != null && !targetEnemy.isDestroyed()) {
                applyHoming(targetEnemy, delta);
            }
        }
        
        x += velocityX * delta;
        y += velocityY * delta;
        spr.setPosition(x, y);
        
        float angle = (float) Math.toDegrees(Math.atan2(velocityY, velocityX)) - 90;
        spr.setRotation(angle);
        
        if (x < -width || x > Gdx.graphics.getWidth() + width) {
            destroyed = true;
        }
        if (y < -height || y > Gdx.graphics.getHeight() + height) {
            destroyed = true;
        }
    }
    
    private void applyHomingToBounce(Enemy target, float delta) {
        float targetX = target.getX() + target.getWidth() / 2;
        float targetY = target.getY() + target.getHeight() / 2;
        float centerX = x + width / 2;
        float centerY = y + height / 2;
        
        float dx = targetX - centerX;
        float dy = targetY - centerY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        
        if (distance > 0) {
            float currentSpeed = (float) Math.sqrt(velocityX * velocityX + velocityY * velocityY);
            velocityX = (dx / distance) * currentSpeed;
            velocityY = (dy / distance) * currentSpeed;
        }
    }
    
    private Enemy findClosestEnemy() {
        if (enemies == null || enemies.isEmpty()) return null;
        
        Enemy closest = null;
        float minDistance = Float.MAX_VALUE;
        
        for (Enemy enemy : enemies) {
            if (enemy.isDestroyed()) continue;
            
            float dx = (enemy.getX() + enemy.getWidth() / 2) - (x + width / 2);
            float dy = (enemy.getY() + enemy.getHeight() / 2) - (y + height / 2);
            float distance = dx * dx + dy * dy;
            
            if (distance < minDistance) {
                minDistance = distance;
                closest = enemy;
            }
        }
        
        return closest;
    }
    
    private void applyHoming(Enemy target, float delta) {
        float targetX = target.getX() + target.getWidth() / 2;
        float targetY = target.getY() + target.getHeight() / 2;
        float centerX = x + width / 2;
        float centerY = y + height / 2;
        
        float dx = targetX - centerX;
        float dy = targetY - centerY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        
        if (distance > 0) {
            float targetDirX = dx / distance;
            float targetDirY = dy / distance;
            
            float currentSpeed = (float) Math.sqrt(velocityX * velocityX + velocityY * velocityY);
            float currentDirX = velocityX / currentSpeed;
            float currentDirY = velocityY / currentSpeed;
            
            float rotationSpeed = homingPrecision * 8f * delta;
            
            float newDirX = currentDirX + (targetDirX - currentDirX) * rotationSpeed;
            float newDirY = currentDirY + (targetDirY - currentDirY) * rotationSpeed;
            
            float newDirLength = (float) Math.sqrt(newDirX * newDirX + newDirY * newDirY);
            if (newDirLength > 0) {
                newDirX /= newDirLength;
                newDirY /= newDirLength;
            }
            
            velocityX = newDirX * currentSpeed;
            velocityY = newDirY * currentSpeed;
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
    
    public void setHomingEnabled(boolean enabled) {
        this.homingEnabled = enabled;
    }
    
    public void setHomingPrecision(float precision) {
        this.homingPrecision = Math.max(0f, Math.min(1f, precision));
    }
    
    public void setEnemies(ArrayList<Enemy> enemies) {
        this.enemies = enemies;
    }
    
    public void setTargetEnemy(Enemy target) {
        this.targetEnemy = target;
    }
}