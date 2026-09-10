package ru.mipt.bit.platformer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import ru.mipt.bit.platformer.obstacle.Obstacle;
import ru.mipt.bit.platformer.tank.Tank;
import ru.mipt.bit.platformer.tank.TankKeyboardController;
import ru.mipt.bit.platformer.world.GameWorld;
import ru.mipt.bit.platformer.world.GameWorldRenderer;

public class GameDesktopLauncher extends ApplicationAdapter {
    private GameWorld world;
    private GameWorldRenderer worldRenderer;
    private TankKeyboardController playerKeyboardController;

    @Override
    public void create() {
        Tank playerTank = new Tank(1, 1);
        world = new GameWorld(playerTank, new Obstacle(1, 3));
        playerKeyboardController = new TankKeyboardController(playerTank, world::canMoveTo);
        worldRenderer = new GameWorldRenderer(world, "level.tmx");
    }

    @Override
    public void render() {
        playerKeyboardController.handleInput(Gdx.input);
        world.update(Gdx.graphics.getDeltaTime());
        worldRenderer.render();
    }

    @Override
    public void dispose() {
        worldRenderer.dispose();
    }

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration windowConfig = new Lwjgl3ApplicationConfiguration();
        // level width: 10 tiles x 128px, height: 8 tiles x 128px
        windowConfig.setWindowedMode(1280, 1024);
        new Lwjgl3Application(new GameDesktopLauncher(), windowConfig);
    }
}
