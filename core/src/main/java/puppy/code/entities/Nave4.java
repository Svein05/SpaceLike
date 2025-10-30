package puppy.code.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import puppy.code.entities.enemies.Ball2;
import puppy.code.screens.PantallaJuego;
import puppy.code.systems.HealthSystem;
import puppy.code.interfaces.Weapon;
import puppy.code.weapons.BasicCannon;

public class Nave4 extends GameObject {
    private boolean destruida = false;
    private Sprite spr;
    private Sound sonidoHerido;
    private Sound soundBala;
    private boolean herido = false;
    private int tiempoHeridoMax = 50;
    private int tiempoHerido;
    
    // Texturas para diferentes estados de vida
    private Texture[] shipTextures;
    private int currentTextureIndex = 0;
    
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
    
    public Nave4(int x, int y, Texture tx, Sound soundChoque, Texture txBala, Sound soundBala) {
        super(x, y, 76, 76);
        sonidoHerido = soundChoque;
        this.soundBala = soundBala;
        
        // Cargar todas las texturas de la nave
        shipTextures = new Texture[4];
        shipTextures[0] = new Texture("Game/Nave/Main Ship/Main Ship - Bases/PNGs/Main Ship - Base - Very damaged.png");
        shipTextures[1] = new Texture("Game/Nave/Main Ship/Main Ship - Bases/PNGs/Main Ship - Base - Damaged.png");
        shipTextures[2] = new Texture("Game/Nave/Main Ship/Main Ship - Bases/PNGs/Main Ship - Base - Slight damage.png");
        shipTextures[3] = new Texture("Game/Nave/Main Ship/Main Ship - Bases/PNGs/Main Ship - Base - Full health.png");
        
        // Inicializar sprite con textura de vida completa
        currentTextureIndex = 3;
        spr = new Sprite(shipTextures[currentTextureIndex]);
        spr.setPosition(x, y);
        spr.setBounds(x, y, 76, 76);
        
        // Inicializar sistema de corazones
        healthSystem = new HealthSystem();
        
        // Inicializar arma basica (ya no necesita textura)
        currentWeapon = new BasicCannon();
    }
    
    private void updateShipTexture() {
        float currentHealth = healthSystem.getCurrentHealth();
        int newTextureIndex;
        
        if (currentHealth >= 5.5f) {
            newTextureIndex = 3; // Full health
        } else if (currentHealth >= 3.5f) {
            newTextureIndex = 2; // Slight damage
        } else if (currentHealth >= 1.5f) {
            newTextureIndex = 1; // Damaged
        } else {
            newTextureIndex = 0; // Very damaged
        }
        
        if (newTextureIndex != currentTextureIndex) {
            currentTextureIndex = newTextureIndex;
            spr.setTexture(shipTextures[currentTextureIndex]);
        }
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
            
            // Verificar limites de pantalla
            if (newX >= 0 && newX + width <= 1920) {
                x = newX;
                spr.setX(x);
            } else {
                velocidadX = 0f;
            }
            if (newY >= 0 && newY + height <= 1080) {
                y = newY;
                spr.setY(y);
            } else {
                velocidadY = 0f;
            }
        } else {
            tiempoHerido--;
            if (tiempoHerido <= 0) herido = false;
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (!herido) {
            spr.draw(batch);
        } else {
            spr.setX(spr.getX() + MathUtils.random(-2, 2));
            spr.draw(batch);
            spr.setX(x);
        }
    }

    @Override
    public Rectangle getBounds() {
        return spr.getBoundingRectangle();
    }
    
    public void handleInput(PantallaJuego juego) {
        // Disparo automático mientras se mantiene presionada la tecla SPACE
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            boolean fired = currentWeapon.fire(spr.getX() + spr.getWidth() / 2 - 5, spr.getY() + spr.getHeight() - 5, juego.getProjectileManager());
            if (fired) {
                soundBala.play();
            }
        }
        
        // Cerrar juego con ESC
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }
      
    public boolean checkCollision(Ball2 b) {
        if (!herido && b.getBounds().overlaps(spr.getBoundingRectangle())) {
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
                
                if (newX >= 0 && newX + spr.getWidth() <= 1920) {
                    spr.setX(newX);
                    x = newX;
                }
                if (newY >= 0 && newY + spr.getHeight() <= 1080) {
                    spr.setY(newY);
                    y = newY;
                }
            }
            
            // Cambiar direccion del asteroide
            b.velocityX = -b.velocityX;
            b.velocityY = -b.velocityY;
            
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
        return !herido && destruida;
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
    
    public void renderHealthHearts(SpriteBatch batch) {
        healthSystem.render(batch, 30, 1030);
    }
    
    public void dispose() {
        if (shipTextures != null) {
            for (Texture texture : shipTextures) {
                if (texture != null) {
                    texture.dispose();
                }
            }
        }
    }
}