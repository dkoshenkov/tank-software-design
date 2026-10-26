package ru.mipt.bit.platformer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import ru.mipt.bit.platformer.app.GameSceneBuilder;
import ru.mipt.bit.platformer.app.GameSession;
import ru.mipt.bit.platformer.level.FileLevelGenerator;
import ru.mipt.bit.platformer.level.LevelGenerator;
import ru.mipt.bit.platformer.level.RandomLevelGenerator;

import java.util.Random;

public class GameDesktopLauncher extends ApplicationAdapter {
    private final String levelPath;
    private GameSession session;

    public GameDesktopLauncher(String levelPath) {
        this.levelPath = levelPath;
    }

    @Override
    public void create() {
        LevelGenerator generator;
        if (levelPath.equals("random")) {
            generator = new RandomLevelGenerator(10, 8, new Random());
        } else {
            generator = new FileLevelGenerator(Gdx.files.internal(levelPath));
        }
        session = new GameSceneBuilder(generator).build(Gdx.input);
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
        windowConfig.setWindowedMode(1280, 1024);
        String levelPath = args.length == 0 ? "level.txt" : args[0];
        new Lwjgl3Application(new GameDesktopLauncher(levelPath), windowConfig);
    }
}
