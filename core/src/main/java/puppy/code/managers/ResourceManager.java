package puppy.code.managers;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class ResourceManager {
    private static ResourceManager instance;
    
    private Map<String, Texture> textures;
    private Map<String, Sound> sounds;
    private Map<String, Music> music;
    private Map<String, BitmapFont> fonts;
    private Map<String, TextureAtlas> atlases;
    
    private ResourceManager() {
        textures = new HashMap<>();
        sounds = new HashMap<>();
        music = new HashMap<>();
        fonts = new HashMap<>();
        atlases = new HashMap<>();
    }
    
    public static ResourceManager getInstance() {
        if (instance == null) {
            instance = new ResourceManager();
        }
        return instance;
    }
    
    public Texture getTexture(String path) {
        if (!textures.containsKey(path)) {
            textures.put(path, new Texture(Gdx.files.internal(path)));
        }
        return textures.get(path);
    }
    
    public Sound getSound(String path) {
        if (!sounds.containsKey(path)) {
            sounds.put(path, Gdx.audio.newSound(Gdx.files.internal(path)));
        }
        return sounds.get(path);
    }
    
    public Music getMusic(String path) {
        if (!music.containsKey(path)) {
            music.put(path, Gdx.audio.newMusic(Gdx.files.internal(path)));
        }
        return music.get(path);
    }
    
    public BitmapFont getFont(String path) {
        if (!fonts.containsKey(path)) {
            fonts.put(path, new BitmapFont(Gdx.files.internal(path)));
        }
        return fonts.get(path);
    }
    
    public TextureAtlas getAtlas(String path) {
        if (!atlases.containsKey(path)) {
            atlases.put(path, new TextureAtlas(Gdx.files.internal(path)));
        }
        return atlases.get(path);
    }
    
    public BitmapFont getDefaultFont() {
        if (!fonts.containsKey("default")) {
            BitmapFont font = new BitmapFont();
            font.getData().setScale(2f);
            fonts.put("default", font);
        }
        return fonts.get("default");
    }
    
    public void preloadCommonResources() {
        getTexture("Rocket2.png");
        
        getSound("hurt.ogg");
        getSound("pop-sound.mp3");
        getSound("explosion.ogg");
        
        // Pistas de música dinámicas
        try {
            getMusic("Audio/Music/NombreProyecto - 1 chill.mp3");
            getMusic("Audio/Music/NombreProyecto - 2 medio.mp3");
            getMusic("Audio/Music/NombreProyecto - 3 epic.mp3");
        } catch (Exception e) {
            System.out.println("Error precargando pistas de música: " + e.getMessage());
        }
        
        getDefaultFont();
    }
    
    public void unloadTexture(String path) {
        if (textures.containsKey(path)) {
            textures.get(path).dispose();
            textures.remove(path);
        }
    }
    
    public void unloadSound(String path) {
        if (sounds.containsKey(path)) {
            sounds.get(path).dispose();
            sounds.remove(path);
        }
    }
    
    public void unloadMusic(String path) {
        if (music.containsKey(path)) {
            music.get(path).dispose();
            music.remove(path);
        }
    }
    
    public void dispose() {
        for (Texture texture : textures.values()) {
            texture.dispose();
        }
        for (Sound sound : sounds.values()) {
            sound.dispose();
        }
        for (Music music : music.values()) {
            music.dispose();
        }
        for (BitmapFont font : fonts.values()) {
            font.dispose();
        }
        for (TextureAtlas atlas : atlases.values()) {
            atlas.dispose();
        }
        
        textures.clear();
        sounds.clear();
        music.clear();
        fonts.clear();
        atlases.clear();
    }
    
    public boolean isTextureLoaded(String path) {
        return textures.containsKey(path);
    }
    
    public int getLoadedTextureCount() {
        return textures.size();
    }
    
    public int getLoadedSoundCount() {
        return sounds.size();
    }
}