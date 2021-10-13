package Scheduler.View_Controller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    // Primary AnchorPane on the Main Controller
    @FXML private AnchorPane mainAnchorPane;

    // Buttons
    @FXML private Button addCustomerButton;
    @FXML private Button addAppointmentButton;

    // Variable for tracking the user logged in
    private int loggedUserID;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public MainController(int loggedUserID) {
        this.loggedUserID = loggedUserID;
    }

    // Closes the current window
    private void closeCurrentWindow() {
        Stage currentStage = (Stage)mainAnchorPane.getScene().getWindow();
        currentStage.close();
    }

    // Closes the main page and switches to the controller where the user is prompted to fill out information to
    // add a new user.
    @FXML
    private void switchToAddCustomerController() throws IOException {
        // Load the FXML file.
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Scheduler/View_Controller/AddCustomerController.fxml"));
        AddCustomerController controller = new AddCustomerController(loggedUserID);
        loader.setController(controller);
        Parent root = loader.load();

        // Close the current window and build the MainController scene to display the appointment calendar
        closeCurrentWindow();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
    }

    // Closes the main page and switches to the controller where the user can view all the customers
    @FXML
    private void switchToViewCustomerController() throws IOException {
        // Load the FXML file.
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Scheduler/View_Controller/ViewCustomerController.fxml"));
        ViewCustomerController controller = new ViewCustomerController(loggedUserID);
        loader.setController(controller);
        Parent root = loader.load();

        // Close the current window and build the MainController scene to display the appointment calendar
        closeCurrentWindow();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
    }

    // Closes the main page and switches to the controller where the user can add a new appointment
    @FXML
    private void switchToAddAppointmentController() throws IOException {
        // Load the FXML file.
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Scheduler/View_Controller/AddAppointmentController.fxml"));
        // TODO: UPDATE
        AddAppointmentController controller = new AddAppointmentController(loggedUserID);
        loader.setController(controller);
        Parent root = loader.load();

        // Close the current window and build the MainController scene to display the appointment calendar
        closeCurrentWindow();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
    }
}
