package puppy.code.entities.enemies.behaviors;

import puppy.code.entities.enemies.Enemy;
import puppy.code.interfaces.MovementBehavior;

public class SimpleDownwardMovement implements MovementBehavior {
    private float velocityX;
    private float velocityY;
    
    public SimpleDownwardMovement(float velocityX, float velocityY) {
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }
    
    @Override
    public void initialize(Enemy enemy) {
    }
    
    @Override
    public void move(Enemy enemy, float delta) {
        enemy.setY(enemy.getY() + velocityY * delta * 60);
        enemy.setX(enemy.getX() + velocityX * delta * 60);
    }
}
