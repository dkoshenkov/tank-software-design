package ru.mipt.bit.platformer.app;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import ru.mipt.bit.platformer.model.level.LevelLayout;
import ru.mipt.bit.platformer.model.tank.Tank;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class RandomTankSpawnerTest {
    private final RandomTankSpawner spawner = new RandomTankSpawner();

    @Test public void generatesRequestedCountWithoutOverlapsInsideMap() {
        LevelLayout layout = new LevelLayout(4, 3, new GridPoint2(0, 0), List.of(new GridPoint2(1, 1)));
        for (int seed = 0; seed < 30; seed++) {
            List<Tank> tanks = spawner.spawn(layout, 3, new Random(seed));
            assertEquals(3, tanks.size());
            Set<GridPoint2> occupied = new HashSet<>(layout.getTreePositions());
            occupied.add(layout.getPlayerPosition());
            for (Tank tank : tanks) {
                GridPoint2 tile = tank.getTileCoordinates();
                assertTrue(tile.x >= 0 && tile.x < 4 && tile.y >= 0 && tile.y < 3);
                assertTrue(occupied.add(tile));
            }
        }
    }

    @Test public void usesAllAvailableCellsWhenRequestedCountExceedsCapacity() {
        LevelLayout layout = new LevelLayout(3, 1, new GridPoint2(0, 0), List.of(new GridPoint2(1, 0)));
        List<Tank> tanks = spawner.spawn(layout, 3, new Random(0));
        assertEquals(1, tanks.size());
        assertEquals(new GridPoint2(2, 0), tanks.get(0).getTileCoordinates());
        assertTrue(spawner.spawn(layout, 0, new Random(0)).isEmpty());
        LevelLayout full = new LevelLayout(1, 1, new GridPoint2(0, 0), List.of());
        assertTrue(spawner.spawn(full, 3, new Random(0)).isEmpty());
    }

    @Test public void sameSeedReproducesPlacement() {
        LevelLayout layout = new LevelLayout(10, 8, new GridPoint2(0, 0), List.of());
        List<GridPoint2> first = positions(spawner.spawn(layout, 3, new Random(5)));
        assertEquals(first, positions(spawner.spawn(layout, 3, new Random(5))));
    }

    @Test
    public void rejectsNegativeCount() {
        assertThrows(IllegalArgumentException.class, () ->
                spawner.spawn(new LevelLayout(1, 1, new GridPoint2(0, 0), List.of()), -1, new Random()));
    }

    private List<GridPoint2> positions(List<Tank> tanks) {
        return tanks.stream().map(Tank::getTileCoordinates).collect(Collectors.toList());
    }
}
