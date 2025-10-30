package puppy.code.screens;

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

import puppy.code.SpaceNavigation;
import puppy.code.graphics.ParallaxBackground;
import puppy.code.entities.Nave4;
import puppy.code.entities.enemies.Ball2;
import puppy.code.systems.XPSystem;
import puppy.code.systems.CollisionSystem;
import puppy.code.managers.ProjectileManager;
import puppy.code.managers.GameStateManager;

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
    
    private Nave4 nave;
    private ArrayList<Ball2> balls1 = new ArrayList<>();
    private ArrayList<Ball2> balls2 = new ArrayList<>();
    
    // Sistema de fondo parallax encapsulado
    private ParallaxBackground parallaxBackground;
    
    // Sistema de XP y niveles
    private XPSystem xpSystem;
    
    // Managers para arquitectura mejorada
    private ProjectileManager projectileManager;
    private GameStateManager gameState;
    private CollisionSystem collisionSystem;

    public PantallaJuego(SpaceNavigation game, int ronda, int vidas, int score,  
            int velXAsteroides, int velYAsteroides, int cantAsteroides) {
        this(game, ronda, vidas, score, velXAsteroides, velYAsteroides, cantAsteroides, null);
    }
    
    public PantallaJuego(SpaceNavigation game, int ronda, int vidas, int score,  
            int velXAsteroides, int velYAsteroides, int cantAsteroides, XPSystem xpSystem) {
        this.game = game;
        this.ronda = ronda;
        this.velXAsteroides = velXAsteroides;
        this.velYAsteroides = velYAsteroides;
        this.cantAsteroides = cantAsteroides;
        
        batch = game.getBatch();
        camera = new OrthographicCamera();    
        camera.setToOrtho(false, 1920, 1080);
        
        viewport = new FitViewport(1920, 1080, camera);
        
        parallaxBackground = new ParallaxBackground();
        
        // Inicializar managers
        projectileManager = new ProjectileManager();
        gameState = GameStateManager.getInstance();
        
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
        
        nave = new Nave4(Gdx.graphics.getWidth() / 2 - 50, 30, new Texture(Gdx.files.internal("MainShip3.png")),
                        Gdx.audio.newSound(Gdx.files.internal("hurt.ogg")), 
                        new Texture(Gdx.files.internal("Rocket2.png")), 
                        Gdx.audio.newSound(Gdx.files.internal("pop-sound.mp3"))); 
        nave.setVidas(vidas);
        
        Random r = new Random();
        for (int i = 0; i < cantAsteroides; i++) {
            Ball2 bb = new Ball2(r.nextInt(1920),
                      50 + r.nextInt(1080 - 50),
                      20 + r.nextInt(10), velXAsteroides + r.nextInt(4), velYAsteroides + r.nextInt(4), 
                      new Texture(Gdx.files.internal("aGreyMedium4.png")));       
            balls1.add(bb);
            balls2.add(bb);
        }
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
        
        if (!nave.estaHerido()) {
            // Usar CollisionSystem para colisiones balas-enemigos
            collisionSystem.checkBulletCollisions(
                projectileManager.getActiveProjectiles(), 
                balls1,
                balls2,
                projectileManager
            );
            
            // Usar CollisionSystem para colisiones nave-enemigos  
            collisionSystem.checkShipCollisions(nave, balls1);
            
            // Actualizar movimiento de asteroides
            for (Ball2 ball : balls1) {
                ball.update(delta);
            }
            
            // Colisiones entre asteroides
            for (int i = 0; i < balls1.size(); i++) {
                Ball2 ball1 = balls1.get(i);   
                for (int j = 0; j < balls2.size(); j++) {
                    Ball2 ball2 = balls2.get(j); 
                    if (i < j) {
                        ball1.checkCollision(ball2);
                    }
                }
            } 
        }
        
        // Dibujar proyectiles usando ProjectileManager
        projectileManager.render(batch);
        
        nave.draw(batch);
        nave.renderHealthHearts(batch);
        
        // Dibujar asteroides y manejar colision con nave
        for (int i = 0; i < balls1.size(); i++) {
            Ball2 b = balls1.get(i);
            b.draw(batch);
            
            if (nave.checkCollision(b)) {
                balls1.remove(i);
                balls2.remove(i);
                i--;
            }      
        }
        
        if (nave.estaDestruido()) {
            int currentScore = gameState.getScore();
            if (currentScore > game.getHighScore())
                game.setHighScore(currentScore);
            Screen ss = new PantallaGameOver(game);
            ss.resize(1920, 1080);
            game.setScreen(ss);
            dispose();
        }
        
        batch.end();
        
        // Nivel completado
        if (balls1.size() == 0) {
            System.out.println("¡Nivel completado! Avanzando a ronda " + (ronda + 1));
            int currentScore = gameState.getScore();
            Screen ss = new PantallaJuego(game, ronda + 1, nave.getVidas(), currentScore, 
                    velXAsteroides + 3, velYAsteroides + 3, cantAsteroides + 10, xpSystem);
            ss.resize(1920, 1080);
            game.setScreen(ss);
            dispose();
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
        
        if (parallaxBackground != null) {
            parallaxBackground.dispose();
        }
        
        if (projectileManager != null) {
            projectileManager.clear();
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