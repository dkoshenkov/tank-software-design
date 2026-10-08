package ru.mipt.bit.platformer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import ru.mipt.bit.platformer.app.GameSceneBuilder;
import ru.mipt.bit.platformer.app.GameSession;

public class GameDesktopLauncher extends ApplicationAdapter {
    private GameSession session;

    @Override
    public void create() {
        session = new GameSceneBuilder().build();
    }

    @Override
    public void render() {
        session.renderFrame(Gdx.input, Gdx.graphics.getDeltaTime());
    }

    @Override
    public void dispose() {
        if (session != null) {
            session.dispose();
        }
    }

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration windowConfig = new Lwjgl3ApplicationConfiguration();
        // level width: 10 tiles x 128px, height: 8 tiles x 128px
        windowConfig.setWindowedMode(1280, 1024);
        new Lwjgl3Application(new GameDesktopLauncher(), windowConfig);
    }
}
