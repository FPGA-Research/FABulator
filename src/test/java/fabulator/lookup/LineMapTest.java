package fabulator.lookup;

import fabulator.ui.builder.LineBuilder;
import javafx.scene.Group;
import javafx.scene.shape.Line;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import testutil.TestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LineMapTest {

    private static LineMap lineMap;

    @BeforeAll
    @DisplayName("Initialize LineMap")
    static void initializeLineMap() {
        TestUtils.initializeJavaFX();
        lineMap = new LineMap(10);
    }

    @Test
    @DisplayName("Test Insertion and Retrieval")
    void testInsertionAndRetrieval() {
        Group lineGroup = new Group();

        Line line1 = new LineBuilder()
                .setStart(0, 0)
                .setEnd(8, 0)
                .build();
        Line line2 = new LineBuilder()
                .setStart(8, 0)
                .setEnd(8, 4)
                .build();
        Line line3 = new LineBuilder()
                .setStart(8, 0)
                .setEnd(8, -4)
                .build();
        Line line4 = new LineBuilder()
                .setStart(8, 0)
                .setEnd(12, 0)
                .build();
        Line line5 = new LineBuilder()
                .setStart(12, 0)
                .setEnd(16, 0)
                .build();

        lineGroup.getChildren().addAll(
                line1, line2, line3, line4, line5
        );

        lineMap.addAll(
                line1, line2, line3, line4, line5
        );

        List<Line> atLine1 = lineMap.adjacentTo(line1);
        assertEquals(3, atLine1.size());
        assertTrue(atLine1.contains(line2));
        assertTrue(atLine1.contains(line3));
        assertTrue(atLine1.contains(line4));
        assertFalse(atLine1.contains(line1));
        assertFalse(atLine1.contains(line5));

        List<Line> atLine5 = lineMap.adjacentTo(line5);
        assertEquals(1, atLine5.size());
        assertTrue(atLine5.contains(line4));
        assertFalse(atLine5.contains(line1));
        assertFalse(atLine5.contains(line2));
        assertFalse(atLine5.contains(line3));
        assertFalse(atLine5.contains(line5));
    }
}
