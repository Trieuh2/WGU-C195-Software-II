package Scheduler.View_Controller;

import helper.JDBC;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZoneId;
import java.util.Locale;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    // Primary Anchor Pane
    @FXML private AnchorPane loginPrimaryAnchorPane;

    // Label FXML variables
    @FXML private Label loginTitleLabel;
    @FXML private Label usernameLabel;
    @FXML private Label passwordLabel;
    @FXML private Label locationLabel;
    @FXML private Label languageLabel;
    @FXML private Label exceptionLabel;

    // TextField/PasswordField FXML variables
    @FXML private TextField usernameTextField;
    @FXML private PasswordField passwordTextField;

    // Variable for tracking the user logged in
    private int loggedUserID;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        JDBC.openConnection();

        ZoneId zoneId = ZoneId.systemDefault();
        Locale locale = Locale.getDefault();

        // PRODUCTION CODE
        String location = zoneId.systemDefault().toString();
        String language = locale.getDefault().getDisplayLanguage();

        // TODO: TEST FRENCH AND REMOVE ON FINAL COMMIT
        // Locale locale = Locale.FRANCE;
        // String location = "France";
        // String language = "French";

        // Set language based on location
        ResourceBundle languageBundle = ResourceBundle.getBundle("Scheduler/View_Controller/Login", locale);

        loginTitleLabel.setText(languageBundle.getString("loginTitle"));
        usernameLabel.setText(languageBundle.getString("username"));
        passwordLabel.setText(languageBundle.getString("password"));
        locationLabel.setText(languageBundle.getString("location") + location);
        languageLabel.setText(languageBundle.getString("language") + language);

        // TODO: Remove on final commit
        usernameTextField.setText("test");
        passwordTextField.setText("test");
    }

    // Tests the inputted credentials to see if they are the correct credentials into the database
    public void authorize() throws IOException {
        Locale locale = Locale.getDefault();

        // TODO: TEST FRENCH AND REMOVE ON FINAL COMMIT
        // Testing French
        // Locale locale = Locale.FRANCE;

        ResourceBundle languageBundle = ResourceBundle.getBundle("Scheduler/View_Controller/Login", locale);

        // Check if one of the username or password fields are empty
        if(usernameTextField.getText().isEmpty() || passwordTextField.getText().isEmpty()) {
            exceptionLabel.setText(languageBundle.getString("emptyCredentialsException"));
        }
        // Attempt authorization
        else {
            try{
                // Check if the inputted credentials belong to a user within the database
                String query = "SELECT * FROM users WHERE User_Name = '" + usernameTextField.getText() +
                                                    "' AND Password = '" + passwordTextField.getText() + "'";
                Statement st = JDBC.connection.createStatement();
                ResultSet rs = st.executeQuery(query);

                // If creds are correct, record the user that is logging in and move to main controller
                if(rs.next()) {
                    loggedUserID = rs.getInt(1);
                    switchToMainController();
                }
                else {
                    exceptionLabel.setText(languageBundle.getString("invalidCredentialsException"));
                }
            }
            catch (SQLException e) {
                System.out.println("Error authorizing user against database.");
            }
        }
    }

    // Closes the login screen and switches to MainController where the appointment calendar is displayed
    private void switchToMainController() throws IOException {
        // Load the FXML file.
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Scheduler/View_Controller/MainController.fxml"));
        MainController controller = new MainController(loggedUserID, true);
        loader.setController(controller);
        Parent root = loader.load();

        // Close the current window and build the MainController scene to display the appointment calendar
        closeCurrentWindow();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
    }

    // Closes the current window
    private void closeCurrentWindow() {
        Stage currentStage = (Stage)loginPrimaryAnchorPane.getScene().getWindow();
        currentStage.close();
    }
}
