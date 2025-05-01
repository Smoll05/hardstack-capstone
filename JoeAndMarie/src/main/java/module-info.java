module com.example.joeandmarie {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.almasb.fxgl.all;

    requires java.desktop;
    requires javafx.media;
    requires jdk.compiler;

    opens com.example.joeandmarie to javafx.fxml;  // Allows FXMLLoader to access the main package
    opens com.example.joeandmarie.Controller to javafx.fxml;  // Add this line to open the Controller package

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

    exports com.example.joeandmarie.Starting;
    opens com.example.joeandmarie.Starting to javafx.fxml;
}