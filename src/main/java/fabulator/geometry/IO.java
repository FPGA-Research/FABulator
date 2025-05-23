package fabulator.geometry;

public enum IO {
    INPUT("INPUT"),
    OUTPUT("OUTPUT"),
    INOUT("INOUT"),
    NULL("NULL");

    private String identifier;

    IO(String identifier) {
        this.identifier = identifier;
    }

    public static IO fromIdentifier(String identifier) {
        IO io = switch (identifier) {
            case "INPUT" -> IO.INPUT;
            case "OUTPUT" -> IO.OUTPUT;
            case "INOUT" -> IO.INOUT;
            default -> IO.NULL;
        };
        return io;
    }
}
