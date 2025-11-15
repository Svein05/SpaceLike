package puppy.code.systems;

import puppy.code.entities.Nave;
import puppy.code.stats.ShipStats;
import puppy.code.stats.UpgradeType;

public class UpgradeManager {
    
    public static void applyUpgrade(Nave nave, UpgradeType upgradeType) {
        ShipStats stats = nave.getShipStats();
        
        switch (upgradeType) {
            case HEALTH:
                stats.addHealthUpgrade(upgradeType.getValue());
                nave.getHealthSystem().refreshFromStats();
                float healAmount = upgradeType.getValue() * ShipStats.getBaseMaxHealth();
                nave.getHealthSystem().setVidas(
                    nave.getHealthSystem().getVidas() + (int)Math.ceil(healAmount / 2.0f)
                );
                break;
                
            case DEFENSE:
                stats.addDefenseUpgrade(upgradeType.getValue());
                break;
                
            case DAMAGE:
                stats.addDamageUpgrade(upgradeType.getValue());
                break;
                
            case FIRE_RATE:
                stats.addFireRateUpgrade(upgradeType.getValue());
                break;
        }
    }
}
