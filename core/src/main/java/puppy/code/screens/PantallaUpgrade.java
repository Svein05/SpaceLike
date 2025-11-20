package puppy.code.screens;

import puppy.code.SpaceNavigation;
import puppy.code.entities.Nave;
import puppy.code.graphics.ParallaxBackground;
import puppy.code.stats.UpgradeOption;
import puppy.code.stats.UpgradeType;
import puppy.code.systems.UpgradeManager;
import puppy.code.systems.XPSystem;
import puppy.code.systems.LevelSystem;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PantallaUpgrade implements Screen {

    private SpaceNavigation game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    
    private List<UpgradeOption> upgradeOptions;
    private Random random;
    
    private Nave nave;
    private ParallaxBackground parallaxBackground;
    private XPSystem xpSystem;
    private LevelSystem levelSystem;
    
    private int ronda;
    private int score;
    private int velXAsteroides;
    private int velYAsteroides;
    private int cantAsteroides;
    private float naveX;
    private float naveY;
    private puppy.code.managers.BonusManager bonusManager;
    
    public PantallaUpgrade(SpaceNavigation game, PantallaJuego pantallaJuego, 
                          Nave nave, ParallaxBackground parallax, XPSystem xpSystem,
                          LevelSystem levelSystem, int ronda, int score, 
                          int velXAsteroides, int velYAsteroides, int cantAsteroides,
                          puppy.code.managers.BonusManager bonusManager) {
        this.game = game;
        this.nave = nave;
        this.parallaxBackground = parallax;
        this.xpSystem = xpSystem;
        this.levelSystem = levelSystem;
        this.ronda = ronda;
        this.score = score;
        this.velXAsteroides = velXAsteroides;
        this.velYAsteroides = velYAsteroides;
        this.cantAsteroides = cantAsteroides;
        this.bonusManager = bonusManager;
        this.naveX = nave.getX();
        this.naveY = nave.getY();
        
        xpSystem.consumeLevelUp();
        
        batch = game.getBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);
        viewport = new FitViewport(1920, 1080, camera);
        
        shapeRenderer = new ShapeRenderer();
        random = new Random();
        
        generateUpgradeOptions();
    }
    
    private void generateUpgradeOptions() {
        upgradeOptions = new ArrayList<>();
        
        float optionWidth = 400;
        float optionHeight = 200;
        float spacing = 100;
        float totalWidth = (optionWidth * 3) + (spacing * 2);
        float startX = (1920 - totalWidth) / 2;
        float y = 400;
        
        UpgradeType[] allTypes = UpgradeType.values();
        List<UpgradeType> availableTypes = new ArrayList<>();
        for (UpgradeType type : allTypes) {
            if (type == UpgradeType.SPINNER_UNLOCK) {
                if (nave.getShipStats().isSpinnerUnlocked()) {
                    continue;
                }
            }
            
            if (type == UpgradeType.SPINNER_COUNT) {
                if (!nave.getShipStats().isSpinnerUnlocked() || nave.getShipStats().getSpinnerCount() >= 10) {
                    continue;
                }
            }
            
            if (type == UpgradeType.SPINNER_DAMAGE) {
                if (!nave.getShipStats().isSpinnerUnlocked()) {
                    continue;
                }
            }
            availableTypes.add(type);
        }
        
        for (int i = 0; i < 3 && !availableTypes.isEmpty(); i++) {
            int randomIndex = random.nextInt(availableTypes.size());
            UpgradeType selectedType = availableTypes.remove(randomIndex);
            
            float x = startX + (i * (optionWidth + spacing));
            UpgradeOption option = new UpgradeOption(selectedType, x, y, optionWidth, optionHeight, nave);
            upgradeOptions.add(option);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        viewport.apply();
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
        
        if (batch.isDrawing()) {
            batch.end();
        }
        
        int mouseX = Gdx.input.getX();
        int mouseY = 1080 - Gdx.input.getY();
        
        for (UpgradeOption option : upgradeOptions) {
            boolean isHovered = option.contains(mouseX, mouseY);
            option.setHovered(isHovered);
            
            if (isHovered && Gdx.input.justTouched()) {
                selectUpgrade(option.getType());
                return;
            }
        }
        
        for (UpgradeOption option : upgradeOptions) {
            option.renderBackground(shapeRenderer);
        }
        
        batch.begin();
        
        if (parallaxBackground != null) {
            parallaxBackground.render(batch);
        }
        
        game.getFont().getData().setScale(3f);
        game.getFont().setColor(Color.GOLD);
        String title = "¡NIVEL " + levelSystem.getCurrentLevel() + "!";
        float titleWidth = game.getFont().getRegions().get(0).getRegionWidth() * title.length() * 1.5f;
        game.getFont().draw(batch, title, (1920 - titleWidth) / 2, 900);
        
        game.getFont().getData().setScale(2f);
        game.getFont().setColor(Color.WHITE);
        String subtitle = "Elige una mejora:";
        float subtitleWidth = game.getFont().getRegions().get(0).getRegionWidth() * subtitle.length();
        game.getFont().draw(batch, subtitle, (1920 - subtitleWidth) / 2, 800);
        
        for (UpgradeOption option : upgradeOptions) {
            option.renderText(batch, game.getFont());
        }
        
        game.getFont().getData().setScale(1f);
        game.getFont().setColor(Color.GRAY);
        String helpText = "Haz clic en una opcion para seleccionarla";
        game.getFont().draw(batch, helpText, 1920/2 - 200, 200);
        
        game.getFont().setColor(1, 1, 1, 1);
        
        batch.end();
    }
    
    private void selectUpgrade(UpgradeType upgradeType) {
        UpgradeManager.applyUpgrade(nave, upgradeType);
        
        levelSystem.levelUpScreenShown();
        
        Screen ss = new PantallaJuego(game, ronda, nave.getVidas(), score,
                                     velXAsteroides, velYAsteroides, cantAsteroides,
                                     xpSystem, parallaxBackground, naveX, naveY, nave, bonusManager);
        ss.resize(1920, 1080);
        game.setScreen(ss);
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
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
    }
}