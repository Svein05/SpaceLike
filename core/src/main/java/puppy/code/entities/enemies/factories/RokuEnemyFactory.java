package puppy.code.entities.enemies.factories;

import com.badlogic.gdx.graphics.Texture;
import puppy.code.entities.enemies.Enemy;
import puppy.code.entities.enemies.RokuEnemy;
import puppy.code.entities.enemies.behaviors.ZigZagMovement;
import puppy.code.entities.enemies.weapons.EnemyBallWeapon;
import puppy.code.interfaces.EnemyWeapon;
import puppy.code.interfaces.MovementBehavior;
import puppy.code.managers.ResourceManager;

public class RokuEnemyFactory extends EnemyFactory {
    
    @Override
    protected Enemy createEnemy(float x, float y, float velocityX, float velocityY, int round) {
        Texture texture = ResourceManager.getInstance().getTexture("Game/Enemys/EnemyShips/Roku.png");
        int health = 10 + (round * 3);
        return new RokuEnemy(x, y, texture, health, Math.abs(velocityY));
    }

    @Override
    protected EnemyWeapon createWeapon() {
        return new EnemyBallWeapon(1.5f, 6f);
    }

    @Override
    protected MovementBehavior createMovementBehavior(float velocityX, float velocityY) {
        return new ZigZagMovement(0, velocityY * 0.3f, 120f, 4f);
    }

    @Override
    public String getFactoryName() {
        return "RokuEnemyFactory";
    }
}
