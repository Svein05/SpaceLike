package puppy.code.weapons;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import puppy.code.interfaces.Weapon;
import puppy.code.managers.ProjectileManager;

public class BasicCannon implements Weapon {
    private float fireRate;
    private float lastFireTime;
    private int damage;
    
    public BasicCannon() {
        this.fireRate = 0.3f; // Disparos cada 0.3 segundos
        this.damage = 1;
        this.lastFireTime = 0;
    }
    
    @Override
    public boolean fire(float x, float y, ProjectileManager projectileManager) {
        if (canFire()) {
            projectileManager.createBullet(x, y, 0, 300); // Velocidad más lenta y hacia arriba
            lastFireTime = 0; // Resetear timer
            return true; // Disparó exitosamente
        }
        return false; // No pudo disparar (cooldown)
    }
    
    @Override
    public void update(float delta) {
        lastFireTime += delta; // Incrementar timer con delta
    }
    
    @Override
    public void draw(SpriteBatch batch) {
        // El cannon basico no tiene representacion visual propia
    }
    
    @Override
    public boolean canFire() {
        return lastFireTime >= fireRate; // Verificar si ha pasado suficiente tiempo
    }
    
    @Override
    public String getName() {
        return "Basic Cannon";
    }
    
    @Override
    public int getDamage() {
        return damage;
    }
    
    @Override
    public float getFireRate() {
        return fireRate;
    }
}