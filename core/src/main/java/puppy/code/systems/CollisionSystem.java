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
                    if (projectile.hasHitEnemy(enemy)) {
                        continue;
                    }
                    
                    int baseDamage = projectile.getDamage();
                    int finalDamage = baseDamage;
                    
                    if (nave != null && nave.getShipStats() != null) {
                        float calculatedDamage = nave.getShipStats().calculateDamage(baseDamage);
                        finalDamage = Math.round(calculatedDamage);
                    }
                    
                    enemy.takeDamage(finalDamage);
                    projectile.addHitEnemy(enemy);

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
                    
                    if (projectile.getRemainingBounces() > 0) {
                        Enemy nextTarget = findNearestEnemy(projectile, enemies);
                        if (nextTarget != null) {
                            float speed = (float) Math.sqrt(
                                projectile.velocityX * projectile.velocityX + 
                                projectile.velocityY * projectile.velocityY
                            );
                            float targetCenterX = nextTarget.getX() + nextTarget.getWidth() / 2;
                            float targetCenterY = nextTarget.getY() + nextTarget.getHeight() / 2;
                            
                            projectile.redirectTo(targetCenterX, targetCenterY, speed);
                            projectile.consumeBounce();
                            
                            if (projectile instanceof puppy.code.entities.projectiles.Bullet) {
                                ((puppy.code.entities.projectiles.Bullet) projectile).setTargetEnemy(nextTarget);
                            }
                            break;
                        }
                    }
                    
                    projectile.destroy();
                    projectileManager.removeProjectile(projectile);
                    break;
                }
            }
        }
    }
    
    private Enemy findNearestEnemy(Projectile projectile, ArrayList<Enemy> enemies) {
        Enemy nearest = null;
        float minDistance = Float.MAX_VALUE;
        
        float projCenterX = projectile.getX() + projectile.getWidth() / 2;
        float projCenterY = projectile.getY() + projectile.getHeight() / 2;
        
        for (Enemy enemy : enemies) {
            if (projectile.hasHitEnemy(enemy)) continue;
            if (enemy.isDestroyed()) continue;
            
            if (enemy instanceof puppy.code.entities.enemies.MeteoriteEnemy) {
                puppy.code.entities.enemies.MeteoriteEnemy asteroid = (puppy.code.entities.enemies.MeteoriteEnemy) enemy;
                if (asteroid.isExploding()) continue;
            }
            
            float enemyCenterX = enemy.getX() + enemy.getWidth() / 2;
            float enemyCenterY = enemy.getY() + enemy.getHeight() / 2;
            
            float dx = enemyCenterX - projCenterX;
            float dy = enemyCenterY - projCenterY;
            float distance = dx * dx + dy * dy;
            
            if (distance < minDistance) {
                minDistance = distance;
                nearest = enemy;
            }
        }
        
        return nearest;
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
            
            boolean isEnemyProjectile = (projectile instanceof puppy.code.entities.projectiles.EnemyBall) || 
                                        (projectile instanceof puppy.code.entities.projectiles.SniperProjectile);
            if (!isEnemyProjectile) continue;
            
            if (projectile.getBounds().overlaps(nave.getBounds())) {
                System.out.println("[COLLISION] Enemy projectile (" + projectile.getClass().getSimpleName() + ") hit player");
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