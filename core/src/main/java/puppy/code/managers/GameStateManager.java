package puppy.code.managers;

public class GameStateManager {
    private static GameStateManager instance;
    
    private int score;
    private int lives;
    private int round;
    private int highScore;
    private boolean gameOver;
    private boolean paused;
    
    // Configuracion del juego
    private int asteroidVelocityX;
    private int asteroidVelocityY;
    private int asteroidCount;
    
    private GameStateManager() {
        resetGame();
    }
    
    public static GameStateManager getInstance() {
        if (instance == null) {
            instance = new GameStateManager();
        }
        return instance;
    }
    
    public void resetGame() {
        score = 0;
        lives = 3;
        round = 1;
        gameOver = false;
        paused = false;
        asteroidVelocityX = 3;
        asteroidVelocityY = 3;
        asteroidCount = 5;
    }
    
    public void nextRound() {
        round++;
        asteroidVelocityX++;
        asteroidVelocityY++;
        asteroidCount++;
    }
    
    public void addScore(int points) {
        score += points;
        if (score > highScore) {
            highScore = score;
        }
    }
    
    public void loseLife() {
        lives--;
        if (lives <= 0) {
            gameOver = true;
        }
    }
    
    // Getters y setters
    public int getScore() { return score; }
    public int getLives() { return lives; }
    public int getRound() { return round; }
    public int getHighScore() { return highScore; }
    public boolean isGameOver() { return gameOver; }
    public boolean isPaused() { return paused; }
    
    public int getAsteroidVelocityX() { return asteroidVelocityX; }
    public int getAsteroidVelocityY() { return asteroidVelocityY; }
    public int getAsteroidCount() { return asteroidCount; }
    
    public void setLives(int lives) { this.lives = lives; }
    public void setPaused(boolean paused) { this.paused = paused; }
    public void setHighScore(int highScore) { this.highScore = highScore; }
    public void setScore(int score) { this.score = score; }
}