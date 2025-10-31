package puppy.code.entities.projectiles;

import com.badlogic.gdx.graphics.Texture;
import puppy.code.managers.ResourceManager;

public class ProjectileFactory {
    private ResourceManager resourceManager;
    
    public ProjectileFactory() {
        this.resourceManager = ResourceManager.getInstance();
    }
    
    public Projectile createProjectile(ProjectileType type, float x, float y, float velocityX, float velocityY) {
        Texture texture = resourceManager.getTexture(type.getTexturePath());
        
        switch (type) {
            case BULLET:
                return new Bullet(x, y, velocityX, velocityY, texture);
            // TODO: Añadir cuando tengas los assets:
            // case LASER:
            //     return new Laser(x, y, velocityX, velocityY, texture);
            // case MISSILE:
            //     return new Missile(x, y, velocityX, velocityY, texture);
            default:
                return new Bullet(x, y, velocityX, velocityY, texture);
        }
    }
    
    // Factory method para crear proyectiles con configuracion por defecto
    public Projectile createProjectile(ProjectileType type, float x, float y) {
        return createProjectile(type, x, y, 0, type.getDefaultSpeed());
    }
}