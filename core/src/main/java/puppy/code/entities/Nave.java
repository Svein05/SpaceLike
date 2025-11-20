package puppy.code.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import puppy.code.entities.enemies.MeteoriteEnemy;
import puppy.code.entities.enemies.Enemy;
import puppy.code.screens.PantallaJuego;
import puppy.code.systems.HealthSystem;
import puppy.code.systems.TurboSystem;
import puppy.code.upgrades.SpinnerSystem;
import puppy.code.interfaces.Weapon;
import puppy.code.interfaces.ShootingBehavior;
import puppy.code.weapons.BasicCannon;
import puppy.code.stats.ShipStats;

public class Nave extends GameObject {
    private boolean destruida = false;
    private Sprite spr;
    private Sound sonidoHerido;
    public static boolean DEBUG_DRAW_HITBOX = false;
    private ShapeRenderer shapeRenderer = null;
    private ShapeRenderer homingIndicatorRenderer = null;
    private boolean herido = false;
    private int tiempoHeridoMax = 50;
    private int tiempoHerido;
    private float velocidadX = 0f;
    private float velocidadY = 0f;
    private static final float ACELERACION = 0.3f;
    private float velocidadMaximaBase = 7.5f;
    private float velocidadMaximaActual = 7.5f;
    private static final float FRICCION = 0.96f;
    private static final float UMBRAL_PARADA = 0.1f;
    
    private TurboSystem turboSystem;
    private boolean turboActive = false;
    private static final float TURBO_MULTIPLIER = 2.0f;
    
    private SpinnerSystem spinnerSystem;
    
    private boolean homingEnabled = true;
    private boolean cKeyPressed = false;
    
    // Sistema de corazones
    private HealthSystem healthSystem;
    
    // Sistema de armas
    private Weapon currentWeapon;
    private ShootingBehavior bonusShootingBehavior;
    
    // Sistema de estadisticas de la nave
    private ShipStats shipStats;
    
    // Sistema de invencibilidad
    private boolean invincible = false;
    
    private TextureAtlas mainShipAtlas;
    private TextureRegion middleBaseRegion;
    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> powerAnimation;
    private Animation<TextureRegion> powerVerticalAnimation;
    
    private Animation<TextureRegion> turboIdleAnimation;
    private Animation<TextureRegion> turboPowerAnimation;
    private Animation<TextureRegion> turboPowerVerticalAnimation;
    
    // Sistema de animacion de disparo
    private TextureAtlas basicCannonAtlas;
    private Animation<TextureRegion> basicShootingAnimation;
    
    // Control de animaciones
    private float animationTime = 0f;
    private PropulsionState currentPropulsionState = PropulsionState.IDLE;
    private static final float MOVEMENT_THRESHOLD = 0.2f;
    private static final float ANIMATION_FRAME_DURATION = 0.1f;
    
    private boolean isShooting = false;
    private float shootingAnimationTime = 0f;
    private float lastShotTime = 0f;
    
    // Parametros configurables para posiciones de disparo
    private static final float CENTER_OFFSET_X = -5f;
    private static final float CENTER_OFFSET_Y = -5f;

    private static final float LEFT_WING_OFFSET_X = -24f;
    private static final float LEFT_WING_OFFSET_Y = 10f;

    private static final float RIGHT_WING_OFFSET_X = 15f;
    private static final float RIGHT_WING_OFFSET_Y = 10f;
    
    private float targetWidth = 96f;
    private float targetHeight = 96f;
    
    // Enum para estados de propulsión
    public enum PropulsionState {
        IDLE, POWER, POWER_VERTICAL
    }
    
    public Nave(int x, int y, Texture tx, Sound soundChoque) {
        super(x, y, 76, 76);
        sonidoHerido = soundChoque;
        spr = new Sprite(tx);
        
        // Configurar tamaño escalado inicial 
        float scale = 2.0f;
        float scaledWidth = tx.getWidth() * scale;
        float scaledHeight = tx.getHeight() * scale;
        
        initializeNave(x, y, scaledWidth, scaledHeight);
    }
    
    public Nave(int x, int y, Sound soundChoque) {
        super(x, y, 76, 76);
        sonidoHerido = soundChoque;
        
        spr = new Sprite();
        
        // Configurar tamaño
        float scaledWidth = 96f;
        float scaledHeight = 96f;
        
        initializeNave(x, y, scaledWidth, scaledHeight);
    }
    
    private void initializeNave(int x, int y, float scaledWidth, float scaledHeight) {
        this.width = scaledWidth;
        this.height = scaledHeight;
        
        // Configurar sprite con dimensiones escaladas
        spr.setPosition(x, y);
        spr.setSize(scaledWidth, scaledHeight);
        
        shipStats = new ShipStats();
        healthSystem = new HealthSystem(shipStats);
        turboSystem = new TurboSystem();
        homingIndicatorRenderer = new ShapeRenderer();
        spinnerSystem = new SpinnerSystem();

        if (DEBUG_DRAW_HITBOX) {
            shapeRenderer = new ShapeRenderer();
        }

        currentWeapon = new BasicCannon();

        loadMultilayerAssets();
        targetWidth = scaledWidth;
        targetHeight = scaledHeight;
    }
    
    private void loadMultilayerAssets() {
        try {
            mainShipAtlas = new TextureAtlas(Gdx.files.internal("Game/Nave/Main/Main Ship.atlas"));
            
            Texture baseTexture = new Texture(Gdx.files.internal("Game/Nave/Main/Main Ship Base.png"));
            middleBaseRegion = new TextureRegion(baseTexture);
            
            basicCannonAtlas = new TextureAtlas(Gdx.files.internal("Game/Nave/Weapons/Ship Basic Cannon.atlas"));
            createShootingAnimation();
            
            createPropulsionAnimations();
            createTurboAnimations();
        } catch (Exception e) {
            System.err.println("Error cargando assets multicapa: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void createPropulsionAnimations() {
        TextureAtlas idleAtlas = new TextureAtlas(Gdx.files.internal("Game/Nave/Propulsion/PropulsionIDLE.atlas"));
        TextureRegion[] idleFrames = new TextureRegion[3];
        idleFrames[0] = idleAtlas.findRegion("Ship Idle 01");
        idleFrames[1] = idleAtlas.findRegion("Ship Idle 02");
        idleFrames[2] = idleAtlas.findRegion("Ship Idle 03");
        idleAnimation = new Animation<>(ANIMATION_FRAME_DURATION, idleFrames);
        idleAnimation.setPlayMode(Animation.PlayMode.LOOP);
        
        TextureAtlas powerAtlas = new TextureAtlas(Gdx.files.internal("Game/Nave/Propulsion/PropulsionPOWER.atlas"));
        TextureRegion[] powerFrames = new TextureRegion[4];
        powerFrames[0] = powerAtlas.findRegion("Ship Power 01");
        powerFrames[1] = powerAtlas.findRegion("Ship Power 02");
        powerFrames[2] = powerAtlas.findRegion("Ship Power 03");
        powerFrames[3] = powerAtlas.findRegion("Ship Power 04");
        powerAnimation = new Animation<>(ANIMATION_FRAME_DURATION, powerFrames);
        powerAnimation.setPlayMode(Animation.PlayMode.LOOP);
        
        TextureAtlas powerVerticalAtlas = new TextureAtlas(Gdx.files.internal("Game/Nave/Propulsion/PropulsionPOWERVERTICAL.atlas"));
        TextureRegion[] powerVerticalFrames = new TextureRegion[4];
        powerVerticalFrames[0] = powerVerticalAtlas.findRegion("Ship Power Vertical 01");
        powerVerticalFrames[1] = powerVerticalAtlas.findRegion("Ship Power Vertical 02");
        powerVerticalFrames[2] = powerVerticalAtlas.findRegion("Ship Power Vertical 03");
        powerVerticalFrames[3] = powerVerticalAtlas.findRegion("Ship Power Vertical 04");
        powerVerticalAnimation = new Animation<>(ANIMATION_FRAME_DURATION, powerVerticalFrames);
        powerVerticalAnimation.setPlayMode(Animation.PlayMode.LOOP);
    }
    
    private void createTurboAnimations() {
        try {
            TextureAtlas turboAtlas = new TextureAtlas(Gdx.files.internal("Game/Nave/Propulsion/Ship Supercharge.atlas"));
            
            TextureRegion[] turboFrames = new TextureRegion[4];
            turboFrames[0] = turboAtlas.findRegion("Ship Supercharge - 0001");
            turboFrames[1] = turboAtlas.findRegion("Ship Supercharge - 0002");
            turboFrames[2] = turboAtlas.findRegion("Ship Supercharge - 0003");
            turboFrames[3] = turboAtlas.findRegion("Ship Supercharge - 0004");
            
            turboIdleAnimation = new Animation<>(ANIMATION_FRAME_DURATION * 0.6f, turboFrames);
            turboIdleAnimation.setPlayMode(Animation.PlayMode.LOOP);
            
            turboPowerAnimation = new Animation<>(ANIMATION_FRAME_DURATION * 0.5f, turboFrames);
            turboPowerAnimation.setPlayMode(Animation.PlayMode.LOOP);
            
            turboPowerVerticalAnimation = new Animation<>(ANIMATION_FRAME_DURATION * 0.5f, turboFrames);
            turboPowerVerticalAnimation.setPlayMode(Animation.PlayMode.LOOP);
        } catch (Exception e) {
            System.err.println("Error cargando animaciones de turbo: " + e.getMessage());
        }
    }
    
    private void createShootingAnimation() {
        if (basicCannonAtlas != null) {
            TextureRegion[] basicFrames = new TextureRegion[7];
            basicFrames[0] = basicCannonAtlas.findRegion("Ship Autocannon lv0_01");
            basicFrames[1] = basicCannonAtlas.findRegion("Ship Autocannon lv0_02");
            basicFrames[2] = basicCannonAtlas.findRegion("Ship Autocannon lv0_03");
            basicFrames[3] = basicCannonAtlas.findRegion("Ship Autocannon lv0_04");
            basicFrames[4] = basicCannonAtlas.findRegion("Ship Autocannon lv0_05");
            basicFrames[5] = basicCannonAtlas.findRegion("Ship Autocannon lv0_06");
            basicFrames[6] = basicCannonAtlas.findRegion("Ship Autocannon lv0_07");
            
            basicShootingAnimation = new Animation<>(ANIMATION_FRAME_DURATION, basicFrames);
            basicShootingAnimation.setPlayMode(Animation.PlayMode.LOOP);
        }
    }
    
    private void updateShipTexture() {
        if (mainShipAtlas == null) return;
    }

    @Override
    public void update(float delta) {
        updateShipTexture();
        
        // Actualizar arma
        if (currentWeapon != null) {
            currentWeapon.update(delta);
        }
        
        if (!herido) {
            boolean shiftPressed = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);
            
            if (shiftPressed && turboSystem.canUseTurbo()) {
                turboActive = true;
                velocidadMaximaActual = velocidadMaximaBase * TURBO_MULTIPLIER;
                turboSystem.consumeStamina(delta);
            } else {
                turboActive = false;
                velocidadMaximaActual = velocidadMaximaBase;
                turboSystem.regenerateStamina(delta);
            }
            
            float aceleracionX = 0f;
            float aceleracionY = 0f;
            
            float aceleracionActual = turboActive ? ACELERACION * TURBO_MULTIPLIER : ACELERACION;
            
            if (Gdx.input.isKeyPressed(Input.Keys.W)) aceleracionY += aceleracionActual;
            if (Gdx.input.isKeyPressed(Input.Keys.S)) aceleracionY -= aceleracionActual;
            if (Gdx.input.isKeyPressed(Input.Keys.A)) aceleracionX -= aceleracionActual;
            if (Gdx.input.isKeyPressed(Input.Keys.D)) aceleracionX += aceleracionActual;
            
            if (Gdx.input.isKeyPressed(Input.Keys.C)) {
                if (!cKeyPressed) {
                    homingEnabled = !homingEnabled;
                    cKeyPressed = true;
                }
            } else {
                cKeyPressed = false;
            }
            
            // Detectar disparo continuo
            if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
                if (!isShooting) {
                    startShooting();
                }
            } else {
                // Dejar de disparar cuando se suelta SPACE
                if (isShooting) {
                    stopShooting();
                }
            }
            
            velocidadX += aceleracionX;
            velocidadY += aceleracionY;
            
            float velocidadTotal = (float) Math.sqrt(velocidadX * velocidadX + velocidadY * velocidadY);
            if (velocidadTotal > velocidadMaximaActual) {
                float factor = velocidadMaximaActual / velocidadTotal;
                velocidadX *= factor;
                velocidadY *= factor;
            }
            
            velocidadX *= FRICCION;
            velocidadY *= FRICCION;
            
            if (Math.abs(velocidadX) < UMBRAL_PARADA) velocidadX = 0f;
            if (Math.abs(velocidadY) < UMBRAL_PARADA) velocidadY = 0f;
            
            float newX = x + velocidadX;
            float newY = y + velocidadY;
            
            if (newX >= 0 && newX + spr.getWidth() <= 1920) {
                x = newX;
                spr.setX(x);
            } else {
                velocidadX = 0f;
            }
            if (newY >= 0 && newY + spr.getHeight() <= 1080) {
                y = newY;
                spr.setY(y);
            } else {
                velocidadY = 0f;
            }
        } else {
            tiempoHerido--;
            if (tiempoHerido <= 0) herido = false;
        }
        
        updatePropulsionState();
        updateShootingAnimation(delta);
        animationTime += delta;
        
        if (spinnerSystem != null) {
            spinnerSystem.update(delta, x, y, targetWidth, targetHeight);
        }
    }
    
    private void updatePropulsionState() {
        PropulsionState newState;
        
        float absVelX = Math.abs(velocidadX);
        float absVelY = Math.abs(velocidadY);
        
        if (velocidadY < -MOVEMENT_THRESHOLD) {
            newState = PropulsionState.IDLE;
        } else if (absVelY > MOVEMENT_THRESHOLD && absVelY > absVelX && velocidadY > 0) {
            newState = PropulsionState.POWER_VERTICAL;
        } else if (absVelX > MOVEMENT_THRESHOLD || (absVelY > MOVEMENT_THRESHOLD && velocidadY > 0)) {
            newState = PropulsionState.POWER;
        } else {
            newState = PropulsionState.IDLE;
        }
        
        if (newState != currentPropulsionState) {
            currentPropulsionState = newState;
            animationTime = 0f;

        }
    }
    
    private void startShooting() {
        if (!isShooting) {
            isShooting = true;
            shootingAnimationTime = 0f;
        }
    }
    
    private void stopShooting() {
        isShooting = false;
        shootingAnimationTime = 0f;
    }
    
    private void updateShootingAnimation(float delta) {
        lastShotTime += delta;
        if (isShooting) {
            shootingAnimationTime += delta;
        }
    }
    
    public boolean canShoot() {
        float effectiveFireRate = shipStats.getEffectiveFireRate(puppy.code.entities.projectiles.ProjectileType.BULLET);
        return isShooting && lastShotTime >= effectiveFireRate;
    }
    
    public void resetShotCooldown() {
        lastShotTime = 0f;
    }
    
    public boolean isShooting() {
        return isShooting;
    }
    
    public float getCenterShootX() {
        return x + targetWidth/2 + CENTER_OFFSET_X;
    }
    
    public float getCenterShootY() {
        return y + targetHeight + CENTER_OFFSET_Y;
    }
    
    public float getLeftWingShootX() {
        return x + targetWidth/2 + LEFT_WING_OFFSET_X;
    }
    
    public float getLeftWingShootY() {
        return y + targetHeight/2 + LEFT_WING_OFFSET_Y;
    }
    
    public float getRightWingShootX() {
        return x + targetWidth/2 + RIGHT_WING_OFFSET_X;
    }
    
    public float getRightWingShootY() {
        return y + targetHeight/2 + RIGHT_WING_OFFSET_Y;
    }
    
    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return targetWidth; }
    public float getHeight() { return targetHeight; }
    
    public void setBonusShootingBehavior(ShootingBehavior behavior) {
        this.bonusShootingBehavior = behavior;
    }
    
    public ShootingBehavior getShootingBehavior() {
        return bonusShootingBehavior;
    }
    
    public void executeShoot(puppy.code.managers.ProjectileManager projectileManager) {
        executeBasicShoot(projectileManager);
        
        if (bonusShootingBehavior != null && bonusShootingBehavior.isActive()) {
            bonusShootingBehavior.shoot(this, projectileManager);
        }
    }
    
    private void executeBasicShoot(puppy.code.managers.ProjectileManager projectileManager) {
        float effectiveSpeed = shipStats.getEffectiveProjectileSpeed(puppy.code.entities.projectiles.ProjectileType.BULLET);
        projectileManager.createProjectile(
            puppy.code.entities.projectiles.ProjectileType.BULLET,
            getCenterShootX(), 
            getCenterShootY(), 
            0, 
            effectiveSpeed
        );
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (spinnerSystem != null) {
            spinnerSystem.render(batch);
        }
        
        float drawX = herido ? spr.getX() + MathUtils.random(-2, 2) : spr.getX();
        float drawY = spr.getY();
        
        drawPropulsionLayer(batch, drawX, drawY);
        drawMiddleLayer(batch, drawX, drawY);
        drawBonusEffectLayer(batch, drawX, drawY);
        drawLifeStateLayer(batch, drawX, drawY);
        
        if (DEBUG_DRAW_HITBOX) {
            // Cerrar batch para usar ShapeRenderer
            batch.end();
            shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
            shapeRenderer.begin(ShapeType.Line);
            shapeRenderer.setColor(Color.RED);
            Rectangle hb = getBounds();
            shapeRenderer.rect(hb.x, hb.y, hb.width, hb.height);
            shapeRenderer.end();
            batch.begin();
        }
    }
    
    private void drawPropulsionLayer(SpriteBatch batch, float x, float y) {
        Animation<TextureRegion> currentAnimation = null;

        if (turboActive) {
            switch (currentPropulsionState) {
                case IDLE:
                    currentAnimation = turboIdleAnimation != null ? turboIdleAnimation : idleAnimation;
                    break;
                case POWER:
                    currentAnimation = turboPowerAnimation != null ? turboPowerAnimation : powerAnimation;
                    break;
                case POWER_VERTICAL:
                    currentAnimation = turboPowerVerticalAnimation != null ? turboPowerVerticalAnimation : powerVerticalAnimation;
                    break;
            }
        } else {
            switch (currentPropulsionState) {
                case IDLE:
                    currentAnimation = idleAnimation;
                    break;
                case POWER:
                    currentAnimation = powerAnimation;
                    break;
                case POWER_VERTICAL:
                    currentAnimation = powerVerticalAnimation;
                    break;
            }
        }
        
        if (currentAnimation != null) {
            TextureRegion currentFrame = currentAnimation.getKeyFrame(animationTime);
            if (currentFrame != null) {
                batch.draw(currentFrame, x, y, targetWidth, targetHeight);
            }
        }
    }
    
    private void drawMiddleLayer(SpriteBatch batch, float x, float y) {
        if (isShooting) {
            if (basicShootingAnimation != null) {
                TextureRegion basicFrame = basicShootingAnimation.getKeyFrame(shootingAnimationTime);
                if (basicFrame != null) {
                    batch.draw(basicFrame, x, y, targetWidth, targetHeight);
                }
            }
        } else if (middleBaseRegion != null) {
            batch.draw(middleBaseRegion, x, y, targetWidth, targetHeight);
        }
    }
    
    private void drawBonusEffectLayer(SpriteBatch batch, float x, float y) {
        if (bonusShootingBehavior != null && bonusShootingBehavior.isActive() && isShooting) {
            if (bonusShootingBehavior instanceof puppy.code.entities.bonus.BonusAutocannon) {
                puppy.code.entities.bonus.BonusAutocannon bonus = 
                    (puppy.code.entities.bonus.BonusAutocannon) bonusShootingBehavior;
                bonus.renderEffect(batch, this);
            }
        }
    }
    
    private void drawLifeStateLayer(SpriteBatch batch, float x, float y) {
        if (mainShipAtlas != null) {
            String regionName = getLifeStateRegionName();
            TextureRegion lifeStateRegion = mainShipAtlas.findRegion(regionName);
            
            if (lifeStateRegion != null) {
                batch.draw(lifeStateRegion, x, y, targetWidth, targetHeight);
            } else {
                System.err.println("No se encontró región: " + regionName);
            }
        }
    }
    
    private String getLifeStateRegionName() {
        float currentHealth = healthSystem.getCurrentHealth();
        
        if (currentHealth >= 5.5f) {
            return "Main Ship - Base - Full health";
        } else if (currentHealth >= 3.5f) {
            return "Main Ship - Base - Slight damage";
        } else if (currentHealth >= 1.5f) {
            return "Main Ship - Base - Damaged";
        } else {
            return "Main Ship - Base - Very damaged";
        }
    }

    @Override
    public Rectangle getBounds() {
        float preciseWidth = spr.getWidth() * 0.5f;
        float preciseHeight = spr.getHeight() * 0.5f;

        float offsetX = (spr.getWidth() - preciseWidth) / 2f;
        float offsetY = (spr.getHeight() - preciseHeight) / 2f;

        return new Rectangle(spr.getX() + offsetX, spr.getY() + offsetY, preciseWidth, preciseHeight);
    }
    
    public void handleInput(PantallaJuego juego) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }
      
    public boolean checkCollision(MeteoriteEnemy b) {
    if (!herido && !invincible && b.getBounds().overlaps(getBounds())) {
            float pushForce = 20f;
            
            float centerX = spr.getX() + spr.getWidth() / 2;
            float centerY = spr.getY() + spr.getHeight() / 2;
            float ballCenterX = b.getBounds().x + b.getBounds().width / 2;
            float ballCenterY = b.getBounds().y + b.getBounds().height / 2;
            
            float deltaX = centerX - ballCenterX;
            float deltaY = centerY - ballCenterY;
            float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
            
            if (distance > 0) {
                deltaX /= distance;
                deltaY /= distance;
                
                float newX = spr.getX() + deltaX * pushForce;
                float newY = spr.getY() + deltaY * pushForce;
                
                if (newX >= 0 && newX + spr.getWidth() <= 1920) {
                    spr.setX(newX);
                    x = newX;
                }
                if (newY >= 0 && newY + spr.getHeight() <= 1080) {
                    spr.setY(newY);
                    y = newY;
                }
            }
            
            b.velocityX = -b.velocityX;
            if (b.velocityY > 0) {
                b.velocityY = -Math.abs(b.velocityY);
            }
            
            healthSystem.takeDamage();
            herido = true;
            tiempoHerido = tiempoHeridoMax;
            
            velocidadX = 0f;
            velocidadY = 0f;
            
            sonidoHerido.play();
            if (healthSystem.isDead()) 
                destruida = true; 
            return true;
        }
        return false;
    }
    
    public boolean checkCollision(Enemy enemy) {
        if (!herido && !invincible && enemy.getBounds().overlaps(getBounds())) {
            float pushForce = 20f;
            
            float centerX = spr.getX() + spr.getWidth() / 2;
            float centerY = spr.getY() + spr.getHeight() / 2;
            float enemyCenterX = enemy.getX() + enemy.getWidth() / 2;
            float enemyCenterY = enemy.getY() + enemy.getHeight() / 2;
            
            float deltaX = centerX - enemyCenterX;
            float deltaY = centerY - enemyCenterY;
            float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
            
            if (distance > 0) {
                deltaX /= distance;
                deltaY /= distance;
                
                float newX = spr.getX() + deltaX * pushForce;
                float newY = spr.getY() + deltaY * pushForce;
                
                if (newX >= 0 && newX + spr.getWidth() <= 1920) {
                    spr.setX(newX);
                    x = newX;
                }
                if (newY >= 0 && newY + spr.getHeight() <= 1080) {
                    spr.setY(newY);
                    y = newY;
                }
            }
            
            healthSystem.takeDamage();
            herido = true;
            tiempoHerido = tiempoHeridoMax;
            
            velocidadX = 0f;
            velocidadY = 0f;
            
            sonidoHerido.play();
            if (healthSystem.isDead()) 
                destruida = true; 
            return true;
        }
        return false;
    }
    
    public boolean estaDestruido() {
        return destruida;
    }
    
    public boolean estaHerido() {
        return herido;
    }
    
    public void takeDamage() {
        if (!herido && !invincible) {
            healthSystem.takeDamage();
            herido = true;
            tiempoHerido = tiempoHeridoMax;
            velocidadX = 0f;
            velocidadY = 0f;
            sonidoHerido.play();
            if (healthSystem.isDead()) {
                destruida = true;
            }
        }
    }
    
    public int getVidas() {
        return healthSystem.getVidas();
    }
    
    public void setVidas(int vidas2) {
        healthSystem.setVidas(vidas2);
    }
    
    public HealthSystem getHealthSystem() {
        return healthSystem;
    }
    
    public void renderHealthHearts(SpriteBatch batch) {
        healthSystem.render(batch, 30, 1030);
    }
    
    public void renderHomingIndicator(SpriteBatch batch) {
        if (shipStats.getHomingPrecision() <= 0) return;
        
        batch.end();
        
        homingIndicatorRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        homingIndicatorRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        if (homingEnabled) {
            homingIndicatorRenderer.setColor(0f, 1f, 0f, 1f);
        } else {
            homingIndicatorRenderer.setColor(1f, 0f, 0f, 1f);
        }
        
        homingIndicatorRenderer.circle(50, 540, 15);
        homingIndicatorRenderer.end();
        
        batch.begin();
    }
    
    public void setWeapon(Weapon newWeapon) {
        this.currentWeapon = newWeapon;
    }
    
    public Weapon getCurrentWeapon() {
        return currentWeapon;
    }
    
    
    public ShipStats getShipStats() {
        return shipStats;
    }
    
    public void resetStats() {
        shipStats.resetToDefaults();
    }
    
    public boolean isInvincible() {
        return invincible;
    }
    
    public void setInvincible(boolean invincible) {
        this.invincible = invincible;
    }
    
    public void dispose() {
        if (mainShipAtlas != null) {
            mainShipAtlas.dispose();
        }
        
        if (middleBaseRegion != null && middleBaseRegion.getTexture() != null) {
            middleBaseRegion.getTexture().dispose();
        }
        
        if (basicCannonAtlas != null) {
            basicCannonAtlas.dispose();
        }
        
        if (shapeRenderer != null) {
            try {
                shapeRenderer.dispose();
            } catch (Exception e) {
                System.err.println("Error liberando ShapeRenderer: " + e.getMessage());
            }
            shapeRenderer = null;
        }
        
        if (turboSystem != null) {
            turboSystem.dispose();
        }
        
        if (homingIndicatorRenderer != null) {
            try {
                homingIndicatorRenderer.dispose();
            } catch (Exception e) {
                System.err.println("Error liberando homingIndicatorRenderer: " + e.getMessage());
            }
            homingIndicatorRenderer = null;
        }
        
        if (spinnerSystem != null) {
            spinnerSystem.dispose();
        }
    }
    
    public TurboSystem getTurboSystem() {
        return turboSystem;
    }
    
    public boolean isTurboActive() {
        return turboActive;
    }
    
    public boolean isHomingEnabled() {
        return homingEnabled;
    }
    
    public SpinnerSystem getSpinnerSystem() {
        return spinnerSystem;
    }
}