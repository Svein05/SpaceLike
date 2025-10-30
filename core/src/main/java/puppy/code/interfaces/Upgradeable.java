package puppy.code.interfaces;

public interface Upgradeable {
    void upgrade(String upgradeType, int level);
    boolean canUpgrade(String upgradeType);
    int getUpgradeLevel(String upgradeType);
    int getUpgradeCost(String upgradeType);
}