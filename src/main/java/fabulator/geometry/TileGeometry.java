package fabulator.geometry;

import fabulator.logging.LogManager;
import fabulator.logging.Logger;
import fabulator.object.Location;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A class for storing information about a tile of the
 * fabric. Objects of this class are contained in a
 * {@link FabricGeometry} object, which contains the
 * geometry information of the fabric.
 */
@Getter
@Setter
public class TileGeometry {

    /**
     * Name of the tile
     */
    private String name;

    /**
     * {@link SwitchMatrixGeometry} object of the switch
     * matrix of the tile.
     */
    private SwitchMatrixGeometry smGeometry;

    /**
     * List of all {@link BelGeometry} objects of the bels
     * of the tile.
     */
    private List<BelGeometry> belGeometryList = new ArrayList<>();

    /**
     * List of all {@link WireGeometry} objects of the wires
     * of the tile.
     */
    private List<WireGeometry> wireGeometryList = new ArrayList<>();

    /**
     * List of all {@link LowLodWiresGeometry} objects
     * generated from the {@link WireGeometry} objects as a
     * low LOD substitute.
     */
    private List<LowLodWiresGeometry> lowLodWiresGeoms = new ArrayList<>();

    /**
     * List of all {@link LowLodWiresGeometry} objects
     * which represent areas in which wires overlap. These
     * are rendered lighter to make the transition to
     * higher LOD smoother.
     */
    private List<LowLodWiresGeometry> lowLodOverlays = new ArrayList<>();

    /**
     * Width of the tile.
     */
    private double width;

    /**
     * Height of the tile
     */
    private double height;

    /**
     * Construct a {@link TileGeometry} object with the name
     * of the corresponding tile.
     *
     * @param name name of the tile
     */
    public TileGeometry(String name) {
        this.name = name;
    }

    /**
     * Generate all the {@link LowLodWiresGeometry} objects
     * from all the {@link WireGeometry} objects.
     */
    public void generateLowLodRouting() {
        int[][] wirePointsMat = new int[(int) this.width + 1][(int) this.height + 1];
        boolean[][] covered = new boolean[(int) this.width + 1][(int) this.height + 1];

        for (WireGeometry wireGeom : this.wireGeometryList) {
            List<Location> path = wireGeom.getPath();

            for (int pathCounter = path.size(); pathCounter >= 2; pathCounter--) {
                Location start = path.get(pathCounter - 1);
                Location end = path.get(pathCounter - 2);

                Location wirePoint = new Location(
                        Math.min(start.getX(), end.getX()),
                        Math.min(start.getY(), end.getY())
                );
                Location endPoint = new Location(
                        Math.max(start.getX(), end.getX()),
                        Math.max(start.getY(), end.getY())
                );

                int indexX = (int) wirePoint.getX();
                int indexY = (int) wirePoint.getY();

                if (indexX < 0 || indexY < 0 || endPoint.getX() > this.width || endPoint.getY() > this.height) {
                    Logger logger = LogManager.getLogger();
                    logger.error(
                            "Wire '" + wireGeom.getName() + "' in tile '" + this.name + "' "
                            + "exceeds tile bounds (width=" + this.width + ", height=" + this.height + "): "
                            + "segment from (" + start.getX() + ", " + start.getY() + ") "
                            + "to (" + end.getX() + ", " + end.getY() + "). "
                            + "This indicates a geometry input issue."
                    );
                    continue;
                }

                if (start.getX() == end.getX()) {
                    while (indexY <= endPoint.getY()) {
                        wirePointsMat[indexX][indexY]++;
                        wirePoint.setY(wirePoint.getY() + 1);
                        indexY = (int) wirePoint.getY();
                    }

                } else if (start.getY() == end.getY()) {
                    while (indexX <= endPoint.getX()) {
                        wirePointsMat[indexX][indexY]++;
                        wirePoint.setX(wirePoint.getX() + 1);
                        indexX = (int) wirePoint.getX();
                    }
                }
            }
        }

        int pointsThresh = 1;
        for (int x = 0; x <= this.width; x++) {
            for (int y = 0; y <= this.height; y++) {
                if (wirePointsMat[x][y] >= pointsThresh && !covered[x][y]) {
                    LowLodWiresGeometry lowLodWiresGeom = buildLowLodRect(
                            x, y, wirePointsMat, covered, pointsThresh
                    );
                    this.lowLodWiresGeoms.add(lowLodWiresGeom);
                }
            }
        }

        for (boolean[] arr : covered) {
            Arrays.fill(arr, false);
        }

        pointsThresh = 2;
        for (int x = 0; x <= this.width; x++) {
            for (int y = 0; y <= this.height; y++) {
                if (wirePointsMat[x][y] >= pointsThresh && !covered[x][y]) {
                    LowLodWiresGeometry lowLodOverlay = buildLowLodRect(
                            x, y, wirePointsMat, covered, pointsThresh
                    );
                    if (lowLodOverlay.significant()) {
                        this.lowLodOverlays.add(lowLodOverlay);
                    }
                }
            }
        }
    }

    private LowLodWiresGeometry buildLowLodRect(int topLeftX, int topLeftY, int[][] wirePointsMat, boolean[][] covered, int pointsThresh) {
        int currX = topLeftX;
        int currY = topLeftY;
        int botLeftY = topLeftY;

        while (currY <= this.getHeight() && wirePointsMat[currX][currY] >= pointsThresh) {
            botLeftY = currY;
            currY++;
        }

        int botRightX = topLeftX;
        currY = topLeftY;

        while (currX <= this.getWidth() && wirePointsMat[currX][currY] >= pointsThresh) {
            while (currY <= botLeftY && wirePointsMat[currX][currY] >= pointsThresh) {
                currY++;
            }
            if (currY >= botLeftY) {
                botRightX = currX;
                currY = topLeftY;
                currX++;
            } else {
                break;
            }
        }

        for (int x = topLeftX; x <= botRightX; x++) {
            for (int y = topLeftY; y <= botLeftY; y++) {
                covered[x][y] = true;
            }
        }

        LowLodWiresGeometry lowLodWiresGeom = new LowLodWiresGeometry(
                topLeftX,
                topLeftY,
                botRightX - topLeftX,
                botLeftY - topLeftY
        );
        return lowLodWiresGeom;
    }
}
