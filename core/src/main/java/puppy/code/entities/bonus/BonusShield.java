package puppy.code.entities.bonus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import puppy.code.entities.GameObject;
import puppy.code.entities.Nave;
import puppy.code.interfaces.Bonus;

public class BonusShield extends GameObject implements Bonus {
    // Assets del collectable (bonus cayendo)
    private TextureAtlas bonusAtlas;
    private Animation<TextureRegion> bonusAnimation;
    
    // Assets del efecto visual (escudo activo)
    private TextureAtlas effectAtlas;
    private Animation<TextureRegion> effectAnimation;
    
    private float animationTime = 0f;
    private float fallSpeed = 100f;
    
    private boolean collected = false;
    private boolean effectActive = false;
    private int value = 150;
    
    // Constantes de animacion
    private static final float ANIMATION_FRAME_DURATION = 0.15f;
    private static final float BONUS_SIZE = 44f;
    
    public BonusShield(float x, float y) {
        super(x, y, BONUS_SIZE, BONUS_SIZE);
        loadAssets();
    }
    
    private void loadAssets() {
        try {
            // Assets para el collectable
            bonusAtlas = new TextureAtlas(Gdx.files.internal("Game/Nave/Bonnus/Icons/Bonnus02.atlas"));
            createBonusAnimation();
            
            // Assets para el efecto visual
            effectAtlas = new TextureAtlas(Gdx.files.internal("Game/Nave/Bonnus/Active/Ship Bonnus Shield.atlas"));
            createEffectAnimation();
        } catch (Exception e) {
            System.err.println("Error cargando assets del BonusShield: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void createBonusAnimation() {
        if (bonusAtlas == null) return;
        
        // Animacion del bonus cayendo
        TextureRegion[] bonusFrames = new TextureRegion[5];
        bonusFrames[0] = bonusAtlas.findRegion("Shield - 0001");
        bonusFrames[1] = bonusAtlas.findRegion("Shield - 0002");
        bonusFrames[2] = bonusAtlas.findRegion("Shield - 0003");
        bonusFrames[3] = bonusAtlas.findRegion("Shield - 0004");
        bonusFrames[4] = bonusAtlas.findRegion("Shield - 0005");
        
        bonusAnimation = new Animation<>(ANIMATION_FRAME_DURATION, bonusFrames);
        bonusAnimation.setPlayMode(Animation.PlayMode.LOOP);
    }
    
    private void createEffectAnimation() {
        if (effectAtlas == null) return;
        
        // Animacion del escudo sobre la nave
        TextureRegion[] effectFrames = new TextureRegion[6];
        effectFrames[0] = effectAtlas.findRegion("Ship Bonnus Shield - 0001");
        effectFrames[1] = effectAtlas.findRegion("Ship Bonnus Shield - 0002");
        effectFrames[2] = effectAtlas.findRegion("Ship Bonnus Shield - 0003");
        effectFrames[3] = effectAtlas.findRegion("Ship Bonnus Shield - 0004");
        effectFrames[4] = effectAtlas.findRegion("Ship Bonnus Shield - 0005");
        effectFrames[5] = effectAtlas.findRegion("Ship Bonnus Shield - 0006");
        
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
    
    // Renderiza el efecto del escudo encima de la nave
    public void renderShieldEffect(SpriteBatch batch, Nave nave) {
        if (!effectActive || effectAnimation == null) {
            return;
        }
        
        TextureRegion effectFrame = effectAnimation.getKeyFrame(animationTime);
        if (effectFrame != null) {
            float shieldScale = 1.1f;
            float shieldWidth = nave.getWidth() * shieldScale;
            float shieldHeight = nave.getHeight() * shieldScale;
            
            float shieldX = nave.getX() + (nave.getWidth() - shieldWidth) / 2f;
            float shieldY = nave.getY() + (nave.getHeight() - shieldHeight) / 2f;
            
            batch.draw(effectFrame, shieldX, shieldY, shieldWidth, shieldHeight);
        }
    }
    
    // -- Implementacion de la interfaz bonus --
    @Override
    public void renderEffect(SpriteBatch batch, Nave nave) {
        renderShieldEffect(batch, nave);
    }
    
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
        return "SHIELD_BONUS";
    }
    
    public boolean isOffScreen() {
        return y + height < 0;
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