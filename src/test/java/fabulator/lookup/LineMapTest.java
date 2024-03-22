package fabulator.lookup;

import fabulator.ui.builder.LineBuilder;
import javafx.scene.Group;
import javafx.scene.shape.Line;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import testutil.TestUtils;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class LineMapTest {

    private static LineMap lineMap;
    private static Line line1;
    private static Line line2;
    private static Line line3;
    private static Line line4;
    private static Line line5;
    private static Line line6;

    @BeforeAll
    @DisplayName("Initialize LineMap")
    static void initializeLineMap() {
        TestUtils.initializeJavaFX();
        lineMap = new LineMap(10);

        Group lineGroup = new Group();

        line1 = new LineBuilder()
                .setStart(0, 0)
                .setEnd(8, 0)
                .build();
        line2 = new LineBuilder()
                .setStart(8, 0)
                .setEnd(8, 4)
                .build();
        line3 = new LineBuilder()
                .setStart(8, 0)
                .setEnd(8, -4)
                .build();
        line4 = new LineBuilder()
                .setStart(8, 0)
                .setEnd(12, 0)
                .build();
        line5 = new LineBuilder()
                .setStart(12, 0)
                .setEnd(16, 0)
                .build();
        line6 = new LineBuilder()
                .setStart(17, 0)
                .setEnd(20, 0)
                .build();

        lineGroup.getChildren().addAll(
                line1, line2, line3, line4, line5, line6
        );

        lineMap.addAll(
                line1, line2, line3, line4, line5, line6
        );
    }

    @Test
    @DisplayName("Test retrieval of lines")
    void testRetrievalOfLines() {
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

    @Test
    @DisplayName("Test getting all Lines at a Line")
    public void testGettingAllLinesAtALine() {
        Set<Line> linesAtLine1 = lineMap.allLinesAt(line1);

        assertEquals(5, linesAtLine1.size());
        assertTrue(linesAtLine1.contains(line1));
        assertTrue(linesAtLine1.contains(line2));
        assertTrue(linesAtLine1.contains(line3));
        assertTrue(linesAtLine1.contains(line4));
        assertTrue(linesAtLine1.contains(line5));
    }
}
