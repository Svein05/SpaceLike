package puppy.code.systems;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public class XPSystem {
    private int currentXP;
    private int currentLevel;
    private int xpToNextLevel;
    private boolean leveledUp;
    private boolean levelUpConsumed;
    private float levelUpMessageTimer;
    private static final float LEVEL_UP_MESSAGE_DURATION = 2.0f;
    private GlyphLayout layout;
    
    public XPSystem() {
        this.currentXP = 0;
        this.currentLevel = 1;
        this.xpToNextLevel = 100;
        this.leveledUp = false;
        this.levelUpConsumed = true;
        this.levelUpMessageTimer = 0;
        this.layout = new GlyphLayout();
    }
    
    public void addXP(int xp) {
        currentXP += xp;
        checkLevelUp();
    }
    
    private void checkLevelUp() {
        while (currentXP >= xpToNextLevel) {
            currentLevel++;
            currentXP -= xpToNextLevel;
            xpToNextLevel = (int)(xpToNextLevel * 1.5f);
            leveledUp = true;
            levelUpConsumed = false;
            levelUpMessageTimer = LEVEL_UP_MESSAGE_DURATION;
        }
    }
    
    public void update(float delta) {
        if (leveledUp) {
            levelUpMessageTimer -= delta;
            if (levelUpMessageTimer <= 0) {
                leveledUp = false;
            }
        }
    }
    
    public void render(SpriteBatch batch, BitmapFont font, float screenWidth) {
        String levelText = "Nivel: " + currentLevel;
        String xpText = "XP: " + currentXP + " / " + xpToNextLevel;
        
        font.getData().setScale(1.5f);
        font.setColor(1, 1, 1, 1);
        
        layout.setText(font, levelText);
        float levelX = (screenWidth - layout.width) / 2;
        
        layout.setText(font, xpText);
        float xpX = (screenWidth - layout.width) / 2;

        font.draw(batch, levelText, levelX, 1050);
        font.draw(batch, xpText, xpX, 1020);
        
        if (leveledUp) {
            String levelUpText = "¡Has subido de nivel!";
            font.getData().setScale(2.0f);
            font.setColor(1, 1, 0, 1);
            layout.setText(font, levelUpText);
            float levelUpX = (screenWidth - layout.width) / 2;
            font.draw(batch, levelUpText, levelUpX, 600);
            font.setColor(1, 1, 1, 1);
        }
    }
    
    public int getCurrentLevel() {
        return currentLevel;
    }
    
    public int getCurrentXP() {
        return currentXP;
    }
    
    public int getXPToNextLevel() {
        return xpToNextLevel;
    }
    
    public boolean hasLeveledUp() {
        return !levelUpConsumed && leveledUp;
    }
    
    public void consumeLevelUp() {
        levelUpConsumed = true;
    }
}