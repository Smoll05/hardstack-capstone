module com.example.joeandmarie {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.almasb.fxgl.all;
    requires java.desktop;

    opens com.example.joeandmarie to javafx.fxml;
    opens assets.textures;
    opens assets.sounds;
    exports com.example.joeandmarie;
}