package fabulator.ui.view;

public interface View {

    /**
     * Initializes a View object. Usually called at
     * Construction. May be re-called if the object is
     * re-initialized with new data.
     */
    default void init() {
        this.buildParts();
        this.buildWhole();
    }

    /**
     * Builds the parts that make up the View object.
     */
    void buildParts();

    /**
     * Builds the View object itself.
     */
    void buildWhole();
}
