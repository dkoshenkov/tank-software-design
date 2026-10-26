package ru.mipt.bit.platformer.app;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Disposable;

import ru.mipt.bit.platformer.input.TankControls;
import ru.mipt.bit.platformer.level.LevelGenerator;
import ru.mipt.bit.platformer.model.TileBlocker;
import ru.mipt.bit.platformer.model.Updatable;
import ru.mipt.bit.platformer.model.level.LevelBounds;
import ru.mipt.bit.platformer.model.level.LevelLayout;
import ru.mipt.bit.platformer.model.obstacle.Tree;
import ru.mipt.bit.platformer.model.tank.Tank;
import ru.mipt.bit.platformer.rendering.GameWorldRenderer;
import ru.mipt.bit.platformer.world.GameWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
        List<Tank> computerTanks = spawnComputerTanks(layout, random);
        List<Tank> tanks = collectTanks(player, computerTanks);
        List<Tree> trees = createTrees(layout);

        GameWorld world = createWorld(layout, tanks, trees);
        GameWorldRenderer worldRenderer = new SceneGraphicsBuilder().build(layout, tanks, trees, ownedResources);
        TankControls controls = new TankControls(input, player, computerTanks, world::canMoveTo, random);

        return new GameSession(
                world,
                controls::controlTanks,
                worldRenderer::render,
                ownedResources);
    }

    private Tank createPlayer(LevelLayout layout) {
        GridPoint2 position = layout.getPlayerPosition();
        return new Tank(position.x, position.y);
    }

    private List<Tank> spawnComputerTanks(LevelLayout layout, Random random) {
        return new RandomTankSpawner().spawn(layout, RANDOM_TANK_COUNT, random);
    }

    private List<Tank> collectTanks(Tank player, List<Tank> computerTanks) {
        List<Tank> tanks = new ArrayList<>();
        tanks.add(player);
        tanks.addAll(computerTanks);
        return tanks;
    }

    private List<Tree> createTrees(LevelLayout layout) {
        List<Tree> trees = new ArrayList<>();
        for (GridPoint2 position : layout.getTreePositions()) {
            trees.add(new Tree(position.x, position.y));
        }
        return trees;
    }

    private GameWorld createWorld(LevelLayout layout, List<Tank> tanks, List<Tree> trees) {
        LevelBounds bounds = new LevelBounds(layout.getWidth(), layout.getHeight());
        List<TileBlocker> obstacles = new ArrayList<>(trees);
        obstacles.addAll(tanks);
        List<Updatable> updatables = new ArrayList<>(tanks);
        return new GameWorld(updatables, bounds, obstacles);
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
