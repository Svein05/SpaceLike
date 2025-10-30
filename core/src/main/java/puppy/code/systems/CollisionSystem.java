package puppy.code.systems;

import java.util.ArrayList;

import puppy.code.entities.Nave4;
import puppy.code.entities.enemies.Ball2;
import puppy.code.entities.projectiles.Projectile;
import puppy.code.managers.GameStateManager;
import puppy.code.managers.ProjectileManager;

public class CollisionSystem {
    private GameStateManager gameState;
    private XPSystem xpSystem;
    
    public CollisionSystem(XPSystem xpSystem) {
        this.gameState = GameStateManager.getInstance();
        this.xpSystem = xpSystem;
    }
    
    // Usar el método de colisión existente de Bullet pero con Projectiles
    public void checkBulletCollisions(ArrayList<Projectile> projectiles, ArrayList<Ball2> enemies1, ArrayList<Ball2> enemies2, ProjectileManager projectileManager) {
        // Usar índices en lugar de iteradores para evitar ConcurrentModificationException
        for (int i = projectiles.size() - 1; i >= 0; i--) {
            Projectile projectile = projectiles.get(i);
            if (projectile.isDestroyed()) continue;
            
            for (int j = enemies1.size() - 1; j >= 0; j--) {
                Ball2 enemy = enemies1.get(j);
                
                // Verificar colisión usando bounds
                if (projectile.getBounds().overlaps(enemy.getBounds())) {
                    System.out.println("¡Colisión! Enemigo destruido. Quedan: " + (enemies1.size() - 1));
                    
                    // Dar puntos y XP
                    gameState.addScore(enemy.getScoreValue());
                    xpSystem.addXP(enemy.getXPValue());
                    
                    // Remover enemigo de ambas listas (balls1 y balls2)
                    enemies1.remove(j);
                    enemies2.remove(j);
                    
                    // Marcar proyectil como destruido
                    projectile.destroy();
                    projectileManager.removeProjectile(projectile);
                    break;
                }
            }
        }
    }
    
    // Usar el método de colisión existente de Nave4
    public void checkShipCollisions(Nave4 nave, ArrayList<Ball2> enemies) {
        for (Ball2 enemy : enemies) {
            if (nave.checkCollision(enemy)) {
                break; // Solo una colisión por frame
            }
        }
    }
}