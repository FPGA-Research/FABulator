package fabulator.geometry;

public enum Side {
    NORTH("NORTH"),
    SOUTH("SOUTH"),
    EAST("EAST"),
    WEST("WEST"),
    ANY("ANY");

    private String identifier;

    Side(String identifier) {
        this.identifier = identifier;
    }

    public static Side fromIdentifier(String identifier) {
        Side side = switch (identifier) {
            case "NORTH" -> Side.NORTH;
            case "SOUTH" -> Side.SOUTH;
            case "EAST" -> Side.EAST;
            case "WEST" -> Side.WEST;
            case default -> Side.ANY;
        };
        return side;
    }
}
