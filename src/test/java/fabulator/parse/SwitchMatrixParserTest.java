package fabulator.parse;

import fabulator.ui.fabric.port.SmPort;
import fabulator.geometry.IO;
import fabulator.geometry.PortGeometry;
import fabulator.lookup.SmConnectivity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import testutil.TestUtils;

import java.net.URL;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SwitchMatrixParserTest {

    private static SmConnectivity smConnectivity;

    @BeforeAll
    @DisplayName("Test Parsing")
    public static void testMatrixParsing() {
        TestUtils.initializeJavaFX();

        URL matrixSourceUrl = SwitchMatrixParserTest.class.getResource("/parse/test_matrix.csv");
        assertNotNull(matrixSourceUrl);

        String fileName = matrixSourceUrl.getFile();
        SwitchMatrixParser parser = new SwitchMatrixParser(fileName);

        smConnectivity = parser.getConnection();
    }

    // TODO: finish
    @Test
    @DisplayName("Test Port Lookup")
    void testPortLookup() {
        PortGeometry portGeom = new PortGeometry("N1END0");
        portGeom.setIo(IO.INOUT);
        SmPort port1 = new SmPort(portGeom, null);

        String[] namesExpected = {
                "NN4BEG2",
                "EE4BEG2",
                "SS4BEG2",
                "WW4BEG2",
                "JN2BEG7",
                "JE2BEG1",
                "JE2BEG7",
                "JS2BEG7",
                "JW2BEG1",
                "JW2BEG7",
        };
        Map<String, Boolean> names = smConnectivity.connectedNamesOf(port1);

        assertEquals(namesExpected.length, names.size());

        for (String expectedName : namesExpected) {
            assertTrue(names.containsKey(expectedName));
        }
    }
}
