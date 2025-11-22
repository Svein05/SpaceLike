package puppy.code.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import puppy.code.SpaceNavigation;
import puppy.code.graphics.ParallaxBackground;
import puppy.code.entities.Nave;
import puppy.code.systems.XPSystem;
import puppy.code.systems.LevelSystem;
import puppy.code.systems.CollisionSystem;
import puppy.code.systems.TutorialSystem;
import puppy.code.managers.ProjectileManager;
import puppy.code.managers.GameStateManager;
import puppy.code.managers.EnemyManager;
import puppy.code.managers.BonusManager;

public class PantallaJuego implements Screen {

    private static final int MAX_WAVES = 14;
    
    private SpaceNavigation game;
    private OrthographicCamera camera;    
    private Viewport viewport;
    private SpriteBatch batch;
    private Sound explosionSound;
    private Music gameMusic;
    private Music currentTrack;
    private String currentMusicPath;
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
    private TutorialSystem tutorialSystem;
    private boolean waveTransitionInProgress = false;
    private ShapeRenderer shapeRenderer;
    private static boolean godModeActive = false;
    private static float godModeToggleCooldown = 0f;
    private boolean wasGodModeKeyPressed = false;
    private static final float GOD_MODE_COOLDOWN_TIME = 5.0f;

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
        
        shapeRenderer = new ShapeRenderer();
        
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
        
        if (ronda == 1) {
            tutorialSystem = new TutorialSystem();
        }
        
        explosionSound = Gdx.audio.newSound(Gdx.files.internal("Audio/SFX/Explosions/Boom10.mp3"));
        explosionSound.setVolume(1, 0.5f);
        
        initializeMusic();

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
        
        if (godModeActive) {
            nave.setInvincible(true);
            
            if (nave.getShipStats().getDamageMultiplier() < 5.0f) {
                nave.getShipStats().addDamageUpgrade(4.0f);
            }
            
            for (int i = 0; i < 5; i++) {
                nave.getShipStats().addHomingUpgrade(0.1f);
            }
            
            for (int i = 0; i < 5; i++) {
                nave.getShipStats().addBouncingBulletsLevel();
            }
        }
        
        enemyManager = new EnemyManager(nave);
        
        collisionSystem.setNave(nave);
        
        projectileManager.setNave(nave);
        
        if (this.ronda <= MAX_WAVES) {
            enemyManager.startWave(this.ronda);
        }
    }
    
    public void dibujaEncabezado() {
        CharSequence str = "Ronda: " + ronda;
        game.getFont().getData().setScale(2f);
        game.getFont().setColor(1, 1, 1, 1);
        game.getFont().draw(batch, str, 30, 50);
        game.getFont().draw(batch, "Score:" + gameState.getScore(), 1920 - 300, 50);
        game.getFont().draw(batch, "HighScore:" + game.getHighScore(), 1920 / 2 - 150, 50);

        if (!isTransitioningToLevelUp) {
            xpSystem.render(batch, game.getFont(), 1920);
        }
    }
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        checkGodModeToggle();
        
        viewport.apply();
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        
        batch.begin();
        
        parallaxBackground.update(delta);
        parallaxBackground.render(batch);

        dibujaEncabezado();
        
        nave.setInputBlocked(false);
        nave.handleInput(this);
        
        nave.update(delta);
        
        handleShipShooting();

        xpSystem.update(delta);
        levelSystem.update();
        
        boolean canShowLevelUp = !isTransitioningToLevelUp && !waveTransitionInProgress;
        
        if (levelSystem.shouldShowLevelUpScreen() && canShowLevelUp) {
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
        
        if (tutorialSystem != null) {
            tutorialSystem.update(delta);
            tutorialSystem.render(batch);
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

        boolean isWaveComplete = enemyManager.isWaveComplete();
        
        if (isWaveComplete && !waveTransitionInProgress) {
            System.out.println("[TESTING] Ronda " + ronda + " completada");
            if (!waveTransitionInProgress) {
                waveTransitionInProgress = true;
            }
            
            if (tutorialSystem != null) {
                tutorialSystem.startFadeOut();
            }
            
            int currentScore = gameState.getScore();
            if (xpSystem != null) {
                if (!xpSystem.hasLeveledUp() && !levelSystem.shouldShowLevelUpScreen()) {
                    System.out.println("[TESTING] Forzando level-up");
                    xpSystem.forceLevelUp();
                    levelSystem.update();
                }
            }
            
            System.out.println("[TESTING] shouldShowLevelUpScreen: " + levelSystem.shouldShowLevelUpScreen());
            System.out.println("[TESTING] isTransitioningToLevelUp: " + isTransitioningToLevelUp);
            
            if (levelSystem.shouldShowLevelUpScreen() && !isTransitioningToLevelUp) {
                System.out.println("[TESTING] Mostrando pantalla de upgrade");
                isTransitioningToLevelUp = true;
                gameMusic.pause();
                bonusManager.forceExpireBonus(nave);
                
                Screen ss = new PantallaUpgrade(game, this, nave, parallaxBackground, 
                                               xpSystem, levelSystem, ronda, currentScore,
                                               velXAsteroides, velYAsteroides, cantAsteroides, bonusManager);
                ss.resize(1920, 1080);
                game.setScreen(ss);
                return;
            }
            
            float currentNaveX = nave.getX();
            float currentNaveY = nave.getY();
            
            int nextRound = ronda + 1;
            System.out.println("[TESTING] Siguiente ronda: " + nextRound + " (MAX_WAVES=" + MAX_WAVES + ")");
            if (nextRound >= 15) {
                System.out.println("[TESTING] Transicionando a PantallaBoss");
                gameMusic.stop();
                bonusManager.forceExpireBonus(nave);
                
                Screen bossScreen = new PantallaBoss(game, nave, xpSystem,
                                                     parallaxBackground, currentScore, bonusManager,
                                                     projectileManager);
                bossScreen.resize(1920, 1080);
                game.setScreen(bossScreen);
                return;
            }
            
            String nextMusicPath = getMusicPathForRound(nextRound);
            if (!nextMusicPath.equals(currentMusicPath)) {
                if (gameMusic != null) {
                    gameMusic.stop();
                }
            }
            
            shouldDisposeParallax = false;
            if (nextRound <= MAX_WAVES) {
                waveTransitionInProgress = false;
            }
            System.out.println("[TESTING] Creando PantallaJuego para ronda " + nextRound);
            PantallaJuego newScreen = new PantallaJuego(game, nextRound, nave.getVidas(), currentScore, 
                    velXAsteroides + 3, velYAsteroides + 3, cantAsteroides + 10, xpSystem, parallaxBackground,
                    currentNaveX, currentNaveY, nave);
            
            if (nextRound >= MAX_WAVES) {
                newScreen.waveTransitionInProgress = true;
            }
            
            newScreen.resize(1920, 1080);
            game.setScreen(newScreen);
            gameMusic.dispose();
        }
    }
    
    @Override
    public void show() {
        if (gameMusic != null && !gameMusic.isPlaying()) {
            gameMusic.play();
        }
    }

    public void onReturnFromUpgrade() {
        this.isTransitioningToLevelUp = false;
        levelSystem.levelUpScreenShown();
        
        if (gameMusic != null && !gameMusic.isPlaying()) {
            gameMusic.play();
        }
        
        System.out.println("[TESTING] Regreso de upgrade, ronda actual: " + ronda);
        
        if (enemyManager.isWaveComplete()) {
            int nextRound = ronda + 1;
            System.out.println("[TESTING] Continuando transicion, siguiente ronda: " + nextRound);
            
            if (nextRound >= 15) {
                System.out.println("[TESTING] Transicionando a PantallaBoss desde upgrade");
                gameMusic.stop();
                bonusManager.forceExpireBonus(nave);
                
                int currentScore = gameState.getScore();
                Screen bossScreen = new PantallaBoss(game, nave, xpSystem,
                                                     parallaxBackground, currentScore, bonusManager,
                                                     projectileManager);
                bossScreen.resize(1920, 1080);
                game.setScreen(bossScreen);
                return;
            }
            
            float currentNaveX = nave.getX();
            float currentNaveY = nave.getY();
            int currentScore = gameState.getScore();
            
            String nextMusicPath = getMusicPathForRound(nextRound);
            if (!nextMusicPath.equals(currentMusicPath)) {
                if (gameMusic != null) {
                    gameMusic.stop();
                }
            }
            
            shouldDisposeParallax = false;
            System.out.println("[TESTING] Creando PantallaJuego para ronda " + nextRound + " desde upgrade");
            PantallaJuego newScreen = new PantallaJuego(game, nextRound, nave.getVidas(), currentScore, 
                    velXAsteroides + 3, velYAsteroides + 3, cantAsteroides + 10, xpSystem, parallaxBackground,
                    currentNaveX, currentNaveY, nave);
            
            newScreen.resize(1920, 1080);
            game.setScreen(newScreen);
            gameMusic.dispose();
        }
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
                int bonusValue = bonusManager.getActiveBonus().getValue();
                bonusManager.bonusCollected(nave);
                gameState.addScore(bonusValue);
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
        if (this.gameMusic != null) {
            this.gameMusic.dispose();
        }
        if (this.currentTrack != null && this.currentTrack != this.gameMusic) {
            this.currentTrack.dispose();
        }
        
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
        
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
        
        if (tutorialSystem != null) {
            tutorialSystem.dispose();
        }
    }
    
    private void initializeMusic() {
        String musicPath = getMusicPathForRound(ronda);
        currentMusicPath = musicPath;
        
        try {
            gameMusic = Gdx.audio.newMusic(Gdx.files.internal(musicPath));
            currentTrack = gameMusic;
            
            gameMusic.setLooping(true);
            gameMusic.setVolume(0.5f);
            gameMusic.play();
        } catch (Exception e) {
            System.out.println("Error cargando música: " + musicPath + " - " + e.getMessage());
            gameMusic = null;
            currentTrack = null;
        }
    }
    
    private String getMusicPathForRound(int round) {
        if (round >= 1 && round <= 4) {
            return "Audio/Music/NombreProyecto - 1 chill.mp3";
        } else if (round >= 5 && round <= 8) {
            return "Audio/Music/NombreProyecto - 2 medio.mp3";
        } else {
            return "Audio/Music/NombreProyecto - 3 epic.mp3";
        }
    }
    
    private void checkGodModeToggle() {
        if (godModeToggleCooldown > 0) {
            godModeToggleCooldown -= Gdx.graphics.getDeltaTime();
            return;
        }
        
        boolean ctrlPressed = Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.CONTROL_LEFT) ||
                              Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.CONTROL_RIGHT);
        boolean pPressed = Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.P);
        boolean bothPressed = ctrlPressed && pPressed;
        
        if (bothPressed && !wasGodModeKeyPressed) {
            wasGodModeKeyPressed = true;
            godModeToggleCooldown = GOD_MODE_COOLDOWN_TIME;
            godModeActive = !godModeActive;
            
            if (godModeActive) {
                nave.setInvincible(true);
                
                if (nave.getShipStats().getDamageMultiplier() < 5.0f) {
                    nave.getShipStats().addDamageUpgrade(4.0f);
                }
                
                for (int i = 0; i < 5; i++) {
                    nave.getShipStats().addHomingUpgrade(0.1f);
                }
                
                for (int i = 0; i < 5; i++) {
                    nave.getShipStats().addBouncingBulletsLevel();
                }
            } else {
                nave.setInvincible(false);
            }
        }
        
        if (!bothPressed) {
            wasGodModeKeyPressed = false;
        }
    }
    
    public ProjectileManager getProjectileManager() {
        return projectileManager;
    }
}