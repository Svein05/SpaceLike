package puppy.code.upgrades;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;

public class SpinnerSystem {
    private static final float ORBIT_RADIUS = 80f;
    private static final float ROTATION_SPEED = 120f;
    private static final float SPINNER_SIZE = 16f;
    private static final int BASE_DAMAGE = 5;
    
    private int spinnerCount;
    private float damageMultiplier;
    private float currentAngle;
    private ShapeRenderer shapeRenderer;
    
    private ArrayList<SpinnerBall> spinners;
    
    public class SpinnerBall {
        public float x;
        public float y;
        public float angle;
        
        public SpinnerBall(float angle) {
            this.angle = angle;
        }
        
        public Rectangle getBounds() {
            return new Rectangle(x - SPINNER_SIZE/2, y - SPINNER_SIZE/2, SPINNER_SIZE, SPINNER_SIZE);
        }
    }
    
    public SpinnerSystem() {
        this.spinnerCount = 0;
        this.damageMultiplier = 1.0f;
        this.currentAngle = 0f;
        this.spinners = new ArrayList<>();
        this.shapeRenderer = new ShapeRenderer();
    }
    
    public void addSpinner() {
        if (spinnerCount < 10) {
            spinnerCount++;
            updateSpinners();
        }
    }
    
    public void upgradeDamage(float percentIncrease) {
        damageMultiplier += percentIncrease;
    }
    
    private void updateSpinners() {
        spinners.clear();
        float angleStep = 360f / spinnerCount;
        for (int i = 0; i < spinnerCount; i++) {
            spinners.add(new SpinnerBall(i * angleStep));
        }
    }
    
    public void update(float delta, float naveX, float naveY, float naveWidth, float naveHeight) {
        if (spinnerCount == 0) return;
        
        currentAngle += ROTATION_SPEED * delta;
        if (currentAngle >= 360f) {
            currentAngle -= 360f;
        }
        
        float centerX = naveX + naveWidth / 2;
        float centerY = naveY + naveHeight / 2;
        
        for (SpinnerBall spinner : spinners) {
            float totalAngle = currentAngle + spinner.angle;
            float radians = (float) Math.toRadians(totalAngle);
            spinner.x = centerX + (float) Math.cos(radians) * ORBIT_RADIUS;
            spinner.y = centerY + (float) Math.sin(radians) * ORBIT_RADIUS;
        }
    }
    
    public void render(SpriteBatch batch) {
        if (spinnerCount == 0) return;
        
        // TODO: Reemplazar ShapeRenderer con sprite de bola
        batch.end();
        
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.2f, 0.8f, 1f, 1f);
        
        for (SpinnerBall spinner : spinners) {
            shapeRenderer.circle(spinner.x, spinner.y, SPINNER_SIZE / 2);
        }
        
        shapeRenderer.end();
        batch.begin();
    }
    
    public ArrayList<SpinnerBall> getSpinners() {
        return spinners;
    }
    
    public int getDamage() {
        return (int)(BASE_DAMAGE * damageMultiplier);
    }
    
    public int getSpinnerCount() {
        return spinnerCount;
    }
    
    public float getDamageMultiplier() {
        return damageMultiplier;
    }
    
    public void setSpinnerCount(int count) {
        this.spinnerCount = Math.min(count, 10);
        updateSpinners();
    }
    
    public void setDamageMultiplier(float multiplier) {
        this.damageMultiplier = multiplier;
    }
    
    public void dispose() {
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
    }
}
