package puppy.code.entities.enemies.factories;

import com.badlogic.gdx.graphics.Texture;
import puppy.code.entities.enemies.Enemy;
import puppy.code.entities.enemies.ShooterEnemy;
import puppy.code.entities.enemies.behaviors.ZigZagMovement;
import puppy.code.entities.enemies.weapons.BasicEnemyLaser;
import puppy.code.interfaces.EnemyWeapon;
import puppy.code.interfaces.MovementBehavior;
import puppy.code.managers.ResourceManager;

public class ShooterEnemyFactory extends EnemyFactory {
    
    @Override
    protected Enemy createEnemy(float x, float y, float velocityX, float velocityY, int round) {
        Texture texture = ResourceManager.getInstance().getTexture("special.png");
        int health = 15 + (round * 5);
        return new ShooterEnemy(x, y, texture, health, Math.abs(velocityY));
    }

    @Override
    protected EnemyWeapon createWeapon() {
        return new BasicEnemyLaser(2.0f, 8f);
    }

    @Override
    protected MovementBehavior createMovementBehavior(float velocityX, float velocityY) {
        return new ZigZagMovement(0, velocityY, 100f, 3f);
    }

    @Override
    public String getFactoryName() {
        return "ShooterFactory";
    }
}
