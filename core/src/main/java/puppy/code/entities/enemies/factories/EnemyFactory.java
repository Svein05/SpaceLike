package puppy.code.entities.enemies.factories;

import puppy.code.entities.enemies.Enemy;
import puppy.code.interfaces.EnemyWeapon;
import puppy.code.interfaces.MovementBehavior;

public abstract class EnemyFactory {
    
    public final Enemy createCompleteEnemy(float x, float y, float velocityX, float velocityY, int round) {
        Enemy enemy = createEnemy(x, y, velocityX, velocityY, round);
        EnemyWeapon weapon = createWeapon();
        MovementBehavior movement = createMovementBehavior(velocityX, velocityY);
        
        enemy.setWeapon(weapon);
        enemy.setMovementBehavior(movement);
        movement.initialize(enemy);
        
        return enemy;
    }
    
    protected abstract Enemy createEnemy(float x, float y, float velocityX, float velocityY, int round);
    protected abstract EnemyWeapon createWeapon();
    protected abstract MovementBehavior createMovementBehavior(float velocityX, float velocityY);
    
    public abstract String getFactoryName();
}
