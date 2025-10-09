package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class HealthSystem {
    
    // Estados de los corazones
    public enum HeartState {
        FULL,    // Corazón completo
        HALF,    // Medio corazón
        EMPTY    // Corazón vacío
    }
    
    private TextureRegion heartFull;
    private TextureRegion heartHalf;
    private TextureRegion heartEmpty;
    
    private HeartState[] hearts;
    private int maxHearts;
    private float currentHealth; // Salud total en "medios corazones" (6 = 3 corazones completos)
    
    public HealthSystem() {
        // Cargar el atlas de corazones
        TextureAtlas heartAtlas = new TextureAtlas(Gdx.files.internal("UI/Heart/HeartsUwU.atlas"));
        
        // Obtener las regiones por nombre exacto desde el atlas
        heartFull = heartAtlas.findRegion("HeartFull");    // Corazón completo
        heartHalf = heartAtlas.findRegion("HeartMid");     // Medio corazón  
        heartEmpty = heartAtlas.findRegion("Heart0");      // Corazón vacío
        
        // Reflejar el HeartMid horizontalmente (como espejo)
        if (heartHalf != null) {
            heartHalf.flip(true, false); // flip(horizontal, vertical)
        }
        
        // Verificar que se cargaron correctamente
        if (heartFull == null || heartHalf == null || heartEmpty == null) {
            throw new RuntimeException("No se pudieron cargar los sprites de corazones desde el atlas. " +
                    "Sprites encontrados en atlas: HeartFull, HeartMid, Heart0");
        }
        
        // Inicializar sistema con 3 corazones completos
        maxHearts = 3;
        hearts = new HeartState[maxHearts];
        currentHealth = 6; // 3 corazones * 2 medios = 6 medios corazones
        
        // Inicializar todos los corazones como completos
        for (int i = 0; i < maxHearts; i++) {
            hearts[i] = HeartState.FULL;
        }
    }
    
    /**
     * Quita medio corazón de vida
     */
    public void takeDamage() {
        if (currentHealth > 0) {
            currentHealth--;
            updateHeartStates();
        }
    }
    
    /**
     * Actualiza el estado visual de los corazones basado en la salud actual
     */
    private void updateHeartStates() {
        for (int i = 0; i < maxHearts; i++) {
            float heartHealth = currentHealth - (i * 2); // Cada corazón representa 2 medios
            
            if (heartHealth >= 2) {
                hearts[i] = HeartState.FULL;    // Corazón completo
            } else if (heartHealth >= 1) {
                hearts[i] = HeartState.HALF;    // Medio corazón
            } else {
                hearts[i] = HeartState.EMPTY;   // Corazón vacío
            }
        }
    }
    
    /**
     * Renderiza los corazones en pantalla
     */
    public void render(SpriteBatch batch, float x, float y) {
        float heartWidth = 32;  // Hacer los corazones más grandes (original es 12x12)
        float heartHeight = 32;
        float heartSpacing = 40; // Espacio entre corazones
        
        for (int i = 0; i < maxHearts; i++) {
            TextureRegion heartTexture;
            
            switch (hearts[i]) {
                case FULL:
                    heartTexture = heartFull;
                    break;
                case HALF:
                    heartTexture = heartHalf;
                    break;
                case EMPTY:
                default:
                    heartTexture = heartEmpty;
                    break;
            }
            
            // Dibujar corazón en posición calculada
            batch.draw(heartTexture, x + (i * heartSpacing), y, heartWidth, heartHeight);
        }
    }
    
    /**
     * Verifica si el jugador está muerto (sin vida)
     */
    public boolean isDead() {
        return currentHealth <= 0;
    }
    
    /**
     * Obtiene la vida actual en "medios corazones"
     */
    public float getCurrentHealth() {
        return currentHealth;
    }
    
    /**
     * Obtiene las vidas como número entero (para compatibilidad con el sistema actual)
     */
    public int getVidas() {
        return (int) Math.ceil(currentHealth / 2.0f); // Convierte medios corazones a vidas
    }
    
    /**
     * Establece las vidas (para compatibilidad con el sistema actual)
     */
    public void setVidas(int vidas) {
        currentHealth = vidas * 2; // Cada vida = 2 medios corazones
        updateHeartStates();
    }
    
    /**
     * Restaura toda la vida
     */
    public void fullHeal() {
        currentHealth = maxHearts * 2;
        updateHeartStates();
    }
}