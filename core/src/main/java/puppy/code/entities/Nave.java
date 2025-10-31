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
import puppy.code.screens.PantallaJuego;
import puppy.code.systems.HealthSystem;
import puppy.code.interfaces.Weapon;
import puppy.code.weapons.BasicCannon;

public class Nave extends GameObject {
    private boolean destruida = false;
    private Sprite spr;
    private Sound sonidoHerido;
    // Flag para dibujar hitbox en modo testing
    public static boolean DEBUG_DRAW_HITBOX = false; // Deshabilitado temporalmente
    // ShapeRenderer para debug (creado solo si DEBUG_DRAW_HITBOX es true)
    private ShapeRenderer shapeRenderer = null;
    private boolean herido = false;
    private int tiempoHeridoMax = 50;
    private int tiempoHerido;
    
    // SISTEMA MULTICAPA: texturas obsoletas eliminadas
    // Las texturas ahora se manejan a través de atlas multicapa
    
    // Sistema de fisica realista para la nave
    private float velocidadX = 0f;
    private float velocidadY = 0f;
    private static final float ACELERACION = 0.3f;
    private static final float VELOCIDAD_MAXIMA = 5f;
    private static final float FRICCION = 0.96f;
    private static final float UMBRAL_PARADA = 0.1f;
    
    // Sistema de corazones
    private HealthSystem healthSystem;
    
    // Sistema de armas
    private Weapon currentWeapon;
    
    // Sistema multicapa de renderizado
    private TextureAtlas mainShipAtlas;           // Capa Top: estados de vida
    private TextureRegion middleBaseRegion;       // Capa Middle: base/esqueleto
    private Animation<TextureRegion> idleAnimation;        // Capa Low: propulsión idle
    private Animation<TextureRegion> powerAnimation;       // Capa Low: propulsión power
    private Animation<TextureRegion> powerVerticalAnimation; // Capa Low: propulsión vertical
    
    // Control de animaciones
    private float animationTime = 0f;
    private PropulsionState currentPropulsionState = PropulsionState.IDLE;
    private static final float MOVEMENT_THRESHOLD = 0.2f; // Umbral para detectar movimiento
    private static final float ANIMATION_FRAME_DURATION = 0.1f;
    
    // Tamaño objetivo para todas las capas
    private float targetWidth = 192f;   // Escalado actual de la nave (48*4=192)
    private float targetHeight = 192f;
    
    // Enum para estados de propulsión
    public enum PropulsionState {
        IDLE, POWER, POWER_VERTICAL
    }
    
    public Nave(int x, int y, Texture tx, Sound soundChoque) {
        super(x, y, 76, 76); // Sera ajustado despues
        sonidoHerido = soundChoque;
        
        // SISTEMA MULTICAPA: Ya no usamos texturas individuales
        // Las texturas se cargan desde el atlas en loadMultilayerAssets()
        
        // Crear sprite temporal con la textura pasada (para compatibilidad)
        // Luego será reemplazado por el sistema multicapa
        spr = new Sprite(tx);
        
        // Configurar tamaño escalado inicial 
        float scale = 4.0f; // Escalar 4x para que sea 100% más grande (era 2x, ahora 4x)
        float scaledWidth = tx.getWidth() * scale;
        float scaledHeight = tx.getHeight() * scale;
        
        // Actualizar las dimensiones del GameObject base
        this.width = scaledWidth;
        this.height = scaledHeight;
        
        // Configurar sprite con dimensiones escaladas
        spr.setPosition(x, y);
        spr.setSize(scaledWidth, scaledHeight);
        
        // Inicializar sistema de corazones
        healthSystem = new HealthSystem();

        // Inicializar ShapeRenderer solo si el debug esta activo
        if (DEBUG_DRAW_HITBOX) {
            shapeRenderer = new ShapeRenderer();
        }

        // Inicializar arma basica (ya no necesita textura)
        currentWeapon = new BasicCannon();
        
        // Cargar recursos para sistema multicapa
        loadMultilayerAssets();
        
        // Actualizar tamaño objetivo basado en el sprite escalado
        targetWidth = scaledWidth;
        targetHeight = scaledHeight;
    }
    
    private void loadMultilayerAssets() {
        try {
            // Cargar atlas de estados de vida (Capa Top)
            mainShipAtlas = new TextureAtlas(Gdx.files.internal("Game/Nave/Main/Main Ship.atlas"));
            
            // Cargar base/esqueleto (Capa Middle)
            Texture baseTexture = new Texture(Gdx.files.internal("Game/Nave/Main/Main Ship Base.png"));
            middleBaseRegion = new TextureRegion(baseTexture);
            
            // Cargar y crear animaciones de propulsión (Capas Low)
            createPropulsionAnimations();
        } catch (Exception e) {
            System.err.println("Error cargando assets multicapa: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void createPropulsionAnimations() {
        // Cargar atlas de propulsión IDLE
        TextureAtlas idleAtlas = new TextureAtlas(Gdx.files.internal("Game/Nave/Propulsion/PropulsionIDLE.atlas"));
        TextureRegion[] idleFrames = new TextureRegion[3];
        idleFrames[0] = idleAtlas.findRegion("Ship Idle 01");
        idleFrames[1] = idleAtlas.findRegion("Ship Idle 02");
        idleFrames[2] = idleAtlas.findRegion("Ship Idle 03");
        idleAnimation = new Animation<>(ANIMATION_FRAME_DURATION, idleFrames);
        idleAnimation.setPlayMode(Animation.PlayMode.LOOP);
        
        // Cargar atlas de propulsión POWER
        TextureAtlas powerAtlas = new TextureAtlas(Gdx.files.internal("Game/Nave/Propulsion/PropulsionPOWER.atlas"));
        TextureRegion[] powerFrames = new TextureRegion[4];
        powerFrames[0] = powerAtlas.findRegion("Ship Power 01");
        powerFrames[1] = powerAtlas.findRegion("Ship Power 02");
        powerFrames[2] = powerAtlas.findRegion("Ship Power 03");
        powerFrames[3] = powerAtlas.findRegion("Ship Power 04");
        powerAnimation = new Animation<>(ANIMATION_FRAME_DURATION, powerFrames);
        powerAnimation.setPlayMode(Animation.PlayMode.LOOP);
        
        // Cargar atlas de propulsión POWER VERTICAL
        TextureAtlas powerVerticalAtlas = new TextureAtlas(Gdx.files.internal("Game/Nave/Propulsion/PropulsionPOWERVERTICAL.atlas"));
        TextureRegion[] powerVerticalFrames = new TextureRegion[4];
        powerVerticalFrames[0] = powerVerticalAtlas.findRegion("Ship Power Vertical 01");
        powerVerticalFrames[1] = powerVerticalAtlas.findRegion("Ship Power Vertical 02");
        powerVerticalFrames[2] = powerVerticalAtlas.findRegion("Ship Power Vertical 03");
        powerVerticalFrames[3] = powerVerticalAtlas.findRegion("Ship Power Vertical 04");
        powerVerticalAnimation = new Animation<>(ANIMATION_FRAME_DURATION, powerVerticalFrames);
        powerVerticalAnimation.setPlayMode(Animation.PlayMode.LOOP);
    }
    
    private void updateShipTexture() {
        // SISTEMA MULTICAPA: La textura de vida se obtiene del atlas
        // en tiempo de renderizado, no se cambia el sprite base
        
        if (mainShipAtlas == null) return; // Aún no se han cargado los assets
        
        // La lógica de selección de textura por vida ahora se maneja
        // en el método getCurrentHealthRegion() que se llama durante draw()
    }
    
    private TextureRegion getCurrentHealthRegion() {
        if (mainShipAtlas == null) return null;
        
        float currentHealth = healthSystem.getCurrentHealth();
        String regionName;
        
        if (currentHealth >= 5.5f) {
            regionName = "Main Ship - Base - Full health";
        } else if (currentHealth >= 3.5f) {
            regionName = "Main Ship - Base - Slight damage";
        } else if (currentHealth >= 1.5f) {
            regionName = "Main Ship - Base - Damaged";
        } else {
            regionName = "Main Ship - Base - Very damaged";
        }
        
        return mainShipAtlas.findRegion(regionName);
    }

    @Override
    public void update(float delta) {
        updateShipTexture();
        
        // Actualizar arma
        if (currentWeapon != null) {
            currentWeapon.update(delta);
        }
        
        if (!herido) {
            float aceleracionX = 0f;
            float aceleracionY = 0f;
            
            // Detectar entrada de controles WASD
            if (Gdx.input.isKeyPressed(Input.Keys.W)) aceleracionY += ACELERACION;
            if (Gdx.input.isKeyPressed(Input.Keys.S)) aceleracionY -= ACELERACION;
            if (Gdx.input.isKeyPressed(Input.Keys.A)) aceleracionX -= ACELERACION;
            if (Gdx.input.isKeyPressed(Input.Keys.D)) aceleracionX += ACELERACION;
            
            // Aplicar aceleracion a la velocidad
            velocidadX += aceleracionX;
            velocidadY += aceleracionY;
            
            // Limitar velocidad maxima
            float velocidadTotal = (float) Math.sqrt(velocidadX * velocidadX + velocidadY * velocidadY);
            if (velocidadTotal > VELOCIDAD_MAXIMA) {
                float factor = VELOCIDAD_MAXIMA / velocidadTotal;
                velocidadX *= factor;
                velocidadY *= factor;
            }
            
            // Aplicar friccion espacial
            velocidadX *= FRICCION;
            velocidadY *= FRICCION;
            
            // Parar completamente si la velocidad es muy baja
            if (Math.abs(velocidadX) < UMBRAL_PARADA) velocidadX = 0f;
            if (Math.abs(velocidadY) < UMBRAL_PARADA) velocidadY = 0f;
            
            // Calcular nueva posicion
            float newX = x + velocidadX;
            float newY = y + velocidadY;
            
            // Verificar limites de pantalla usando dimensiones exactas del sprite
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
        
        // Actualizar animaciones multicapa
        updatePropulsionState();
        animationTime += delta;
    }
    
    private void updatePropulsionState() {
        PropulsionState newState;
        
        // Determinar el estado de propulsión basado en la velocidad
        float absVelX = Math.abs(velocidadX);
        float absVelY = Math.abs(velocidadY);
        
        // NUEVA LÓGICA: Si se mueve hacia arriba (Y negativo), mostrar IDLE como efecto de frenado
        if (velocidadY < -MOVEMENT_THRESHOLD) {
            newState = PropulsionState.IDLE;
        } else if (absVelY > MOVEMENT_THRESHOLD && absVelY > absVelX && velocidadY > 0) {
            // Movimiento vertical dominante hacia abajo
            newState = PropulsionState.POWER_VERTICAL;
        } else if (absVelX > MOVEMENT_THRESHOLD || (absVelY > MOVEMENT_THRESHOLD && velocidadY > 0)) {
            // Movimiento horizontal o vertical hacia abajo
            newState = PropulsionState.POWER;
        } else {
            // Sin movimiento significativo
            newState = PropulsionState.IDLE;
        }
        
        // Si el estado cambió, reiniciar el tiempo de animación para suavidad
        if (newState != currentPropulsionState) {
            currentPropulsionState = newState;
            animationTime = 0f;

        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        // Obtener la posición actual (con shake effect si está herido)
        float drawX = herido ? spr.getX() + MathUtils.random(-2, 2) : spr.getX();
        float drawY = spr.getY();
        
        // Renderizar sistema multicapa en orden: Low → Middle → Top
        
        // CAPA LOW: Propulsión (fondo)
        drawPropulsionLayer(batch, drawX, drawY);
        
        // CAPA MIDDLE: Base/esqueleto de la nave
        if (middleBaseRegion != null) {
            batch.draw(middleBaseRegion, drawX, drawY, targetWidth, targetHeight);
        }
        
        // CAPA TOP: Estados de vida (encima de todo)
        drawLifeStateLayer(batch, drawX, drawY);
        
        // Sistema de sprite legacy (mantener por compatibilidad pero hacerlo invisible)
        // El sprite legacy no se dibuja, pero mantiene su lógica de posición
        
        // Dibujar hitbox en modo testing
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
        
        // Seleccionar la animación correcta según el estado actual
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
        
        // Dibujar el frame actual de la animación seleccionada
        if (currentAnimation != null) {
            TextureRegion currentFrame = currentAnimation.getKeyFrame(animationTime);
            if (currentFrame != null) {
                batch.draw(currentFrame, x, y, targetWidth, targetHeight);
            }
        }
    }
    
    private void drawLifeStateLayer(SpriteBatch batch, float x, float y) {
        if (mainShipAtlas != null) {
            // Determinar el estado de vida actual basado en el healthSystem
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
        // Crear hitbox basado en el 50% del area visible (sprite escalado)
        // Esto asegura que el hitbox este centrado y sea mucho mas restrictivo que la imagen completa
        float preciseWidth = spr.getWidth() * 0.5f;
        float preciseHeight = spr.getHeight() * 0.5f;

        float offsetX = (spr.getWidth() - preciseWidth) / 2f;
        float offsetY = (spr.getHeight() - preciseHeight) / 2f;

        return new Rectangle(spr.getX() + offsetX, spr.getY() + offsetY, preciseWidth, preciseHeight);
    }
    
    public void handleInput(PantallaJuego juego) {
        // Disparo automático mientras se mantiene presionada la tecla SPACE
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            currentWeapon.fire(spr.getX() + spr.getWidth() / 2 - 5, spr.getY() + spr.getHeight() - 5, juego.getProjectileManager());
        }
        
        // Cerrar juego con ESC
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }
      
    public boolean checkCollision(MeteoriteEnemy b) {
    if (!herido && b.getBounds().overlaps(getBounds())) {
            // Rebote simplificado
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
                
                // Usar dimensiones exactas del sprite para limites
                if (newX >= 0 && newX + spr.getWidth() <= 1920) {
                    spr.setX(newX);
                    x = newX;
                }
                if (newY >= 0 && newY + spr.getHeight() <= 1080) {
                    spr.setY(newY);
                    y = newY;
                }
            }
            
            // Solo cambiar direccion horizontal del asteroide, mantener vertical hacia abajo
            b.velocityX = -b.velocityX; // Rebotar horizontalmente
            // NO cambiar velocityY para que siga yendo hacia abajo
            // Asegurar que vaya hacia abajo si por alguna razon esta yendo hacia arriba
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
    
    public boolean estaDestruido() {
        return destruida; // Si esta destruida, no importa si esta herida
    }
    
    public boolean estaHerido() {
        return herido;
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
    
    // Método para cambiar armas dinamicamente
    public void setWeapon(Weapon newWeapon) {
        this.currentWeapon = newWeapon;
    }
    
    public Weapon getCurrentWeapon() {
        return currentWeapon;
    }
    
    public void dispose() {
        // SISTEMA MULTICAPA: shipTextures obsoleto, ahora se usan atlas
        
        // Liberar recursos multicapa
        if (mainShipAtlas != null) {
            mainShipAtlas.dispose();
        }
        
        if (middleBaseRegion != null && middleBaseRegion.getTexture() != null) {
            middleBaseRegion.getTexture().dispose();
        }
        
        if (shapeRenderer != null) {
            try {
                shapeRenderer.dispose();
            } catch (Exception e) {
                System.err.println("Error liberando ShapeRenderer: " + e.getMessage());
            }
            shapeRenderer = null;
        }
    }
}