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
	
	// Variables para fade in de la música
	private boolean musicFadingIn = true;
	private float musicFadeTimer = 0f;
	private final float MUSIC_FADE_DURATION = 2.0f; // 2 segundos de fade in
	private final float TARGET_MUSIC_VOLUME = 0.4f; // Volumen objetivo
	
	private int score;
	private int ronda;
	private int velXAsteroides; 
	private int velYAsteroides; 
	private int cantAsteroides;
	
	private Nave4 nave;
	private  ArrayList<Ball2> balls1 = new ArrayList<>();
	private  ArrayList<Ball2> balls2 = new ArrayList<>();
	private  ArrayList<Bullet> balas = new ArrayList<>();
	
	// Textura compartida para asteroides (optimización)
	private Texture asteroidTexture;
	
	// Fondo parallax
	private Texture fondoSpaceBackground; // Capa 0 - Space Background
	private Texture fondoSpaceStars01; // Capa 1 - Space Stars 01
	private Texture fondoSpaceDust; // Capa 1.1 - Space Dust (principal)
	private Texture[] fondoSpaceDustVariations; // Array con todas las variaciones de Space Dust
	private Texture fondoNebulose; // Capa 2 - Space Nebulose (principal)
	private Texture[] fondoNebuloseVariations; // Array con todas las variaciones de Space Nebulose
	private Texture fondoSpaceStars03; // Capa 3 - Space Stars 03 (principal)
	private Texture[] fondoSpaceStars03Variations; // Array con todas las variaciones de Space Stars 03
	private float spaceBackgroundOffsetY; // Desplazamiento para Space Background
	private float spaceStars01OffsetY; // Desplazamiento para Space Stars 01
	private float spaceDustOffsetY; // Desplazamiento para Space Dust
	private float nebuloseOffsetY; // Desplazamiento para la capa de nebulosa
	private float spaceStars03OffsetY; // Desplazamiento para Space Stars 03
	
	// Sistema de transiciones dinámicas para Space Dust
	private float spaceDustCycleTimer; // Timer para controlar los ciclos
	private float spaceDustCycleDuration; // Duración de cada ciclo
	private int currentSpaceDustIndex; // Índice de la textura actual
	private int nextSpaceDustIndex; // Índice de la siguiente textura para cross-fade
	private float spaceDustAlpha; // Alpha dinámico para transiciones
	private float nextSpaceDustAlpha; // Alpha para la siguiente textura en cross-fade
	private boolean inSpaceDustTransition; // Flag para saber si estamos en transición
	
	// Sistema de transiciones dinámicas para Space Nebulose
	private float nebuloseTransitionTimer;
	private float nebuloseCycleDuration;
	private int currentNebuloseIndex;
	private int nextNebuloseIndex;
	private float nebuloseAlpha;
	private float nextNebuloseAlpha;
	private boolean inNebuloseTransition;
	
	// Sistema de transiciones dinámicas para Space Stars 03
	private float spaceStars03TransitionTimer;
	private float spaceStars03CycleDuration;
	private int currentSpaceStars03Index;
	private int nextSpaceStars03Index;
	private float spaceStars03Alpha;
	private float nextSpaceStars03Alpha;
	private boolean inSpaceStars03Transition;
	
	// Sistema de planetas dinámicos
	private Texture[] planetTextures; // Array con todas las texturas de planetas
	private Texture currentPlanetTexture; // Textura del planeta actual
	private float planetTimer; // Timer para siguiente planeta
	private float planetSpawnDelay; // Tiempo hasta el próximo planeta (60-180s)
	private boolean planetActive; // Si hay un planeta activo en pantalla
	private float planetX, planetY; // Posición del planeta
	private float planetScale; // Escala del planeta (100-300px)
	private float planetSpeed; // Velocidad de movimiento del planeta
	private float planetLifetime; // Tiempo de vida del planeta
	private float planetAge; // Edad actual del planeta
	private boolean renderBelowNebulose; // Si se renderiza debajo de nebulose (true) o dust (false)
	
	private Random random; // Para duraciones aleatorias

	// Sistema de generación gradual de asteroides
	private int asteroidesToSpawn; // Asteroides restantes por generar
	private float asteroidSpawnTimer; // Timer para generar asteroides
	private float asteroidSpawnDelay; // Tiempo entre asteroides


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
		
		// Cargar todas las variaciones de Space Dust
		fondoSpaceDustVariations = new Texture[4];
		fondoSpaceDustVariations[0] = new Texture(Gdx.files.internal("Game/Fondo/Backgrounds/PNGs/Split up/01_1 Space Dust.png"));
		fondoSpaceDustVariations[1] = new Texture(Gdx.files.internal("Game/Fondo/Backgrounds/PNGs/Split up/01_1 Space Dust 2.png"));
		fondoSpaceDustVariations[2] = new Texture(Gdx.files.internal("Game/Fondo/Backgrounds/PNGs/Split up/01_1 Space Dust 3.png"));
		fondoSpaceDustVariations[3] = new Texture(Gdx.files.internal("Game/Fondo/Backgrounds/PNGs/Split up/01_1 Space Dust 4.png"));
		
		fondoNebulose = new Texture(Gdx.files.internal("Game/Fondo/Backgrounds/PNGs/Split up/02 Space Nebulose.png"));
		
		// Cargar todas las variaciones de Space Nebulose
		fondoNebuloseVariations = new Texture[4];
		fondoNebuloseVariations[0] = new Texture(Gdx.files.internal("Game/Fondo/Backgrounds/PNGs/Split up/02 Space Nebulose.png"));
		fondoNebuloseVariations[1] = new Texture(Gdx.files.internal("Game/Fondo/Backgrounds/PNGs/Split up/02 Space Nebulose 2.png"));
		fondoNebuloseVariations[2] = new Texture(Gdx.files.internal("Game/Fondo/Backgrounds/PNGs/Split up/02 Space Nebulose 3.png"));
		fondoNebuloseVariations[3] = new Texture(Gdx.files.internal("Game/Fondo/Backgrounds/PNGs/Split up/02 Space Nebulose 4.png"));
		
		fondoSpaceStars03 = new Texture(Gdx.files.internal("Game/Fondo/Backgrounds/PNGs/Split up/03 Space Stars.png"));
		
		// Cargar todas las variaciones de Space Stars 03
		fondoSpaceStars03Variations = new Texture[4];
		fondoSpaceStars03Variations[0] = new Texture(Gdx.files.internal("Game/Fondo/Backgrounds/PNGs/Split up/03 Space Stars.png"));
		fondoSpaceStars03Variations[1] = new Texture(Gdx.files.internal("Game/Fondo/Backgrounds/PNGs/Split up/03 Space Stars 2.png"));
		fondoSpaceStars03Variations[2] = new Texture(Gdx.files.internal("Game/Fondo/Backgrounds/PNGs/Split up/03 Space Stars 3.png"));
		fondoSpaceStars03Variations[3] = new Texture(Gdx.files.internal("Game/Fondo/Backgrounds/PNGs/Split up/03 Space Stars 4.png"));
		
		// Cargar todas las texturas de planetas (13 planetas disponibles)
		planetTextures = new Texture[13];
		planetTextures[0] = new Texture(Gdx.files.internal("Game/Fondo/Planets/Planet 1.png"));
		planetTextures[1] = new Texture(Gdx.files.internal("Game/Fondo/Planets/Planet 2.png"));
		planetTextures[2] = new Texture(Gdx.files.internal("Game/Fondo/Planets/Planet 3.png"));
		planetTextures[3] = new Texture(Gdx.files.internal("Game/Fondo/Planets/Planet 4.png"));
		planetTextures[4] = new Texture(Gdx.files.internal("Game/Fondo/Planets/Planet 5.png"));
		planetTextures[5] = new Texture(Gdx.files.internal("Game/Fondo/Planets/Planet 6.png"));
		planetTextures[6] = new Texture(Gdx.files.internal("Game/Fondo/Planets/Planet 7.png"));
		planetTextures[7] = new Texture(Gdx.files.internal("Game/Fondo/Planets/Planet 8.png"));
		planetTextures[8] = new Texture(Gdx.files.internal("Game/Fondo/Planets/Planet 9.png"));
		planetTextures[9] = new Texture(Gdx.files.internal("Game/Fondo/Planets/Planet 10.png"));
		planetTextures[10] = new Texture(Gdx.files.internal("Game/Fondo/Planets/Planet 11.png"));
		planetTextures[11] = new Texture(Gdx.files.internal("Game/Fondo/Planets/Planet 12.png"));
		planetTextures[12] = new Texture(Gdx.files.internal("Game/Fondo/Planets/Planet 13.png"));
		
		// Inicializar offsets para parallax
		spaceBackgroundOffsetY = 0f; // Inicializar offset Space Background
		spaceStars01OffsetY = 0f; // Inicializar offset Space Stars 01
		spaceDustOffsetY = 0f; // Inicializar offset Space Dust
		nebuloseOffsetY = 0f; // Inicializar offset de la nebulosa
		spaceStars03OffsetY = 0f; // Inicializar offset Space Stars 03
		
		// Inicializar sistema de transiciones dinámicas para Space Dust (60 segundos)
		random = new Random();
		spaceDustCycleTimer = 0f;
		spaceDustCycleDuration = 60f; // Exactamente 60 segundos
		currentSpaceDustIndex = 0;
		nextSpaceDustIndex = 1; // Preparar siguiente textura
		spaceDustAlpha = 1.0f;
		nextSpaceDustAlpha = 0.0f;
		inSpaceDustTransition = false;
		
		// Inicializar sistema de transiciones dinámicas para Space Nebulose (30 segundos)
		// Desfase de 10 segundos para evitar coincidencias
		nebuloseTransitionTimer = 10f; // Comenzar con desfase
		nebuloseCycleDuration = 30f; // Exactamente 30 segundos
		currentNebuloseIndex = 0;
		nextNebuloseIndex = 1;
		nebuloseAlpha = 1.0f;
		nextNebuloseAlpha = 0.0f;
		inNebuloseTransition = false;
		
		// Inicializar sistema de transiciones dinámicas para Space Stars 03 (10 segundos)
		// Desfase de 5 segundos para evitar coincidencias
		spaceStars03TransitionTimer = 5f; // Comenzar con desfase
		spaceStars03CycleDuration = 10f; // Exactamente 10 segundos
		currentSpaceStars03Index = 0;
		nextSpaceStars03Index = 1;
		spaceStars03Alpha = 1.0f;
		nextSpaceStars03Alpha = 0.0f;
		inSpaceStars03Transition = false;
		
		// Inicializar sistema de planetas dinámicos
		planetTimer = 0f;
		planetSpawnDelay = 5f; // Primer planeta aparece inmediatamente (5 segundos)
		planetActive = false;
		currentPlanetTexture = null;
		planetX = 0f;
		planetY = 0f;
		planetScale = 1.0f;
		planetSpeed = 0f;
		planetLifetime = 0f;
		planetAge = 0f;
		renderBelowNebulose = false;
		
		//inicializar assets; musica de fondo y efectos de sonido
		explosionSound = Gdx.audio.newSound(Gdx.files.internal("explosion.ogg"));
		gameMusic = Gdx.audio.newMusic(Gdx.files.internal("Game/VFX/Music/Metallica - Nothing Else Matters (instrumental version).mp3")); //
		
		gameMusic.setLooping(true);
		gameMusic.setVolume(0.0f); // Empezar en silencio para el fade in
		gameMusic.play();
		
		// Cargar textura de asteroide una sola vez (optimización)
		asteroidTexture = new Texture(Gdx.files.internal("aGreyMedium4.png"));
		
	    // cargar imagen de la nave, 64x64   
	    nave = new Nave4(Gdx.graphics.getWidth()/2-50,30,new Texture(Gdx.files.internal("MainShip3.png")),
	    				Gdx.audio.newSound(Gdx.files.internal("hurt.ogg")), 
	    				new Texture(Gdx.files.internal("Rocket2.png")), 
	    				Gdx.audio.newSound(Gdx.files.internal("pop-sound.mp3"))); 
        nave.setVidas(vidas);
        //crear asteroides de forma gradual
        setupAsteroidSpawning(cantAsteroides);
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
		  // Sistema de fade in para la música
		  if (musicFadingIn) {
		      musicFadeTimer += delta;
		      float fadeProgress = musicFadeTimer / MUSIC_FADE_DURATION;
		      
		      if (fadeProgress >= 1.0f) {
		          // Fade in completado
		          musicFadingIn = false;
		          gameMusic.setVolume(TARGET_MUSIC_VOLUME);
		      } else {
		          // Aplicar fade in gradual
		          float currentVolume = TARGET_MUSIC_VOLUME * fadeProgress;
		          gameMusic.setVolume(currentVolume);
		      }
		  }
		  
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
          
          // Reducir opacidad al 40% para Space Stars 01
          batch.setColor(1f, 1f, 1f, 0.4f); // RGBA: blanco con 40% de opacidad
          
          // Dibujar capa 1 - Space Stars 01 con opacidad reducida
          // Primera instancia (principal)
          batch.draw(fondoSpaceStars01, 0, spaceStars01OffsetY, 1920, 1080);
          // Segunda instancia (para continuidad cuando la primera sale por abajo)
          batch.draw(fondoSpaceStars01, 0, spaceStars01OffsetY + 1080, 1920, 1080);
          
          // Restaurar opacidad original
          batch.setColor(1f, 1f, 1f, 1f); // RGBA: blanco con 100% de opacidad
          
          // Sistema de transiciones dinámicas para Space Dust con cross-fade suave
          spaceDustCycleTimer += delta;
          
          // Calcular progreso del ciclo (0.0 a 1.0)
          float cycleProgress = spaceDustCycleTimer / spaceDustCycleDuration;
          
          if (cycleProgress >= 1.0f) {
              // Completar transición y reiniciar ciclo
              spaceDustCycleTimer = 0f;
              spaceDustCycleDuration = 60f; // Tiempo fijo de 60 segundos
              currentSpaceDustIndex = nextSpaceDustIndex; // La siguiente se convierte en actual
              
              // Seleccionar nueva textura siguiente (diferente a la actual)
              do {
                  nextSpaceDustIndex = random.nextInt(fondoSpaceDustVariations.length);
              } while (nextSpaceDustIndex == currentSpaceDustIndex);
              
              spaceDustAlpha = 1.0f;
              nextSpaceDustAlpha = 0.0f;
              inSpaceDustTransition = false;
              
          } else if (cycleProgress >= 0.5f) {
              // Fase de cross-fade: últimos 50% del ciclo (transición muy lenta y suave)
              inSpaceDustTransition = true;
              float fadeProgress = (cycleProgress - 0.5f) / 0.5f; // 0.0 a 1.0 en la fase de cross-fade
              
              // Cross-fade muy suave: la textura actual se desvanece mientras la siguiente aparece
              spaceDustAlpha = 1.0f - fadeProgress; // De 1.0 a 0.0
              nextSpaceDustAlpha = fadeProgress; // De 0.0 a 1.0
              
          } else {
              // Fase normal: solo textura actual visible
              inSpaceDustTransition = false;
              spaceDustAlpha = 1.0f;
              nextSpaceDustAlpha = 0.0f;
          }
          
          // Sistema de transiciones dinámicas para Space Nebulose (30 segundos)
          nebuloseTransitionTimer += delta;
          
          float nebuloseProgress = nebuloseTransitionTimer / nebuloseCycleDuration;
          
          if (nebuloseProgress >= 1.0f) {
              // Completar transición y reiniciar ciclo
              nebuloseTransitionTimer = 0f;
              nebuloseCycleDuration = 30f; // Tiempo fijo de 30 segundos
              currentNebuloseIndex = nextNebuloseIndex;
              
              // Seleccionar nueva textura siguiente (diferente a la actual)
              do {
                  nextNebuloseIndex = random.nextInt(fondoNebuloseVariations.length);
              } while (nextNebuloseIndex == currentNebuloseIndex);
              
              nebuloseAlpha = 1.0f;
              nextNebuloseAlpha = 0.0f;
              inNebuloseTransition = false;
              
          } else if (nebuloseProgress >= 0.5f) {
              // Fase de cross-fade: últimos 50% del ciclo
              inNebuloseTransition = true;
              float nebuloseFadeProgress = (nebuloseProgress - 0.5f) / 0.5f;
              
              nebuloseAlpha = 1.0f - nebuloseFadeProgress;
              nextNebuloseAlpha = nebuloseFadeProgress;
              
          } else {
              // Fase normal: solo textura actual visible
              inNebuloseTransition = false;
              nebuloseAlpha = 1.0f;
              nextNebuloseAlpha = 0.0f;
          }
          
          // Sistema de transiciones dinámicas para Space Stars 03 (10 segundos)
          spaceStars03TransitionTimer += delta;
          
          float stars03Progress = spaceStars03TransitionTimer / spaceStars03CycleDuration;
          
          if (stars03Progress >= 1.0f) {
              // Completar transición y reiniciar ciclo
              spaceStars03TransitionTimer = 0f;
              spaceStars03CycleDuration = 10f; // Tiempo fijo de 10 segundos
              currentSpaceStars03Index = nextSpaceStars03Index;
              
              // Seleccionar nueva textura siguiente (diferente a la actual)
              do {
                  nextSpaceStars03Index = random.nextInt(fondoSpaceStars03Variations.length);
              } while (nextSpaceStars03Index == currentSpaceStars03Index);
              
              spaceStars03Alpha = 1.0f;
              nextSpaceStars03Alpha = 0.0f;
              inSpaceStars03Transition = false;
              
          } else if (stars03Progress >= 0.5f) {
              // Fase de cross-fade: últimos 50% del ciclo
              inSpaceStars03Transition = true;
              float stars03FadeProgress = (stars03Progress - 0.5f) / 0.5f;
              
              spaceStars03Alpha = 1.0f - stars03FadeProgress;
              nextSpaceStars03Alpha = stars03FadeProgress;
              
          } else {
              // Fase normal: solo textura actual visible
              inSpaceStars03Transition = false;
              spaceStars03Alpha = 1.0f;
              nextSpaceStars03Alpha = 0.0f;
          }
          
          // Sistema de planetas dinámicos
          planetTimer += delta;
          
          if (!planetActive) {
              // No hay planeta activo, verificar si es hora de crear uno
              if (planetTimer >= planetSpawnDelay) {
                  // Spawner nuevo planeta
                  planetActive = true;
                  planetTimer = 0f;
                  
                  // Seleccionar planeta aleatorio
                  int planetIndex = random.nextInt(planetTextures.length);
                  currentPlanetTexture = planetTextures[planetIndex];
                  
                  // Seleccionar capa aleatoria (debajo de Nebulose o Space Dust)
                  renderBelowNebulose = random.nextBoolean();
                  
                  // Seleccionar tamaño aleatorio (100-300px)
                  float baseSize = 100f + random.nextFloat() * 200f; // 100-300px
                  planetScale = baseSize / Math.max(currentPlanetTexture.getWidth(), currentPlanetTexture.getHeight());
                  
                  // Posición inicial aleatoria en X, arriba de la pantalla
                  planetX = random.nextFloat() * (1920f - baseSize);
                  planetY = 1080f + baseSize; // Comienza arriba de la pantalla
                  
                  // Seleccionar tiempo de vida aleatorio (60-180 segundos)
                  planetLifetime = 60f + random.nextFloat() * 120f;
                  
                  // Calcular velocidad para que salga por abajo en el tiempo asignado
                  // Debe recorrer desde arriba de pantalla hasta abajo de pantalla
                  float totalDistance = 1080f + baseSize + baseSize; // Altura total + tamaño del planeta
                  planetSpeed = totalDistance / planetLifetime;
                  
                  planetAge = 0f;
                  
                  // Programar próximo planeta (60-180 segundos después de que este salga)
                  // No resetear planetSpawnDelay aquí, se hará cuando el planeta termine
              }
          } else {
              // Hay planeta activo, actualizarlo
              planetAge += delta;
              planetY -= planetSpeed * delta; // Mover hacia abajo
              
              // Verificar si el planeta ha completado su ciclo de vida
              if (planetAge >= planetLifetime || planetY < -currentPlanetTexture.getHeight() * planetScale) {
                  // Planeta completó su ciclo, desactivar
                  planetActive = false;
                  currentPlanetTexture = null;
                  planetTimer = 0f; // Reiniciar timer para próximo planeta
                  planetSpawnDelay = 5f + random.nextFloat() * 10f; // Próximo planeta en 5-15 segundos (más rápido)
              }
          }
          
          // Renderizar planeta si está activo y debe ir debajo de Space Dust (encima de Space Stars 01)
          if (planetActive && !renderBelowNebulose && currentPlanetTexture != null) {
              batch.setColor(1f, 1f, 1f, 0.8f); // 80% de opacidad (reducido 20%)
              float planetWidth = currentPlanetTexture.getWidth() * planetScale;
              float planetHeight = currentPlanetTexture.getHeight() * planetScale;
              batch.draw(currentPlanetTexture, planetX, planetY, planetWidth, planetHeight);
              batch.setColor(1f, 1f, 1f, 1f); // Restaurar opacidad original
          }
          
          // Capa 1.1 - Space Dust (velocidad intermedia entre Stars 01 y Nebulose)
          spaceDustOffsetY -= 90f * delta; // Velocidad: 90 pixels por segundo
          
          // Resetear cuando la imagen se sale completamente de pantalla por abajo
          if (spaceDustOffsetY <= -1080) {
              spaceDustOffsetY = 0f;
          }
          
          // Dibujar capa 1.1 - Space Dust con sistema dinámico de cross-fade
          
          // Dibujar textura actual
          batch.setColor(1f, 1f, 1f, spaceDustAlpha * 0.6f); // Combinar alpha dinámico con opacidad base del 60%
          // Primera instancia (principal) - textura actual
          batch.draw(fondoSpaceDustVariations[currentSpaceDustIndex], 0, spaceDustOffsetY, 1920, 1080);
          // Segunda instancia (continuidad) - textura actual
          batch.draw(fondoSpaceDustVariations[currentSpaceDustIndex], 0, spaceDustOffsetY + 1080, 1920, 1080);
          
          // Si estamos en transición, superponer la siguiente textura
          if (inSpaceDustTransition && nextSpaceDustAlpha > 0.0f) {
              batch.setColor(1f, 1f, 1f, nextSpaceDustAlpha * 0.6f); // Alpha de la siguiente textura
              // Primera instancia (principal) - siguiente textura
              batch.draw(fondoSpaceDustVariations[nextSpaceDustIndex], 0, spaceDustOffsetY, 1920, 1080);
              // Segunda instancia (continuidad) - siguiente textura
              batch.draw(fondoSpaceDustVariations[nextSpaceDustIndex], 0, spaceDustOffsetY + 1080, 1920, 1080);
          }
          
          // Restaurar opacidad original
          batch.setColor(1f, 1f, 1f, 1f); // RGBA: blanco con 100% de opacidad
          
          // Renderizar planeta si está activo y debe ir debajo de Space Nebulose (encima de Space Dust)
          if (planetActive && renderBelowNebulose && currentPlanetTexture != null) {
              batch.setColor(1f, 1f, 1f, 0.8f); // 80% de opacidad (reducido 20%)
              float planetWidth = currentPlanetTexture.getWidth() * planetScale;
              float planetHeight = currentPlanetTexture.getHeight() * planetScale;
              batch.draw(currentPlanetTexture, planetX, planetY, planetWidth, planetHeight);
              batch.setColor(1f, 1f, 1f, 1f); // Restaurar opacidad original
          }
          
          // Capa 2 - Space Nebulose (velocidad media)
          nebuloseOffsetY -= 100f * delta; // Velocidad: 100 pixels por segundo
          
          // Resetear cuando la imagen se sale completamente de pantalla por abajo
          if (nebuloseOffsetY <= -1080) {
              nebuloseOffsetY = 0f;
          }
          
          // Dibujar capa 2 - Space Nebulose con sistema dinámico de cross-fade
          
          // Dibujar textura actual de nebulosa
          batch.setColor(1f, 1f, 1f, nebuloseAlpha * 0.8f); // Combinar alpha dinámico con opacidad base del 80%
          // Primera instancia (principal) - textura actual
          batch.draw(fondoNebuloseVariations[currentNebuloseIndex], 0, nebuloseOffsetY, 1920, 1080);
          // Segunda instancia (continuidad) - textura actual
          batch.draw(fondoNebuloseVariations[currentNebuloseIndex], 0, nebuloseOffsetY + 1080, 1920, 1080);
          
          // Si estamos en transición, superponer la siguiente textura
          if (inNebuloseTransition && nextNebuloseAlpha > 0.0f) {
              batch.setColor(1f, 1f, 1f, nextNebuloseAlpha * 0.8f); // Alpha de la siguiente textura
              // Primera instancia (principal) - siguiente textura
              batch.draw(fondoNebuloseVariations[nextNebuloseIndex], 0, nebuloseOffsetY, 1920, 1080);
              // Segunda instancia (continuidad) - siguiente textura
              batch.draw(fondoNebuloseVariations[nextNebuloseIndex], 0, nebuloseOffsetY + 1080, 1920, 1080);
          }
          
          // Restaurar opacidad original
          batch.setColor(1f, 1f, 1f, 1f); // RGBA: blanco con 100% de opacidad
          
          // Capa 3 - Space Stars 03 (velocidad más rápida, estrellas más próximas)
          spaceStars03OffsetY -= 120f * delta; // Velocidad: 120 pixels por segundo
          
          // Resetear cuando la imagen se sale completamente de pantalla por abajo
          if (spaceStars03OffsetY <= -1080) {
              spaceStars03OffsetY = 0f;
          }
          
          // Dibujar capa 3 - Space Stars 03 con sistema dinámico de cross-fade
          
          // Dibujar textura actual de estrellas
          batch.setColor(1f, 1f, 1f, spaceStars03Alpha); // Alpha dinámico (opacidad base 100%)
          // Primera instancia (principal) - textura actual
          batch.draw(fondoSpaceStars03Variations[currentSpaceStars03Index], 0, spaceStars03OffsetY, 1920, 1080);
          // Segunda instancia (continuidad) - textura actual
          batch.draw(fondoSpaceStars03Variations[currentSpaceStars03Index], 0, spaceStars03OffsetY + 1080, 1920, 1080);
          
          // Si estamos en transición, superponer la siguiente textura
          if (inSpaceStars03Transition && nextSpaceStars03Alpha > 0.0f) {
              batch.setColor(1f, 1f, 1f, nextSpaceStars03Alpha); // Alpha de la siguiente textura
              // Primera instancia (principal) - siguiente textura
              batch.draw(fondoSpaceStars03Variations[nextSpaceStars03Index], 0, spaceStars03OffsetY, 1920, 1080);
              // Segunda instancia (continuidad) - siguiente textura
              batch.draw(fondoSpaceStars03Variations[nextSpaceStars03Index], 0, spaceStars03OffsetY + 1080, 1920, 1080);
          }
          
          // Restaurar opacidad original
          batch.setColor(1f, 1f, 1f, 1f); // RGBA: blanco con 100% de opacidad
          
          // Actualizar generación gradual de asteroides
          updateAsteroidSpawning(delta);
          
		  dibujaEncabezado();
	      if (!nave.estaHerido()) {
		      // colisiones entre balas y asteroides y su destruccion  
	    	  for (int i = 0; i < balas.size(); i++) {
		            Bullet b = balas.get(i);
		            b.update();
		            for (int j = 0; j < balls1.size(); j++) {    
		              if (b.checkCollision(balls1.get(j))) {          
		            	 explosionSound.play(0.05f);
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
	      if (balls1.size()==0 && asteroidesToSpawn==0) {
			// Usar el nuevo método que no recarga texturas
			nextRound();
		  }
	    	 
	}
    
    public boolean agregarBala(Bullet bb) {
    	return balas.add(bb);
    }
    
    /**
     * Configura el sistema de generación gradual de asteroides
     */
    private void setupAsteroidSpawning(int totalAsteroids) {
        asteroidesToSpawn = totalAsteroids;
        asteroidSpawnTimer = 0f;
        asteroidSpawnDelay = 0.3f; // Generar un asteroide cada 300ms
    }
    
    /**
     * Actualiza la generación gradual de asteroides
     */
    private void updateAsteroidSpawning(float delta) {
        if (asteroidesToSpawn > 0) {
            asteroidSpawnTimer += delta;
            if (asteroidSpawnTimer >= asteroidSpawnDelay) {
                // Generar un asteroide
                Random r = new Random();
                Ball2 bb = new Ball2(r.nextInt(1920),
                        50+r.nextInt(1080-50),
                        20+r.nextInt(10), velXAsteroides+r.nextInt(4), velYAsteroides+r.nextInt(4), 
                        asteroidTexture);	   
                balls1.add(bb);
                balls2.add(bb);
                
                asteroidesToSpawn--;
                asteroidSpawnTimer = 0f;
            }
        }
    }
    
    /**
     * Reinicia la ronda sin recargar las texturas del fondo
     */
    private void nextRound() {
        // Incrementar ronda y dificultad
        ronda++;
        velXAsteroides += 3;
        velYAsteroides += 3;
        cantAsteroides += 10;
        
        // Limpiar asteroides existentes
        balls1.clear();
        balls2.clear();
        
        // Limpiar balas
        balas.clear();
        
        // Reiniciar generación de asteroides
        setupAsteroidSpawning(cantAsteroides);
        
        // Mantener posición de la nave
        // No necesitamos resetear la nave ya que mantenemos la instancia existente
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
		// Limpiar todas las variaciones de Space Dust
		if (fondoSpaceDustVariations != null) {
			for (Texture dustTexture : fondoSpaceDustVariations) {
				if (dustTexture != null) {
					dustTexture.dispose();
				}
			}
		}
		if (fondoNebulose != null) {
			fondoNebulose.dispose();
		}
		// Limpiar todas las variaciones de Space Nebulose
		if (fondoNebuloseVariations != null) {
			for (Texture nebuloseTexture : fondoNebuloseVariations) {
				if (nebuloseTexture != null) {
					nebuloseTexture.dispose();
				}
			}
		}
		if (fondoSpaceStars03 != null) {
			fondoSpaceStars03.dispose();
		}
		// Limpiar todas las variaciones de Space Stars 03
		if (fondoSpaceStars03Variations != null) {
			for (Texture starsTexture : fondoSpaceStars03Variations) {
				if (starsTexture != null) {
					starsTexture.dispose();
				}
			}
		}
		
		// Limpiar todas las texturas de planetas
		if (planetTextures != null) {
			for (Texture planetTexture : planetTextures) {
				if (planetTexture != null) {
					planetTexture.dispose();
				}
			}
		}
		
		// Limpiar texturas de la nave
		if (nave != null) {
			nave.dispose();
		}
		
		// Limpiar textura de asteroide
		if (asteroidTexture != null) {
			asteroidTexture.dispose();
		}
	}
   
}
