package puppy.code.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import puppy.code.entities.projectiles.Projectile;
import puppy.code.entities.projectiles.ProjectileFactory;
import puppy.code.entities.projectiles.ProjectileType;

public class ProjectileManager {
    private ArrayList<Projectile> activeProjectiles;
    private Map<ProjectileType, ArrayList<Projectile>> pools;
    private static final int POOL_SIZE_PER_TYPE = 20;
    private ProjectileFactory factory;
    private ResourceManager resourceManager;
    
    public ProjectileManager() {
        activeProjectiles = new ArrayList<>();
        pools = new HashMap<>();
        factory = new ProjectileFactory();
        resourceManager = ResourceManager.getInstance();
        
        // Inicializar pools para cada tipo de proyectil
        for (ProjectileType type : ProjectileType.values()) {
            pools.put(type, new ArrayList<>());
            // Pre-crear algunos proyectiles para object pooling
            for (int i = 0; i < POOL_SIZE_PER_TYPE; i++) {
                Projectile projectile = factory.createProjectile(type, 0, 0, 0, 0);
                projectile.destroy(); // Marcarlo como destruido para que esté listo para reutilizar
                pools.get(type).add(projectile);
            }
        }
    }
    
    // Método genérico para crear cualquier tipo de proyectil
    public void createProjectile(ProjectileType type, float x, float y, float velocityX, float velocityY) {
        Projectile projectile = getPooledProjectile(type);
        if (projectile != null) {
            projectile.reset(x, y, velocityX, velocityY);
            activeProjectiles.add(projectile);
            
            // Reproducir sonido específico del tipo de proyectil
            resourceManager.getSound(type.getSoundPath()).play();
        }
    }
    
    // Método de conveniencia para crear proyectiles con velocidad por defecto
    public void createProjectile(ProjectileType type, float x, float y) {
        createProjectile(type, x, y, 0, type.getDefaultSpeed());
    }
    
    // Método legacy para mantener compatibilidad con BasicCannon
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
        
        // Si no hay proyectiles disponibles en el pool, crear uno nuevo
        return factory.createProjectile(type, 0, 0, 0, 0);
    }
    
    private void returnToPool(Projectile projectile, ProjectileType type) {
        ArrayList<Projectile> pool = pools.get(type);
        if (pool.size() < POOL_SIZE_PER_TYPE) {
            projectile.destroy(); // Marcarlo como destruido
            pool.add(projectile);
        }
    }
    
    // Determinar el tipo de proyectil basado en su clase
    private ProjectileType getProjectileType(Projectile projectile) {
        String className = projectile.getClass().getSimpleName().toUpperCase();
        try {
            return ProjectileType.valueOf(className);
        } catch (IllegalArgumentException e) {
            return ProjectileType.BULLET; // Default fallback
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
}