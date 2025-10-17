package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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

	public PantallaMenu(SpaceNavigation game) {
		this.game = game;
        
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 1920, 1080); // Consistente con PantallaJuego
		viewport = new FitViewport(1920, 1080, camera);
		
		// Cargar imagen de portada
		portadaTexture = new Texture(Gdx.files.internal("UI/Portada/Gemini_Generated_Image_yonktcyonktcyonk (2).png"));
		
		// Definir área grande para el botón START GAME - cubre toda la parte inferior
		// Área súper amplia para que funcione sin importar dónde esté exactamente
		float buttonWidth = 600f;  // Área bien amplia
		float buttonHeight = 200f; // Área bien alta
		float buttonX = (1920f - buttonWidth) / 2f; // Centrado horizontalmente
		float buttonY = 50f; // Desde muy abajo hasta más arriba
		startButtonArea = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
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

		// Detectar clic y mostrar coordenadas en consola (sin texto en pantalla)
		if (Gdx.input.justTouched()) {
			Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(mousePos);
			
			// Mostrar en consola las coordenadas exactas donde hiciste clic
			System.out.println("COORDENADAS DEL CLIC: X=" + (int)mousePos.x + " Y=" + (int)mousePos.y);
			
			// Por ahora, cualquier clic inicia el juego (temporal)
			Screen ss = new PantallaJuego(game,1,3,0,1,1,10);
			ss.resize(1920, 1080);
			game.setScreen(ss);
			dispose();
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
	}
   
}