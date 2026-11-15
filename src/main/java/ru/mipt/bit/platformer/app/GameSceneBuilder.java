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
        GameWorld world = createWorld(layout);
        GameWorldRenderer worldRenderer = attachRenderer(world, layout, player, ownedResources, toggleHealthBars);
        populateWorld(world, layout, player, computerTanks);

        TankControls controls = createTankControls(input, player, world, computerTanks, random);
        Runnable inputHandler = createInputHandler(input, controls, toggleHealthBars);
        return new GameSession(world, inputHandler, worldRenderer::render, ownedResources);
    }

    private GameWorld createWorld(LevelLayout layout) {
        GameWorld world = new GameWorld(new LevelBounds(layout.getWidth(), layout.getHeight()));
        new CombatSystem(world);
        return world;
    }

    private GameWorldRenderer attachRenderer(GameWorld world, LevelLayout layout, Tank player,
                                             List<Disposable> ownedResources, ToggleHealthBarsCommand toggleHealthBars) {
        GameWorldRenderer renderer = new SceneGraphicsBuilder()
                .build(layout, player, ownedResources, toggleHealthBars::isVisible);
        world.addListener(renderer);
        return renderer;
    }

    private static TankControls createTankControls(Input input, Tank player, GameWorld world,
                                                   List<Tank> computerTanks, Random random) {
        TankControls controls = new TankControls();
        bindPlayerControls(controls, input, player, world);
        attachComputerControllers(controls, computerTanks, world, random);
        return controls;
    }

    private static void bindPlayerControls(TankControls controls, Input input, Tank player, GameWorld world) {
        controls.addStrategy(TankKeyboardController.withCommands(input,
                direction -> new MoveCommand(player, direction, world::canMoveTo),
                new ShootCommand(player, world::add)));
    }

    private static void attachComputerControllers(TankControls controls, List<Tank> computerTanks,
                                                  GameWorld world, Random random) {
        for (Tank tank : computerTanks) {
            controls.addStrategy(new RandomTankController(tank, world::canMoveTo, random, world::add,
                    world::getElapsedSeconds));
        }
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

    static Runnable createInputHandler(Input input, TankControls controls, ToggleHealthBarsCommand toggleHealthBars) {
        KeyboardController keyboard = new KeyboardController();
        keyboard.bindJustPressed(toggleHealthBars, L);
        return () -> {
            keyboard.handleInput(input, controls::submit);
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
