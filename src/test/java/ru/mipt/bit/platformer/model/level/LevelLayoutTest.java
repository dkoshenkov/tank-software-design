package ru.mipt.bit.platformer.model.level;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class LevelLayoutTest {
    @Test public void keepsPositionsWhenInputOrReturnedObjectsChange() {
        GridPoint2 player = new GridPoint2(1, 1);
        GridPoint2 tree = new GridPoint2(2, 1);
        List<GridPoint2> trees = new ArrayList<>(List.of(tree));
        LevelLayout layout = new LevelLayout(4, 3, player, trees);

        player.set(0, 0);
        tree.set(0, 0);
        trees.clear();
        layout.getPlayerPosition().set(0, 0);
        List<GridPoint2> returnedTrees = layout.getTreePositions();
        returnedTrees.get(0).set(0, 0);
        returnedTrees.clear();

        assertEquals(new GridPoint2(1, 1), layout.getPlayerPosition());
        assertEquals(List.of(new GridPoint2(2, 1)), layout.getTreePositions());
    }

    @Test public void rejectsPositionsOutsideLevelAndTreesOnPlayerTile() {
        List<GridPoint2> outside = List.of(
                new GridPoint2(-1, 1), new GridPoint2(4, 1),
                new GridPoint2(1, -1), new GridPoint2(1, 3));
        for (GridPoint2 position : outside) {
            assertThrows(IllegalArgumentException.class, () ->
                    new LevelLayout(4, 3, position, List.of()));
            assertThrows(IllegalArgumentException.class, () ->
                    new LevelLayout(4, 3, new GridPoint2(1, 1), List.of(position)));
        }
        assertThrows(IllegalArgumentException.class, () ->
                new LevelLayout(4, 3, new GridPoint2(1, 1), List.of(new GridPoint2(1, 1))));
    }
}
