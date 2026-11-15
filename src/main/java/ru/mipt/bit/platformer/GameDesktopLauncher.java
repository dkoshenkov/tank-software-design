package ru.mipt.bit.platformer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import org.lwjgl.glfw.GLFW;
import ru.mipt.bit.platformer.app.GameSceneBuilder;
import ru.mipt.bit.platformer.app.GameSession;
import ru.mipt.bit.platformer.level.FileLevelGenerator;
import ru.mipt.bit.platformer.level.LevelGenerator;
import ru.mipt.bit.platformer.level.RandomLevelGenerator;

import java.util.Random;

/**
 * Входной адаптер libGDX. Передаёт игре время между кадрами.
 */
public class GameDesktopLauncher extends ApplicationAdapter {
    private final String levelPath;
    private GameSession session;

    public GameDesktopLauncher(String levelPath) {
        this.levelPath = levelPath;
    }

    @Override
    public void create() {
        session = new GameSceneBuilder(createLevelGenerator()).build(Gdx.input);
        preserveWindowAspectRatio();
    }

    private LevelGenerator createLevelGenerator() {
        if (levelPath.equals("random")) {
            return new RandomLevelGenerator(10, 8, new Random());
        }
        return new FileLevelGenerator(Gdx.files.internal(levelPath));
    }

    private void preserveWindowAspectRatio() {
        Lwjgl3Graphics graphics = (Lwjgl3Graphics) Gdx.graphics;
        GLFW.glfwSetWindowAspectRatio(
                graphics.getWindow().getWindowHandle(),
                graphics.getWidth(), graphics.getHeight()
        );
    }

    @Override
    public void resize(int width, int height) {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
    }

    @Override
    public void render() {
        session.renderFrame(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void dispose() {
        if (session != null) {
            session.dispose();
        }
    }

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration windowConfig = new Lwjgl3ApplicationConfiguration();
        String levelPath = args.length == 0 ? "level.txt" : args[0];
        new Lwjgl3Application(new GameDesktopLauncher(levelPath), windowConfig);
    }
}
