package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


public class PantallaMenu implements Screen {

	private SpaceNavigation game;
	private OrthographicCamera camera;
	private Viewport viewport;
	
	// Imagen de fondo de portada
	private Texture portadaTexture;
	
	// Área del botón Start Game que ya está en la imagen
	private Rectangle startButtonArea;
	
	// Música del menú
	private Music menuMusic;
	
	// Efectos de sonido
	private Sound selectSound;
	private Sound startGameSound;
	
	// Variables para el fade out
	private boolean fadingOut = false;
	private float fadeTimer = 0f;
	private final float FADE_DURATION = 1.0f; // 1 segundo de fade
	
	// Control de hover
	private boolean isHovering = false;

	public PantallaMenu(SpaceNavigation game) {
		this.game = game;
        
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 1920, 1080); // Consistente con PantallaJuego
		viewport = new FitViewport(1920, 1080, camera);
		
		// Cargar imagen de portada
		portadaTexture = new Texture(Gdx.files.internal("UI/Portada/Gemini_Generated_Image_yonktcyonktcyonk (2).png"));
		
		// Área EXACTA del botón "Start Game" basada en el rectángulo rojo de la imagen
		// El botón está centrado horizontalmente pero MUCHO más abajo del centro vertical
		float buttonWidth = 320f;  // Ancho del rectángulo rojo que marcaste
		float buttonHeight = 70f;  // Alto del rectángulo rojo que marcaste  
		float buttonX = (1920f - buttonWidth) / 2f; // Centrado horizontalmente (800-1120)
		float buttonY = 250f; // Posición vertical MUCHO más abajo
		startButtonArea = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
		
		// Cargar y configurar música del menú - Over Now
		menuMusic = Gdx.audio.newMusic(Gdx.files.internal("Game/VFX/Music/Over Now.mp3"));
		menuMusic.setLooping(true);
		menuMusic.setVolume(0.25f); // Mismo volumen que otros elementos
		menuMusic.play();
		
		// Cargar efectos de sonido
		selectSound = Gdx.audio.newSound(Gdx.files.internal("Game/VFX/Effects/select.mp3"));
		startGameSound = Gdx.audio.newSound(Gdx.files.internal("Game/VFX/Effects/StartGame.mp3"));
	}

	@Override
	public void render(float delta) {
		ScreenUtils.clear(0, 0, 0, 1); // Fondo negro

		viewport.apply();
		camera.update();
		game.getBatch().setProjectionMatrix(camera.combined);

		game.getBatch().begin();
		
		// Dibujar imagen de portada como fondo (escalada a pantalla completa)
		game.getBatch().draw(portadaTexture, 0, 0, 1920, 1080);
		
		game.getBatch().end();

		// Sistema de fade out
		if (fadingOut) {
			fadeTimer += delta;
			float fadeProgress = fadeTimer / FADE_DURATION;
			
			// Reducir volumen gradualmente
			float newVolume = 0.25f * (1.0f - fadeProgress);
			menuMusic.setVolume(Math.max(0f, newVolume));
			
			// Cuando el fade termine, cambiar de pantalla
			if (fadeTimer >= FADE_DURATION) {
				menuMusic.stop();
				Screen ss = new PantallaJuego(game,1,3,0,1,1,10);
				ss.resize(1920, 1080);
				game.setScreen(ss);
				dispose();
			}
		} else {
			// Obtener posición del mouse de forma inmediata
			Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(mousePos);
			
			// Detectar hover sobre el área del botón con detección inmediata
			boolean currentlyHovering = startButtonArea.contains(mousePos.x, mousePos.y);
			
			// Si acabamos de entrar al área del botón (hover), reproducir sonido select
			if (currentlyHovering && !isHovering) {
				selectSound.play(0.3f);
			}
			
			// Actualizar estado de hover inmediatamente
			isHovering = currentlyHovering;
			
			// Detectar clic SOLO si está dentro del área del botón
			if (Gdx.input.justTouched() && startButtonArea.contains(mousePos.x, mousePos.y)) {
				// Reproducir sonido de inicio antes del fade
				startGameSound.play(0.25f);
				
				// Iniciar el fade out
				fadingOut = true;
				fadeTimer = 0f;
			}
		}
	}
	
	
	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resize(int width, int height) {
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
		if (portadaTexture != null) {
			portadaTexture.dispose();
		}
		if (menuMusic != null) {
			menuMusic.stop();
			menuMusic.dispose();
		}
		if (selectSound != null) {
			selectSound.dispose();
		}
		if (startGameSound != null) {
			startGameSound.dispose();
		}
	}
   
}