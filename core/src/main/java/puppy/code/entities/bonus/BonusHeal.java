package puppy.code.entities.bonus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import puppy.code.entities.GameObject;
import puppy.code.interfaces.Bonus;

public class BonusHeal extends GameObject implements Bonus {
    private Texture bonusTexture;
    private float fallSpeed = 100f;
    private boolean collected = false;
    private int value = 200;
    private int healAmount = 2;
    
    private static final float BONUS_SIZE = 44f;
    
    public BonusHeal(float x, float y) {
        super(x, y, BONUS_SIZE, BONUS_SIZE);
        loadAssets();
    }
    
    private void loadAssets() {
        try {
            // TODO: Cambiar sprite temporal por el sprite final de Heal
            bonusTexture = new Texture(Gdx.files.internal("Game/Nave/Bonnus/Icons/BonnusHeal.png"));
        } catch (Exception e) {
            System.err.println("Error cargando sprite BonusHeal: " + e.getMessage());
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
    
    public void renderEffect(SpriteBatch batch, puppy.code.entities.Nave nave) {
    }
    
    public void activateEffect() {
    }
    
    public void deactivateEffect() {
    }
    
    public boolean isEffectActive() {
        return false;
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
        return "HEAL_BONUS";
    }
    
    public boolean isOffScreen() {
        return y + height < 0;
    }
    
    public int getHealAmount() {
        return healAmount;
    }
    
    public void dispose() {
        if (bonusTexture != null) {
            bonusTexture.dispose();
        }
    }
}
