package ru.mipt.bit.platformer.app;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.model.level.LevelBounds;
import ru.mipt.bit.platformer.model.level.LevelLayout;
import ru.mipt.bit.platformer.model.tank.Direction;
import ru.mipt.bit.platformer.model.tank.Tank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public final class RandomTankSpawner {
    public List<Tank> spawn(LevelLayout layout, int count, Random random) {
        if (count < 0) {
            throw new IllegalArgumentException("Tank count must not be negative");
        }
        List<GridPoint2> freeTiles = findReachableTiles(layout);
        Collections.shuffle(freeTiles, random);
        return createTanks(freeTiles, count);
    }

    private List<GridPoint2> findReachableTiles(LevelLayout layout) {
        Set<GridPoint2> visited = new HashSet<>(layout.getTreePositions());
        GridPoint2 start = layout.getPlayerPosition();
        visited.add(start);
        List<GridPoint2> freeTiles = new ArrayList<>();
        LevelBounds bounds = new LevelBounds(layout.getWidth(), layout.getHeight());
        addReachableNeighbors(start, bounds, visited, freeTiles);
        for (int i = 0; i < freeTiles.size(); i++) {
            addReachableNeighbors(freeTiles.get(i), bounds, visited, freeTiles);
        }
        return freeTiles;
    }

    private void addReachableNeighbors(GridPoint2 tile, LevelBounds bounds, Set<GridPoint2> visited,
                                       List<GridPoint2> freeTiles) {
        for (Direction direction : Direction.values()) {
            GridPoint2 next = direction.getAdjacentTileCoordinates(tile);
            if (bounds.containsTile(next) && visited.add(next)) {
                freeTiles.add(next);
            }
        }
    }

    private List<Tank> createTanks(List<GridPoint2> freeTiles, int count) {
        List<Tank> tanks = new ArrayList<>();
        for (int i = 0; i < Math.min(count, freeTiles.size()); i++) {
            GridPoint2 tile = freeTiles.get(i);
            tanks.add(new Tank(tile.x, tile.y));
        }
        return tanks;
    }
}
