package puppy.code.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import puppy.code.SpaceNavigation;
import puppy.code.graphics.ParallaxBackground;
import puppy.code.entities.Nave;
import puppy.code.systems.XPSystem;
import puppy.code.systems.LevelSystem;
import puppy.code.systems.CollisionSystem;
import puppy.code.managers.ProjectileManager;
import puppy.code.managers.GameStateManager;
import puppy.code.managers.EnemyManager;
import puppy.code.managers.BonusManager;

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
    private ParallaxBackground parallaxBackground;
    private boolean shouldDisposeParallax = true;
    private boolean shouldDisposeNave = true;
    private boolean isTransitioningToLevelUp = false;
    private XPSystem xpSystem;
    private LevelSystem levelSystem;
    
    private ProjectileManager projectileManager;
    private GameStateManager gameState;
    private CollisionSystem collisionSystem;
    private EnemyManager enemyManager;
    private BonusManager bonusManager;

    public PantallaJuego(SpaceNavigation game, int ronda, int vidas, int score,  
            int velXAsteroides, int velYAsteroides, int cantAsteroides) {
        this(game, ronda, vidas, score, velXAsteroides, velYAsteroides, cantAsteroides, null, null, -1, -1);
    }
    
    public PantallaJuego(SpaceNavigation game, int ronda, int vidas, int score,  
            int velXAsteroides, int velYAsteroides, int cantAsteroides, XPSystem xpSystem) {
        this(game, ronda, vidas, score, velXAsteroides, velYAsteroides, cantAsteroides, xpSystem, null, -1, -1);
    }
    
    public PantallaJuego(SpaceNavigation game, int ronda, int vidas, int score,  
            int velXAsteroides, int velYAsteroides, int cantAsteroides, XPSystem xpSystem, ParallaxBackground existingParallax,
            float naveX, float naveY) {
        this(game, ronda, vidas, score, velXAsteroides, velYAsteroides, cantAsteroides, 
             xpSystem, existingParallax, naveX, naveY, null, null);
    }
    
    public PantallaJuego(SpaceNavigation game, int ronda, int vidas, int score,  
            int velXAsteroides, int velYAsteroides, int cantAsteroides, XPSystem xpSystem, 
            ParallaxBackground existingParallax, float naveX, float naveY, Nave existingNave) {
        this(game, ronda, vidas, score, velXAsteroides, velYAsteroides, cantAsteroides, 
             xpSystem, existingParallax, naveX, naveY, existingNave, null);
    }
    
    public PantallaJuego(SpaceNavigation game, int ronda, int vidas, int score,  
            int velXAsteroides, int velYAsteroides, int cantAsteroides, XPSystem xpSystem, 
            ParallaxBackground existingParallax, float naveX, float naveY, Nave existingNave, 
            puppy.code.managers.BonusManager existingBonusManager) {
        this.game = game;
        this.ronda = ronda;
        this.velXAsteroides = velXAsteroides;
        this.velYAsteroides = velYAsteroides;
        this.cantAsteroides = cantAsteroides;
        
        batch = game.getBatch();
        camera = new OrthographicCamera();    
        camera.setToOrtho(false, 1920, 1080);
        
        viewport = new FitViewport(1920, 1080, camera);
        
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
        
        if (existingBonusManager != null) {
            bonusManager = existingBonusManager;
        } else {
            bonusManager = new BonusManager();
        }
        
        gameState.setScore(score);
        
        if (xpSystem != null) {
            this.xpSystem = xpSystem;
        } else {
            this.xpSystem = new XPSystem();
        }
        
        this.levelSystem = new LevelSystem(this.xpSystem);
        
        collisionSystem = new CollisionSystem(this.xpSystem);
        
        explosionSound = Gdx.audio.newSound(Gdx.files.internal("explosion.ogg"));
        explosionSound.setVolume(1, 0.5f);
        gameMusic = Gdx.audio.newMusic(Gdx.files.internal("piano-loops.wav"));
        
        gameMusic.setLooping(true);
        gameMusic.setVolume(0.5f);
        gameMusic.play();

        int inicialX = (naveX >= 0) ? (int)naveX : Gdx.graphics.getWidth() / 2 - 50;
        int inicialY = (naveY >= 0) ? (int)naveY : 30;
        
        if (existingNave != null) {
            nave = existingNave;
            shouldDisposeNave = false;
        } else {
            nave = new Nave(inicialX, inicialY, 
                            Gdx.audio.newSound(Gdx.files.internal("hurt.ogg")));
            nave.setVidas(vidas);
        }
        
        collisionSystem.setNave(nave);
        
        projectileManager.setNave(nave);
        
        enemyManager.startWave(ronda);
    }
    
    public void dibujaEncabezado() {
        CharSequence str = "Ronda: " + ronda;
        game.getFont().getData().setScale(2f);
        game.getFont().setColor(1, 1, 1, 1);
        game.getFont().draw(batch, str, 30, 50);
        game.getFont().draw(batch, "Score:" + gameState.getScore(), 1920 - 300, 50);
        game.getFont().draw(batch, "HighScore:" + game.getHighScore(), 1920 / 2 - 150, 50);
        
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
        
        nave.update(delta);
        nave.handleInput(this);
        
        handleShipShooting();

        xpSystem.update(delta);
        levelSystem.update();
        
        if (levelSystem.shouldShowLevelUpScreen() && !isTransitioningToLevelUp) {
            isTransitioningToLevelUp = true;
            int currentScore = gameState.getScore();
            gameMusic.pause();
            
            bonusManager.forceExpireBonus(nave);
            
            Screen ss = new PantallaUpgrade(game, this, nave, parallaxBackground, 
                                           xpSystem, levelSystem, ronda, currentScore,
                                           velXAsteroides, velYAsteroides, cantAsteroides, bonusManager);
            ss.resize(1920, 1080);
            game.setScreen(ss);
            return;
        }
        
        projectileManager.update(delta);
        projectileManager.setEnemies(enemyManager.getActiveEnemies());
        enemyManager.update(delta);
        enemyManager.updateEnemyShooting(projectileManager);
        bonusManager.update(delta);
        bonusManager.updateShipBehavior(nave);
        
        if (!nave.estaHerido()) {
            collisionSystem.checkProjectileEnemyCollisions(
                projectileManager.getActiveProjectiles(),
                enemyManager.getActiveEnemies(),
                projectileManager
            );

            collisionSystem.checkShipEnemyCollisions(nave, enemyManager.getActiveEnemies());
            
            collisionSystem.checkEnemyProjectileShipCollisions(
                projectileManager.getActiveProjectiles(),
                nave,
                projectileManager
            );
            
            if (nave.getSpinnerSystem() != null) {
                collisionSystem.checkSpinnerEnemyCollisions(
                    nave.getSpinnerSystem(),
                    enemyManager.getActiveEnemies()
                );
                
                collisionSystem.checkSpinnerProjectileCollisions(
                    nave.getSpinnerSystem(),
                    projectileManager.getActiveProjectiles(),
                    projectileManager
                );
            }

            checkBonusCollisions();
        }
        
        projectileManager.render(batch);
        
        enemyManager.render(batch);
        
        if (bonusManager.getActiveBonus() != null) {
            bonusManager.getActiveBonus().draw(batch);
        }
        
        nave.draw(batch);
        
        if (bonusManager.isBonusEffectActive() && 
            bonusManager.getActiveBonusType() == puppy.code.entities.bonus.BonusType.SHIELD) {
            bonusManager.getActiveBonus().renderEffect(batch, nave);
        }
        
        nave.renderHealthHearts(batch);
        
        nave.renderHomingIndicator(batch);
        
        if (nave.getTurboSystem() != null) {
            nave.getTurboSystem().render(batch);
        }
        
        
        batch.end();
        
        if (nave.estaDestruido()) {
            int currentScore = gameState.getScore();
            if (currentScore > game.getHighScore())
                game.setHighScore(currentScore);
            Screen ss = new PantallaGameOver(game);
            ss.resize(1920, 1080);
            game.setScreen(ss);
            dispose();
            return;
        }

        if (enemyManager.isWaveComplete()) {
            int currentScore = gameState.getScore();
            if (xpSystem != null) {
                xpSystem.forceLevelUp();
            }
            
            float currentNaveX = nave.getX();
            float currentNaveY = nave.getY();
            
            shouldDisposeParallax = false;
            Screen ss = new PantallaJuego(game, ronda + 1, nave.getVidas(), currentScore, 
                    velXAsteroides + 3, velYAsteroides + 3, cantAsteroides + 10, xpSystem, parallaxBackground,
                    currentNaveX, currentNaveY, nave);
            ss.resize(1920, 1080);
            game.setScreen(ss);
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
    
    private void checkBonusCollisions() {
        if (bonusManager.getActiveBonus() != null && !bonusManager.getActiveBonus().isCollected()) {
            if (nave.getBounds().overlaps(bonusManager.getActiveBonus().getBounds())) {
                bonusManager.bonusCollected();
                gameState.addScore(bonusManager.getActiveBonus().getValue());
            }
        }
    }
    
    private void handleShipShooting() {
        if (nave.canShoot()) {
            nave.executeShoot(projectileManager);
            nave.resetShotCooldown();
        }
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
        
        if (nave != null && shouldDisposeNave) {
            nave.dispose();
        }
        
        if (bonusManager != null) {
            bonusManager.dispose();
        }
    }
    
    public ProjectileManager getProjectileManager() {
        return projectileManager;
    }
}