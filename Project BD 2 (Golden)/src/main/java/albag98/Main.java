package albag98;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Main extends Application {

    public static EntityManagerFactory factory =
            Persistence.createEntityManagerFactory("MyUnit");


    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/LoginScreen.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("Login");
        primaryStage.setScene(new Scene(root, 500, 450));
        primaryStage.show();
    }


    public static void main(String[] args) {

        launch(args);



    }
}
