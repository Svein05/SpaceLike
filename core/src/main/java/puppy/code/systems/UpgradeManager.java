package puppy.code.systems;

import puppy.code.entities.Nave;
import puppy.code.stats.ShipStats;
import puppy.code.stats.UpgradeType;

public class UpgradeManager {
    
    public static void applyUpgrade(Nave nave, UpgradeType upgradeType) {
        ShipStats stats = nave.getShipStats();
        
        switch (upgradeType) {
            case HEALTH:
                float currentHearts = nave.getHealthSystem().getCurrentHealth();
                stats.addHealthUpgrade(1.0f);
                nave.getHealthSystem().refreshFromStats();
                nave.getHealthSystem().setCurrentHealth(currentHearts + 1.0f);
                break;
                
            case DAMAGE:
                stats.addDamageUpgrade(upgradeType.getValue());
                break;
                
            case FIRE_RATE:
                stats.addFireRateUpgrade(upgradeType.getValue());
                break;
                
            case HOMING:
                stats.addHomingUpgrade(upgradeType.getValue());
                break;
                
            case SPINNER_UNLOCK:
                stats.unlockSpinner();
                if (nave.getSpinnerSystem() != null) {
                    nave.getSpinnerSystem().setSpinnerCount(1);
                    nave.getSpinnerSystem().setDamageMultiplier(stats.getSpinnerDamageMultiplier());
                }
                break;
                
            case SPINNER_COUNT:
                stats.addSpinner();
                if (nave.getSpinnerSystem() != null) {
                    nave.getSpinnerSystem().setSpinnerCount(stats.getSpinnerCount());
                    nave.getSpinnerSystem().setDamageMultiplier(stats.getSpinnerDamageMultiplier());
                }
                break;
                
            case SPINNER_DAMAGE:
                stats.addSpinnerDamageUpgrade(upgradeType.getValue());
                if (nave.getSpinnerSystem() != null) {
                    nave.getSpinnerSystem().setDamageMultiplier(stats.getSpinnerDamageMultiplier());
                }
                break;
                
            case BOUNCING_BULLETS:
                stats.addBouncingBulletsLevel();
                break;
        }
    }
}
