package puppy.code.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import puppy.code.SpaceNavigation;
import puppy.code.entities.Nave;
import puppy.code.entities.enemies.BossEnemy;
import puppy.code.entities.enemies.BossPatternType;
import puppy.code.entities.projectiles.BossBullet;
import puppy.code.graphics.ParallaxBackground;
import puppy.code.managers.BonusManager;
import puppy.code.managers.GameStateManager;
import puppy.code.managers.ProjectileManager;
import puppy.code.systems.BossPatternExecutor;
import puppy.code.systems.CollisionSystem;
import puppy.code.systems.XPSystem;

public class PantallaBoss implements Screen {
    
    private SpaceNavigation game;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;
    private ShapeRenderer shapeRenderer;
    
    // Entidades
    private Nave nave;
    private BossEnemy boss;
    
    // Sistemas
    private XPSystem xpSystem;
    private CollisionSystem collisionSystem;
    private ProjectileManager projectileManager;
    private BonusManager bonusManager;
    private GameStateManager gameState;
    private BossPatternExecutor patternExecutor;
    
    // Background
    private ParallaxBackground parallaxBackground;
    
    // Musica
    private Music bossMusic;
    
    // Cinematica
    private float naveTargetX;
    private float naveTargetY;
    private float naveSpeedX;
    private float naveSpeedY;
    
    public PantallaBoss(SpaceNavigation game, Nave nave, XPSystem xpSystem,
                       ParallaxBackground parallaxBackground, int score, BonusManager bonusManager,
                       ProjectileManager projectileManager) {
        this.game = game;
        this.nave = nave;
        this.xpSystem = xpSystem;
        this.parallaxBackground = parallaxBackground;
        this.bonusManager = bonusManager;
        this.projectileManager = projectileManager;
        
        projectileManager.getActiveProjectiles().clear();
        bonusManager.forceExpireBonus(nave);
        
        this.batch = game.getBatch();
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, 1920, 1080);
        this.viewport = new FitViewport(1920, 1080, camera);
        this.shapeRenderer = new ShapeRenderer();
        
        this.collisionSystem = new CollisionSystem(xpSystem);
        this.gameState = GameStateManager.getInstance();
        this.gameState.setScore(score);
        this.patternExecutor = new BossPatternExecutor();
        
        spawnBoss();
        
        bossMusic = Gdx.audio.newMusic(Gdx.files.internal("Audio/Music/NombreProyecto - 3 epic.mp3"));
        bossMusic.setLooping(true);
        bossMusic.setVolume(0.5f);
        bossMusic.play();
    }
    
    private void spawnBoss() {
        TextureAtlas bossAtlas = new TextureAtlas(Gdx.files.internal("Game/Enemys/Boss/Agiss.atlas"));
        boss = new BossEnemy(0, 0, bossAtlas, 1000);
        
        // Inicializar cinematica
        naveTargetX = (1920 - nave.getWidth()) / 2f;
        naveTargetY = 50f;
        
        float bossDistance = 1080f - 165f;
        float bossTime = bossDistance / 100f;
        
        float naveDistanceX = Math.abs(naveTargetX - nave.getSpriteX());
        float naveDistanceY = Math.abs(naveTargetY - nave.getSpriteY());
        
        naveSpeedX = naveDistanceX / bossTime;
        naveSpeedY = naveDistanceY / bossTime;
    }

    @Override
    public void show() {
        
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
        
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        
        batch.begin();
        
        parallaxBackground.render(batch);
        
        if (boss.isEntering()) {
            updateBossCinematic(delta);
        }
        
        boss.update(delta);
        
        if (!boss.isEntering()) {
            nave.update(delta);
            handleShipShooting();
            updateBossPatterns(delta);
        }
        
        patternExecutor.update(delta);
        
        projectileManager.update(delta);
        projectileManager.setEnemies(new java.util.ArrayList<>());
        
        if (!boss.isEntering()) {
            bonusManager.update(delta);
            bonusManager.updateShipBehavior(nave);
        }
        
        xpSystem.update(delta);

        if (!nave.estaHerido() && !boss.isEntering()) {
            collisionSystem.checkProjectileBossCollisions(
                projectileManager.getActiveProjectiles(),
                boss,
                projectileManager
            );
            
            collisionSystem.checkEnemyProjectileShipCollisions(
                projectileManager.getActiveProjectiles(),
                nave,
                projectileManager
            );
            
            checkBonusCollisions();
        }
        
        parallaxBackground.render(batch);
        
        batch.end();
        
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (BossBullet bullet : patternExecutor.getBossBullets()) {
            bullet.render(shapeRenderer);
        }
        shapeRenderer.end();
        
        batch.begin();
        
        boss.draw(batch);
        
        projectileManager.render(batch);
        
        if (bonusManager.getActiveBonus() != null) {
            bonusManager.getActiveBonus().draw(batch);
        }
        
        nave.draw(batch);
        
        if (bonusManager.isBonusEffectActive() && 
            bonusManager.getActiveBonusType() == puppy.code.entities.bonus.BonusType.SHIELD) {
            bonusManager.getActiveBonus().renderEffect(batch, nave);
        }

        if (!boss.isEntering()) {
            nave.renderHealthHearts(batch);
            nave.renderHomingIndicator(batch);
            
            if (nave.getTurboSystem() != null) {
                nave.getTurboSystem().render(batch);
            }
        }
        
        batch.end();

        if (!boss.isEntering()) {
            renderBossHealthBar();
        }

        if (boss.getHealth() <= 0) {
            bossMusic.stop();
            Screen winScreen = new PantallaGameWin(game);
            winScreen.resize(1920, 1080);
            game.setScreen(winScreen);
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
        
        // Fondo negro
        shapeRenderer.setColor(0f, 0f, 0f, 1f);
        shapeRenderer.rect(barX, barY, barWidth, barHeight);
        
        // Color segun fase
        BossEnemy.BossState state = boss.getCurrentState();
        if (state == BossEnemy.BossState.PHASE_1) {
            shapeRenderer.setColor(0.6f, 0.2f, 0.8f, 1f);
        } else if (state == BossEnemy.BossState.PHASE_2) {
            shapeRenderer.setColor(0.8f, 0.3f, 0.3f, 1f);
        } else if (state == BossEnemy.BossState.PHASE_3) {
            shapeRenderer.setColor(0.9f, 0.7f, 0.1f, 1f);
        }
        
        // Barra actual
        shapeRenderer.rect(barX, barY, currentBarWidth, barHeight);
        
        // Divisores de fase
        shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 1f);
        float divider1X = barX + (barWidth * 0.333f);
        float divider2X = barX + (barWidth * 0.666f);
        shapeRenderer.rectLine(divider1X, barY, divider1X, barY + barHeight, 3f);
        shapeRenderer.rectLine(divider2X, barY, divider2X, barY + barHeight, 3f);
        
        // Borde
        shapeRenderer.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.8f, 0.8f, 0.8f, 1f);
        shapeRenderer.rect(barX, barY, barWidth, barHeight);
        shapeRenderer.end();
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
    
    private void handleShipShooting() {
        if (nave.canShoot()) {
            nave.executeShoot(projectileManager);
            nave.resetShotCooldown();
        }
    }
    
    private void updateBossPatterns(float delta) {
        if (boss.getCurrentState() == BossEnemy.BossState.PHASE_1) {
            boss.updatePatternTimer(delta);
            
            if (boss.getPatternTimer() >= boss.getEffectiveCooldown()) {
                patternExecutor.executePattern(boss, BossPatternType.RADIAL_ROTATING);
                boss.onShotFired();
                boss.resetPatternTimer();
            }
        }
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
    
    public void onReturnFromUpgrade() {
        if (bossMusic != null && !bossMusic.isPlaying()) {
            bossMusic.play();
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

    @Override
    public void dispose() {
        if (bossMusic != null) {
            bossMusic.dispose();
        }
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
    }
}
