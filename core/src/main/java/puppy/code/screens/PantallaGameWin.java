package puppy.code.screens;

import puppy.code.SpaceNavigation;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import puppy.code.graphics.ParallaxBackground;
import com.badlogic.gdx.graphics.Texture;

public class PantallaGameWin implements Screen {

    private SpaceNavigation game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private ParallaxBackground parallaxBackground;
    private GlyphLayout layout;
    private Texture winTexture;
    private float timer;

    public PantallaGameWin(SpaceNavigation game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);
        viewport = new FitViewport(1920, 1080, camera);
        parallaxBackground = new ParallaxBackground();
        layout = new GlyphLayout();
        timer = 0f;
        loadWinTexture();
    }

    private void loadWinTexture() {
        try {
            if (Gdx.files.internal("UI/Menu/YouWin.png").exists()) {
                winTexture = new Texture(Gdx.files.internal("UI/Menu/YouWin.png"));
            } else {
                winTexture = null;
            }
        } catch (Exception e) {
            winTexture = null;
        }
    }

    @Override
    public void render(float delta) {
        timer += delta;
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        viewport.apply();
        camera.update();
        game.getBatch().setProjectionMatrix(camera.combined);
        game.getBatch().begin();

        parallaxBackground.render(game.getBatch());

        game.getFont().getData().setScale(3.0f);
        String winText = "YOU WIN";
        layout.setText(game.getFont(), winText);
        float x = (1920 - layout.width) / 2;
        float y = 1080 - 200;

        game.getFont().setColor(1f, 0.9f, 0.3f, 1f);
        game.getFont().draw(game.getBatch(), winText, x, y);

        if (winTexture != null) {
            float w = winTexture.getWidth() * 1.5f;
            float h = winTexture.getHeight() * 1.5f;
            game.getBatch().draw(winTexture, (1920 - w) / 2, (1080 - h) / 2, w, h);
        }

        game.getFont().getData().setScale(1.2f);
        String continueText = "Presiona ENTER para volver a jugar o ESC para salir";
        layout.setText(game.getFont(), continueText);
        float cx = (1920 - layout.width) / 2;
        float cy = 150;
        game.getFont().setColor(1f,1f,1f,1f);
        game.getFont().draw(game.getBatch(), continueText, cx, cy);

        game.getBatch().end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            Screen ss = new PantallaJuego(game,1,3,0,1,1,10);
            ss.resize(1920, 1080);
            game.setScreen(ss);
            dispose();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        if (parallaxBackground != null) parallaxBackground.dispose();
        if (winTexture != null) winTexture.dispose();
    }
}
