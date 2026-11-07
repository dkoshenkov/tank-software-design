package ru.mipt.bit.platformer.app;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Disposable;

import ru.mipt.bit.platformer.input.TankControls;
import ru.mipt.bit.platformer.input.ai.RandomTankController;
import ru.mipt.bit.platformer.input.command.MoveCommand;
import ru.mipt.bit.platformer.input.command.ShootCommand;
import ru.mipt.bit.platformer.input.command.ToggleHealthBarsCommand;
import ru.mipt.bit.platformer.input.keyboard.KeyboardController;
import ru.mipt.bit.platformer.input.keyboard.TankKeyboardController;
import ru.mipt.bit.platformer.level.LevelGenerator;
import ru.mipt.bit.platformer.model.level.LevelBounds;
import ru.mipt.bit.platformer.model.level.LevelLayout;
import ru.mipt.bit.platformer.model.obstacle.Tree;
import ru.mipt.bit.platformer.model.tank.Tank;
import ru.mipt.bit.platformer.rendering.GameWorldRenderer;
import ru.mipt.bit.platformer.world.CombatSystem;
import ru.mipt.bit.platformer.world.GameWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.badlogic.gdx.Input.Keys.L;

public final class GameSceneBuilder {
    private static final int RANDOM_TANK_COUNT = 3;

    private final LevelGenerator levelGenerator;

    public GameSceneBuilder(LevelGenerator levelGenerator) {
        this.levelGenerator = levelGenerator;
    }

    public GameSession build(Input input) {
        List<Disposable> ownedResources = new ArrayList<>();
        try {
            return assembleScene(input, ownedResources);
        } catch (RuntimeException | Error failure) {
            disposeFailedScene(ownedResources, failure);
            throw failure;
        }
    }

    private GameSession assembleScene(Input input, List<Disposable> ownedResources) {
        LevelLayout layout = levelGenerator.generate();
        Random random = new Random();

        Tank player = createPlayer(layout);
        List<Tank> computerTanks = new RandomTankSpawner().spawn(layout, RANDOM_TANK_COUNT, random);

        ToggleHealthBarsCommand toggleHealthBars = new ToggleHealthBarsCommand();
        GameWorld world = new GameWorld(new LevelBounds(layout.getWidth(), layout.getHeight()));
        new CombatSystem(world);
        GameWorldRenderer worldRenderer = new SceneGraphicsBuilder()
                .build(layout, ownedResources, toggleHealthBars::isVisible);
        world.addListener(worldRenderer);
        populateWorld(world, layout, player, computerTanks);

        TankControls controls = new TankControls();
        controls.addStrategy(new TankKeyboardController(input, direction -> {
            if (!player.isDestroyed()) {
                controls.submit(new MoveCommand(player, direction, world::canMoveTo));
            }
        }, () -> {
            if (!player.isDestroyed()) {
                controls.submit(new ShootCommand(player, world::add));
            }
        }));
        for (Tank tank : computerTanks) {
            controls.addStrategy(new RandomTankController(tank, world::canMoveTo, random, world::add, controls::submit));
        }
        Runnable inputHandler = createInputHandler(input, controls, toggleHealthBars);

        return new GameSession(
                world,
                inputHandler,
                worldRenderer::render,
                ownedResources);
    }

    private Tank createPlayer(LevelLayout layout) {
        GridPoint2 position = layout.getPlayerPosition();
        return new Tank(position.x, position.y);
    }

    private void populateWorld(GameWorld world, LevelLayout layout, Tank player, List<Tank> computerTanks) {
        for (GridPoint2 position : layout.getTreePositions()) {
            world.add(new Tree(position.x, position.y));
        }
        world.add(player);
        computerTanks.forEach(world::add);
    }

    private Runnable createInputHandler(Input input, TankControls controls, ToggleHealthBarsCommand toggleHealthBars) {
        KeyboardController keyboard = new KeyboardController();
        keyboard.bindJustPressed(toggleHealthBars, L);
        return () -> {
            keyboard.handleInput(input);
            controls.controlTanks();
        };
    }

    private void disposeFailedScene(List<Disposable> resources, Throwable failure) {
        while (!resources.isEmpty()) {
            try {
                resources.remove(resources.size() - 1).dispose();
            } catch (RuntimeException | Error disposeFailure) {
                failure.addSuppressed(disposeFailure);
            }
        }
    }
}
