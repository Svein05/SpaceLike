package puppy.code.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import puppy.code.entities.projectiles.Projectile;
import puppy.code.entities.projectiles.ProjectileFactory;
import puppy.code.entities.projectiles.ProjectileType;
import puppy.code.entities.projectiles.Bullet;
import puppy.code.entities.enemies.Enemy;
import puppy.code.entities.Nave;

public class ProjectileManager {
    private ArrayList<Projectile> activeProjectiles;
    private Map<ProjectileType, ArrayList<Projectile>> pools;
    private static final int POOL_SIZE_PER_TYPE = 20;
    private ProjectileFactory factory;
    private ResourceManager resourceManager;
    private Nave nave;
    private ArrayList<Enemy> enemies;
    
    public ProjectileManager() {
        activeProjectiles = new ArrayList<>();
        pools = new HashMap<>();
        factory = new ProjectileFactory();
        resourceManager = ResourceManager.getInstance();
        
        for (ProjectileType type : ProjectileType.values()) {
            pools.put(type, new ArrayList<>());
            for (int i = 0; i < POOL_SIZE_PER_TYPE; i++) {
                Projectile projectile = factory.createProjectile(type, 0, 0, 0, 0);
                projectile.destroy();
                pools.get(type).add(projectile);
            }
        }
    }
    
    public void createProjectile(ProjectileType type, float x, float y, float velocityX, float velocityY) {
        Projectile projectile = getPooledProjectile(type);
        if (projectile != null) {
            projectile.reset(x, y, velocityX, velocityY);
            
            if (type == ProjectileType.BULLET && nave != null) {
                int bouncingLevel = nave.getShipStats().getBouncingBulletsLevel();
                projectile.setRemainingBounces(bouncingLevel);
            }
            
            activeProjectiles.add(projectile);
            
            resourceManager.getSound(type.getSoundPath()).play();
        }
    }
    
    public void createProjectile(ProjectileType type, float x, float y) {
        createProjectile(type, x, y, 0, type.getDefaultSpeed());
    }
    
    public void createBullet(float x, float y, float velocityX, float velocityY) {
        createProjectile(ProjectileType.BULLET, x, y, velocityX, velocityY);
    }
    
    private Projectile getPooledProjectile(ProjectileType type) {
        ArrayList<Projectile> pool = pools.get(type);
        for (int i = pool.size() - 1; i >= 0; i--) {
            Projectile projectile = pool.get(i);
            if (projectile.isDestroyed()) {
                pool.remove(i);
                return projectile;
            }
        }
        
        return factory.createProjectile(type, 0, 0, 0, 0);
    }
    
    private void returnToPool(Projectile projectile, ProjectileType type) {
        ArrayList<Projectile> pool = pools.get(type);
        if (pool.size() < POOL_SIZE_PER_TYPE) {
            projectile.destroy();
            pool.add(projectile);
        }
    }
    
    private ProjectileType getProjectileType(Projectile projectile) {
        String className = projectile.getClass().getSimpleName();
        
        if (className.equals("Bullet")) {
            return ProjectileType.BULLET;
        } else if (className.equals("EnemyBall")) {
            return ProjectileType.ENEMY_BALL;
        }
        
        return ProjectileType.BULLET;
    }
    
    public void update(float delta) {
        Iterator<Projectile> iterator = activeProjectiles.iterator();
        while (iterator.hasNext()) {
            Projectile projectile = iterator.next();
            
            if (projectile instanceof Bullet && nave != null && enemies != null) {
                Bullet bullet = (Bullet) projectile;
                bullet.setHomingEnabled(nave.isHomingEnabled());
                bullet.setHomingPrecision(nave.getShipStats().getHomingPrecision());
                bullet.setEnemies(enemies);
            }
            
            projectile.update(delta);
            
            if (isOutOfBounds(projectile)) {
                iterator.remove();
                returnToPool(projectile, getProjectileType(projectile));
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
            returnToPool(projectile, getProjectileType(projectile));
        }
    }
    
    public void clear() {
        for (Projectile projectile : activeProjectiles) {
            returnToPool(projectile, getProjectileType(projectile));
        }
        activeProjectiles.clear();
    }
    
    public void setNave(Nave nave) {
        this.nave = nave;
    }
    
    public void setEnemies(ArrayList<Enemy> enemies) {
        this.enemies = enemies;
    }
}