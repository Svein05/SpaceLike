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
    
    // Sistema de vida y feedback visual
    private int maxHealth;
    private int currentRound;
    private float damageFlashTimer;
    private static final float DAMAGE_FLASH_DURATION = 0.2f;
    private boolean showDamageFlash;
    
    // Sistema de atlas y animacion
    private TextureAtlas asteroidAtlas;
    private boolean atlasLoaded = false;
    private ExplosionAnimation explosionAnimation;
    private boolean isExploding = false;

    public MeteoriteEnemy(int x, int y, int size, int xSpeed, int ySpeed, Texture tx) {
        this(x, y, size, xSpeed, ySpeed, tx, 1); // Round 1 por defecto
    }
    
    // Constructor con round para vida escalable
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
        
        // Aplicar sprite inicial basado en vida
        updateSpriteByHealth();
        
        // Validar que borde de esfera no quede fuera
        if (x - size < 0) this.x = x + size;
        if (x + size > 1920) this.x = x - size;
        
        if (y - size < 0) this.y = y + size;
        if (y + size > 1080) this.y = y - size;
        
        spr.setPosition(this.x, this.y);
        
        // Si no se cargo el atlas, ajustar dimensiones al sprite original
        if (!atlasLoaded) {
            this.width = spr.getWidth();
            this.height = spr.getHeight();
        }
    }
    
    // Calcular vida basada en el round (escalable)
    private static int calculateHealthForRound(int round) {
        // Round 1: 2 vida, Round 2: 3 vida, Round 3: 4 vida, etc.
        // Máximo 8 vida para evitar que sea muy tedioso
        return Math.min(1 + round, 8);
    }

    @Override
    public void update(float delta) {
        // Si esta explotando, actualizar animacion
        if (isExploding && explosionAnimation != null) {
            explosionAnimation.update(delta);
            if (explosionAnimation.isFinished()) {
                isExploding = false; // Marcar explosion como terminada para que EnemyManager pueda removerlo
            }
            return; // No actualizar movimiento mientras explota
        }
        
        // Actualizar movimiento normal
        x += velocityX * delta * 60; // Convertir a frame-rate independiente
        y += velocityY * delta * 60;

        // Comportamiento de rebote en bordes laterales
        if (x < 0 || x + spr.getWidth() > 1920) {
            velocityX *= -1;
            // Mantener dentro de los límites
            if (x < 0) x = 0;
            if (x + spr.getWidth() > 1920) x = 1920 - spr.getWidth();
        }
        
        // Reciclaje cuando sale por arriba o abajo
        if (y > 1080 + 100) {
            // Sale por arriba, reaparecer desde arriba con nueva inclinación
            respawnFromTop();
        } else if (y + spr.getHeight() < -100) {
            // Sale por abajo, reaparecer desde arriba con nueva inclinación
            respawnFromTop();
        }
            
        spr.setPosition(x, y);
        
        // Actualizar timer de flash de daño
        if (showDamageFlash) {
            damageFlashTimer -= delta;
            if (damageFlashTimer <= 0) {
                showDamageFlash = false;
                spr.setColor(1f, 1f, 1f, 1f); // Restaurar color normal
            }
        }
        
        // Actualizar region del sprite segun porcentaje de vida
        updateSpriteByHealth();
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (isExploding && explosionAnimation != null) {
            // Renderizar animacion de explosion
            explosionAnimation.render(batch);
        } else {
            // Renderizar sprite normal
            spr.draw(batch);
        }
        
        // DEBUG: Descomentar para ver el hitbox visualmente
        /*
        Rectangle bounds = getBounds();
        ShapeRenderer shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        batch.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        shapeRenderer.end();
        batch.begin();
        */
    }

    @Override
    public Rectangle getBounds() {
        // Si esta explotando, no debe tener hitbox (no puede recibir mas disparos)
        if (isExploding) {
            return new Rectangle(0, 0, 0, 0); // Hitbox vacio
        }
        
        // Usar las dimensiones exactas de la region del atlas si esta disponible
        if (atlasLoaded && asteroidAtlas != null) {
            // Usar las dimensiones del sprite que ya fueron ajustadas por updateSpriteByHealth()
            float regionWidth = spr.getWidth();
            float regionHeight = spr.getHeight();
            
            // Usar las dimensiones exactas como hitbox
            return new Rectangle(spr.getX(), spr.getY(), regionWidth, regionHeight);
        } else {
            // Fallback: usar dimensiones del sprite con reduccion
            float actualWidth = spr.getWidth();
            float actualHeight = spr.getHeight();
            
            // Reducir hitbox al 70% del tamaño del sprite para ser mas justo
            float hitboxWidth = actualWidth * 0.7f;
            float hitboxHeight = actualHeight * 0.7f;
            
            // Centrar el hitbox en el sprite
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
        // Si ya esta explotando, no puede recibir mas daño
        if (isExploding) {
            return;
        }
        
        health -= damage;
        
        // Activar flash de daño visual
        if (health > 0) {
            showDamageFlash = true;
            damageFlashTimer = DAMAGE_FLASH_DURATION;
            
            // Calcular intensidad del color rojo basado en vida restante
            float healthPercentage = (float) health / maxHealth;
            float redIntensity = 1.0f - (healthPercentage * 0.3f); // Más rojo = menos vida
            
            spr.setColor(1f, 1f - redIntensity, 1f - redIntensity, 1f);
        } else {
            // Iniciar animacion de explosion y marcar como destruido
            destroyed = true; // ¡CLAVE! Marcar como destruido inmediatamente
            startExplosionAnimation();
        }
    }

    // Cargar atlas de asteroides
    private void loadAsteroidAtlas() {
        try {
            asteroidAtlas = new TextureAtlas(Gdx.files.internal("Game/Enemys/Asteroids/Asteroid.atlas"));
            atlasLoaded = true;
        } catch (Exception e) {
            atlasLoaded = false;
        }
    }
    
    // Iniciar animacion de explosion
    private void startExplosionAnimation() {
        isExploding = true;
        explosionAnimation = new ExplosionAnimation(x, y, spr.getWidth(), spr.getHeight());
        
        // Reproducir sonido de explosion inmediatamente cuando empieza la animacion
        try {
            puppy.code.managers.ResourceManager.getInstance().getSound(getDestructionSound()).play();
        } catch (Exception e) {
        }
    }

    // Actualizar el sprite segun los thresholds de vida para usar las regiones del atlas
    private void updateSpriteByHealth() {
        if (!atlasLoaded || asteroidAtlas == null) return;
        
        float percent = (float) health / (float) maxHealth;
        TextureRegion region = null;

        if (health <= 0) {
            // No cambiar sprite aqui, la animacion se encarga
            return;
        } else if (percent > 0.66f) {
            region = asteroidAtlas.findRegion("Asteroid - No Damage");
        } else if (percent > 0.33f) {
            region = asteroidAtlas.findRegion("Asteroid - Low Damage");
        } else {
            region = asteroidAtlas.findRegion("Asteroid - Mid Damage");
        }

        if (region != null) {
            spr.setRegion(region);
            
            // Usar las dimensiones exactas de la region del atlas
            float regionWidth = region.getRegionWidth();
            float regionHeight = region.getRegionHeight();
            
            // Ajustar el tamaño del sprite a las dimensiones exactas de la region
            spr.setSize(regionWidth, regionHeight);
            
            // Actualizar las dimensiones del GameObject base para que coincidan
            this.width = regionWidth;
            this.height = regionHeight;
            
            // Mantener la posicion
            spr.setPosition(x, y);
        }
    }

    @Override
    public int getScoreValue() {
        // Puntos escalables basados en la vida máxima (más dificil = más puntos)
        return 5 * maxHealth; // Base 10 puntos para round 1, escala con vida
    }

    @Override
    public int getXPValue() {
        // XP escalable basado en la dificultad
        return 2 + currentRound; // Base 3 XP para round 1, escala con round
    }

    @Override
    public String getDestructionSound() {
        return "explosion.ogg"; // Sonido clasico de explosion para asteroides
    }
    
    // Metodos adicionales para obtener informacion del asteroide
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
    
    // Metodo para reaparecer desde arriba cuando sale de pantalla
    private void respawnFromTop() {
        // Nueva posicion X aleatoria
        x = (float) (Math.random() * (1920 - spr.getWidth()));
        // Reaparecer arriba de la pantalla
        y = 1080 + 50;
        
        // Nueva inclinacion aleatoria pero controlada
        velocityX = ((float) Math.random() - 0.5f) * 4f; // Entre -2 y +2
        velocityY = Math.abs(velocityY); // Asegurar que vaya hacia abajo
        // Mantener velocidad hacia abajo pero variar un poco
        velocityY = -(2f + (float) Math.random() * 2f); // Entre -2 y -4
    }

    public void checkCollision(MeteoriteEnemy b2) {
        if (spr.getBoundingRectangle().overlaps(b2.spr.getBoundingRectangle())) {
            // Solo rebotar horizontalmente, mantener direccion vertical hacia abajo
            if (velocityX == 0) velocityX = velocityX + b2.velocityX / 2;
            if (b2.velocityX == 0) b2.velocityX = b2.velocityX + velocityX / 2;
            velocityX = -velocityX;
            b2.velocityX = -b2.velocityX;
            
            // NO cambiar velocityY, mantener direccion hacia abajo
            // Asegurar que ambos asteroides sigan yendo hacia abajo
            if (velocityY > 0) velocityY = -Math.abs(velocityY);
            if (b2.velocityY > 0) b2.velocityY = -Math.abs(b2.velocityY);
        }
    }
}