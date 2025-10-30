package puppy.code.managers;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import puppy.code.entities.projectiles.Bullet;
import puppy.code.entities.projectiles.Projectile;

public class ProjectileManager {
    private ArrayList<Projectile> activeProjectiles;
    private ArrayList<Projectile> projectilePool;
    private static final int POOL_SIZE = 50;
    private Texture bulletTexture;
    
    public ProjectileManager() {
        activeProjectiles = new ArrayList<>();
        projectilePool = new ArrayList<>();
        bulletTexture = new Texture(Gdx.files.internal("Rocket2.png"));
        
        // Pre-crear proyectiles para object pooling
        for (int i = 0; i < POOL_SIZE; i++) {
            projectilePool.add(new Bullet(0, 0, 0, 0, bulletTexture));
        }
    }
    
    public void createBullet(float x, float y, float velocityX, float velocityY) {
        Projectile bullet = getPooledProjectile();
        if (bullet != null) {
            bullet.reset(x, y, velocityX, velocityY);
            activeProjectiles.add(bullet);
        }
    }
    
    private Projectile getPooledProjectile() {
        if (!projectilePool.isEmpty()) {
            return projectilePool.remove(projectilePool.size() - 1);
        }
        return null; // Pool vacio, no crear mas
    }
    
    public void returnToPool(Projectile projectile) {
        if (projectilePool.size() < POOL_SIZE) {
            projectilePool.add(projectile);
        }
    }
    
    public void update(float delta) {
        Iterator<Projectile> iterator = activeProjectiles.iterator();
        while (iterator.hasNext()) {
            Projectile projectile = iterator.next();
            projectile.update(delta);
            
            // Remover proyectiles fuera de pantalla
            if (isOutOfBounds(projectile)) {
                iterator.remove();
                returnToPool(projectile);
            }
        }
    }
    
    public void render(SpriteBatch batch) {
        for (Projectile projectile : activeProjectiles) {
            projectile.draw(batch);
        }
    }
    
    private boolean isOutOfBounds(Projectile projectile) {
        return projectile.getY() > 1080 || projectile.getY() < 0 || 
               projectile.getX() > 1920 || projectile.getX() < 0;
    }
    
    public ArrayList<Projectile> getActiveProjectiles() {
        return activeProjectiles;
    }
    
    public void removeProjectile(Projectile projectile) {
        if (activeProjectiles.remove(projectile)) {
            returnToPool(projectile);
        }
    }
    
    public void clear() {
        for (Projectile projectile : activeProjectiles) {
            returnToPool(projectile);
        }
        activeProjectiles.clear();
    }
}