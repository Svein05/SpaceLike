package puppy.code.graphics;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ParallaxBackground {
    private Texture fondoSpaceBackground;
    private Texture fondoSpaceStars01;
    private Texture[] fondoSpaceDustVariations;
    private Texture[] fondoNebuloseVariations;
    private Texture[] fondoSpaceStars03Variations;

    private float spaceBackgroundOffsetY;
    private float spaceStars01OffsetY;
    private float spaceDustOffsetY;
    private float nebuloseOffsetY;
    private float spaceStars03OffsetY;
    
    private float spaceDustCycleTimer;
    private float spaceDustCycleDuration;
    private int currentSpaceDustIndex;
    private int nextSpaceDustIndex;
    private float spaceDustAlpha;
    private float nextSpaceDustAlpha;
    private boolean inSpaceDustTransition;
    
    private float nebuloseTransitionTimer;
    private float nebuloseCycleDuration;
    private int currentNebuloseIndex;
    private int nextNebuloseIndex;
    private float nebuloseAlpha;
    private float nextNebuloseAlpha;
    private boolean inNebuloseTransition;
    
    private float spaceStars03TransitionTimer;
    private float spaceStars03CycleDuration;
    private int currentSpaceStars03Index;
    private int nextSpaceStars03Index;
    private float spaceStars03Alpha;
    private float nextSpaceStars03Alpha;
    private boolean inSpaceStars03Transition;

    private Texture[] planetTextures;
    private Texture currentPlanetTexture;
    private float planetTimer;
    private float planetSpawnDelay;
    private boolean planetActive;
    private float planetX, planetY;
    private float planetScale;
    private float planetSpeed;
    private float planetLifetime;
    private float planetAge;
    private boolean renderBelowNebulose;
    
    private Random random;
    
    private static final float SPACE_BACKGROUND_SPEED = 60f;
    private static final float SPACE_STARS_01_SPEED = 80f;
    private static final float SPACE_DUST_SPEED = 90f;
    private static final float NEBULOSE_SPEED = 100f;
    private static final float SPACE_STARS_03_SPEED = 120f;
    
    private static final float SPACE_STARS_01_OPACITY = 0.4f;
    private static final float SPACE_DUST_OPACITY = 0.6f;
    private static final float NEBULOSE_OPACITY = 0.8f;
    private static final float PLANET_OPACITY = 0.8f;
    
    public ParallaxBackground() {
        random = new Random();
        loadTextures();
        initializeOffsets();
        initializeTransitionSystems();
        initializePlanetSystem();
    }
    
    private void loadTextures() {
        // Cargar texturas principales
        fondoSpaceBackground = new Texture(Gdx.files.internal("Game/Fondo/Backgrounds/PNGs/Split up/00 Space Background.png"));
        fondoSpaceStars01 = new Texture(Gdx.files.internal("Game/Fondo/Backgrounds/PNGs/Split up/01 Space Stars.png"));
        
        // Cargar variaciones de Space Dust
        fondoSpaceDustVariations = new Texture[4];
        fondoSpaceDustVariations[0] = new Texture(Gdx.files.internal("Game/Fondo/Backgrounds/PNGs/Split up/01_1 Space Dust.png"));
        fondoSpaceDustVariations[1] = new Texture(Gdx.files.internal("Game/Fondo/Backgrounds/PNGs/Split up/01_1 Space Dust 2.png"));
        fondoSpaceDustVariations[2] = new Texture(Gdx.files.internal("Game/Fondo/Backgrounds/PNGs/Split up/01_1 Space Dust 3.png"));
        fondoSpaceDustVariations[3] = new Texture(Gdx.files.internal("Game/Fondo/Backgrounds/PNGs/Split up/01_1 Space Dust 4.png"));
        
        // Cargar variaciones de Space Nebulose
        fondoNebuloseVariations = new Texture[4];
        fondoNebuloseVariations[0] = new Texture(Gdx.files.internal("Game/Fondo/Backgrounds/PNGs/Split up/02 Space Nebulose.png"));
        fondoNebuloseVariations[1] = new Texture(Gdx.files.internal("Game/Fondo/Backgrounds/PNGs/Split up/02 Space Nebulose 2.png"));
        fondoNebuloseVariations[2] = new Texture(Gdx.files.internal("Game/Fondo/Backgrounds/PNGs/Split up/02 Space Nebulose 3.png"));
        fondoNebuloseVariations[3] = new Texture(Gdx.files.internal("Game/Fondo/Backgrounds/PNGs/Split up/02 Space Nebulose 4.png"));
        
        // Cargar variaciones de Space Stars 03
        fondoSpaceStars03Variations = new Texture[4];
        fondoSpaceStars03Variations[0] = new Texture(Gdx.files.internal("Game/Fondo/Backgrounds/PNGs/Split up/03 Space Stars.png"));
        fondoSpaceStars03Variations[1] = new Texture(Gdx.files.internal("Game/Fondo/Backgrounds/PNGs/Split up/03 Space Stars 2.png"));
        fondoSpaceStars03Variations[2] = new Texture(Gdx.files.internal("Game/Fondo/Backgrounds/PNGs/Split up/03 Space Stars 3.png"));
        fondoSpaceStars03Variations[3] = new Texture(Gdx.files.internal("Game/Fondo/Backgrounds/PNGs/Split up/03 Space Stars 4.png"));
        
        // Cargar texturas de planetas
        planetTextures = new Texture[13];
        for (int i = 0; i < 13; i++) {
            planetTextures[i] = new Texture(Gdx.files.internal("Game/Fondo/Planets/Planet " + (i + 1) + ".png"));
        }
    }
    
    private void initializeOffsets() {
        spaceBackgroundOffsetY = 0f;
        spaceStars01OffsetY = 0f;
        spaceDustOffsetY = 0f;
        nebuloseOffsetY = 0f;
        spaceStars03OffsetY = 0f;
    }
    
    private void initializeTransitionSystems() {
        spaceDustCycleTimer = 0f;
        spaceDustCycleDuration = 60f;
        currentSpaceDustIndex = 0;
        nextSpaceDustIndex = 1;
        spaceDustAlpha = 1.0f;
        nextSpaceDustAlpha = 0.0f;
        inSpaceDustTransition = false;
        
        nebuloseTransitionTimer = 10f;
        nebuloseCycleDuration = 30f;
        currentNebuloseIndex = 0;
        nextNebuloseIndex = 1;
        nebuloseAlpha = 1.0f;
        nextNebuloseAlpha = 0.0f;
        inNebuloseTransition = false;
        
        spaceStars03TransitionTimer = 5f;
        spaceStars03CycleDuration = 10f;
        currentSpaceStars03Index = 0;
        nextSpaceStars03Index = 1;
        spaceStars03Alpha = 1.0f;
        nextSpaceStars03Alpha = 0.0f;
        inSpaceStars03Transition = false;
    }
    
    private void initializePlanetSystem() {
        planetTimer = 0f;
        planetSpawnDelay = 5f;
        planetActive = false;
        currentPlanetTexture = null;
        planetX = 0f;
        planetY = 0f;
        planetScale = 1.0f;
        planetSpeed = 0f;
        planetLifetime = 0f;
        planetAge = 0f;
        renderBelowNebulose = false;
    }
    
    public void update(float delta) {
        updateParallaxMovement(delta);
        updateTransitionSystems(delta);
        updatePlanetSystem(delta);
    }
    
    private void updateParallaxMovement(float delta) {
        spaceBackgroundOffsetY -= SPACE_BACKGROUND_SPEED * delta;
        spaceStars01OffsetY -= SPACE_STARS_01_SPEED * delta;
        spaceDustOffsetY -= SPACE_DUST_SPEED * delta;
        nebuloseOffsetY -= NEBULOSE_SPEED * delta;
        spaceStars03OffsetY -= SPACE_STARS_03_SPEED * delta;
        
        if (spaceBackgroundOffsetY <= -1080) spaceBackgroundOffsetY = 0f;
        if (spaceStars01OffsetY <= -1080) spaceStars01OffsetY = 0f;
        if (spaceDustOffsetY <= -1080) spaceDustOffsetY = 0f;
        if (nebuloseOffsetY <= -1080) nebuloseOffsetY = 0f;
        if (spaceStars03OffsetY <= -1080) spaceStars03OffsetY = 0f;
    }
    
    private void updateTransitionSystems(float delta) {
        updateSpaceDustTransitions(delta);
        updateNebuloseTransitions(delta);
        updateSpaceStars03Transitions(delta);
    }
    
    private void updateSpaceDustTransitions(float delta) {
        spaceDustCycleTimer += delta;
        float cycleProgress = spaceDustCycleTimer / spaceDustCycleDuration;
        
        if (cycleProgress >= 1.0f) {
            spaceDustCycleTimer = 0f;
            spaceDustCycleDuration = 60f;
            currentSpaceDustIndex = nextSpaceDustIndex;
            
            do {
                nextSpaceDustIndex = random.nextInt(fondoSpaceDustVariations.length);
            } while (nextSpaceDustIndex == currentSpaceDustIndex);
            
            spaceDustAlpha = 1.0f;
            nextSpaceDustAlpha = 0.0f;
            inSpaceDustTransition = false;
            
        } else if (cycleProgress >= 0.5f) {
            inSpaceDustTransition = true;
            float fadeProgress = (cycleProgress - 0.5f) / 0.5f;
            spaceDustAlpha = 1.0f - fadeProgress;
            nextSpaceDustAlpha = fadeProgress;
            
        } else {
            inSpaceDustTransition = false;
            spaceDustAlpha = 1.0f;
            nextSpaceDustAlpha = 0.0f;
        }
    }
    
    private void updateNebuloseTransitions(float delta) {
        nebuloseTransitionTimer += delta;
        float nebuloseProgress = nebuloseTransitionTimer / nebuloseCycleDuration;
        
        if (nebuloseProgress >= 1.0f) {
            nebuloseTransitionTimer = 0f;
            nebuloseCycleDuration = 30f;
            currentNebuloseIndex = nextNebuloseIndex;
            
            do {
                nextNebuloseIndex = random.nextInt(fondoNebuloseVariations.length);
            } while (nextNebuloseIndex == currentNebuloseIndex);
            
            nebuloseAlpha = 1.0f;
            nextNebuloseAlpha = 0.0f;
            inNebuloseTransition = false;
            
        } else if (nebuloseProgress >= 0.5f) {
            inNebuloseTransition = true;
            float nebuloseFadeProgress = (nebuloseProgress - 0.5f) / 0.5f;
            nebuloseAlpha = 1.0f - nebuloseFadeProgress;
            nextNebuloseAlpha = nebuloseFadeProgress;
            
        } else {
            inNebuloseTransition = false;
            nebuloseAlpha = 1.0f;
            nextNebuloseAlpha = 0.0f;
        }
    }
    
    private void updateSpaceStars03Transitions(float delta) {
        spaceStars03TransitionTimer += delta;
        float stars03Progress = spaceStars03TransitionTimer / spaceStars03CycleDuration;
        
        if (stars03Progress >= 1.0f) {
            spaceStars03TransitionTimer = 0f;
            spaceStars03CycleDuration = 10f;
            currentSpaceStars03Index = nextSpaceStars03Index;
            
            do {
                nextSpaceStars03Index = random.nextInt(fondoSpaceStars03Variations.length);
            } while (nextSpaceStars03Index == currentSpaceStars03Index);
            
            spaceStars03Alpha = 1.0f;
            nextSpaceStars03Alpha = 0.0f;
            inSpaceStars03Transition = false;
            
        } else if (stars03Progress >= 0.5f) {
            inSpaceStars03Transition = true;
            float stars03FadeProgress = (stars03Progress - 0.5f) / 0.5f;
            spaceStars03Alpha = 1.0f - stars03FadeProgress;
            nextSpaceStars03Alpha = stars03FadeProgress;
            
        } else {
            inSpaceStars03Transition = false;
            spaceStars03Alpha = 1.0f;
            nextSpaceStars03Alpha = 0.0f;
        }
    }
    
    private void updatePlanetSystem(float delta) {
        planetTimer += delta;
        
        if (!planetActive) {
            if (planetTimer >= planetSpawnDelay) {
                spawnNewPlanet();
            }
        } else {
            planetAge += delta;
            planetY -= planetSpeed * delta;
            
            if (planetAge >= planetLifetime || planetY < -currentPlanetTexture.getHeight() * planetScale) {
                planetActive = false;
                currentPlanetTexture = null;
                planetTimer = 0f;
                planetSpawnDelay = 5f + random.nextFloat() * 10f;
            }
        }
    }
    
    private void spawnNewPlanet() {
        planetActive = true;
        planetTimer = 0f;
        
        int planetIndex = random.nextInt(planetTextures.length);
        currentPlanetTexture = planetTextures[planetIndex];
        
        renderBelowNebulose = random.nextBoolean();
        
        float baseSize = 100f + random.nextFloat() * 200f;
        planetScale = baseSize / Math.max(currentPlanetTexture.getWidth(), currentPlanetTexture.getHeight());
        
        planetX = random.nextFloat() * (1920f - baseSize);
        planetY = 1080f + baseSize;
        
        planetLifetime = 60f + random.nextFloat() * 120f;
        
        float totalDistance = 1080f + baseSize + baseSize;
        planetSpeed = totalDistance / planetLifetime;
        
        planetAge = 0f;
    }
    
    public void render(SpriteBatch batch) {
        renderSpaceBackground(batch);
        renderSpaceStars01(batch);
        renderPlanetBelowDust(batch);
        renderSpaceDust(batch);
        renderPlanetBelowNebulose(batch);
        renderNebulose(batch);
        renderSpaceStars03(batch);
    }
    
    private void renderSpaceBackground(SpriteBatch batch) {
        batch.draw(fondoSpaceBackground, 0, spaceBackgroundOffsetY, 1920, 1080);
        batch.draw(fondoSpaceBackground, 0, spaceBackgroundOffsetY + 1080, 1920, 1080);
    }
    
    private void renderSpaceStars01(SpriteBatch batch) {
        batch.setColor(1f, 1f, 1f, SPACE_STARS_01_OPACITY);
        batch.draw(fondoSpaceStars01, 0, spaceStars01OffsetY, 1920, 1080);
        batch.draw(fondoSpaceStars01, 0, spaceStars01OffsetY + 1080, 1920, 1080);
        batch.setColor(1f, 1f, 1f, 1f);
    }
    
    private void renderPlanetBelowDust(SpriteBatch batch) {
        if (planetActive && !renderBelowNebulose && currentPlanetTexture != null) {
            batch.setColor(1f, 1f, 1f, PLANET_OPACITY);
            float planetWidth = currentPlanetTexture.getWidth() * planetScale;
            float planetHeight = currentPlanetTexture.getHeight() * planetScale;
            batch.draw(currentPlanetTexture, planetX, planetY, planetWidth, planetHeight);
            batch.setColor(1f, 1f, 1f, 1f);
        }
    }
    
    private void renderSpaceDust(SpriteBatch batch) {
        batch.setColor(1f, 1f, 1f, spaceDustAlpha * SPACE_DUST_OPACITY);
        batch.draw(fondoSpaceDustVariations[currentSpaceDustIndex], 0, spaceDustOffsetY, 1920, 1080);
        batch.draw(fondoSpaceDustVariations[currentSpaceDustIndex], 0, spaceDustOffsetY + 1080, 1920, 1080);
        
        if (inSpaceDustTransition && nextSpaceDustAlpha > 0.0f) {
            batch.setColor(1f, 1f, 1f, nextSpaceDustAlpha * SPACE_DUST_OPACITY);
            batch.draw(fondoSpaceDustVariations[nextSpaceDustIndex], 0, spaceDustOffsetY, 1920, 1080);
            batch.draw(fondoSpaceDustVariations[nextSpaceDustIndex], 0, spaceDustOffsetY + 1080, 1920, 1080);
        }
        
        batch.setColor(1f, 1f, 1f, 1f);
    }
    
    private void renderPlanetBelowNebulose(SpriteBatch batch) {
        if (planetActive && renderBelowNebulose && currentPlanetTexture != null) {
            batch.setColor(1f, 1f, 1f, PLANET_OPACITY);
            float planetWidth = currentPlanetTexture.getWidth() * planetScale;
            float planetHeight = currentPlanetTexture.getHeight() * planetScale;
            batch.draw(currentPlanetTexture, planetX, planetY, planetWidth, planetHeight);
            batch.setColor(1f, 1f, 1f, 1f);
        }
    }
    
    private void renderNebulose(SpriteBatch batch) {
        batch.setColor(1f, 1f, 1f, nebuloseAlpha * NEBULOSE_OPACITY);
        batch.draw(fondoNebuloseVariations[currentNebuloseIndex], 0, nebuloseOffsetY, 1920, 1080);
        batch.draw(fondoNebuloseVariations[currentNebuloseIndex], 0, nebuloseOffsetY + 1080, 1920, 1080);
        
        if (inNebuloseTransition && nextNebuloseAlpha > 0.0f) {
            batch.setColor(1f, 1f, 1f, nextNebuloseAlpha * NEBULOSE_OPACITY);
            batch.draw(fondoNebuloseVariations[nextNebuloseIndex], 0, nebuloseOffsetY, 1920, 1080);
            batch.draw(fondoNebuloseVariations[nextNebuloseIndex], 0, nebuloseOffsetY + 1080, 1920, 1080);
        }
        
        batch.setColor(1f, 1f, 1f, 1f);
    }
    
    private void renderSpaceStars03(SpriteBatch batch) {
        batch.setColor(1f, 1f, 1f, spaceStars03Alpha);
        batch.draw(fondoSpaceStars03Variations[currentSpaceStars03Index], 0, spaceStars03OffsetY, 1920, 1080);
        batch.draw(fondoSpaceStars03Variations[currentSpaceStars03Index], 0, spaceStars03OffsetY + 1080, 1920, 1080);
        
        if (inSpaceStars03Transition && nextSpaceStars03Alpha > 0.0f) {
            batch.setColor(1f, 1f, 1f, nextSpaceStars03Alpha);
            batch.draw(fondoSpaceStars03Variations[nextSpaceStars03Index], 0, spaceStars03OffsetY, 1920, 1080);
            batch.draw(fondoSpaceStars03Variations[nextSpaceStars03Index], 0, spaceStars03OffsetY + 1080, 1920, 1080);
        }
        
        batch.setColor(1f, 1f, 1f, 1f);
    }
    
    public void dispose() {
        if (fondoSpaceBackground != null) fondoSpaceBackground.dispose();
        if (fondoSpaceStars01 != null) fondoSpaceStars01.dispose();
        
        if (fondoSpaceDustVariations != null) {
            for (Texture dustTexture : fondoSpaceDustVariations) {
                if (dustTexture != null) dustTexture.dispose();
            }
        }
        
        if (fondoNebuloseVariations != null) {
            for (Texture nebuloseTexture : fondoNebuloseVariations) {
                if (nebuloseTexture != null) nebuloseTexture.dispose();
            }
        }
        
        if (fondoSpaceStars03Variations != null) {
            for (Texture starsTexture : fondoSpaceStars03Variations) {
                if (starsTexture != null) starsTexture.dispose();
            }
        }
        
        if (planetTextures != null) {
            for (Texture planetTexture : planetTextures) {
                if (planetTexture != null) planetTexture.dispose();
            }
        }
    }
}