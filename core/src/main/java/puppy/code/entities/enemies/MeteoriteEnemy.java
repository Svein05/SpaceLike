package puppy.code.entities.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import puppy.code.entities.effects.ExplosionAnimation;

public class MeteoriteEnemy extends Enemy {
    private Sprite spr;
    public float velocityX, velocityY;
    
    private int maxHealth;
    private int currentRound;
    private float damageFlashTimer;
    private static final float DAMAGE_FLASH_DURATION = 0.2f;
    private boolean showDamageFlash;
    
    private TextureAtlas asteroidAtlas;
    private boolean atlasLoaded = false;
    private ExplosionAnimation explosionAnimation;
    private boolean isExploding = false;

    public MeteoriteEnemy(int x, int y, int size, int xSpeed, int ySpeed, Texture tx) {
        this(x, y, size, xSpeed, ySpeed, tx, 1);
    }
    
    public MeteoriteEnemy(int x, int y, int size, int xSpeed, int ySpeed, int round) {
        super(x, y, size, size, calculateHealthForRound(round), Math.max(Math.abs(xSpeed), Math.abs(ySpeed)));
        
        this.currentRound = round;
        this.maxHealth = health;
        this.damageFlashTimer = 0;
        this.showDamageFlash = false;
        
        spr = new Sprite();
        this.velocityX = xSpeed;
        this.velocityY = ySpeed;
        
        loadAsteroidAtlas();
        setupSpriteFromAtlas();
    }
    
    private void setupSpriteFromAtlas() {
        if (atlasLoaded && asteroidAtlas != null) {
            TextureRegion firstFrame = asteroidAtlas.findRegion("Asteroid - High");
            if (firstFrame == null) firstFrame = asteroidAtlas.findRegion("Asteroid - Mid");
            if (firstFrame == null) firstFrame = asteroidAtlas.findRegion("Asteroid - Low");
            
            if (firstFrame != null) {
                spr = new Sprite(firstFrame);
                spr.setPosition(x, y);
                spr.setSize(width, height);
            }
        }
    }
    
    public MeteoriteEnemy(int x, int y, int size, int xSpeed, int ySpeed, Texture tx, int round) {
        super(x, y, size, size, calculateHealthForRound(round), Math.max(Math.abs(xSpeed), Math.abs(ySpeed)));
        
        this.currentRound = round;
        this.maxHealth = health;
        this.damageFlashTimer = 0;
        this.showDamageFlash = false;
        
        spr = new Sprite(tx);
        this.velocityX = xSpeed;
        this.velocityY = ySpeed;
        
        // Cargar atlas de asteroides
        loadAsteroidAtlas();
        
        updateSpriteByHealth();
        
        if (x - size < 0) this.x = x + size;
        if (x + size > 1920) this.x = x - size;
        
        if (y - size < 0) this.y = y + size;
        if (y + size > 1080) this.y = y - size;
        
        spr.setPosition(this.x, this.y);
        
        if (!atlasLoaded) {
            this.width = spr.getWidth();
            this.height = spr.getHeight();
        }
    }
    
    private static int calculateHealthForRound(int round) {
        return Math.min((1 + round) * 5, 40);
    }

    @Override
    public void update(float delta) {
        if (isExploding && explosionAnimation != null) {
            explosionAnimation.update(delta);
            if (explosionAnimation.isFinished()) {
                isExploding = false;
            }
            return;
        }
        
        x += velocityX * delta * 60;
        y += velocityY * delta * 60;

        if (x < 0 || x + spr.getWidth() > 1920) {
            velocityX *= -1;
            if (x < 0) x = 0;
            if (x + spr.getWidth() > 1920) x = 1920 - spr.getWidth();
        }
        
        if (y > 1080 + 100) {
            respawnFromTop();
        } else if (y + spr.getHeight() < -100) {
            respawnFromTop();
        }
            
        spr.setPosition(x, y);
        
        if (showDamageFlash) {
            damageFlashTimer -= delta;
            if (damageFlashTimer <= 0) {
                showDamageFlash = false;
                spr.setColor(1f, 1f, 1f, 1f);
            }
        }
        
        updateSpriteByHealth();
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (isExploding && explosionAnimation != null) {
            explosionAnimation.render(batch);
        } else {
            spr.draw(batch);
        }
    }

    @Override
    public Rectangle getBounds() {
        if (isExploding) {
            return new Rectangle(0, 0, 0, 0);
        }
        
        if (atlasLoaded && asteroidAtlas != null) {
            float regionWidth = spr.getWidth();
            float regionHeight = spr.getHeight();
            
            return new Rectangle(spr.getX(), spr.getY(), regionWidth, regionHeight);
        } else {
            float actualWidth = spr.getWidth();
            float actualHeight = spr.getHeight();
            
            float hitboxWidth = actualWidth * 0.7f;
            float hitboxHeight = actualHeight * 0.7f;
            
            float offsetX = (actualWidth - hitboxWidth) / 2;
            float offsetY = (actualHeight - hitboxHeight) / 2;
            
            return new Rectangle(
                spr.getX() + offsetX, 
                spr.getY() + offsetY, 
                hitboxWidth, 
                hitboxHeight
            );
        }
    }

    @Override
    public void takeDamage(int damage) {
        if (isExploding) {
            return;
        }
        
        health -= damage;
        
        if (health > 0) {
            showDamageFlash = true;
            damageFlashTimer = DAMAGE_FLASH_DURATION;
            
            float healthPercentage = (float) health / maxHealth;
            float redIntensity = 1.0f - (healthPercentage * 0.3f);
            
            spr.setColor(1f, 1f - redIntensity, 1f - redIntensity, 1f);
        } else {
            destroyed = true;
            startExplosionAnimation();
        }
    }

    private void loadAsteroidAtlas() {
        try {
            asteroidAtlas = new TextureAtlas(Gdx.files.internal("Game/Enemys/Asteroids/Asteroid.atlas"));
            atlasLoaded = true;
        } catch (Exception e) {
            atlasLoaded = false;
        }
    }
    
    private void startExplosionAnimation() {
        isExploding = true;
        explosionAnimation = new ExplosionAnimation(x, y, spr.getWidth(), spr.getHeight());
        
        try {
            puppy.code.managers.ResourceManager.getInstance().getSound(getDestructionSound()).play();
        } catch (Exception e) {
        }
    }

    private void updateSpriteByHealth() {
        if (!atlasLoaded || asteroidAtlas == null) return;
        
        float percent = (float) health / (float) maxHealth;
        TextureRegion region = null;

        if (health <= 0) {
            region = asteroidAtlas.findRegion("Asteroid - Full Damage (Exploted)");
        } else if (percent > 0.66f) {
            region = asteroidAtlas.findRegion("Asteroid - No Damage");
        } else if (percent > 0.33f) {
            region = asteroidAtlas.findRegion("Asteroid - Low Damage");
        } else {
            region = asteroidAtlas.findRegion("Asteroid - Mid Damage");
        }

        if (region != null) {
            spr.setRegion(region);
            
            float regionWidth = region.getRegionWidth();
            float regionHeight = region.getRegionHeight();
            
            spr.setSize(regionWidth, regionHeight);
            
            this.width = regionWidth;
            this.height = regionHeight;
            
            spr.setPosition(x, y);
        }
    }

    @Override
    public int getScoreValue() {
        return 5 * maxHealth;
    }

    @Override
    public int getXPValue() {
        return 2 + currentRound;
    }

    @Override
    public String getDestructionSound() {
        return "explosion.ogg";
    }
    
    public int getMaxHealth() {
        return maxHealth;
    }
    
    public int getCurrentRound() {
        return currentRound;
    }
    
    public float getHealthPercentage() {
        return (float) health / maxHealth;
    }
    
    public boolean isDamaged() {
        return health < maxHealth;
    }
    
    public boolean isExploding() {
        return isExploding;
    }
    
    private void respawnFromTop() {
        x = (float) (Math.random() * (1920 - spr.getWidth()));
        y = 1080 + 50;
        
        velocityX = ((float) Math.random() - 0.5f) * 4f;
        velocityY = Math.abs(velocityY);
        velocityY = -(2f + (float) Math.random() * 2f);
    }

    public void checkCollision(MeteoriteEnemy b2) {
        if (spr.getBoundingRectangle().overlaps(b2.spr.getBoundingRectangle())) {
            if (velocityX == 0) velocityX = velocityX + b2.velocityX / 2;
            if (b2.velocityX == 0) b2.velocityX = b2.velocityX + velocityX / 2;
            velocityX = -velocityX;
            b2.velocityX = -b2.velocityX;
            
            if (velocityY > 0) velocityY = -Math.abs(velocityY);
            if (b2.velocityY > 0) b2.velocityY = -Math.abs(b2.velocityY);
        }
    }
}