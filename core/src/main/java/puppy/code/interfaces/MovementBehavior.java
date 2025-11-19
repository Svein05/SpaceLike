package puppy.code.interfaces;

import puppy.code.entities.enemies.Enemy;

public interface MovementBehavior {
    void move(Enemy enemy, float delta);
    void initialize(Enemy enemy);
}
