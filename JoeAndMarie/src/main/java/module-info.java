module com.example.joeandmarie {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.almasb.fxgl.all;
    requires java.desktop;
    requires javafx.media;

    opens com.example.joeandmarie to javafx.fxml;  // Allows FXMLLoader to access the main package
    opens com.example.joeandmarie.Controller to javafx.fxml;  // Add this line to open the Controller package

    opens assets.textures;
    opens assets.sounds;
    exports com.example.joeandmarie;
    exports com.example.joeandmarie.Starting;
    opens com.example.joeandmarie.Starting to javafx.fxml;
}
