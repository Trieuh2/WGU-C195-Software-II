package Scheduler.View_Controller;

import helper.JDBC;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AddAppointmentController implements Initializable {

    // AnchorPane
    @FXML AnchorPane addApptAnchorPane;

    // TextFields/TextAreas
    @FXML TextField appointmentIDTextField;
    @FXML TextField titleTextField;
    @FXML TextArea descriptionTextArea;
    @FXML TextField locationTextField;
    @FXML TextField typeTextField;
    @FXML TextField customerTextField;
    @FXML TextField userTextField;

    // Menus
    @FXML MenuButton contactMenuButton;

    // Required fields to add an Appointment record in the DB
    private int appointmentID;
    private int selectedContactID;
    private int customerID;
    private int userID;
    private String selectedContactName;
    private String description;
    private String location;
    private String title;
    private String type;
    // start date and time
    // end date and time

    // Variable for tracking the user logged in
    private int loggedUserID;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        prepopulateAppointmentID();
        prepopulateContacts();
    }

    public AddAppointmentController(int loggedUserID) {
        this.loggedUserID = loggedUserID;
    }

    // DONE
    // Pre-populates the appointment ID for the new appointment being added
    private void prepopulateAppointmentID() {
        try {
            appointmentID = 1;

            // DB Query
            String query = "SELECT MAX(Appointment_ID) FROM appointments";
            Statement st = JDBC.connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            while(rs.next()) {
                appointmentID= rs.getInt(1);
            }

            appointmentID++;
            appointmentIDTextField.setText("" + appointmentID);
        }
        catch (SQLException e) {
            System.out.println("Error retrieving Appointment_IDs from the database");
        }
    }

    // TODO:
    // Pre-populates the options within the drop-down menu for contacts
    private void prepopulateContacts() {
        try {
            ArrayList<String> contactNames = new ArrayList<String>();

            // DB Query
            String query = "SELECT * FROM contacts";
            Statement st = JDBC.connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            // Add the country options to the ArrayList
            while(rs.next()) {
                // Create Menu Items for each Contact found in the DB
                int contactID = Integer.parseInt(rs.getString(1));
                MenuItem contactName = new MenuItem(rs.getString(2));

                // Create the onAction event for each of the Contacts for when a Contact is selected from the drop-down
                contactName.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        // Set the text of the menuButton to the selected item
                        contactMenuButton.setText(contactName.getText());

                        // Set the ID value to the selected contact's ID
                        selectedContactID = contactID;
                        selectedContactName = contactName.getText();

                        // Record the Contact ID
                    }
                });
                // Add the discovered contact to the menu
                contactMenuButton.getItems().add(contactName);
            }
        }
        catch(SQLException e) {
            System.out.println("Error fetching contact names from the database.");
        }
        System.out.println(selectedContactID);
    }

    // DONE
    // Closes current scene and switches back to the main controller
    @FXML
    private void returnToMainController() {
        try {
            // Load the FXML file.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Scheduler/View_Controller/MainController.fxml"));
            MainController controller = new MainController(loggedUserID);
            loader.setController(controller);
            Parent root = loader.load();

            // Close the current window and build the MainController scene to display the appointment calendar
            closeCurrentWindow();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        }
        catch (IOException e) {
            System.out.println("Error switching back to Main Controller.");
        }
    }

    // DONE
    // Closes the current window
    private void closeCurrentWindow() {
        Stage currentStage = (Stage)addApptAnchorPane.getScene().getWindow();
        currentStage.close();
    }
}
