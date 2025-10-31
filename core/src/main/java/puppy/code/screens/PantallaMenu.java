package puppy.code.screens;

import puppy.code.SpaceNavigation;
import puppy.code.graphics.ParallaxBackground;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class PantallaMenu implements Screen {

	private SpaceNavigation game;
	private OrthographicCamera camera;
	private Viewport viewport;
	private ParallaxBackground parallaxBackground;
	
	// Variables para efectos de animación
	private float textAnimationTimer;
	private float logoFloatTimer;
	
	// Textura del logo
	private Texture logoTexture;
	
	// Para centrar texto correctamente
	private GlyphLayout layout;

	public PantallaMenu(SpaceNavigation game) {
		this.game = game;
        
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 1920, 1080);
		viewport = new FitViewport(1920, 1080, camera);
		
		// Inicializar el fondo parallax
		parallaxBackground = new ParallaxBackground();
		
		// Inicializar timers de animación
		textAnimationTimer = 0f;
		logoFloatTimer = 0f;
		
		// Inicializar layout para centrar texto
		layout = new GlyphLayout();
		
		// Cargar logo
		loadLogo();
	}
	
	/**
	 * Carga el logo del juego. Si no existe, continúa sin él.
	 */
	private void loadLogo() {
		try {
			if (Gdx.files.internal("UI/Menu/logo.png").exists()) {
				logoTexture = new Texture(Gdx.files.internal("UI/Menu/logo.png"));
			} else {
				System.out.println("Logo no encontrado en UI/Menu/logo.png - usando texto por defecto");
			}
		} catch (Exception e) {
			System.out.println("Error cargando logo: " + e.getMessage());
		}
	}

	@Override
	public void render(float delta) {
		// Actualizar timers de animación
		textAnimationTimer += delta;
		logoFloatTimer += delta;
		
		// Limpiar la pantalla
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		viewport.apply();
		camera.update();
		game.getBatch().setProjectionMatrix(camera.combined);

		game.getBatch().begin();
		
		// Actualizar y renderizar el fondo parallax
		parallaxBackground.update(delta);
		parallaxBackground.render(game.getBatch());
		
		// Calcular efectos de animación
		float logoFloat = 10f * (float)Math.sin(logoFloatTimer * 1.5f); // Flotación suave
		float logoScale = 1.5f + 0.1f * (float)Math.sin(logoFloatTimer * 2.0f); // Pulsación sutil - LOGO MÁS GRANDE
		float instructionAlpha = 0.7f + 0.3f * (float)Math.sin(textAnimationTimer * 3.0f);
		
		// Dibujar logo si está disponible
		if (logoTexture != null) {
			// Calcular posición centrada del logo
			float logoWidth = logoTexture.getWidth() * logoScale;
			float logoHeight = logoTexture.getHeight() * logoScale;
			float logoX = (1920 - logoWidth) / 2;
			float logoY = (1080 / 2 - 100) + logoFloat; // Más abajo - "mitad de la mitad"
			
			// Dibujar sombra del logo
			game.getBatch().setColor(0, 0, 0, 0.5f);
			game.getBatch().draw(logoTexture, logoX + 5, logoY - 5, logoWidth, logoHeight);
			
			// Dibujar logo principal
			game.getBatch().setColor(1, 1, 1, 1);
			game.getBatch().draw(logoTexture, logoX, logoY, logoWidth, logoHeight);
		} else {
			// Fallback: usar texto si no hay logo
			game.getFont().getData().setScale(4.5f * logoScale); // Texto más grande también
			game.getFont().setColor(0, 0, 0, 0.8f);
			game.getFont().draw(game.getBatch(), "SPACE LIKE", 1920/2-300, (1080/2+50) + logoFloat);
			game.getFont().setColor(1.0f, 0.9f, 0.3f, 1.0f);
			game.getFont().draw(game.getBatch(), "SPACE LIKE", 1920/2-302, (1080/2+52) + logoFloat);
		}
		
		// Dibujar las instrucciones con parpadeo
		game.getFont().getData().setScale(1.5f);
		
		// Calcular el ancho del texto para centrarlo correctamente usando GlyphLayout
		String instructionText = "Presiona cualquier tecla para comenzar...";
		layout.setText(game.getFont(), instructionText);
		float instructionX = (1920 - layout.width) / 2; // Centrado horizontal preciso
		float instructionY = 1080/2 - 150; // Más abajo para mantener separación del logo
		
		// Sombra de las instrucciones
		game.getFont().setColor(0, 0, 0, instructionAlpha * 0.8f);
		game.getFont().draw(game.getBatch(), instructionText, instructionX + 2, instructionY - 2);
		
		// Instrucciones principales
		game.getFont().setColor(0.8f, 0.8f, 1.0f, instructionAlpha);
		game.getFont().draw(game.getBatch(), instructionText, instructionX, instructionY);
		
		// Instrucciones adicionales (más pequeñas)
		game.getFont().getData().setScale(1.0f);
		String subText = "¡Prepárate para la aventura espacial!";
		layout.setText(game.getFont(), subText);
		float subTextX = (1920 - layout.width) / 2; // Centrado horizontal preciso
		game.getFont().setColor(0.6f, 0.6f, 0.6f, 0.8f);
		game.getFont().draw(game.getBatch(), subText, subTextX, instructionY - 40);
		
		// Restaurar colores y escala por defecto
		game.getFont().setColor(1, 1, 1, 1);
		game.getFont().getData().setScale(1.0f);
		game.getBatch().setColor(1, 1, 1, 1);
	
		game.getBatch().end();

		if (Gdx.input.isTouched() || Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
			Screen ss = new PantallaJuego(game,1,3,0,1,1,10);
			ss.resize(1920, 1080);
			game.setScreen(ss);
			dispose();
		}
	}
	
	@Override
	public void show() {
		
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void hide() {
		
	}

	@Override
	public void dispose() {
		if (parallaxBackground != null) {
			parallaxBackground.dispose();
		}
		if (logoTexture != null) {
			logoTexture.dispose();
		}
	}
}