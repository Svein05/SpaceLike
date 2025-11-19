package puppy.code.entities.enemies.behaviors;

import puppy.code.entities.enemies.Enemy;
import puppy.code.interfaces.MovementBehavior;

public class SinusoidalMovement implements MovementBehavior {
    private float baseVelocityY;
    private float amplitude;
    private float frequency;
    private float time;
    private float startX;

    public SinusoidalMovement(float velocityY, float amplitude, float frequency) {
        this.baseVelocityY = velocityY;
        this.amplitude = amplitude;
        this.frequency = frequency;
        this.time = 0f;
    }

    @Override
    public void initialize(Enemy enemy) {
        this.startX = enemy.getX();
        this.time = 0f;
    }

    @Override
    public void move(Enemy enemy, float delta) {
        time += delta;
        
        float newX = startX + (float) Math.sin(time * frequency) * amplitude;
        
        if (newX < 0) newX = 0;
        if (newX + enemy.getWidth() > 1920) newX = 1920 - enemy.getWidth();
        
        enemy.setX(newX);
        enemy.setY(enemy.getY() + baseVelocityY * delta * 60);
    }
}
