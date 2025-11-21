package puppy.code.entities.enemies.behaviors;

import puppy.code.entities.enemies.Enemy;
import puppy.code.interfaces.MovementBehavior;

// Patron: Strategy (Concrete Strategy)
public class StraightMovement implements MovementBehavior {
    private float velocityX;
    private float velocityY;

    public StraightMovement(float velocityX, float velocityY) {
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    @Override
    public void initialize(Enemy enemy) {
    }

    @Override
    public void move(Enemy enemy, float delta) {
        enemy.setX(enemy.getX() + velocityX * delta * 60);
        enemy.setY(enemy.getY() + velocityY * delta * 60);

        if (enemy.getX() < 0 || enemy.getX() + enemy.getWidth() > 1920) {
            velocityX *= -1;
            if (enemy.getX() < 0) enemy.setX(0);
            if (enemy.getX() + enemy.getWidth() > 1920) {
                enemy.setX(1920 - enemy.getWidth());
            }
        }
    }

    public float getVelocityX() { return velocityX; }
    public float getVelocityY() { return velocityY; }
    public void setVelocityX(float vx) { this.velocityX = vx; }
    public void setVelocityY(float vy) { this.velocityY = vy; }
}
