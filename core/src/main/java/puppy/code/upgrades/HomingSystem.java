package puppy.code.upgrades;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class HomingSystem {
    private boolean enabled = true;
    private float precision = 0.0f;
    
    private ShapeRenderer indicatorRenderer;
    
    public HomingSystem() {
        this.indicatorRenderer = new ShapeRenderer();
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void toggle() {
        enabled = !enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public float getPrecision() {
        return precision;
    }
    
    public void setPrecision(float precision) {
        this.precision = Math.max(0.0f, Math.min(1.0f, precision));
    }
    
    public void addPrecision(float amount) {
        precision += amount;
        precision = Math.max(0.0f, Math.min(1.0f, precision));
    }
    
    public void renderIndicator(SpriteBatch batch, float x, float y) {
        batch.end();
        
        indicatorRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        indicatorRenderer.begin(ShapeType.Filled);
        
        if (enabled) {
            indicatorRenderer.setColor(0.0f, 1.0f, 0.0f, 1.0f);
        } else {
            indicatorRenderer.setColor(1.0f, 0.0f, 0.0f, 1.0f);
        }
        
        indicatorRenderer.circle(x, y, 8f);
        indicatorRenderer.end();
        
        batch.begin();
    }
    
    public void dispose() {
        if (indicatorRenderer != null) {
            indicatorRenderer.dispose();
        }
    }
}
