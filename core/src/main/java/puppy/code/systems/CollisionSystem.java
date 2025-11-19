package puppy.code.systems;

import java.util.ArrayList;

import puppy.code.entities.Nave;
import puppy.code.entities.enemies.MeteoriteEnemy;
import puppy.code.entities.enemies.Enemy;
import puppy.code.entities.projectiles.Projectile;
import puppy.code.managers.GameStateManager;
import puppy.code.managers.ProjectileManager;
import puppy.code.managers.ResourceManager;

public class CollisionSystem {
    private GameStateManager gameState;
    private XPSystem xpSystem;
    private ResourceManager resourceManager;
    private Nave nave;
    
    public CollisionSystem(XPSystem xpSystem) {
        this.gameState = GameStateManager.getInstance();
        this.xpSystem = xpSystem;
        this.resourceManager = ResourceManager.getInstance();
    }
    
    public void setNave(Nave nave) {
        this.nave = nave;
    }
    
    public void checkBulletCollisions(ArrayList<Projectile> projectiles, ArrayList<MeteoriteEnemy> enemies1, ArrayList<MeteoriteEnemy> enemies2, ProjectileManager projectileManager) {
        for (int i = projectiles.size() - 1; i >= 0; i--) {
            Projectile projectile = projectiles.get(i);
            if (projectile.isDestroyed()) continue;
            
            for (int j = enemies1.size() - 1; j >= 0; j--) {
                MeteoriteEnemy enemy = enemies1.get(j);
                
                if (enemy.isExploding()) {
                    continue;
                }
                
                if (projectile.getBounds().overlaps(enemy.getBounds())) {
                    enemy.takeDamage(projectile.getDamage());
                    
                    if (enemy.isDestroyed()) {
                        gameState.addScore(enemy.getScoreValue());
                        xpSystem.addXP(enemy.getXPValue());
                        
                        enemies1.remove(j);
                        enemies2.remove(j);
                    }

                    projectile.destroy();
                    projectileManager.removeProjectile(projectile);
                    break;
                }
            }
        }
    }
    
    public void checkShipCollisions(Nave nave, ArrayList<MeteoriteEnemy> enemies) {
        for (MeteoriteEnemy enemy : enemies) {
            if (nave.checkCollision(enemy)) {
                break;
            }
        }
    }
    
    public void checkProjectileEnemyCollisions(ArrayList<Projectile> projectiles, ArrayList<Enemy> enemies, ProjectileManager projectileManager) {
        for (int i = projectiles.size() - 1; i >= 0; i--) {
            Projectile projectile = projectiles.get(i);
            if (projectile.isDestroyed()) continue;
            
            if (projectile instanceof puppy.code.entities.projectiles.EnemyBall) continue;
            
            for (int j = enemies.size() - 1; j >= 0; j--) {
                Enemy enemy = enemies.get(j);
                if (enemy.isDestroyed()) continue;
                
                if (enemy instanceof puppy.code.entities.enemies.MeteoriteEnemy) {
                    puppy.code.entities.enemies.MeteoriteEnemy asteroid = (puppy.code.entities.enemies.MeteoriteEnemy) enemy;
                    if (asteroid.isExploding()) {
                        continue;
                    }
                }
                
                if (projectile.getBounds().overlaps(enemy.getBounds())) {
                    int baseDamage = projectile.getDamage();
                    int finalDamage = baseDamage;
                    
                    if (nave != null && nave.getShipStats() != null) {
                        float calculatedDamage = nave.getShipStats().calculateDamage(baseDamage);
                        finalDamage = Math.round(calculatedDamage);
                    }
                    
                    int healthBefore = enemy.getHealth();
                    enemy.takeDamage(finalDamage);
                    int healthAfter = enemy.getHealth();
                    
                    System.out.println("=== DAMAGE LOG ===");
                    System.out.println("Enemy Type: " + enemy.getClass().getSimpleName());
                    System.out.println("Health Before: " + healthBefore);
                    System.out.println("Base Damage: " + baseDamage);
                    System.out.println("Final Damage (with multiplier): " + finalDamage);
                    System.out.println("Health After: " + healthAfter);
                    if (nave != null && nave.getShipStats() != null) {
                        System.out.println("Damage Multiplier: " + nave.getShipStats().getDamageMultiplier());
                        System.out.println("Calculated Damage (float): " + nave.getShipStats().calculateDamage(baseDamage));
                    }
                    System.out.println("==================");

                    if (enemy.isDestroyed()) {
                        if (!(enemy instanceof puppy.code.entities.enemies.MeteoriteEnemy)) {
                            try {
                                resourceManager.getSound(enemy.getDestructionSound()).play();
                            } catch (Exception e) {
                            }
                        }
    
                        int scoreAdded = enemy.getScoreValue();
                        gameState.addScore(scoreAdded);
                        
                        if (xpSystem != null) {
                            int xpAdded = enemy.getXPValue();
                            xpSystem.addXP(xpAdded);
                        }
                    }
                    
                    projectile.destroy();
                    projectileManager.removeProjectile(projectile);
                    break;
                }
            }
        }
    }
    
    public void checkShipEnemyCollisions(Nave nave, ArrayList<Enemy> enemies) {
        for (Enemy enemy : enemies) {
            if (enemy.isDestroyed()) continue;
            
            if (nave.checkCollision(enemy)) {
                break;
            }
        }
    }
    
    public void checkEnemyProjectileShipCollisions(ArrayList<Projectile> projectiles, Nave nave, ProjectileManager projectileManager) {
        if (nave.estaHerido()) return;
        
        for (int i = projectiles.size() - 1; i >= 0; i--) {
            Projectile projectile = projectiles.get(i);
            if (projectile.isDestroyed()) continue;
            
            if (!(projectile instanceof puppy.code.entities.projectiles.EnemyBall)) continue;
            
            if (projectile.getBounds().overlaps(nave.getBounds())) {
                nave.takeDamage();
                
                projectile.destroy();
                projectileManager.removeProjectile(projectile);
                break;
            }
        }
    }
    
    public void checkSpinnerEnemyCollisions(puppy.code.upgrades.SpinnerSystem spinnerSystem, ArrayList<Enemy> enemies) {
        if (spinnerSystem == null) return;
        
        ArrayList<puppy.code.upgrades.SpinnerSystem.SpinnerBall> spinners = spinnerSystem.getSpinners();
        int spinnerDamage = spinnerSystem.getDamage();
        
        for (puppy.code.upgrades.SpinnerSystem.SpinnerBall spinner : spinners) {
            for (int j = enemies.size() - 1; j >= 0; j--) {
                Enemy enemy = enemies.get(j);
                if (enemy.isDestroyed()) continue;
                
                if (enemy instanceof puppy.code.entities.enemies.MeteoriteEnemy) {
                    puppy.code.entities.enemies.MeteoriteEnemy asteroid = (puppy.code.entities.enemies.MeteoriteEnemy) enemy;
                    if (asteroid.isExploding()) continue;
                }
                
                if (spinner.getBounds().overlaps(enemy.getBounds())) {
                    enemy.takeDamage(spinnerDamage);
                    
                    if (enemy.isDestroyed()) {
                        if (!(enemy instanceof puppy.code.entities.enemies.MeteoriteEnemy)) {
                            try {
                                resourceManager.getSound(enemy.getDestructionSound()).play();
                            } catch (Exception e) {}
                        }
                        
                        gameState.addScore(enemy.getScoreValue());
                        if (xpSystem != null) {
                            xpSystem.addXP(enemy.getXPValue());
                        }
                    }
                    break;
                }
            }
        }
    }
    
    public void checkSpinnerProjectileCollisions(puppy.code.upgrades.SpinnerSystem spinnerSystem, ArrayList<Projectile> projectiles, ProjectileManager projectileManager) {
        if (spinnerSystem == null) return;
        
        ArrayList<puppy.code.upgrades.SpinnerSystem.SpinnerBall> spinners = spinnerSystem.getSpinners();
        
        for (puppy.code.upgrades.SpinnerSystem.SpinnerBall spinner : spinners) {
            for (int i = projectiles.size() - 1; i >= 0; i--) {
                Projectile projectile = projectiles.get(i);
                if (projectile.isDestroyed()) continue;
                
                if (!(projectile instanceof puppy.code.entities.projectiles.EnemyBall)) continue;
                
                if (spinner.getBounds().overlaps(projectile.getBounds())) {
                    projectile.destroy();
                    projectileManager.removeProjectile(projectile);
                    break;
                }
            }
        }
    }
}