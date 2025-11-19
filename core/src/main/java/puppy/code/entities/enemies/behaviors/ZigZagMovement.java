package puppy.code.entities.enemies.behaviors;

import puppy.code.entities.enemies.Enemy;
import puppy.code.interfaces.MovementBehavior;

public class ZigZagMovement implements MovementBehavior {
    private float baseVelocityY;
    private float zigzagFrequency;
    private float time;
    private static final float HORIZONTAL_SPEED = 200f;

    public ZigZagMovement(float velocityX, float velocityY, float amplitude, float frequency) {
        this.baseVelocityY = velocityY;
        this.zigzagFrequency = frequency;
        this.time = 0f;
    }

    @Override
    public void initialize(Enemy enemy) {
        this.time = 0f;
    }

    @Override
    public void move(Enemy enemy, float delta) {
        time += delta;
        
        float horizontalDirection = (float) Math.sin(time * zigzagFrequency * 0.25f);
        float velocityX = horizontalDirection * HORIZONTAL_SPEED;
        
        enemy.setX(enemy.getX() + velocityX * delta);
        
        if (enemy instanceof puppy.code.entities.enemies.EnemyShip1) {
            puppy.code.entities.enemies.EnemyShip1 ship = (puppy.code.entities.enemies.EnemyShip1) enemy;
            if (!ship.hasReachedTarget()) {
                enemy.setY(enemy.getY() + baseVelocityY * delta * 60);
            }
        } else {
            enemy.setY(enemy.getY() + baseVelocityY * delta * 60);
        }

        if (enemy.getX() < 0) {
            enemy.setX(0);
        }
        if (enemy.getX() + enemy.getWidth() > 1920) {
            enemy.setX(1920 - enemy.getWidth());
        }
    }
}
