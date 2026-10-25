package ru.mipt.bit.platformer.app;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Disposable;
import ru.mipt.bit.platformer.input.keyboard.TankKeyboardController;
import ru.mipt.bit.platformer.model.TileBlocker;
import ru.mipt.bit.platformer.model.level.LevelMap;
import ru.mipt.bit.platformer.model.level.LevelMap;
import ru.mipt.bit.platformer.model.obstacle.Tree;
import ru.mipt.bit.platformer.model.tank.Tank;
import ru.mipt.bit.platformer.rendering.GameWorldRenderer;
import ru.mipt.bit.platformer.level.LevelGenerator;
import ru.mipt.bit.platformer.model.level.LevelLayout;
import ru.mipt.bit.platformer.world.GameWorld;

import java.util.ArrayList;
import java.util.List;

public final class GameSceneBuilder {
    private final LevelGenerator levelGenerator;

    public GameSceneBuilder(LevelGenerator levelGenerator) {
        this.levelGenerator = levelGenerator;
    }

    public GameSession build() {
        List<Disposable> ownedResources = new ArrayList<>();
        try {
            return createSession(ownedResources);
        } catch (RuntimeException | Error failure) {
            disposeFailedScene(ownedResources, failure);
            throw failure;
        }
    }

    private GameSession createSession(List<Disposable> ownedResources) {
        LevelLayout layout = levelGenerator.generate();
        GridPoint2 position = layout.getPlayerPosition();
        Tank player = new Tank(position.x, position.y);
        List<Tree> trees = new ArrayList<>();
        for (GridPoint2 treePosition : layout.getTreePositions()) {
            trees.add(new Tree(treePosition.x, treePosition.y));
        }

        List<TileBlocker> obstacles = new ArrayList<>(trees);
        GameWorld world = new GameWorld(
                List.of(player),
                new LevelMap(layout.getWidth(), layout.getHeight()),
                obstacles);

        TankKeyboardController keyboard = new TankKeyboardController(
                direction -> player.tryMove(direction, world::canMoveTo));

        GameWorldRenderer renderer = new SceneGraphicsBuilder()
                .build(layout, player, trees, ownedResources);

        return new GameSession(world, keyboard::handleInput, renderer::render, ownedResources);
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
