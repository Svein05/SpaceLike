package puppy.code.screens;

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

import puppy.code.SpaceNavigation;
import puppy.code.graphics.ParallaxBackground;
import puppy.code.entities.Nave;
import puppy.code.systems.XPSystem;
import puppy.code.systems.CollisionSystem;
import puppy.code.managers.ProjectileManager;
import puppy.code.managers.GameStateManager;
import puppy.code.managers.EnemyManager;

public class PantallaJuego implements Screen {

    private SpaceNavigation game;
    private OrthographicCamera camera;    
    private Viewport viewport;
    private SpriteBatch batch;
    private Sound explosionSound;
    private Music gameMusic;
    private int ronda;
    private int velXAsteroides; 
    private int velYAsteroides; 
    private int cantAsteroides;
    
    private Nave nave;
    // Legacy asteroid lists removed - using EnemyManager instead
    
    // Sistema de fondo parallax encapsulado
    private ParallaxBackground parallaxBackground;
    
    // Flag para controlar si se debe hacer dispose del parallax
    private boolean shouldDisposeParallax = true;
    
    // Sistema de XP y niveles
    private XPSystem xpSystem;
    
    // Managers para arquitectura mejorada
    private ProjectileManager projectileManager;
    private GameStateManager gameState;
    private CollisionSystem collisionSystem;
    private EnemyManager enemyManager;

    public PantallaJuego(SpaceNavigation game, int ronda, int vidas, int score,  
            int velXAsteroides, int velYAsteroides, int cantAsteroides) {
        this(game, ronda, vidas, score, velXAsteroides, velYAsteroides, cantAsteroides, null, null, -1, -1);
    }
    
    public PantallaJuego(SpaceNavigation game, int ronda, int vidas, int score,  
            int velXAsteroides, int velYAsteroides, int cantAsteroides, XPSystem xpSystem) {
        this(game, ronda, vidas, score, velXAsteroides, velYAsteroides, cantAsteroides, xpSystem, null, -1, -1);
    }
    
    // Constructor completo con parallax opcional y posición de nave
    public PantallaJuego(SpaceNavigation game, int ronda, int vidas, int score,  
            int velXAsteroides, int velYAsteroides, int cantAsteroides, XPSystem xpSystem, ParallaxBackground existingParallax,
            float naveX, float naveY) {
        this.game = game;
        this.ronda = ronda;
        this.velXAsteroides = velXAsteroides;
        this.velYAsteroides = velYAsteroides;
        this.cantAsteroides = cantAsteroides;
        
        batch = game.getBatch();
        camera = new OrthographicCamera();    
        camera.setToOrtho(false, 1920, 1080);
        
        viewport = new FitViewport(1920, 1080, camera);
        
        // Usar parallax existente o crear uno nuevo
        if (existingParallax != null) {
            this.parallaxBackground = existingParallax;
            this.shouldDisposeParallax = false;
        } else {
            this.parallaxBackground = new ParallaxBackground();
            this.shouldDisposeParallax = true;
        }
        
        // Inicializar managers
        projectileManager = new ProjectileManager();
        gameState = GameStateManager.getInstance();
        enemyManager = new EnemyManager();
        
        // Configurar estado actual en GameStateManager
        gameState.setScore(score);
        
        // Mantener XP entre rondas o crear nuevo sistema
        if (xpSystem != null) {
            this.xpSystem = xpSystem;
        } else {
            this.xpSystem = new XPSystem();
        }
        
        // Inicializar sistema de colisiones
        collisionSystem = new CollisionSystem(this.xpSystem);
        
        explosionSound = Gdx.audio.newSound(Gdx.files.internal("explosion.ogg"));
        explosionSound.setVolume(1, 0.5f);
        gameMusic = Gdx.audio.newMusic(Gdx.files.internal("piano-loops.wav"));
        
        gameMusic.setLooping(true);
        gameMusic.setVolume(0.5f);
        gameMusic.play();
        // Inicializar nave en posición específica o posición inicial
        int inicialX = (naveX >= 0) ? (int)naveX : Gdx.graphics.getWidth() / 2 - 50;
        int inicialY = (naveY >= 0) ? (int)naveY : 30;
        
        nave = new Nave(inicialX, inicialY, 
                        new Texture(Gdx.files.internal("MainShip3.png")),
                        Gdx.audio.newSound(Gdx.files.internal("hurt.ogg"))); 
        nave.setVidas(vidas);
        
        enemyManager.startWave(ronda);
    }
    
    public void dibujaEncabezado() {
        CharSequence str = "Ronda: " + ronda;
        game.getFont().getData().setScale(2f);        
        game.getFont().draw(batch, str, 30, 50);
        game.getFont().draw(batch, "Score:" + gameState.getScore(), 1920 - 300, 50);
        game.getFont().draw(batch, "HighScore:" + game.getHighScore(), 1920 / 2 - 150, 50);
        
        // Renderizar sistema de XP centrado arriba
        xpSystem.render(batch, game.getFont(), 1920);
    }
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        viewport.apply();
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        
        batch.begin();
        
        parallaxBackground.update(delta);
        parallaxBackground.render(batch);
        
        dibujaEncabezado();
        
        // Actualizar nave
        nave.update(delta);
        nave.handleInput(this);
        
        // Actualizar sistema de XP
        xpSystem.update(delta);
        
        // Actualizar ProjectileManager
        projectileManager.update(delta);
        
        // Actualizar EnemyManager
        enemyManager.update(delta);
        
        if (!nave.estaHerido()) {
            // Colisiones balas-enemigos usando EnemyManager
            collisionSystem.checkProjectileEnemyCollisions(
                projectileManager.getActiveProjectiles(),
                enemyManager.getActiveEnemies(),
                projectileManager
            );

            // Colisiones nave-enemigos usando EnemyManager
            collisionSystem.checkShipEnemyCollisions(nave, enemyManager.getActiveEnemies());
        }
        
        // Dibujar proyectiles usando ProjectileManager
        projectileManager.render(batch);
        
        // Dibujar enemigos usando EnemyManager
        enemyManager.render(batch);
        
        nave.draw(batch);
        nave.renderHealthHearts(batch);
        
        // Enemigos ya son dibujados por EnemyManager
        
        batch.end();
        
        // PRIORIDAD 1: Game Over - verificar ANTES que level completado
        if (nave.estaDestruido()) {
            int currentScore = gameState.getScore();
            if (currentScore > game.getHighScore())
                game.setHighScore(currentScore);
            Screen ss = new PantallaGameOver(game);
            ss.resize(1920, 1080);
            game.setScreen(ss);
            dispose();
            return; // Salir inmediatamente para evitar otras verificaciones
        }
        
        // PRIORIDAD 2: Nivel completado - solo si no hay game over
        if (enemyManager.isWaveComplete()) {
            int currentScore = gameState.getScore();
            // Otorgar XP al completar ronda
            if (xpSystem != null) {
                int xpReward = 50 * ronda; // XP basado en ronda completada
                xpSystem.addXP(xpReward);
            }
            
            // Obtener posición actual de la nave para mantenerla
            float currentNaveX = nave.getX();
            float currentNaveY = nave.getY();
            
            // Pasar el parallax existente y la posición de la nave para mantener continuidad visual
            shouldDisposeParallax = false; // No dispose del parallax al avanzar ronda
            Screen ss = new PantallaJuego(game, ronda + 1, nave.getVidas(), currentScore, 
                    velXAsteroides + 3, velYAsteroides + 3, cantAsteroides + 10, xpSystem, parallaxBackground,
                    currentNaveX, currentNaveY);
            ss.resize(1920, 1080);
            game.setScreen(ss);
            // Hacer dispose manual sin incluir parallax
            gameMusic.dispose();
        }
    }
    
    @Override
    public void show() {
        gameMusic.play();
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
        this.explosionSound.dispose();
        this.gameMusic.dispose();
        
        if (parallaxBackground != null && shouldDisposeParallax) {
            parallaxBackground.dispose();
        }
        
        if (projectileManager != null) {
            projectileManager.clear();
        }
        
        if (enemyManager != null) {
            enemyManager.clearAllEnemies();
        }
        
        if (nave != null) {
            nave.dispose();
        }
    }
    
    // Getter para el ProjectileManager
    public ProjectileManager getProjectileManager() {
        return projectileManager;
    }
}