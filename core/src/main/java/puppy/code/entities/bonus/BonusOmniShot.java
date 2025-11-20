package puppy.code.entities.bonus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import puppy.code.entities.GameObject;
import puppy.code.entities.Nave;
import puppy.code.interfaces.Bonus;
import puppy.code.interfaces.ShootingBehavior;
import puppy.code.managers.ProjectileManager;

public class BonusOmniShot extends GameObject implements Bonus, ShootingBehavior {
    private Texture bonusTexture;
    private float fallSpeed = 100f;
    private boolean collected = false;
    private boolean effectActive = false;
    private int value = 150;
    
    private static final float BONUS_SIZE = 44f;
    
    public BonusOmniShot(float x, float y) {
        super(x, y, BONUS_SIZE, BONUS_SIZE);
        loadAssets();
    }
    
    private void loadAssets() {
        try {
            // TODO: Cambiar sprite temporal por el sprite final de OmniShot
            bonusTexture = new Texture(Gdx.files.internal("Game/Nave/Bonnus/Icons/BonnusOmniShot.png"));
        } catch (Exception e) {
            System.err.println("Error cargando sprite BonusOmniShot: " + e.getMessage());
        }
    }
    
    @Override
    public void update(float delta) {
        if (!collected) {
            y -= fallSpeed * delta;
        }
    }
    
    @Override
    public void draw(SpriteBatch batch) {
        if (!collected && bonusTexture != null) {
            batch.draw(bonusTexture, x, y, width, height);
        }
    }
    
    public void renderEffect(SpriteBatch batch, Nave nave) {
    }
    
    public void activateEffect() {
        effectActive = true;
    }
    
    public void deactivateEffect() {
        effectActive = false;
    }
    
    public boolean isEffectActive() {
        return effectActive;
    }
    
    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
    
    @Override
    public void collect() {
        collected = true;
    }
    
    @Override
    public boolean isCollected() {
        return collected;
    }
    
    @Override
    public int getValue() {
        return value;
    }
    
    @Override
    public String getType() {
        return "OMNISHOT_BONUS";
    }
    
    public boolean isOffScreen() {
        return y + height < 0;
    }
    
    @Override
    public void shoot(Nave nave, ProjectileManager projectileManager) {
        if (!effectActive) return;
        
        float effectiveSpeed = nave.getShipStats().getEffectiveProjectileSpeed(
            puppy.code.entities.projectiles.ProjectileType.BULLET
        );
        
        int totalBullets = 6;
        float angleStep = 360f / totalBullets;
        float startAngle = 90f + angleStep;
        
        float centerX = nave.getX() + nave.getWidth() / 2;
        float centerY = nave.getY() + nave.getHeight() / 2;
        
        for (int i = 0; i < 5; i++) {
            float angle = startAngle + (i * angleStep);
            float radians = (float) Math.toRadians(angle);
            
            float velocityX = (float) Math.cos(radians) * effectiveSpeed;
            float velocityY = (float) Math.sin(radians) * effectiveSpeed;
            
            projectileManager.createProjectile(
                puppy.code.entities.projectiles.ProjectileType.BULLET,
                centerX - 8,
                centerY - 8,
                velocityX,
                velocityY
            );
        }
    }
    
    @Override
    public boolean isActive() {
        return effectActive;
    }
    
    @Override
    public void activate() {
        activateEffect();
    }
    
    @Override
    public void deactivate() {
        deactivateEffect();
    }
    
    public void dispose() {
        if (bonusTexture != null) {
            bonusTexture.dispose();
        }
    }
}
