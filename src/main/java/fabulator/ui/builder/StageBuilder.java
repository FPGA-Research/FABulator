package fabulator.ui.builder;

import fabulator.language.Text;
import fabulator.ui.icon.ImageIcon;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Builder;

public class StageBuilder implements Builder<Stage> {

    private Stage stage;

    public StageBuilder() {
        this.stage = new Stage();
    }

    public StageBuilder setTitle(Text text) {
        this.stage.titleProperty().bind(text.stringProperty());
        return this;
    }

    public StageBuilder setResizable(boolean resizable) {
        this.stage.setResizable(resizable);
        return this;
    }

    public StageBuilder initModality(Modality modality) {
        this.stage.initModality(modality);
        return this;
    }

    public StageBuilder setIcon(ImageIcon icon) {
        this.stage.getIcons().add(icon.getImage());
        return this;
    }

    @Override
    public Stage build() {
        return this.stage;
    }
}
