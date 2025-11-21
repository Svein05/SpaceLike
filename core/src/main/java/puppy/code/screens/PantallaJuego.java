package puppy.code.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import puppy.code.SpaceNavigation;
import puppy.code.graphics.ParallaxBackground;
import puppy.code.entities.Nave;
import puppy.code.entities.enemies.BossEnemy;
import puppy.code.systems.XPSystem;
import puppy.code.systems.LevelSystem;
import puppy.code.systems.CollisionSystem;
import puppy.code.systems.TutorialSystem;
import puppy.code.managers.ProjectileManager;
import puppy.code.managers.GameStateManager;
import puppy.code.managers.EnemyManager;
import puppy.code.managers.BonusManager;

public class PantallaJuego implements Screen {

    private static final int MAX_WAVES = 15;
    
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
    private BossEnemy boss;
    private boolean bossSpawned = false;
    private boolean pendingSpawnBoss = false;
    private boolean waveTransitionInProgress = false;
    private boolean pendingBossTransition = false;
    private ShapeRenderer shapeRenderer;
    private static boolean godModeActive = false;
    private static float godModeToggleCooldown = 0f;
    private boolean wasGodModeKeyPressed = false;
    private static final float GOD_MODE_COOLDOWN_TIME = 5.0f;
    
    // Cinematica de entrada del boss
    private float naveTargetX;
    private float naveTargetY;
    private float naveSpeedX;
    private float naveSpeedY;

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
        this(game, ronda, vidas, score, velXAsteroides, velYAsteroides, cantAsteroides,
             xpSystem, existingParallax, naveX, naveY, existingNave, existingBonusManager, null);
    }
    
    public PantallaJuego(SpaceNavigation game, int ronda, int vidas, int score,  
            int velXAsteroides, int velYAsteroides, int cantAsteroides, XPSystem xpSystem, 
            ParallaxBackground existingParallax, float naveX, float naveY, Nave existingNave, 
            puppy.code.managers.BonusManager existingBonusManager, BossEnemy existingBoss) {
        this.game = game;
        
        this.ronda = ronda;
        
        this.velXAsteroides = velXAsteroides;
        this.velYAsteroides = velYAsteroides;
        this.cantAsteroides = cantAsteroides;
        
        if (existingBoss != null) {
            this.boss = existingBoss;
            this.bossSpawned = true;
            
            if (boss.isEntering()) {
                naveTargetX = (1920 - 66f) / 2f;
                naveTargetY = 50f;
                
                float bossDistance = 1080f - 165f;
                float bossTime = bossDistance / 100f;
                
                float naveDistanceX = Math.abs(naveTargetX - naveX);
                float naveDistanceY = Math.abs(naveTargetY - naveY);
                
                naveSpeedX = naveDistanceX / bossTime;
                naveSpeedY = naveDistanceY / bossTime;
            }
        }
        
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
        
        if (this.ronda < MAX_WAVES) {
            enemyManager.startWave(this.ronda);
        }
    }
    
    public void dibujaEncabezado() {
        if (boss != null) {
            return;
        }
        
        CharSequence str = "Ronda: " + ronda;
        game.getFont().getData().setScale(2f);
        game.getFont().setColor(1, 1, 1, 1);
        game.getFont().draw(batch, str, 30, 50);
        game.getFont().draw(batch, "Score:" + gameState.getScore(), 1920 - 300, 50);
        game.getFont().draw(batch, "HighScore:" + game.getHighScore(), 1920 / 2 - 150, 50);

        if (!pendingBossTransition && !isTransitioningToLevelUp) {
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

        if (boss != null) {
            boss.update(delta);
            boss.draw(batch);
        }
        
        dibujaEncabezado();
        
        if (boss != null && boss.isEntering()) {
            nave.setInputBlocked(true);
        } else {
            nave.setInputBlocked(false);
            nave.handleInput(this);
        }
        
        nave.update(delta);
        
        if (boss != null && boss.isEntering()) {
            updateBossCinematic(delta);
        }
        
        handleShipShooting();

        xpSystem.update(delta);
        levelSystem.update();
        
        boolean allowLevelUpInBossRound = (ronda >= MAX_WAVES && !bossSpawned && !pendingSpawnBoss);
        boolean allowLevelUpForBossTransition = pendingBossTransition;
        boolean canShowLevelUp = !isTransitioningToLevelUp && (!waveTransitionInProgress || allowLevelUpInBossRound || allowLevelUpForBossTransition) && !pendingSpawnBoss;
        
        if (levelSystem.shouldShowLevelUpScreen() && canShowLevelUp) {
            isTransitioningToLevelUp = true;
            int currentScore = gameState.getScore();
            gameMusic.pause();
            
            bonusManager.forceExpireBonus(nave);
            
            Screen ss = new PantallaUpgrade(game, this, nave, parallaxBackground, 
                                           xpSystem, levelSystem, ronda, currentScore,
                                           velXAsteroides, velYAsteroides, cantAsteroides, bonusManager, boss);
            ss.resize(1920, 1080);
            game.setScreen(ss);
            return;
        }
        
        projectileManager.update(delta);
        projectileManager.setEnemies(enemyManager.getActiveEnemies());
        
        if (boss == null) {
            enemyManager.update(delta);
            enemyManager.updateEnemyShooting(projectileManager);
        }

        if (boss == null || !boss.isEntering()) {
            bonusManager.update(delta);
            bonusManager.updateShipBehavior(nave);
        }
        
        if (!nave.estaHerido()) {
            if (boss == null) {
                collisionSystem.checkProjectileEnemyCollisions(
                    projectileManager.getActiveProjectiles(),
                    enemyManager.getActiveEnemies(),
                    projectileManager
                );

                collisionSystem.checkShipEnemyCollisions(nave, enemyManager.getActiveEnemies());
            }
            
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
            
            if (boss != null) {
                collisionSystem.checkProjectileBossCollisions(
                    projectileManager.getActiveProjectiles(),
                    boss,
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

        if (boss == null || !boss.isEntering()) {
            nave.renderHealthHearts(batch);
        }

        if (boss == null || !boss.isEntering()) {
            nave.renderHomingIndicator(batch);
        }

        if (nave.getTurboSystem() != null) {
            if (boss == null || !boss.isEntering()) {
                nave.getTurboSystem().render(batch);
            }
        }
        
        if (tutorialSystem != null) {
            tutorialSystem.update(delta);
            tutorialSystem.render(batch);
        }
        
        batch.end();
        
        if (boss != null && !boss.isEntering()) {
            renderBossHealthBar();
            if (boss.isDestroyed()) {
                Screen ss = new PantallaGameWin(game);
                ss.resize(1920, 1080);
                game.setScreen(ss);
                dispose();
                return;
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
            return;
        }

        if (boss != null) {
            return;
        }

        if (pendingSpawnBoss) {
            return;
        }

        boolean isWaveComplete = enemyManager.isWaveComplete();
        boolean allowBossRoundEntry = (ronda >= MAX_WAVES && !bossSpawned && waveTransitionInProgress);
        
        if (isWaveComplete && (!waveTransitionInProgress || allowBossRoundEntry)) {
            if (!waveTransitionInProgress) {
                waveTransitionInProgress = true;
            }
            
            if (tutorialSystem != null) {
                tutorialSystem.startFadeOut();
            }
            
            int currentScore = gameState.getScore();
            if (xpSystem != null) {
                if (!xpSystem.hasLeveledUp() && !levelSystem.shouldShowLevelUpScreen()) {
                    xpSystem.forceLevelUp();
                }
            }
            
            float currentNaveX = nave.getX();
            float currentNaveY = nave.getY();
            
            int nextRound = ronda + 1;
            if (nextRound > MAX_WAVES) {
                gameMusic.stop();
                bonusManager.forceExpireBonus(nave);
                
                Screen bossScreen = new PantallaBoss(game, nave, xpSystem,
                                                     parallaxBackground, currentScore, bonusManager,
                                                     projectileManager);
                bossScreen.resize(1920, 1080);
                game.setScreen(bossScreen);
                return;
            }
            
            if (ronda == 14) {
                pendingBossTransition = true;
                return;
            }
            
            String nextMusicPath = getMusicPathForRound(nextRound);
            if (!nextMusicPath.equals(currentMusicPath)) {
                if (gameMusic != null) {
                    gameMusic.stop();
                }
            }
            
            shouldDisposeParallax = false;
            if (nextRound < MAX_WAVES) {
                waveTransitionInProgress = false;
            }
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
    
    private void renderBossHealthBar() {
        float barWidth = 1200f;
        float barHeight = 30f;
        float barX = (1920 - barWidth) / 2f;
        float barY = 1020f;
        
        float healthPercentage = (float) boss.getHealth() / boss.getMaxHealth();
        float currentBarWidth = barWidth * healthPercentage;
        
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        shapeRenderer.setColor(0f, 0f, 0f, 1f);
        shapeRenderer.rect(barX, barY, barWidth, barHeight);

        BossEnemy.BossState state = boss.getCurrentState();
        if (state == BossEnemy.BossState.PHASE_1) {
            shapeRenderer.setColor(0.6f, 0.2f, 0.8f, 1f);
        } else if (state == BossEnemy.BossState.PHASE_2) {
            shapeRenderer.setColor(0.8f, 0.3f, 0.3f, 1f);
        } else if (state == BossEnemy.BossState.PHASE_3) {
            shapeRenderer.setColor(0.9f, 0.7f, 0.1f, 1f);
        }

        shapeRenderer.rect(barX, barY, currentBarWidth, barHeight);
        
        shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 1f);
        float divider1X = barX + (barWidth * 0.333f);
        float divider2X = barX + (barWidth * 0.666f);
        shapeRenderer.rectLine(divider1X, barY, divider1X, barY + barHeight, 3f);
        shapeRenderer.rectLine(divider2X, barY, divider2X, barY + barHeight, 3f);
        
        shapeRenderer.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.8f, 0.8f, 0.8f, 1f);
        shapeRenderer.rect(barX, barY, barWidth, barHeight);
        shapeRenderer.end();
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
        
        if (pendingBossTransition) {
            gameMusic.stop();
            bonusManager.forceExpireBonus(nave);
            
            Screen bossScreen = new PantallaBoss(game, nave, xpSystem,
                                                 parallaxBackground, gameState.getScore(), bonusManager,
                                                 projectileManager);
            bossScreen.resize(1920, 1080);
            game.setScreen(bossScreen);
            return;
        }
        
        if (gameMusic != null && !gameMusic.isPlaying()) {
            gameMusic.play();
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
        if (boss != null && boss.isEntering()) {
            if (nave.isShooting()) {
                nave.stopShooting();
            }
            if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.SPACE)) {}
            return;
        }
        
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
    
    private void updateMusicForRound() {
        String newMusicPath = getMusicPathForRound(ronda);
        
        if (!newMusicPath.equals(currentMusicPath)) {
            if (gameMusic != null) {
                gameMusic.stop();
                gameMusic.dispose();
            }
            
            try {
                currentMusicPath = newMusicPath;
                gameMusic = Gdx.audio.newMusic(Gdx.files.internal(newMusicPath));
                currentTrack = gameMusic;
                
                gameMusic.setLooping(true);
                gameMusic.setVolume(0.5f);
                gameMusic.play();
            } catch (Exception e) {
                System.out.println("Error cambiando música a: " + newMusicPath + " - " + e.getMessage());
                gameMusic = null;
                currentTrack = null;
            }
        }
    }
    
    private void spawnBoss() {
        TextureAtlas bossAtlas = new TextureAtlas(Gdx.files.internal("Game/Enemys/Boss/Agiss.atlas"));
        boss = new BossEnemy(0, 0, bossAtlas, 1000);
        bossSpawned = true;
        
        naveTargetX = (1920 - nave.getWidth()) / 2f;
        naveTargetY = 50f;
        
        float bossDistance = 1080f - 165f;
        float bossTime = bossDistance / 100f;
        
        float naveDistanceX = Math.abs(naveTargetX - nave.getSpriteX());
        float naveDistanceY = Math.abs(naveTargetY - nave.getSpriteY());
        
        naveSpeedX = naveDistanceX / bossTime;
        naveSpeedY = naveDistanceY / bossTime;
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
    
    private void updateBossCinematic(float delta) {
        
        float currentX = nave.getSpriteX();
        float currentY = nave.getSpriteY();
        
        float newX = currentX;
        float newY = currentY;
        
        if (Math.abs(currentX - naveTargetX) > 1f) {
            if (currentX < naveTargetX) {
                newX = currentX + naveSpeedX * delta;
            } else {
                newX = currentX - naveSpeedX * delta;
            }
        } else {
            newX = naveTargetX;
        }
        
        if (Math.abs(currentY - naveTargetY) > 1f) {
            if (currentY < naveTargetY) {
                newY = currentY + naveSpeedY * delta;
            } else {
                newY = currentY - naveSpeedY * delta;
            }
        } else {
            newY = naveTargetY;
        }
        
        nave.setPosition(newX, newY);
    }
    
    public ProjectileManager getProjectileManager() {
        return projectileManager;
    }
}