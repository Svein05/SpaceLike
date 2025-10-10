package puppy.code;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


public class PantallaJuego implements Screen {

	private SpaceNavigation game;
	private OrthographicCamera camera;	
	private Viewport viewport; // Viewport para escalado automático
	private SpriteBatch batch;
	private Sound explosionSound;
	private Music gameMusic;
	private int score;
	private int ronda;
	private int velXAsteroides; 
	private int velYAsteroides; 
	private int cantAsteroides;
	
	private Nave4 nave;
	private  ArrayList<Ball2> balls1 = new ArrayList<>();
	private  ArrayList<Ball2> balls2 = new ArrayList<>();
	private  ArrayList<Bullet> balas = new ArrayList<>();
	
	// Fondo parallax
	private Texture fondoSpaceBackground; // Capa 0 - Space Background
	private Texture fondoSpaceStars01; // Capa 1 - Space Stars 01
	private Texture fondoSpaceDust; // Capa 1.1 - Space Dust
	private Texture fondoNebulose; // Capa 2 - Space Nebulose
	private Texture fondoSpaceStars03; // Capa 3 - Space Stars 03
	private float spaceBackgroundOffsetY; // Desplazamiento para Space Background
	private float spaceStars01OffsetY; // Desplazamiento para Space Stars 01
	private float spaceDustOffsetY; // Desplazamiento para Space Dust
	private float nebuloseOffsetY; // Desplazamiento para la capa de nebulosa
	private float spaceStars03OffsetY; // Desplazamiento para Space Stars 03


	public PantallaJuego(SpaceNavigation game, int ronda, int vidas, int score,  
			int velXAsteroides, int velYAsteroides, int cantAsteroides) {
		this.game = game;
		this.ronda = ronda;
		this.score = score;
		this.velXAsteroides = velXAsteroides;
		this.velYAsteroides = velYAsteroides;
		this.cantAsteroides = cantAsteroides;
		
		batch = game.getBatch();
		camera = new OrthographicCamera();	
		camera.setToOrtho(false, 1920, 1080); // Mantener resolución base
		
		// Crear viewport con escalado automático
		viewport = new FitViewport(1920, 1080, camera);
		
		// Cargar texturas para parallax
		fondoSpaceBackground = new Texture(Gdx.files.internal("Game/Fondo/Backgrounds/PNGs/Split up/00 Space Background.png"));
		fondoSpaceStars01 = new Texture(Gdx.files.internal("Game/Fondo/Backgrounds/PNGs/Split up/01 Space Stars.png"));
		fondoSpaceDust = new Texture(Gdx.files.internal("Game/Fondo/Backgrounds/PNGs/Split up/01_1 Space Dust.png"));
		fondoNebulose = new Texture(Gdx.files.internal("Game/Fondo/Backgrounds/PNGs/Split up/02 Space Nebulose.png"));
		fondoSpaceStars03 = new Texture(Gdx.files.internal("Game/Fondo/Backgrounds/PNGs/Split up/03 Space Stars.png"));
		spaceBackgroundOffsetY = 0f; // Inicializar offset Space Background
		spaceStars01OffsetY = 0f; // Inicializar offset Space Stars 01
		spaceDustOffsetY = 0f; // Inicializar offset Space Dust
		nebuloseOffsetY = 0f; // Inicializar offset de la nebulosa
		spaceStars03OffsetY = 0f; // Inicializar offset Space Stars 03
		
		//inicializar assets; musica de fondo y efectos de sonido
		explosionSound = Gdx.audio.newSound(Gdx.files.internal("explosion.ogg"));
		explosionSound.setVolume(1,0.5f);
		gameMusic = Gdx.audio.newMusic(Gdx.files.internal("piano-loops.wav")); //
		
		gameMusic.setLooping(true);
		gameMusic.setVolume(0.5f);
		gameMusic.play();
		
	    // cargar imagen de la nave, 64x64   
	    nave = new Nave4(Gdx.graphics.getWidth()/2-50,30,new Texture(Gdx.files.internal("MainShip3.png")),
	    				Gdx.audio.newSound(Gdx.files.internal("hurt.ogg")), 
	    				new Texture(Gdx.files.internal("Rocket2.png")), 
	    				Gdx.audio.newSound(Gdx.files.internal("pop-sound.mp3"))); 
        nave.setVidas(vidas);
        //crear asteroides
        Random r = new Random();
	    for (int i = 0; i < cantAsteroides; i++) {
	        Ball2 bb = new Ball2(r.nextInt((int)Gdx.graphics.getWidth()),
	  	            50+r.nextInt((int)Gdx.graphics.getHeight()-50),
	  	            20+r.nextInt(10), velXAsteroides+r.nextInt(4), velYAsteroides+r.nextInt(4), 
	  	            new Texture(Gdx.files.internal("aGreyMedium4.png")));	   
	  	    balls1.add(bb);
	  	    balls2.add(bb);
	  	}
	}
    
	public void dibujaEncabezado() {
		// Solo mostrar ronda y scores - los corazones se muestran por separado
		// Usar coordenadas de la cámara 1920x1080
		CharSequence str = "Ronda: "+ronda;
		game.getFont().getData().setScale(2f);		
		game.getFont().draw(batch, str, 30, 50); // Más espacio desde los bordes
		game.getFont().draw(batch, "Score:"+this.score, 1920-300, 50); // Esquina derecha
		game.getFont().draw(batch, "HighScore:"+game.getHighScore(), 1920/2-150, 50); // Centro
	}
	@Override
	public void render(float delta) {
		  Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		  
		  // Actualizar viewport y cámara
		  viewport.apply();
		  camera.update();
		  batch.setProjectionMatrix(camera.combined);
		  
          batch.begin();
          
          // Fondo negro estático (ya se limpia con glClear)
          
          // Parallax vertical hacia abajo (los objetos se quedan atrás mientras avanzamos)
          // Capa 0 - Space Background (velocidad más lenta)
          spaceBackgroundOffsetY -= 60f * delta; // Velocidad: 60 pixels por segundo
          
          // Resetear cuando la imagen se sale completamente de pantalla por abajo
          if (spaceBackgroundOffsetY <= -1080) {
              spaceBackgroundOffsetY = 0f;
          }
          
          // Dibujar capa 0 - Space Background
          // Primera instancia (principal)
          batch.draw(fondoSpaceBackground, 0, spaceBackgroundOffsetY, 1920, 1080);
          // Segunda instancia (para continuidad cuando la primera sale por abajo)
          batch.draw(fondoSpaceBackground, 0, spaceBackgroundOffsetY + 1080, 1920, 1080);
          
          // Capa 1 - Space Stars 01 (velocidad lenta-media) con opacidad reducida al 50%
          spaceStars01OffsetY -= 80f * delta; // Velocidad: 80 pixels por segundo
          
          // Resetear cuando la imagen se sale completamente de pantalla por abajo
          if (spaceStars01OffsetY <= -1080) {
              spaceStars01OffsetY = 0f;
          }
          
          // Reducir opacidad al 50% para Space Stars 01
          batch.setColor(1f, 1f, 1f, 0.5f); // RGBA: blanco con 50% de opacidad
          
          // Dibujar capa 1 - Space Stars 01 con opacidad reducida
          // Primera instancia (principal)
          batch.draw(fondoSpaceStars01, 0, spaceStars01OffsetY, 1920, 1080);
          // Segunda instancia (para continuidad cuando la primera sale por abajo)
          batch.draw(fondoSpaceStars01, 0, spaceStars01OffsetY + 1080, 1920, 1080);
          
          // Restaurar opacidad original
          batch.setColor(1f, 1f, 1f, 1f); // RGBA: blanco con 100% de opacidad
          
          // Capa 1.1 - Space Dust (velocidad intermedia entre Stars 01 y Nebulose)
          spaceDustOffsetY -= 90f * delta; // Velocidad: 90 pixels por segundo
          
          // Resetear cuando la imagen se sale completamente de pantalla por abajo
          if (spaceDustOffsetY <= -1080) {
              spaceDustOffsetY = 0f;
          }
          
          // Dibujar capa 1.1 - Space Dust
          // Primera instancia (principal)
          batch.draw(fondoSpaceDust, 0, spaceDustOffsetY, 1920, 1080);
          // Segunda instancia (para continuidad cuando la primera sale por abajo)
          batch.draw(fondoSpaceDust, 0, spaceDustOffsetY + 1080, 1920, 1080);
          
          // Capa 2 - Space Nebulose (velocidad media)
          nebuloseOffsetY -= 100f * delta; // Velocidad: 100 pixels por segundo
          
          // Resetear cuando la imagen se sale completamente de pantalla por abajo
          if (nebuloseOffsetY <= -1080) {
              nebuloseOffsetY = 0f;
          }
          
          // Dibujar capa 2 - Space Nebulose
          // Primera instancia (principal)
          batch.draw(fondoNebulose, 0, nebuloseOffsetY, 1920, 1080);
          // Segunda instancia (para continuidad cuando la primera sale por abajo)
          batch.draw(fondoNebulose, 0, nebuloseOffsetY + 1080, 1920, 1080);
          
          // Capa 3 - Space Stars 03 (velocidad más rápida, estrellas más próximas)
          spaceStars03OffsetY -= 120f * delta; // Velocidad: 120 pixels por segundo
          
          // Resetear cuando la imagen se sale completamente de pantalla por abajo
          if (spaceStars03OffsetY <= -1080) {
              spaceStars03OffsetY = 0f;
          }
          
          // Dibujar capa 3 - Space Stars 03
          // Primera instancia (principal)
          batch.draw(fondoSpaceStars03, 0, spaceStars03OffsetY, 1920, 1080);
          // Segunda instancia (para continuidad cuando la primera sale por abajo)
          batch.draw(fondoSpaceStars03, 0, spaceStars03OffsetY + 1080, 1920, 1080);
          
		  dibujaEncabezado();
	      if (!nave.estaHerido()) {
		      // colisiones entre balas y asteroides y su destruccion  
	    	  for (int i = 0; i < balas.size(); i++) {
		            Bullet b = balas.get(i);
		            b.update();
		            for (int j = 0; j < balls1.size(); j++) {    
		              if (b.checkCollision(balls1.get(j))) {          
		            	 explosionSound.play();
		            	 balls1.remove(j);
		            	 balls2.remove(j);
		            	 j--;
		            	 score +=10;
		              }   	  
		  	        }
		                
		         //   b.draw(batch);
		            if (b.isDestroyed()) {
		                balas.remove(b);
		                i--; //para no saltarse 1 tras eliminar del arraylist
		            }
		      }
		      //actualizar movimiento de asteroides dentro del area
		      for (Ball2 ball : balls1) {
		          ball.update();
		      }
		      //colisiones entre asteroides y sus rebotes  
		      for (int i=0;i<balls1.size();i++) {
		    	Ball2 ball1 = balls1.get(i);   
		        for (int j=0;j<balls2.size();j++) {
		          Ball2 ball2 = balls2.get(j); 
		          if (i<j) {
		        	  ball1.checkCollision(ball2);
		     
		          }
		        }
		      } 
	      }
	      //dibujar balas
	     for (Bullet b : balas) {       
	          b.draw(batch);
	      }
	      nave.draw(batch, this);
	      
	      // Dibujar corazones de vida
	      nave.renderHealthHearts(batch);
	      
	      //dibujar asteroides y manejar colision con nave
	      for (int i = 0; i < balls1.size(); i++) {
	    	    Ball2 b=balls1.get(i);
	    	    b.draw(batch);
		          //perdió vida o game over
	              if (nave.checkCollision(b)) {
		            //asteroide se destruye con el choque             
	            	 balls1.remove(i);
	            	 balls2.remove(i);
	            	 i--;
              }   	  
  	        }
	      
	      if (nave.estaDestruido()) {
  			if (score > game.getHighScore())
  				game.setHighScore(score);
	    	Screen ss = new PantallaGameOver(game);
  			ss.resize(1920, 1080);
  			game.setScreen(ss);
  			dispose();
  		  }
	      batch.end();
	      //nivel completado
	      if (balls1.size()==0) {
			Screen ss = new PantallaJuego(game,ronda+1, nave.getVidas(), score, 
					velXAsteroides+3, velYAsteroides+3, cantAsteroides+10);
			ss.resize(1920, 1080);
			game.setScreen(ss);
			dispose();
		  }
	    	 
	}
    
    public boolean agregarBala(Bullet bb) {
    	return balas.add(bb);
    }
	
	@Override
	public void show() {
		// TODO Auto-generated method stub
		gameMusic.play();
	}

	@Override
	public void resize(int width, int height) {
		// Actualizar viewport cuando cambie el tamaño de ventana
		viewport.update(width, height);
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// Limpiar recursos
		this.explosionSound.dispose();
		this.gameMusic.dispose();
		if (fondoSpaceBackground != null) {
			fondoSpaceBackground.dispose();
		}
		if (fondoSpaceStars01 != null) {
			fondoSpaceStars01.dispose();
		}
		if (fondoSpaceDust != null) {
			fondoSpaceDust.dispose();
		}
		if (fondoNebulose != null) {
			fondoNebulose.dispose();
		}
		if (fondoSpaceStars03 != null) {
			fondoSpaceStars03.dispose();
		}
	}
   
}
