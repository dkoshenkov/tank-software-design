package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import ru.mipt.bit.platformer.model.level.LevelLayout;
import ru.mipt.bit.platformer.model.level.LevelMap;

import java.util.HashSet;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

public class RandomLevelGeneratorTest {
    @Test public void generatesUniqueTreesInsideLevelWithoutBlockingPlayer() {
        RandomLevelGenerator generator = new RandomLevelGenerator(10, 8, new Random(42));
        LevelMap bounds = new LevelMap(10, 8);
        HashSet<GridPoint2> starts = new HashSet<>();
        HashSet<List<GridPoint2>> treeLayouts = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            LevelLayout layout = generator.generate();
            assertEquals(10, layout.getWidth());
            assertEquals(8, layout.getHeight());
            assertTrue(bounds.containsTile(layout.getPlayerPosition()));
            assertFalse(layout.getTreePositions().contains(layout.getPlayerPosition()));
            assertEquals(layout.getTreePositions().size(), new HashSet<>(layout.getTreePositions()).size());
            for (GridPoint2 tree : layout.getTreePositions()) {
                assertTrue(bounds.containsTile(tree));
            }
            starts.add(layout.getPlayerPosition());
            treeLayouts.add(layout.getTreePositions());
        }
        assertTrue(starts.size() > 1);
        assertTrue(treeLayouts.size() > 1);
    }

    @Test public void singleTileLevelContainsOnlyPlayer() {
        LevelLayout layout = new RandomLevelGenerator(1, 1, new Random(42)).generate();

        assertEquals(new GridPoint2(0, 0), layout.getPlayerPosition());
        assertTrue(layout.getTreePositions().isEmpty());
    }

    @Test public void rejectsNonPositiveDimensions() {
        assertThrows(IllegalArgumentException.class, () -> new RandomLevelGenerator(0, 8, new Random()));
        assertThrows(IllegalArgumentException.class, () -> new RandomLevelGenerator(10, -1, new Random()));
    }
}
