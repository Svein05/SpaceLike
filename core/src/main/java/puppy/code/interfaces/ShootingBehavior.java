package puppy.code.interfaces;

import puppy.code.entities.Nave;
import puppy.code.managers.ProjectileManager;

public interface ShootingBehavior {
    void shoot(Nave nave, ProjectileManager projectileManager);
    boolean isActive();
    void activate();
    void deactivate();
}