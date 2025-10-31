package puppy.code.entities.bonus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import puppy.code.entities.GameObject;
import puppy.code.entities.Nave;
import puppy.code.entities.projectiles.ProjectileType;
import puppy.code.interfaces.Bonus;
import puppy.code.interfaces.ShootingBehavior;
import puppy.code.managers.ProjectileManager;

public class BonusAutocannon extends GameObject implements Bonus, ShootingBehavior {
    // Assets del collectable (bonus cayendo)
    private TextureAtlas bonusAtlas;
    private Animation<TextureRegion> bonusAnimation;
    
    // Assets del efecto visual (cuando esta activo)
    private TextureAtlas effectAtlas;
    private Animation<TextureRegion> effectAnimation;
    
    private float animationTime = 0f;
    private float fallSpeed = 100f;
    
    private boolean collected = false;
    private boolean effectActive = false;
    private int value = 100;
    
    // Constantes de animacion
    private static final float ANIMATION_FRAME_DURATION = 0.15f;
    private static final float BONUS_SIZE = 44f;
    
    public BonusAutocannon(float x, float y) {
        super(x, y, BONUS_SIZE, BONUS_SIZE);
        loadAssets();
    }
    
    private void loadAssets() {
        try {
            // Assets para el collectable
            bonusAtlas = new TextureAtlas(Gdx.files.internal("Game/Nave/Bonnus/Icons/Bonnus01.atlas"));
            createBonusAnimation();
            
            // Assets para el efecto visual
            effectAtlas = new TextureAtlas(Gdx.files.internal("Game/Nave/Bonnus/Active/Ship Bonnus Cannon.atlas"));
            createEffectAnimation();
        } catch (Exception e) {
            System.err.println("Error cargando assets del BonusAutocannon: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void createBonusAnimation() {
        if (bonusAtlas == null) return;
        
        // Animacion del bonus cayendo
        TextureRegion[] bonusFrames = new TextureRegion[5];
        bonusFrames[0] = bonusAtlas.findRegion("DisparosBonnus - 0001");
        bonusFrames[1] = bonusAtlas.findRegion("DisparosBonnus - 0002");
        bonusFrames[2] = bonusAtlas.findRegion("DisparosBonnus - 0003");
        bonusFrames[3] = bonusAtlas.findRegion("DisparosBonnus - 0004");
        bonusFrames[4] = bonusAtlas.findRegion("DisparosBonnus - 0005");
        
        bonusAnimation = new Animation<>(ANIMATION_FRAME_DURATION, bonusFrames);
        bonusAnimation.setPlayMode(Animation.PlayMode.LOOP);
    }
    
    private void createEffectAnimation() {
        if (effectAtlas == null) return;
        
        // Animacion del efecto sobre la nave
        TextureRegion[] effectFrames = new TextureRegion[7];
        effectFrames[0] = effectAtlas.findRegion("Ship Auto Cannon 01");
        effectFrames[1] = effectAtlas.findRegion("Ship Auto Cannon 02");
        effectFrames[2] = effectAtlas.findRegion("Ship Auto Cannon 03");
        effectFrames[3] = effectAtlas.findRegion("Ship Auto Cannon 04");
        effectFrames[4] = effectAtlas.findRegion("Ship Auto Cannon 05");
        effectFrames[5] = effectAtlas.findRegion("Ship Auto Cannon 06");
        effectFrames[6] = effectAtlas.findRegion("Ship Auto Cannon 07");
        
        effectAnimation = new Animation<>(0.1f, effectFrames);
        effectAnimation.setPlayMode(Animation.PlayMode.LOOP);
    }
    
    @Override
    public void update(float delta) {
        animationTime += delta;
        
        if (!collected) {
            y -= fallSpeed * delta;
        }
    }
    
    @Override
    public void draw(SpriteBatch batch) {
        if (!collected && bonusAnimation != null) {
            TextureRegion currentFrame = bonusAnimation.getKeyFrame(animationTime);
            if (currentFrame != null) {
                batch.draw(currentFrame, x, y, width, height);
            }
        }
    }
    
    // Renderiza el efecto visual sobre la nave cuando el bonus esta activo
    public void renderEffect(SpriteBatch batch, Nave nave) {
        if (!effectActive || !nave.isShooting() || effectAnimation == null) {
            return;
        }
        
        TextureRegion effectFrame = effectAnimation.getKeyFrame(animationTime);
        if (effectFrame != null) {
            // Renderizar efecto sobre la nave
            batch.draw(effectFrame, nave.getX(), nave.getY(), nave.getWidth(), nave.getHeight());
        }
    }
    
    // -- Metodos para manejar el efecto--

    public void activateEffect() {
        effectActive = true;
        animationTime = 0f;
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
        return "AUTOCANNON_BONUS";
    }
    
    public boolean isOffScreen() {
        return y + height < 0;
    }
    
    // -- Implementacion de ShootingBehavior --
    
    @Override
    public void shoot(Nave nave, ProjectileManager projectileManager) {
        if (!effectActive) return;
        
        float effectiveSpeed = nave.getShipStats().getEffectiveProjectileSpeed(ProjectileType.BULLET);
        
        projectileManager.createProjectile(
            ProjectileType.BULLET,
            nave.getCenterShootX(), 
            nave.getCenterShootY(), 
            0, 
            effectiveSpeed
        );
        
        projectileManager.createProjectile(
            ProjectileType.BULLET,
            nave.getLeftWingShootX(), 
            nave.getLeftWingShootY(), 
            0, 
            effectiveSpeed
        );
        
        projectileManager.createProjectile(
            ProjectileType.BULLET,
            nave.getRightWingShootX(), 
            nave.getRightWingShootY(), 
            0, 
            effectiveSpeed
        );
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
        if (bonusAtlas != null) {
            bonusAtlas.dispose();
        }
        if (effectAtlas != null) {
            effectAtlas.dispose();
        }
    }
}