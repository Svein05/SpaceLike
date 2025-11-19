package puppy.code.entities.enemies.factories;

import com.badlogic.gdx.graphics.Texture;
import puppy.code.entities.enemies.Enemy;
import puppy.code.entities.enemies.ShooterEnemy;
import puppy.code.entities.enemies.behaviors.SinusoidalMovement;
import puppy.code.entities.enemies.weapons.BasicEnemyLaser;
import puppy.code.interfaces.EnemyWeapon;
import puppy.code.interfaces.MovementBehavior;
import puppy.code.managers.ResourceManager;

public class FastEnemyFactory extends EnemyFactory {
    
    @Override
    protected Enemy createEnemy(float x, float y, float velocityX, float velocityY, int round) {
        Texture texture = ResourceManager.getInstance().getTexture("special.png");
        int health = 8 + (round * 3);
        return new ShooterEnemy(x, y, texture, health, Math.abs(velocityY) * 1.5f);
    }

    @Override
    protected EnemyWeapon createWeapon() {
        return new BasicEnemyLaser(1.5f, 10f);
    }

    @Override
    protected MovementBehavior createMovementBehavior(float velocityX, float velocityY) {
        return new SinusoidalMovement(velocityY * 1.5f, 150f, 5f);
    }

    @Override
    public String getFactoryName() {
        return "FastEnemyFactory";
    }
}
