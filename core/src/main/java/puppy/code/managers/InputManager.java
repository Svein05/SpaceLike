package puppy.code.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class InputManager {
    private static InputManager instance;
    
    // Estados de input del frame anterior para detectar "just pressed"
    private boolean previousLeftPressed = false;
    private boolean previousRightPressed = false;
    private boolean previousUpPressed = false;
    private boolean previousDownPressed = false;
    private boolean previousSpacePressed = false;
    private boolean previousEscapePressed = false;
    
    private InputManager() {
    }
    
    public static InputManager getInstance() {
        if (instance == null) {
            instance = new InputManager();
        }
        return instance;
    }
    
    // Actualizar estados previos - llamar al final de cada frame
    public void update() {
        previousLeftPressed = isLeftPressed();
        previousRightPressed = isRightPressed();
        previousUpPressed = isUpPressed();
        previousDownPressed = isDownPressed();
        previousSpacePressed = isSpacePressed();
        previousEscapePressed = isEscapePressed();
    }
    
    // Métodos para verificar si las teclas están presionadas
    public boolean isLeftPressed() {
        return Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A);
    }
    
    public boolean isRightPressed() {
        return Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D);
    }
    
    public boolean isUpPressed() {
        return Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W);
    }
    
    public boolean isDownPressed() {
        return Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S);
    }
    
    public boolean isSpacePressed() {
        return Gdx.input.isKeyPressed(Input.Keys.SPACE);
    }
    
    public boolean isEscapePressed() {
        return Gdx.input.isKeyPressed(Input.Keys.ESCAPE);
    }
    
    public boolean isEnterPressed() {
        return Gdx.input.isKeyPressed(Input.Keys.ENTER);
    }
    
    // Métodos para detectar "just pressed" (presionado en este frame pero no en el anterior)
    public boolean isLeftJustPressed() {
        return isLeftPressed() && !previousLeftPressed;
    }
    
    public boolean isRightJustPressed() {
        return isRightPressed() && !previousRightPressed;
    }
    
    public boolean isUpJustPressed() {
        return isUpPressed() && !previousUpPressed;
    }
    
    public boolean isDownJustPressed() {
        return isDownPressed() && !previousDownPressed;
    }
    
    public boolean isSpaceJustPressed() {
        return isSpacePressed() && !previousSpacePressed;
    }
    
    public boolean isEscapeJustPressed() {
        return isEscapePressed() && !previousEscapePressed;
    }
    
    public boolean isEnterJustPressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.ENTER);
    }
    
    // Métodos para mouse (para menús)
    public boolean isMouseClicked() {
        return Gdx.input.justTouched();
    }
    
    public int getMouseX() {
        return Gdx.input.getX();
    }
    
    public int getMouseY() {
        return Gdx.graphics.getHeight() - Gdx.input.getY(); // Invertir Y para LibGDX
    }
    
    // Método para obtener información de movimiento analógico
    public float getHorizontalAxis() {
        float axis = 0f;
        if (isLeftPressed()) axis -= 1f;
        if (isRightPressed()) axis += 1f;
        return axis;
    }
    
    public float getVerticalAxis() {
        float axis = 0f;
        if (isDownPressed()) axis -= 1f;
        if (isUpPressed()) axis += 1f;
        return axis;
    }
    
    // Verificar si cualquier tecla de movimiento está presionada
    public boolean isAnyMovementKeyPressed() {
        return isLeftPressed() || isRightPressed() || isUpPressed() || isDownPressed();
    }
    
    // Verificar si alguna tecla de acción está presionada
    public boolean isAnyActionKeyPressed() {
        return isSpacePressed() || isEnterPressed();
    }
}