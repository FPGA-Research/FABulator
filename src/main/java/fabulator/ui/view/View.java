package fabulator.ui.view;

public interface View {

    default void init() {
        this.buildParts();
        this.buildWhole();
    }

    void buildParts();

    void buildWhole();
}
