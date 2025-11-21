package puppy.code.entities.enemies.factories;

import com.badlogic.gdx.graphics.Texture;
import puppy.code.entities.enemies.Enemy;
import puppy.code.entities.enemies.SniperEnemy;
import puppy.code.entities.enemies.behaviors.SniperMovement;
import puppy.code.entities.enemies.weapons.SniperWeapon;
import puppy.code.entities.Nave;
import puppy.code.interfaces.EnemyWeapon;
import puppy.code.interfaces.MovementBehavior;
import puppy.code.managers.ResourceManager;

// Patron: Concrete Factory (Abstract Factory)
public class SniperEnemyFactory extends EnemyFactory {
    
    @Override
    protected Enemy createEnemy(float x, float y, float velocityX, float velocityY, int round, Nave playerShip) {
        if (playerShip == null) {
            throw new IllegalStateException("PlayerShip no configurado en SniperEnemyFactory");
        }
        
        Texture texture = ResourceManager.getInstance().getTexture("Game/Enemys/EnemyShips/Sniper.png");
        int health = 10 + (round * 3);
        return new SniperEnemy(x, y, texture, health, Math.abs(velocityY), playerShip);
    }

    @Override
    protected EnemyWeapon createWeapon() {
        return new SniperWeapon(0.1f);
    }

    @Override
    protected MovementBehavior createMovementBehavior(float velocityX, float velocityY) {
        return new SniperMovement(0, velocityY * 0.3f, 120f, 4f);
    }

    @Override
    public String getFactoryName() {
        return "SniperEnemyFactory";
    }
}
