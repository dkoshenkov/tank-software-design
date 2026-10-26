package ru.mipt.bit.platformer.app;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.model.level.LevelLayout;
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
        List<GridPoint2> freeTiles = findFreeTiles(layout);
        Collections.shuffle(freeTiles, random);
        return createTanks(freeTiles, count);
    }

    private List<GridPoint2> findFreeTiles(LevelLayout layout) {
        Set<GridPoint2> occupied = new HashSet<>(layout.getTreePositions());
        occupied.add(layout.getPlayerPosition());
        List<GridPoint2> freeTiles = new ArrayList<>();
        for (int y = 0; y < layout.getHeight(); y++) {
            for (int x = 0; x < layout.getWidth(); x++) {
                GridPoint2 tile = new GridPoint2(x, y);
                if (!occupied.contains(tile)) {
                    freeTiles.add(tile);
                }
            }
        }
        return freeTiles;
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
