package puppy.code.interfaces;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import puppy.code.entities.Nave;

public interface Bonus extends Collectable {
    void update(float delta);
    void draw(SpriteBatch batch);
    boolean isOffScreen();
    void activateEffect();
    void deactivateEffect();
    boolean isEffectActive();
    void renderEffect(SpriteBatch batch, Nave nave);
    void dispose();
    Rectangle getBounds();
}