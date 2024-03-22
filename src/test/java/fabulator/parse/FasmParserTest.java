package fabulator.parse;

import fabulator.lookup.BitstreamConfiguration;
import fabulator.lookup.Net;
import fabulator.object.DiscreteLocation;
import javafx.util.Pair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

public class FasmParserTest {

    private static BitstreamConfiguration config;

    @BeforeAll
    @DisplayName("Test parsing fasm file")
    public static void testParsingFasmFile() {
        URL fasmSourceUrl = FasmParserTest.class.getResource("/parse/test_user_design.fasm");
        assertNotNull(fasmSourceUrl);

        String fileName = fasmSourceUrl.getFile();
        FasmParser parser = new FasmParser(fileName);

        config = parser.getConfig();
    }

    @Test
    @DisplayName("Test FasmParser correctness")
    public void testFasmParserCorrectness() {
        Net n1 = config.getNetMap().get("test_net_1");
        Net n2 = config.getNetMap().get("test_net_2");
        Net n3 = config.getNetMap().get("test_net_3");

        assertEquals(4, n1.getEntries().size());
        assertEquals(0, n2.getEntries().size());
        assertEquals(2, n3.getEntries().size());

        Pair<DiscreteLocation, BitstreamConfiguration.ConnectedPorts> firstEntry;
        firstEntry = n1.getEntries().get(0);

        DiscreteLocation firstLocation = firstEntry.getKey();
        String portA = firstEntry.getValue().getPortA();
        String portB = firstEntry.getValue().getPortB();

        assertEquals(0, firstLocation.getX());
        assertEquals(1, firstLocation.getY());
        assertEquals("test_port_1", portA);
        assertEquals("test_port_2", portB);
    }
}
