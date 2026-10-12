package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import ru.mipt.bit.platformer.model.level.LevelLayout;

import java.util.List;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class FileLevelGeneratorTest {
    @Test public void readsDimensionsAndPositionsFromTopToBottom() {
        LevelLayout layout = generate("T__\r\n_XT\r\n___\r\n");

        assertEquals(3, layout.getWidth());
        assertEquals(3, layout.getHeight());
        assertEquals(new GridPoint2(1, 1), layout.getPlayerPosition());
        assertEquals(List.of(new GridPoint2(0, 2), new GridPoint2(2, 1)), layout.getTreePositions());
    }

    @Test public void readsAssignmentExample() {
        LevelLayout layout = new FileLevelGenerator(new FileHandle("src/main/resources/level.txt")).generate();

        assertEquals(10, layout.getWidth());
        assertEquals(6, layout.getHeight());
        assertEquals(new GridPoint2(5, 1), layout.getPlayerPosition());
        assertEquals(15, layout.getTreePositions().size());
    }

    @Test public void rejectsEmptyRaggedUnknownAndInvalidPlayerLayouts() {
        String[] invalid = {"", "\n", "X_\n_", "X_\n\n__", "X?", "__\nTT", "XX"};
        for (String text : invalid) {
            assertThrows(IllegalArgumentException.class, () -> generate(text));
        }
    }

    @Test public void acceptsSinglePlayerTileWithoutFinalNewline() {
        LevelLayout layout = generate("X");

        assertEquals(1, layout.getWidth());
        assertEquals(1, layout.getHeight());
        assertEquals(new GridPoint2(0, 0), layout.getPlayerPosition());
        assertTrue(layout.getTreePositions().isEmpty());
    }

    @Test public void layoutKeepsItsPositionsWhenOriginalOrReturnedPointsChange() {
        GridPoint2 player = new GridPoint2(0, 0);
        GridPoint2 tree = new GridPoint2(1, 1);
        List<GridPoint2> trees = new ArrayList<>(List.of(tree));
        LevelLayout layout = new LevelLayout(2, 2, player, trees);
        player.set(9, 9);
        tree.set(9, 9);
        trees.clear();
        layout.getPlayerPosition().set(9, 9);
        layout.getTreePositions().get(0).set(9, 9);
        layout.getTreePositions().clear();

        assertEquals(new GridPoint2(0, 0), layout.getPlayerPosition());
        assertEquals(List.of(new GridPoint2(1, 1)), layout.getTreePositions());
    }

    private LevelLayout generate(String text) {
        FileHandle file = FileHandle.tempFile("level");
        try {
            file.writeString(text, false, "UTF-8");
            return new FileLevelGenerator(file).generate();
        } finally {
            file.delete();
        }
    }
}
