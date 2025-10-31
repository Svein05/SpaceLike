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
    
    public CollisionSystem(XPSystem xpSystem) {
        this.gameState = GameStateManager.getInstance();
        this.xpSystem = xpSystem;
        this.resourceManager = ResourceManager.getInstance();
    }
    
    // Usar el método de colisión existente de Bullet pero con Projectiles
    public void checkBulletCollisions(ArrayList<Projectile> projectiles, ArrayList<MeteoriteEnemy> enemies1, ArrayList<MeteoriteEnemy> enemies2, ProjectileManager projectileManager) {
        // Usar índices en lugar de iteradores para evitar ConcurrentModificationException
        for (int i = projectiles.size() - 1; i >= 0; i--) {
            Projectile projectile = projectiles.get(i);
            if (projectile.isDestroyed()) continue;
            
            for (int j = enemies1.size() - 1; j >= 0; j--) {
                MeteoriteEnemy enemy = enemies1.get(j);
                
                // Si el asteroide esta explotando, no procesar colision
                if (enemy.isExploding()) {
                    continue;
                }
                
                // Verificar colisión usando bounds
                if (projectile.getBounds().overlaps(enemy.getBounds())) {
                    // Dañar enemigo usando el damage del proyectil
                    enemy.takeDamage(projectile.getDamage());
                    
                    // Solo remover y dar puntos si el enemigo fue destruido
                    if (enemy.isDestroyed()) {
                        // Dar puntos y XP
                        gameState.addScore(enemy.getScoreValue());
                        xpSystem.addXP(enemy.getXPValue());
                        
                        // Remover enemigo de ambas listas (balls1 y balls2)
                        enemies1.remove(j);
                        enemies2.remove(j);
                    }
                    
                    // Marcar proyectil como destruido
                    projectile.destroy();
                    projectileManager.removeProjectile(projectile);
                    break;
                }
            }
        }
    }
    
    // Usar el método de colisión existente de Nave4
    public void checkShipCollisions(Nave nave, ArrayList<MeteoriteEnemy> enemies) {
        for (MeteoriteEnemy enemy : enemies) {
            if (nave.checkCollision(enemy)) {
                break; // Solo una colisión por frame
            }
        }
    }
    
    // Método optimizado para trabajar con EnemyManager
    public void checkProjectileEnemyCollisions(ArrayList<Projectile> projectiles, ArrayList<Enemy> enemies, ProjectileManager projectileManager) {
        for (int i = projectiles.size() - 1; i >= 0; i--) {
            Projectile projectile = projectiles.get(i);
            if (projectile.isDestroyed()) continue;
            
            for (int j = enemies.size() - 1; j >= 0; j--) {
                Enemy enemy = enemies.get(j);
                if (enemy.isDestroyed()) continue;
                
                // Si es un asteroide y esta explotando, no procesar colision
                if (enemy instanceof puppy.code.entities.enemies.MeteoriteEnemy) {
                    puppy.code.entities.enemies.MeteoriteEnemy asteroid = (puppy.code.entities.enemies.MeteoriteEnemy) enemy;
                    if (asteroid.isExploding()) {
                        continue; // Saltar este asteroide
                    }
                }
                
                if (projectile.getBounds().overlaps(enemy.getBounds())) {
                    // Dañar enemigo usando el daño del proyectil
                    int damage = projectile.getDamage();
                    enemy.takeDamage(damage);

                    // Si el enemigo fue destruido, dar puntos y XP
                    if (enemy.isDestroyed()) {
                        
                        // No reproducir sonido aqui para asteroides, ellos se encargan
                        if (!(enemy instanceof puppy.code.entities.enemies.MeteoriteEnemy)) {
                            try {
                                resourceManager.getSound(enemy.getDestructionSound()).play();
                            } catch (Exception e) {
                            }
                        }
                        
                        // Los puntos y XP se otorgan inmediatamente aquí en CollisionSystem
                        int scoreAdded = enemy.getScoreValue();
                        gameState.addScore(scoreAdded);
                        
                        if (xpSystem != null) {
                            int xpAdded = enemy.getXPValue();
                            xpSystem.addXP(xpAdded);
                        }
                    }
                    
                    // Marcar proyectil como destruido
                    projectile.destroy();
                    projectileManager.removeProjectile(projectile);
                    break;
                }
            }
        }
    }
    
    // Método para colisiones nave-enemigos genérico
    public void checkShipEnemyCollisions(Nave nave, ArrayList<Enemy> enemies) {
        for (Enemy enemy : enemies) {
            if (enemy.isDestroyed()) continue;
            
            // Cast temporal para usar el método existente de checkCollision
            if (enemy instanceof MeteoriteEnemy) {
                if (nave.checkCollision((MeteoriteEnemy) enemy)) {
                    break; // Solo una colisión por frame
                }
            }
        }
    }
}