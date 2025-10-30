package puppy.code.interfaces;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import puppy.code.managers.ProjectileManager;

public interface Weapon {
    boolean fire(float x, float y, ProjectileManager projectileManager);
    void update(float delta);
    void draw(SpriteBatch batch);
    boolean canFire();
    String getName();
    int getDamage();
    float getFireRate();
}