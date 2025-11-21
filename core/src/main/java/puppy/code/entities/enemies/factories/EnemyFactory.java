package puppy.code.entities.enemies.factories;

import puppy.code.entities.Nave;
import puppy.code.entities.enemies.Enemy;
import puppy.code.interfaces.EnemyWeapon;
import puppy.code.interfaces.MovementBehavior;

// Patron: Abstract Factory + Template Method
public abstract class EnemyFactory {
    
    public final Enemy createCompleteEnemy(float x, float y, float velocityX, float velocityY, int round, Nave playerShip) {
        preCreate(x, y, round);
        
        Enemy enemy = createEnemy(x, y, velocityX, velocityY, round, playerShip);
        if (enemy == null) {
            throw new IllegalStateException("createEnemy retorno null en " + getFactoryName());
        }
        
        EnemyWeapon weapon = createWeapon();
        MovementBehavior movement = createMovementBehavior(velocityX, velocityY);
        
        if (weapon != null) {
            enemy.setWeapon(weapon);
        }
        
        if (movement != null) {
            enemy.setMovementBehavior(movement);
            movement.initialize(enemy);
        }
        
        postCreate(enemy);
        
        return enemy;
    }
    
    protected void preCreate(float x, float y, int round) {
        // Hook opcional: ejecutado antes de crear el enemigo
    }
    
    protected void postCreate(Enemy enemy) {
        // Hook opcional: ejecutado despues de configurar el enemigo
    }
    
    protected abstract Enemy createEnemy(float x, float y, float velocityX, float velocityY, int round, Nave playerShip);
    protected abstract EnemyWeapon createWeapon();
    protected abstract MovementBehavior createMovementBehavior(float velocityX, float velocityY);
    
    public abstract String getFactoryName();
}
