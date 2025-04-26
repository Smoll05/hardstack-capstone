module com.example.joeandmarie {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.almasb.fxgl.all;
    requires jdk.unsupported.desktop;

    opens com.example.joeandmarie to javafx.fxml;
    opens assets.textures;
    opens assets.sounds;
    opens assets.levels;
    exports com.example.joeandmarie;
    exports com.example.joeandmarie.entity;
    opens com.example.joeandmarie.entity to javafx.fxml;
       exports com.example.joeandmarie.component;
    opens com.example.joeandmarie.component to javafx.fxml;
    exports com.example.joeandmarie.factory;
    opens com.example.joeandmarie.factory to javafx.fxml;
}