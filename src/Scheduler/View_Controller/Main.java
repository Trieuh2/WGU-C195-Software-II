package Scheduler.View_Controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    // The start of the page is the Login page
    @Override
    public void start(Stage primaryStage) throws Exception{

        //Load the FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Scheduler/View_Controller/LoginController.fxml"));
        Parent root = loader.load();

        // Build the scene and display the window
        primaryStage.setTitle("Scheduler");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main (String[] args) {
        launch(args);
    }
}
