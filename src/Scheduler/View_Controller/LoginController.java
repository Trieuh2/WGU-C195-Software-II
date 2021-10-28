/**
 * This class allows the user to authenticate against the database using a username and password combination on the form.
 *
 * @author Henry Trieu
 */

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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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

    /**
     * This method opens a connection to the database and sets the location/language based on the end-user's machine.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        JDBC.openConnection();
        setZoneAndLanguage();
    }

    /**
     * Sets the location and language of the application based on the end-user's machine. This method sets the language of
     * the text labels in the login page to the respective language. This currently supports French and English.
     */
    private void setZoneAndLanguage() {
        // Get the end-user's current zone and language settings
        ZoneId zoneId = ZoneId.systemDefault();
        Locale locale = Locale.getDefault();
        String location = zoneId.systemDefault().toString();
        String language = locale.getDefault().getDisplayLanguage();

        // Set language based on location
        ResourceBundle languageBundle = ResourceBundle.getBundle("Scheduler/View_Controller/Login", locale);

        // Update the text on the login form to the respective language associated with the end-user's machine
        loginTitleLabel.setText(languageBundle.getString("loginTitle"));
        usernameLabel.setText(languageBundle.getString("username"));
        passwordLabel.setText(languageBundle.getString("password"));
        locationLabel.setText(languageBundle.getString("location") + location);
        languageLabel.setText(languageBundle.getString("language") + language);
    }

    /**
     * Tests the inputted credentials to see if they are the correct credentials into the database
     *
     * @throws IOException to handle scenario of no value provided into TextFields
     */
    public void authorize() throws IOException {
        Locale locale = Locale.getDefault();
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
                    recordLoginActivity(true);
                    switchToMainController();
                }
                else {
                    recordLoginActivity(false);
                    exceptionLabel.setText(languageBundle.getString("invalidCredentialsException"));
                }
            }
            catch (SQLException e) {
                System.out.println("Error authorizing user against database.");
            }
        }
    }

    /**
     * Writes the login activity and result to the login_activity.txt file in the format:
     * [Timestamp] UserName:''- LOGIN_SUCCESS/LOGIN_FAIL
     *
     * @param successfulLogin determines whether the method will append 'LOGIN_SUCCESS' or 'LOGIN_FAIL'
     */
    private void recordLoginActivity(boolean successfulLogin) {
        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(new File("login_activity.txt"), true);

            // Pieces to the activity log
            String timestamp;
            String username;
            String loginResult;

            // Retrieve the current timestamp in UTC
            LocalDateTime now = LocalDateTime.now();
            ZonedDateTime utcTime = now.atZone(ZoneId.of("UTC"));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'['yyyy-MM-dd HH:mm:ss']'");
            timestamp = formatter.format(utcTime);

            // Record the username of the user logged in
            username = usernameTextField.getText();

            // Set the loginResult
            if(successfulLogin) {
                loginResult = "SUCCESSFUL LOGIN";
            }
            else {
                loginResult = "FAILED LOGIN";
            }

            // Concatenate the pieces of the login activity that will be recorded in the text file
            String record = timestamp + " UserName:'" + username + "' - " +loginResult + "\n";

            // Write to the text file
            fileOutputStream.write(record.getBytes(StandardCharsets.UTF_8));
        }
        catch(FileNotFoundException e) {
            System.out.println("login_activity.txt not found" + e);
        }
        catch(IOException e) {
            System.out.println("Exception writing to login_activity.txt " + e);
        }
        finally {
            // Close the file stream
            try {
                if(fileOutputStream != null) {
                    fileOutputStream.close();
                }
            }
            catch(IOException e) {
                System.out.println("Error closing filestream: " + e);
            }
        }
    }

    /**
     * Closes current scene and switches to the main controller
     */
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

    /**
     * Closes the current window. This method is called within returnToMainController() as a helper function.
     */
    private void closeCurrentWindow() {
        Stage currentStage = (Stage)loginPrimaryAnchorPane.getScene().getWindow();
        currentStage.close();
    }
}
