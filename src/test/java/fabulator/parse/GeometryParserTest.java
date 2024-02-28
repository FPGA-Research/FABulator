package fabulator.parse;

import fabulator.geometry.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GeometryParserTest {

    private static FabricGeometry geometry;

    @BeforeAll
    @DisplayName("Test Parsing")
    public static void testGeometryParsing() {
        URL geomSourceUrl = GeometryParserTest.class.getResource("/parse/test_geometry.csv");
        assertNotNull(geomSourceUrl);

        String fileName = geomSourceUrl.getFile();
        File geometryFile = new File(fileName);
        GeometryParser parser = new GeometryParser(geometryFile);

        geometry = parser.getGeometry();
    }

    @Test
    @DisplayName("Test Parameter Correctness")
    void testParameterCorrectness() {
        assertEquals("test_fabric", geometry.getName());
        assertEquals(80, geometry.getWidth());
        assertEquals(80, geometry.getHeight());
        assertEquals(64, geometry.getNumberOfLines());
    }

    // TODO: better access method for tileName at x,y
    @Test
    @DisplayName("Test Fabric Definition")
    void testFabricDefinition() {
        List<List<String>> tileNames = geometry.getTileNames();

        assertEquals("TEST_TOP", tileNames.get(0).get(0));
        assertEquals("TEST_TOP", tileNames.get(0).get(1));
        assertEquals("TEST_BOT", tileNames.get(1).get(0));
        assertEquals("TEST_BOT", tileNames.get(1).get(1));
    }

    @Test
    @DisplayName("Test Tile Locations")
    void testFabricLocations() {
        List<List<Location>> tileLocations = geometry.getTileLocations();

        assertEquals(
                new Location(0, 0),
                tileLocations.get(0).get(0)
        );
        assertEquals(
                new Location(40, 0),
                tileLocations.get(0).get(1)
        );
        assertEquals(
                new Location(0, 40),
                tileLocations.get(1).get(0)
        );
        assertEquals(
                new Location(40, 40),
                tileLocations.get(1).get(1)
        );
    }

    @Test
    @DisplayName("Test Tiles")
    void testTiles() {
        Map<String, TileGeometry> tileGeomMap = geometry.getTileGeomMap();

        TileGeometry testTopGeom = tileGeomMap.get("TEST_TOP");
        TileGeometry testBotGeom = tileGeomMap.get("TEST_BOT");

        assertNotNull(testTopGeom);
        assertNotNull(testBotGeom);

        assertEquals("TEST_TOP", testTopGeom.getName());
        assertEquals("TEST_BOT", testBotGeom.getName());

        assertEquals(40, testTopGeom.getWidth());
        assertEquals(40, testTopGeom.getHeight());
        assertEquals(40, testBotGeom.getWidth());
        assertEquals(40, testTopGeom.getHeight());
    }

    @Test
    @DisplayName("Test Switch Matrices")
    void testSwitchMatrices() {
        Map<String, TileGeometry> tileGeomMap = geometry.getTileGeomMap();

        TileGeometry testTopGeom = tileGeomMap.get("TEST_TOP");
        TileGeometry testBotGeom = tileGeomMap.get("TEST_BOT");

        assertNotNull(testTopGeom);
        assertNotNull(testBotGeom);

        SwitchMatrixGeometry topMatrixGeom = testTopGeom.getSmGeometry();
        SwitchMatrixGeometry botMatrixGeom = testBotGeom.getSmGeometry();

        assertNotNull(topMatrixGeom);
        assertNotNull(botMatrixGeom);

        assertEquals("test_matrix", topMatrixGeom.getName());
        assertEquals(10, topMatrixGeom.getRelX());
        assertEquals(10, topMatrixGeom.getRelY());
        assertEquals(20, topMatrixGeom.getWidth());
        assertEquals(20, topMatrixGeom.getHeight());
    }

    @Test
    @DisplayName("Test Bels")
    void testBels() {
        Map<String, TileGeometry> tileGeomMap = geometry.getTileGeomMap();

        TileGeometry testBotGeom = tileGeomMap.get("TEST_BOT");
        assertNotNull(testBotGeom);

        BelGeometry belGeom = testBotGeom.getBelGeometryList().get(0);
        assertNotNull(belGeom);

        assertEquals("test_bel", belGeom.getName());
        assertEquals(34, belGeom.getRelX());
        assertEquals(10, belGeom.getRelY());
        assertEquals(4, belGeom.getWidth());
        assertEquals(8, belGeom.getHeight());
    }

    // TODO: Test ports?
}
