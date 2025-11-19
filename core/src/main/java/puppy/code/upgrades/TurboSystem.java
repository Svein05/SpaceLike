package puppy.code.upgrades;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class TurboSystem {
    private float currentStamina;
    private float maxStamina;
    private float drainRate;
    private float regenRate;
    private boolean canUseTurbo;
    
    private static final float BAR_WIDTH = 20f;
    private static final float BAR_HEIGHT = 200f;
    private static final float BAR_X = 1850f;
    private static final float BAR_Y = 440f;
    
    private ShapeRenderer shapeRenderer;
    
    public TurboSystem() {
        this.maxStamina = 100f;
        this.currentStamina = maxStamina;
        this.drainRate = 15f;
        this.regenRate = 20f;
        this.canUseTurbo = true;
        this.shapeRenderer = new ShapeRenderer();
    }
    
    public boolean canUseTurbo() {
        return canUseTurbo && currentStamina > 0;
    }
    
    public void consumeStamina(float delta) {
        currentStamina -= drainRate * delta;
        if (currentStamina < 0) {
            currentStamina = 0;
            canUseTurbo = false;
        }
    }
    
    public void regenerateStamina(float delta) {
        if (currentStamina < maxStamina) {
            currentStamina += regenRate * delta;
            if (currentStamina > maxStamina) {
                currentStamina = maxStamina;
            }
        }
        
        if (currentStamina > 10f) {
            canUseTurbo = true;
        }
    }
    
    public void render(SpriteBatch batch) {
        batch.end();
        
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 0.8f);
        shapeRenderer.rect(BAR_X - 2, BAR_Y - 2, BAR_WIDTH + 4, BAR_HEIGHT + 4);
        
        float fillHeight = (currentStamina / maxStamina) * BAR_HEIGHT;
        
        Color fillColor = getStaminaColor();
        shapeRenderer.setColor(fillColor);
        shapeRenderer.rect(BAR_X, BAR_Y, BAR_WIDTH, fillHeight);
        
        shapeRenderer.end();
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1f, 1f, 1f, 1f);
        shapeRenderer.rect(BAR_X - 2, BAR_Y - 2, BAR_WIDTH + 4, BAR_HEIGHT + 4);
        shapeRenderer.end();
        
        batch.begin();
    }
    
    private Color getStaminaColor() {
        float percentage = currentStamina / maxStamina;
        
        if (percentage > 0.6f) {
            return new Color(0.2f, 1f, 0.2f, 0.9f);
        } else if (percentage > 0.3f) {
            return new Color(1f, 0.8f, 0f, 0.9f);
        } else {
            return new Color(1f, 0.2f, 0.2f, 0.9f);
        }
    }
    
    public float getCurrentStamina() {
        return currentStamina;
    }
    
    public float getMaxStamina() {
        return maxStamina;
    }
    
    public float getStaminaPercentage() {
        return currentStamina / maxStamina;
    }
    
    public void dispose() {
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
    }
}
