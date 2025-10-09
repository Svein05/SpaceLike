package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;



public class Nave4 {
	
	private boolean destruida = false;
    private Sprite spr;
    private Sound sonidoHerido;
    private Sound soundBala;
    private Texture txBala;
    private boolean herido = false;
    private int tiempoHeridoMax=50;
    private int tiempoHerido;
    
    // Velocidad fija para controles directos
    private static final float VELOCIDAD_NAVE = 4f;
    
    // Sistema de corazones
    private HealthSystem healthSystem;
    
    public Nave4(int x, int y, Texture tx, Sound soundChoque, Texture txBala, Sound soundBala) {
    	sonidoHerido = soundChoque;
    	this.soundBala = soundBala;
    	this.txBala = txBala;
    	spr = new Sprite(tx);
    	spr.setPosition(x, y);
    	//spr.setOriginCenter();
    	spr.setBounds(x, y, 45, 45);
    	
    	// Inicializar sistema de corazones
    	healthSystem = new HealthSystem();
    }
    public void draw(SpriteBatch batch, PantallaJuego juego){
        float x = spr.getX();
        float y = spr.getY();
        
        if (!herido) {
	        // Nuevo sistema de movimiento WASD directo
	        float newX = x;
	        float newY = y;
	        
	        // Controles WASD
	        if (Gdx.input.isKeyPressed(Input.Keys.W)) newY += VELOCIDAD_NAVE; // Arriba
	        if (Gdx.input.isKeyPressed(Input.Keys.S)) newY -= VELOCIDAD_NAVE; // Abajo
	        if (Gdx.input.isKeyPressed(Input.Keys.A)) newX -= VELOCIDAD_NAVE; // Izquierda
	        if (Gdx.input.isKeyPressed(Input.Keys.D)) newX += VELOCIDAD_NAVE; // Derecha
	        
	        // Verificar límites de pantalla y aplicar nueva posición
	        if (newX >= 0 && newX + spr.getWidth() <= 1920) {
	            spr.setX(newX);
	        }
	        if (newY >= 0 && newY + spr.getHeight() <= 1080) {
	            spr.setY(newY);
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
}
