module FABulator {
    requires lombok;
    requires java.desktop;
    requires javafx.graphics;
    requires javafx.controls;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.materialdesign2;
    requires org.fxmisc.richtext;
    requires org.fxmisc.flowless;
    requires org.fxmisc.undo;
    requires org.apache.logging.log4j;
    requires org.controlsfx.controls;

    opens fabulator to javafx.graphics, javafx.controls;
    exports fabulator;
}