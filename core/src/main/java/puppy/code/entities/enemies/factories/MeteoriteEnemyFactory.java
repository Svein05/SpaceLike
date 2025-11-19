package puppy.code.entities.enemies.factories;

import puppy.code.entities.enemies.Enemy;
import puppy.code.entities.enemies.MeteoriteEnemy;
import puppy.code.entities.enemies.behaviors.StraightMovement;
import puppy.code.entities.enemies.weapons.NoWeapon;
import puppy.code.interfaces.EnemyWeapon;
import puppy.code.interfaces.MovementBehavior;

public class MeteoriteEnemyFactory extends EnemyFactory {
    
    @Override
    protected Enemy createEnemy(float x, float y, float velocityX, float velocityY, int round) {
        return new MeteoriteEnemy((int)x, (int)y, 20, (int)velocityX, (int)velocityY, round);
    }

    @Override
    protected EnemyWeapon createWeapon() {
        return new NoWeapon();
    }

    @Override
    protected MovementBehavior createMovementBehavior(float velocityX, float velocityY) {
        return new StraightMovement(velocityX, velocityY);
    }

    @Override
    public String getFactoryName() {
        return "MeteoriteFactory";
    }
}
