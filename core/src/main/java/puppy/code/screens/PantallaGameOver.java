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

public class PantallaGameOver implements Screen {

	private SpaceNavigation game;
	private OrthographicCamera camera;
	private Viewport viewport;
	
	// Parallax background estático
	private ParallaxBackground parallaxBackground;
	
	// Imagen de la nave destruida
	private Texture destroyedShipTexture;
	
	// Para centrar texto
	private GlyphLayout layout;
	
	// Timer para efectos sutiles
	private float animationTimer;

	public PantallaGameOver(SpaceNavigation game) {
		this.game = game;
        
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 1920, 1080);
		viewport = new FitViewport(1920, 1080, camera);
		
		// Inicializar parallax background (estático)
		parallaxBackground = new ParallaxBackground();
		
		// Inicializar layout para centrar texto
		layout = new GlyphLayout();
		
		// Inicializar timer
		animationTimer = 0f;
		
		// Cargar imagen de nave destruida
		loadDestroyedShipTexture();
	}
	
	/**
	 * Carga la imagen de la nave destruida. Si no existe, continúa sin ella.
	 */
	private void loadDestroyedShipTexture() {
		try {
			if (Gdx.files.internal("UI/GameOver/GAMEOVER.png").exists()) {
				destroyedShipTexture = new Texture(Gdx.files.internal("UI/GameOver/GAMEOVER.png"));
			} else {
				System.out.println("Imagen de nave destruida no encontrada en UI/GameOver/GAMEOVER.png");
			}
		} catch (Exception e) {
			System.out.println("Error cargando imagen de nave destruida: " + e.getMessage());
		}
	}

	@Override
	public void render(float delta) {
		// Actualizar timer para efectos sutiles
		animationTimer += delta;
		
		// Limpiar la pantalla
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		viewport.apply();
		camera.update();
		game.getBatch().setProjectionMatrix(camera.combined);

		game.getBatch().begin();
		
		// Renderizar parallax background ESTÁTICO (sin update para que no se mueva)
		parallaxBackground.render(game.getBatch());
		
		// Efectos de parpadeo para el texto
		float textAlpha = 0.8f + 0.2f * (float)Math.sin(animationTimer * 2.0f);
		
		// Título "GAME OVER" más grande y dramático - ARRIBA
		game.getFont().getData().setScale(3.0f);
		String gameOverText = "GAME OVER";
		layout.setText(game.getFont(), gameOverText);
		float gameOverX = (1920 - layout.width) / 2;
		float gameOverY = 1080 - 150; // Posición superior
		
		// Sombra del título
		game.getFont().setColor(0, 0, 0, textAlpha * 0.8f);
		game.getFont().draw(game.getBatch(), gameOverText, gameOverX + 3, gameOverY - 3);
		
		// Título principal en rojo dramático
		game.getFont().setColor(1.0f, 0.3f, 0.3f, textAlpha);
		game.getFont().draw(game.getBatch(), gameOverText, gameOverX, gameOverY);
		
		// Dibujar imagen de nave destruida si está disponible - CENTRO
		if (destroyedShipTexture != null) {
			// Calcular posición centrada con un ligero movimiento flotante
			float shipFloat = 5f * (float)Math.sin(animationTimer * 1.0f);
			float shipScale = 1.8f + 0.05f * (float)Math.sin(animationTimer * 1.5f); // Mucho más grande
			float shipWidth = destroyedShipTexture.getWidth() * shipScale;
			float shipHeight = destroyedShipTexture.getHeight() * shipScale;
			float shipX = (1920 - shipWidth) / 2; // Centrado horizontal perfecto
			float shipY = (1080 / 2 - shipHeight / 2) + shipFloat; // Centrado vertical perfecto
			
			// Dibujar sombra de la nave
			game.getBatch().setColor(0, 0, 0, 0.6f);
			game.getBatch().draw(destroyedShipTexture, shipX + 5, shipY - 5, shipWidth, shipHeight);
			
			// Dibujar nave destruida principal
			game.getBatch().setColor(1, 1, 1, 1);
			game.getBatch().draw(destroyedShipTexture, shipX, shipY, shipWidth, shipHeight);
		}
		
		// Instrucciones de reinicio - PARTE INFERIOR
		game.getFont().getData().setScale(1.5f);
		String restartText = "Presiona ENTER para reiniciar...";
		layout.setText(game.getFont(), restartText);
		float restartX = (1920 - layout.width) / 2;
		float restartY = 200; // Parte inferior de la pantalla
		
		// Sombra de las instrucciones
		game.getFont().setColor(0, 0, 0, textAlpha * 0.6f);
		game.getFont().draw(game.getBatch(), restartText, restartX + 2, restartY - 2);
		
		// Instrucciones principales
		game.getFont().setColor(0.8f, 0.8f, 1.0f, textAlpha);
		game.getFont().draw(game.getBatch(), restartText, restartX, restartY);
		
		// Mensaje adicional
		game.getFont().getData().setScale(1.0f);
		String subText = "¡No te rindas, inténtalo de nuevo!";
		layout.setText(game.getFont(), subText);
		float subX = (1920 - layout.width) / 2;
		game.getFont().setColor(0.6f, 0.6f, 0.6f, 0.8f);
		game.getFont().draw(game.getBatch(), subText, subX, restartY - 40);
		
		// Restaurar colores y escala por defecto
		game.getFont().setColor(1, 1, 1, 1);
		game.getFont().getData().setScale(1.0f);
		game.getBatch().setColor(1, 1, 1, 1);
	
		game.getBatch().end();

		// Solo reiniciar con la tecla ENTER
		if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
			Screen ss = new PantallaJuego(game,1,3,0,1,1,10);
			ss.resize(1920, 1080);
			game.setScreen(ss);
			dispose();
		}
		
		// Salir del juego con ESC
		if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
			Gdx.app.exit();
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
		if (destroyedShipTexture != null) {
			destroyedShipTexture.dispose();
		}
	}
}