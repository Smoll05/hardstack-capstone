package com.example.joeandmarie;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
public class JoeMainMenu extends FXGLMenu {

    public JoeMainMenu() {
        super(MenuType.MAIN_MENU);

        // Background Image
        var bg = new Rectangle(getAppWidth(), getAppHeight());
        Image imagebg = FXGL.image("JOEandMarie.jpg");
        bg.setFill(new ImagePattern(imagebg));


        var rectangle = new Rectangle(getAppWidth() / 2.5, getAppHeight());
        rectangle.setFill(Color.rgb(0, 0, 0, 0.6));

        Font customFont = Font.loadFont(
                getClass().getResourceAsStream("/assets/fonts/headliner.ttf"), 48
        );

        var title = new Text("The Adventure of Joe and Marie");
        title.setFont(customFont != null ? customFont : Font.font(48));
        title.setFill(Color.WHITE);

       var btnStart = new Button("Start Game");
        btnStart.setFont(customFont != null ? customFont : Font.font(28));
        btnStart.setOnAction(e -> fireNewGame());

        var btnExit = new Button("Exit");
        btnExit.setFont(customFont != null ? customFont : Font.font(28));
        btnExit.setOnAction(e -> fireExit());


        var box = new VBox(20, title, btnStart, btnExit);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setTranslateX(60);
        box.setTranslateY(getAppHeight() / 3.0);


        getContentRoot().getChildren().addAll(bg, rectangle, box);
    }
}
