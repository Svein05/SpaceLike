package puppy.code.entities.projectiles;

import com.badlogic.gdx.graphics.Texture;
import puppy.code.managers.ResourceManager;

public class ProjectileFactory {
    private ResourceManager resourceManager;
    
    public ProjectileFactory() {
        this.resourceManager = ResourceManager.getInstance();
    }
    
    public Projectile createProjectile(ProjectileType type, float x, float y, float velocityX, float velocityY) {
        switch (type) {
            case BULLET:
                Texture texture = resourceManager.getTexture(type.getTexturePath());
                return new Bullet(x, y, velocityX, velocityY, texture);
            case ENEMY_BALL:
                return new EnemyBall(x, y, velocityX, velocityY);
            default:
                Texture defaultTexture = resourceManager.getTexture(type.getTexturePath());
                return new Bullet(x, y, velocityX, velocityY, defaultTexture);
        }
    }

    public Projectile createProjectile(ProjectileType type, float x, float y) {
        return createProjectile(type, x, y, 0, type.getDefaultSpeed());
    }
}