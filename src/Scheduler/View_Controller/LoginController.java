package Scheduler.View_Controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.net.URL;
import java.sql.*;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    // Generated and taken from WGU database generation
    // TODO: Remove creds on final commit
    private final String connectionURL = "jdbc:mysql://wgudb.ucertify.com:3306";
    private final String databaseName = "WJ07jtq";
    private final String username = "U07jtq";
    private final String password = "53689048171";

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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
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
    }

    // Tests the inputted credentials to see if they are the correct credentials into the database
    public void authorize() {
        Locale locale = Locale.getDefault();

        // TODO: TEST FRENCH AND REMOVE ON FINAL COMMIT
        // Testing French
        // Locale locale = Locale.FRANCE;

        ResourceBundle languageBundle = ResourceBundle.getBundle("Scheduler/View_Controller/Login", locale);

        // Check if one of the username or password fields are empty
        if(usernameTextField.getText().isEmpty() || passwordTextField.getText().isEmpty()) {
            exceptionLabel.setText(languageBundle.getString("emptyCredentialsException"));
        }
        // Authorize
        else{
            try {
                Connection conn = DriverManager.getConnection(
                        connectionURL,
                        usernameTextField.getText(),
                        passwordTextField.getText());

                // TODO: Transition to next controller
                if(conn != null) {
                    System.out.println("Connection to MySQL Database was successful.");
                }
            } catch (SQLException ex) {
                exceptionLabel.setText(languageBundle.getString("invalidCredentialsException"));
            }
        }
    }
}
