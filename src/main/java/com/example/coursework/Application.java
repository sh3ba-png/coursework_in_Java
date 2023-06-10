package com.example.coursework;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Поиск дубликатов файлов");
        stage.getIcons().add(new Image("https://schtirlitz.ru/800/600/https/shmector.com/_ph/18/432140153.png"));
//        stage.getIcons().add(new Image(""));
//        stage.getIcons().add(new Image("C:\\Users\\khleb\\IdeaProjects\\coursework\\src\\main\\java\\com\\example\\coursework\\432140153.png"));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}