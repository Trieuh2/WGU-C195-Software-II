package Scheduler.View_Controller;

import Model.Appointment;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class ContactScheduleReport implements Initializable {
    @FXML AnchorPane mainAnchorPane;

    // TableColumn variables
    @FXML TableColumn appointmentIdColumn;
    @FXML TableColumn titleColumn;
    @FXML TableColumn typeColumn;
    @FXML TableColumn descriptionColumn;
    @FXML TableColumn startTimeColumn;
    @FXML TableColumn endTimeColumn;
    @FXML TableColumn customerIdColumn;

    // Button variables
    @FXML MenuButton contactMenuButton;

    // TableView variables
    @FXML TableView appointmentTableView;

    // Variable for tracking contact selected
    private int selectedContactID;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setCellFactoryValues();
        loadContacts();
    }

    public ContactScheduleReport() {

    }

    // Sets the column names and accepted property in the column
    private void setCellFactoryValues() {
        appointmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        startTimeColumn.setCellValueFactory(new PropertyValueFactory<>("localStartTimestamp"));
        endTimeColumn.setCellValueFactory(new PropertyValueFactory<>("localEndTimestamp"));
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));
    }

    // Loads the selectable Contacts that the report will be ran on
    private void loadContacts() {
        try {
            // DB Query
            String query = "SELECT * FROM contacts ORDER BY Contact_Name ASC";
            Statement st = JDBC.connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            // Add the country options to the ArrayList
            while(rs.next()) {
                // Create Menu Items for each Contact found in the DB
                int contactID = Integer.parseInt(rs.getString(1));
                MenuItem contactMenuItem = new MenuItem(rs.getString(2));

                // Set the onAction event for each of Contact on the menu
                contactMenuItem.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        // Clear out the current Appointments in the TableView
                        appointmentTableView.getItems().clear();

                        // Set the text of the menuButton to the selected item
                        contactMenuButton.setText(contactMenuItem.getText());
                        selectedContactID = contactID;

                        // Load the appointment list associated with the contact
                        loadAppointmentSchedule();
                    }
                });
                // Add the discovered contact to the menu
                contactMenuButton.getItems().add(contactMenuItem);
            }
        }
        catch(SQLException e) {
            System.out.println("Error Contacts from the database.");
        }
    }

    // Loads the Appointment schedule associated with the contact selected
    private void loadAppointmentSchedule() {
        try {
            // DB Query
            String query = "SELECT * FROM appointments WHERE Contact_ID = " + selectedContactID + " ORDER BY Start ASC";
            Statement st = JDBC.connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            while(rs.next()) {
                // Parse the timestamps from the DB and add it to the table
                // Appointment details
                int appointmentID = rs.getInt("Appointment_ID");
                String title = rs.getString("Title");
                String description = rs.getString("Description");
                String location = rs.getString("Location");
                String type = rs.getString("Type");
                String utcStartTimestamp = rs.getString("Start");
                String utcEndTimestamp = rs.getString("End");
                int customerID = rs.getInt("Customer_ID");
                int userID = rs.getInt("User_ID");
                int contactID = rs.getInt("Contact_ID");

                Appointment tempAppointment = new Appointment(appointmentID, title, description, location,
                        type, utcStartTimestamp, utcEndTimestamp, customerID, userID, contactID);

                appointmentTableView.getItems().add(tempAppointment);
            }
        }
        catch(SQLException e) {
            System.out.println("There was an error retrieving Contact information from the database.");
        }
    }
}
