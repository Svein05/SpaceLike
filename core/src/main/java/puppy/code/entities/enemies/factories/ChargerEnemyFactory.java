package puppy.code.entities.enemies.factories;

import com.badlogic.gdx.graphics.Texture;
import puppy.code.entities.Nave;
import puppy.code.entities.enemies.ChargerEnemy;
import puppy.code.entities.enemies.Enemy;
import puppy.code.entities.enemies.behaviors.SimpleDownwardMovement;
import puppy.code.entities.enemies.weapons.ChargerWeapon;
import puppy.code.interfaces.MovementBehavior;
import puppy.code.interfaces.EnemyWeapon;
import puppy.code.managers.ResourceManager;

public class ChargerEnemyFactory extends EnemyFactory {
    
    private Nave playerShip;
    
    public ChargerEnemyFactory(Nave playerShip) {
        this.playerShip = playerShip;
    }
    
    @Override
    protected Enemy createEnemy(float x, float y, float velocityX, float velocityY, int round) {
        Texture texture = ResourceManager.getInstance().getTexture("Game/Enemys/EnemyShips/Charger.png");
        int health = 15 + (round * 2);
        
        return new ChargerEnemy(x, y, texture, health, Math.abs(velocityY), playerShip);
    }

    @Override
    protected EnemyWeapon createWeapon() {
        return new ChargerWeapon(0.5f, 7f);
    }

    @Override
    protected MovementBehavior createMovementBehavior(float velocityX, float velocityY) {
        return new SimpleDownwardMovement(0, velocityY * 0.5f);
    }

    @Override
    public String getFactoryName() {
        return "ChargerEnemyFactory";
    }
}
