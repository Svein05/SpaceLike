package puppy.code.systems;

public class LevelSystem {
    private XPSystem xpSystem;
    private boolean shouldShowLevelUpScreen;
    
    public LevelSystem(XPSystem xpSystem) {
        this.xpSystem = xpSystem;
        this.shouldShowLevelUpScreen = false;
    }
    
    public void update() {
        if (xpSystem.hasLeveledUp() && !shouldShowLevelUpScreen) {
            shouldShowLevelUpScreen = true;
        }
    }
    
    public boolean shouldShowLevelUpScreen() {
        return shouldShowLevelUpScreen;
    }
    
    public void levelUpScreenShown() {
        shouldShowLevelUpScreen = false;
    }
    
    public int getCurrentLevel() {
        return xpSystem.getCurrentLevel();
    }
}
