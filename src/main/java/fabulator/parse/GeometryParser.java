package fabulator.parse;

import fabulator.geometry.*;
import fabulator.object.Location;
import fabulator.object.Version;
import fabulator.util.StringUtils;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * A CSV parser used for parsing, or rather scanning, fabric geometry files.
 * Uses DFA-like scanning behavior.
 */
@Getter
public class GeometryParser {   //TODO: add more checks to make sure only valid files are being parsed, unite duplicate code

    private File file;
    private FabricGeometry geometry;

    enum ParsingMode {
        NONE,
        PARAMS,
        FABRIC_DEF,
        FABRIC_LOCS,
        TILE,
        SWITCH_MATRIX,
        BEL,
        SM_PORT,
        JUMP_PORT,
        BEL_PORT,
        WIRE
    }
    private ParsingMode parsingMode;

    private Version generatorVersion;

    private String name;
    private int numberOfRows;
    private int numberOfColumns;
    private int width;
    private int height;
    private int numberOfLines;

    List<List<String>> tileNames;
    List<List<Location>> tileLocations;
    Map<String, TileGeometry> tileGeomMap;

    private TileGeometry currentTileGeom;
    private SwitchMatrixGeometry currentSmGeom;
    private BelGeometry currentBelGeom;
    private PortGeometry currentSmPortGeom;
    private PortGeometry currentJumpPortGeom;
    private PortGeometry currentBelPortGeom;
    private WireGeometry currentWireGeom;
    private Location currentWireLoc;


    public GeometryParser(File file) {
        this.file = file;
        this.tileNames = new ArrayList<>();
        this.tileLocations = new ArrayList<>();
        this.tileGeomMap = new HashMap<>();

        try {
            this.parse();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        // TODO: check if it is necessary to also have these parameters here
        //  in addition to FabricGeometry
        this.geometry = new FabricGeometry(
                this.name,
                this.numberOfRows,
                this.numberOfColumns,
                this.width,
                this.height,
                this.numberOfLines,
                this.tileNames,
                this.tileLocations,
                this.tileGeomMap
        );
    }

    private void parse() throws IOException {
        this.parsingMode = ParsingMode.NONE;

        try (BufferedReader br = new BufferedReader(new FileReader(this.file, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                this.processLine(line);
            }
        }
    }

    void processLine(String line) {
        if (line.isEmpty()) return;

        String[] tokens = line.split(",");
        if (tokens.length == 0) return;

        String firstToken = tokens[0];
        switch (firstToken) {
            case "PARAMS"           -> { this.parsingMode = ParsingMode.PARAMS;         return; }
            case "FABRIC_DEF"       -> { this.parsingMode = ParsingMode.FABRIC_DEF;     return; }
            case "FABRIC_LOCS"      -> { this.parsingMode = ParsingMode.FABRIC_LOCS;    return; }
            case "TILE"             -> { this.parsingMode = ParsingMode.TILE;           return; }
            case "SWITCH_MATRIX"    -> { this.parsingMode = ParsingMode.SWITCH_MATRIX;  return; }
            case "BEL"              -> { this.parsingMode = ParsingMode.BEL;            return; }
            case "PORT"             -> { this.parsingMode = ParsingMode.SM_PORT;        return; }
            case "JUMP_PORT"        -> { this.parsingMode = ParsingMode.JUMP_PORT;      return; }
            case "BEL_PORT"         -> { this.parsingMode = ParsingMode.BEL_PORT;       return; }
            case "WIRE"             -> { this.parsingMode = ParsingMode.WIRE;           return; }
        }

        switch (this.parsingMode) {
            case PARAMS         -> this.parseAsParams(tokens, firstToken);
            case FABRIC_DEF     -> this.parseAsFabric(tokens);
            case FABRIC_LOCS    -> this.parseAsLocs(tokens);
            case TILE           -> this.parseAsTile(tokens, firstToken);
            case SWITCH_MATRIX  -> this.parseAsSwitchMatrix(tokens, firstToken);
            case BEL            -> this.parseAsBel(tokens, firstToken);
            case SM_PORT        -> this.parseAsSmPort(tokens, firstToken);
            case JUMP_PORT      -> this.parseAsJumpPort(tokens, firstToken);
            case BEL_PORT       -> this.parseAsBelPort(tokens, firstToken);
            case WIRE           -> this.parseAsWire(tokens, firstToken);
            case NONE           -> {}
        }
    }

    private void parseAsParams(String[] tokens, String attribute) {
        assert tokens.length == 2;

        switch (attribute) {
            case "GeneratorVersion" -> this.generatorVersion = new Version(tokens[1]);
            case "Name"             -> this.name = tokens[1];
            case "Rows"             -> this.numberOfRows = Integer.parseInt(tokens[1]);
            case "Columns"          -> this.numberOfColumns = Integer.parseInt(tokens[1]);
            case "Width"            -> this.width = Integer.parseInt(tokens[1]);
            case "Height"           -> this.height = Integer.parseInt(tokens[1]);
            case "Lines"            -> this.numberOfLines = Integer.parseInt(tokens[1]);
        }
    }

    private void parseAsFabric(String[] tokens) {
        this.tileNames.add(Arrays.asList(tokens));
    }

    private void parseAsLocs(String[] tokens) {
        List<Location> locRow = new ArrayList<>();
        for (String token : tokens) {
            if (!token.equals("Null")) {
                Location location = StringUtils.locFrom(token);
                locRow.add(location);
            } else {
                locRow.add(null);
            }
        }
        this.tileLocations.add(locRow);
    }

    private void parseAsTile(String[] tokens, String attribute) {
        assert tokens.length == 2;

        switch (attribute) {
            case "Name" -> {
                String name = tokens[1];
                this.currentTileGeom = new TileGeometry(name);
                this.tileGeomMap.put(name, this.currentTileGeom);
            }
            case "Width" -> {
                double width = Double.parseDouble(tokens[1]);
                this.currentTileGeom.setWidth(width);
            }
            case "Height" -> {
                double height = Double.parseDouble(tokens[1]);
                this.currentTileGeom.setHeight(height);
            }
        }
    }

    private void parseAsSwitchMatrix(String[] tokens, String attribute) {
        assert tokens.length == 2;

        switch (attribute) {
            case "Name" -> {
                String name = tokens[1];
                this.currentSmGeom = new SwitchMatrixGeometry(name);
                this.currentTileGeom.setSmGeometry(this.currentSmGeom);
            }
            case "RelX" -> {
                double relX = Double.parseDouble(tokens[1]);
                this.currentSmGeom.setRelX(relX);
            }
            case "RelY" -> {
                double relY = Double.parseDouble(tokens[1]);
                this.currentSmGeom.setRelY(relY);
            }
            case "Width" -> {
                double width = Double.parseDouble(tokens[1]);
                this.currentSmGeom.setWidth(width);
            }
            case "Height" -> {
                double height = Double.parseDouble(tokens[1]);
                this.currentSmGeom.setHeight(height);
            }
            case "Src" -> {
                String src = tokens[1];
                this.currentSmGeom.setSrc(src);
            }
            case "Csv" -> {
                String csv = tokens[1];
                this.currentSmGeom.setCsv(csv);
            }
        }
    }

    private void parseAsBel(String[] tokens, String attribute) {
        assert tokens.length == 2;

        switch (attribute) {
            case "Name" -> {
                String name = tokens[1];
                this.currentBelGeom = new BelGeometry(name);
                this.currentTileGeom.getBelGeometryList().add(this.currentBelGeom);
            }
            case "RelX" -> {
                double relX = Double.parseDouble(tokens[1]);
                this.currentBelGeom.setRelX(relX);
            }
            case "RelY" -> {
                double relY = Double.parseDouble(tokens[1]);
                this.currentBelGeom.setRelY(relY);
            }
            case "Width" -> {
                double width = Double.parseDouble(tokens[1]);
                this.currentBelGeom.setWidth(width);
            }
            case "Height" -> {
                double height = Double.parseDouble(tokens[1]);
                this.currentBelGeom.setHeight(height);
            }
            case "Src" -> {
                String src = tokens[1];
                this.currentBelGeom.setSrc(src);
            }
        }
    }

    private void parseAsSmPort(String[] tokens, String attribute) {
        assert tokens.length == 2;

        switch (attribute) {
            case "Name" -> {
                String name = tokens[1];
                this.currentSmPortGeom = new PortGeometry(name);
                this.currentSmGeom.getPortGeometryList().add(this.currentSmPortGeom);
            }
            case "Source" -> {
                String sourceName = tokens[1];
                this.currentSmPortGeom.setSourceName(sourceName);
            }
            case "Dest" -> {
                String destName = tokens[1];
                this.currentSmPortGeom.setDestName(destName);
            }
            case "IO" -> {
                IO io = IO.fromIdentifier(tokens[1]);
                this.currentSmPortGeom.setIo(io);
            }
            case "RelX" -> {
                double relX = Double.parseDouble(tokens[1]);
                this.currentSmPortGeom.setRelX(relX);
            }
            case "RelY" -> {
                double relY = Double.parseDouble(tokens[1]);
                this.currentSmPortGeom.setRelY(relY);
            }
        }
    }

    private void parseAsJumpPort(String[] tokens, String attribute) {
        assert tokens.length == 2;

        switch (attribute) {
            case "Name" -> {
                String name = tokens[1];
                this.currentJumpPortGeom = new PortGeometry(name);
                this.currentSmGeom.getJumpPortGeometryList().add(this.currentJumpPortGeom);
            }
            case "Source" -> {
                String sourceName = tokens[1];
                this.currentJumpPortGeom.setSourceName(sourceName);
            }
            case "Dest" -> {
                String destName = tokens[1];
                this.currentJumpPortGeom.setDestName(destName);
            }
            case "IO" -> {
                IO io = IO.fromIdentifier(tokens[1]);
                this.currentJumpPortGeom.setIo(io);
            }
            case "RelX" -> {
                double relX = Double.parseDouble(tokens[1]);
                this.currentJumpPortGeom.setRelX(relX);
            }
            case "RelY" -> {
                double relY = Double.parseDouble(tokens[1]);
                this.currentJumpPortGeom.setRelY(relY);
            }
        }
    }

    private void parseAsBelPort(String[] tokens, String attribute) {
        assert tokens.length == 2;

        switch (attribute) {
            case "Name" -> {
                String name = tokens[1];
                this.currentBelPortGeom = new PortGeometry(name);
                this.currentBelGeom.getPortGeometryList().add(this.currentBelPortGeom);
            }
            case "Source" -> {
                String sourceName = tokens[1];
                this.currentBelPortGeom.setSourceName(sourceName);
            }
            case "Dest" -> {
                String destName = tokens[1];
                this.currentBelPortGeom.setDestName(destName);
            }
            case "IO" -> {
                IO io = IO.fromIdentifier(tokens[1]);
                this.currentBelPortGeom.setIo(io);
            }
            case "RelX" -> {
                double relX = Double.parseDouble(tokens[1]);
                this.currentBelPortGeom.setRelX(relX);
            }
            case "RelY" -> {
                double relY = Double.parseDouble(tokens[1]);
                this.currentBelPortGeom.setRelY(relY);
            }
        }
    }

    private void parseAsWire(String[] tokens, String attribute) {
        assert tokens.length == 2;

        switch (attribute) {
            case "Name" -> {
                String name = tokens[1];
                this.currentWireGeom = new WireGeometry(name);
                this.currentTileGeom.getWireGeometryList().add(this.currentWireGeom);
            }
            case "RelX" -> {
                double relX = Double.parseDouble(tokens[1]);
                this.currentWireLoc = new Location();
                this.currentWireGeom.getPath().add(this.currentWireLoc);
                this.currentWireLoc.setX(relX);
            }
            case "RelY" -> {
                double relY = Double.parseDouble(tokens[1]);
                this.currentWireLoc.setY(relY);
            }
        }
    }
}

