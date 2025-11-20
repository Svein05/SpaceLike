package puppy.code.entities.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import puppy.code.entities.Nave;

public class SniperEnemy extends Enemy {
    private Sprite sprite;
    private float damageFlashTimer;
    private boolean showDamageFlash;
    private static final float DAMAGE_FLASH_DURATION = 0.2f;
    
    private SniperState state;
    private float stateTimer;
    private Vector2 aimDirection;
    private Vector2 targetPosition;
    private Vector2 laserStartPosition;
    private Nave playerShip;
    private ShapeRenderer lineRenderer;
    private boolean hasCompletedFirstMovement;
    
    private static final float AIMING_DURATION = 2.0f;
    private static final float MOVING_DURATION = 2.0f;
    private static final float SCREEN_HALF_Y = 540f;
    
    public enum SniperState {
        AIMING,
        SHOOTING,
        MOVING
    }

    public SniperEnemy(float x, float y, Texture texture, int health, float speed, Nave playerShip) {
        super(x, y, texture.getWidth() * 1.3f, texture.getHeight() * 1.3f, health, speed);
        this.sprite = new Sprite(texture);
        this.sprite.setSize(width, height);
        
        this.damageFlashTimer = 0f;
        this.showDamageFlash = false;
        this.playerShip = playerShip;
        this.state = SniperState.AIMING;
        this.stateTimer = 0f;
        this.aimDirection = new Vector2();
        this.targetPosition = new Vector2();
        this.laserStartPosition = new Vector2();
        this.lineRenderer = new ShapeRenderer();
        this.hasCompletedFirstMovement = false;
    }

    @Override
    public void update(float delta) {
        stateTimer += delta;
        
        switch (state) {
            case AIMING:
                if (stateTimer >= AIMING_DURATION) {
                    calculateAimDirection();
                    state = SniperState.SHOOTING;
                    stateTimer = 0f;
                }
                break;
                
            case SHOOTING:
                state = SniperState.MOVING;
                stateTimer = 0f;
                break;
                
            case MOVING:
                if (movementBehavior != null) {
                    movementBehavior.move(this, delta);
                }
                
                if (!hasCompletedFirstMovement && y <= SCREEN_HALF_Y) {
                    hasCompletedFirstMovement = true;
                }
                
                if (hasCompletedFirstMovement && y < SCREEN_HALF_Y) {
                    y = SCREEN_HALF_Y;
                }
                
                if (stateTimer >= MOVING_DURATION) {
                    state = SniperState.AIMING;
                    stateTimer = 0f;
                }
                break;
        }
        
        updateWeapon(delta);
        
        if (y < -height) {
            destroyed = true;
        }
        
        if (showDamageFlash) {
            damageFlashTimer -= delta;
            if (damageFlashTimer <= 0) {
                showDamageFlash = false;
            }
        }
    }

    private void calculateAimDirection() {
        float targetX = playerShip.getX() + playerShip.getWidth() / 2;
        float targetY = playerShip.getY() + playerShip.getHeight() / 2;
        
        targetPosition.set(targetX, targetY);
        
        float startX = x + width / 2;
        float startY = y;
        
        laserStartPosition.set(startX, startY);
        
        float dx = targetX - startX;
        float dy = targetY - startY;
        
        aimDirection.set(dx, dy).nor();
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.draw(sprite.getTexture(), x, y, width, height);
        
        batch.end();
        
        lineRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        lineRenderer.begin(ShapeRenderer.ShapeType.Line);
        lineRenderer.setColor(1f, 0f, 0f, 1f);
        lineRenderer.rect(x, y, width, height);
        lineRenderer.end();
        
        if (state == SniperState.AIMING) {
            lineRenderer.setProjectionMatrix(batch.getProjectionMatrix());
            lineRenderer.begin(ShapeRenderer.ShapeType.Filled);
            lineRenderer.setColor(1f, 0f, 0f, 0.5f);
            
            float endX = playerShip.getX() + playerShip.getWidth() / 2;
            float endY = playerShip.getY() + playerShip.getHeight() / 2;
            
            lineRenderer.rectLine(laserStartPosition.x, laserStartPosition.y, endX, endY, 2f);
            lineRenderer.end();
        }
        
        batch.begin();
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    @Override
    public void takeDamage(int damage) {
        health -= damage;
        
        if (health > 0) {
            showDamageFlash = true;
            damageFlashTimer = DAMAGE_FLASH_DURATION;
        } else {
            destroyed = true;
        }
    }

    @Override
    public int getScoreValue() {
        return 100;
    }

    @Override
    public int getXPValue() {
        return 20;
    }

    @Override
    public String getDestructionSound() {
        return "Audio/SFX/Explosions/Boom10.mp3";
    }
    
    public SniperState getState() {
        return state;
    }
    
    public Vector2 getAimDirection() {
        return aimDirection;
    }
    
    public Sprite getSprite() {
        return sprite;
    }
    
    public void triggerShoot() {
        if (state == SniperState.SHOOTING && weapon != null) {
            // Weapon maneja el disparo directamente
        }
    }
    
    public void dispose() {
        if (lineRenderer != null) {
            lineRenderer.dispose();
        }
    }
}
