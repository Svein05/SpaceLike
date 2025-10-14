package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

/*
 * APARTADO NAVE:
 * // TODO: Hacer un spritesheet con atlas de las texturas de la nave
 * // TODO: Implementar la visualización de la animación de propulsión (que va separada de la textura de la nave)
 * // TODO: Cambiar el ataque y la animación de ataque
 * 
 * APARTADO JUGABILIDAD:
 * // TODO: Agregar un sistema roguelike para aumentar estadísticas base a la nave (vida, daño, resistencia, etc)
 * // TODO: Implementar caída de objetos bonus que sean armas diferentes que ayuden de manera temporal al jugador (doble de balas, un rayo laser, etc)
 * // TODO: Implementar final boss por cada etapa terminada (cada etapa tendría que constar de 5 rondas)
 * 
 * APARTADO ENEMIGOS:
 * // TODO: Implementar diversos boss final (aproximados 7)
 * // TODO: Cambiar los sprites de los asteroides y mejorar animación de explosión
 * // TODO: Implementar enemigos únicos variables
 * 
 * APARTADO MENÚS:
 * // TODO: Implementar menú inicial, que contenga la pantalla del juego, con el botón de jugar
 * // TODO: Mejorar interfaces y visibilidad del juego
 * 
 * APARTADO MÚSICA / SOUND EFFECTS:
 * // TODO: Cambiar o implementar nuevos efectos o música para la nave y etapas del juego
 * 
 * ========================================
 */



public class Nave4 {
	
	private boolean destruida = false;
    private Sprite spr;
    private Sound sonidoHerido;
    private Sound soundBala;
    private Texture txBala;
    private boolean herido = false;
    private int tiempoHeridoMax=50;
    private int tiempoHerido;
    
    // Texturas para diferentes estados de vida
    private Texture[] shipTextures;
    private int currentTextureIndex = 0;
    
    // Sistema de física realista para la nave
    private float velocidadX = 0f; // Velocidad actual en X
    private float velocidadY = 0f; // Velocidad actual en Y
    private static final float ACELERACION = 0.3f; // Aceleración por frame
    private static final float VELOCIDAD_MAXIMA = 5f; // Velocidad máxima
    private static final float FRICCION = 0.96f; // Fricción espacial (0-1, donde 1 = sin fricción)
    private static final float UMBRAL_PARADA = 0.1f; // Velocidad mínima antes de parar completamente
    
    // Sistema de corazones
    private HealthSystem healthSystem;
    
    public Nave4(int x, int y, Texture tx, Sound soundChoque, Texture txBala, Sound soundBala) {
    	sonidoHerido = soundChoque;
    	this.soundBala = soundBala;
    	this.txBala = txBala;
    	
    	// Cargar todas las texturas de la nave (del estado más dañado al más saludable)
    	shipTextures = new Texture[4];
    	shipTextures[0] = new Texture("Game/Nave/Main Ship/Main Ship - Bases/PNGs/Main Ship - Base - Very damaged.png");
    	shipTextures[1] = new Texture("Game/Nave/Main Ship/Main Ship - Bases/PNGs/Main Ship - Base - Damaged.png");
    	shipTextures[2] = new Texture("Game/Nave/Main Ship/Main Ship - Bases/PNGs/Main Ship - Base - Slight damage.png");
    	shipTextures[3] = new Texture("Game/Nave/Main Ship/Main Ship - Bases/PNGs/Main Ship - Base - Full health.png");
    	
    	// Inicializar sprite con textura de vida completa
    	currentTextureIndex = 3; // Índice para vida completa
    	spr = new Sprite(shipTextures[currentTextureIndex]);
    	spr.setPosition(x, y);
    	//spr.setOriginCenter();
    	spr.setBounds(x, y, 76, 76); // Aumentado 20% adicional: 63 * 1.2 = 75.6 ≈ 76
    	
    	// Inicializar sistema de corazones
    	healthSystem = new HealthSystem();
    }
    
    /**
     * Actualiza la textura de la nave según su vida actual
     */
    private void updateShipTexture() {
        float currentHealth = healthSystem.getCurrentHealth();
        int newTextureIndex;
        
        // Determinar qué textura usar según la vida
        if (currentHealth >= 5.5f) { // 3 corazones completos o casi
            newTextureIndex = 3; // Full health
        } else if (currentHealth >= 3.5f) { // 2+ corazones
            newTextureIndex = 2; // Slight damage
        } else if (currentHealth >= 1.5f) { // 1+ corazones
            newTextureIndex = 1; // Damaged
        } else { // Menos de 1 corazón
            newTextureIndex = 0; // Very damaged
        }
        
        // Cambiar textura solo si es diferente (evita cargas innecesarias)
        if (newTextureIndex != currentTextureIndex) {
            currentTextureIndex = newTextureIndex;
            spr.setTexture(shipTextures[currentTextureIndex]);
        }
    }
    public void draw(SpriteBatch batch, PantallaJuego juego){
        // Actualizar textura según vida actual
        updateShipTexture();
        
        float x = spr.getX();
        float y = spr.getY();
        
        if (!herido) {
	        // Sistema de física realista con inercia
	        float aceleracionX = 0f;
	        float aceleracionY = 0f;
	        
	        // Detectar entrada de controles WASD y aplicar aceleración
	        if (Gdx.input.isKeyPressed(Input.Keys.W)) aceleracionY += ACELERACION; // Arriba
	        if (Gdx.input.isKeyPressed(Input.Keys.S)) aceleracionY -= ACELERACION; // Abajo
	        if (Gdx.input.isKeyPressed(Input.Keys.A)) aceleracionX -= ACELERACION; // Izquierda
	        if (Gdx.input.isKeyPressed(Input.Keys.D)) aceleracionX += ACELERACION; // Derecha
	        
	        // Aplicar aceleración a la velocidad
	        velocidadX += aceleracionX;
	        velocidadY += aceleracionY;
	        
	        // Limitar velocidad máxima
	        float velocidadTotal = (float) Math.sqrt(velocidadX * velocidadX + velocidadY * velocidadY);
	        if (velocidadTotal > VELOCIDAD_MAXIMA) {
	            float factor = VELOCIDAD_MAXIMA / velocidadTotal;
	            velocidadX *= factor;
	            velocidadY *= factor;
	        }
	        
	        // Aplicar fricción espacial para decelerar gradualmente
	        velocidadX *= FRICCION;
	        velocidadY *= FRICCION;
	        
	        // Parar completamente si la velocidad es muy baja (evitar vibración mínima)
	        if (Math.abs(velocidadX) < UMBRAL_PARADA) velocidadX = 0f;
	        if (Math.abs(velocidadY) < UMBRAL_PARADA) velocidadY = 0f;
	        
	        // Calcular nueva posición basada en la velocidad actual
	        float newX = x + velocidadX;
	        float newY = y + velocidadY;
	        
	        // Verificar límites de pantalla y aplicar nueva posición
	        if (newX >= 0 && newX + spr.getWidth() <= 1920) {
	            spr.setX(newX);
	        } else {
	            // Si choca con los bordes, detener velocidad en X
	            velocidadX = 0f;
	        }
	        if (newY >= 0 && newY + spr.getHeight() <= 1080) {
	            spr.setY(newY);
	        } else {
	            // Si choca con los bordes, detener velocidad en Y
	            velocidadY = 0f;
	        }
         
 		    spr.draw(batch);
        } else {
           spr.setX(spr.getX()+MathUtils.random(-2,2));
 		   spr.draw(batch); 
 		   spr.setX(x);
 		   tiempoHerido--;
 		   if (tiempoHerido<=0) herido = false;
 		}
        
        // Disparo con SPACE
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {         
          Bullet bala = new Bullet(spr.getX()+spr.getWidth()/2-5,spr.getY()+ spr.getHeight()-5,0,3,txBala);
	      juego.agregarBala(bala);
	      soundBala.play();
        }
        
        // Cerrar juego con ESC
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }
      
    public boolean checkCollision(Ball2 b) {
        if(!herido && b.getArea().overlaps(spr.getBoundingRectangle())){
        	// Rebote simplificado - empujar la nave y el asteroide en direcciones opuestas
            float pushForce = 20f; // Fuerza de empuje
            
            // Calcular dirección del empuje
            float centerX = spr.getX() + spr.getWidth()/2;
            float centerY = spr.getY() + spr.getHeight()/2;
            float ballCenterX = b.getArea().x + b.getArea().width/2;
            float ballCenterY = b.getArea().y + b.getArea().height/2;
            
            // Empujar nave en dirección opuesta al asteroide
            float deltaX = centerX - ballCenterX;
            float deltaY = centerY - ballCenterY;
            float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
            
            if (distance > 0) {
                deltaX /= distance;
                deltaY /= distance;
                
                // Mover nave
                float newX = spr.getX() + deltaX * pushForce;
                float newY = spr.getY() + deltaY * pushForce;
                
                // Verificar límites
                if (newX >= 0 && newX + spr.getWidth() <= 1920) {
                    spr.setX(newX);
                }
                if (newY >= 0 && newY + spr.getHeight() <= 1080) {
                    spr.setY(newY);
                }
            }
            
            // Cambiar dirección del asteroide
            b.setXSpeed(-b.getXSpeed());
            b.setySpeed(-b.getySpeed());
            
        	//actualizar vidas y herir
            // En lugar de quitar una vida completa, quitamos medio corazón
            healthSystem.takeDamage();
            herido = true;
  		    tiempoHerido=tiempoHeridoMax;
  		    
  		    // Resetear velocidad al ser herida para evitar movimiento errático
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
    
    public int getVidas() {return healthSystem.getVidas();}
    //public boolean isDestruida() {return destruida;}
    public int getX() {return (int) spr.getX();}
    public int getY() {return (int) spr.getY();}
	public void setVidas(int vidas2) {
		healthSystem.setVidas(vidas2);
	}
	
	/**
	 * Renderiza los corazones de vida en pantalla
	 */
	public void renderHealthHearts(SpriteBatch batch) {
		// Posición de los corazones: esquina superior izquierda del viewport 1920x1080
		healthSystem.render(batch, 30, 1030); // 30 desde izquierda, 1030 es cerca del tope (1080-50)
	}
	
	/**
	 * Libera las texturas de la nave para evitar memory leaks
	 */
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
