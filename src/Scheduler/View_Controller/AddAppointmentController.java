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
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.TimeZone;

public class AddAppointmentController implements Initializable {

    // AnchorPane
    @FXML AnchorPane addApptAnchorPane;

    // TextFields/TextAreas
    @FXML TextField appointmentIDTextField;
    @FXML TextField titleTextField;
    @FXML TextArea descriptionTextArea;
    @FXML TextField locationTextField;
    @FXML TextField typeTextField;

    // Menus
    @FXML MenuButton customerMenuButton;
    @FXML MenuButton contactMenuButton;
    @FXML MenuButton startTimeMenuButton;
    @FXML MenuButton endTimeMenuButton;

    @FXML DatePicker datePicker;


    // Appointment object that is going to be added
    private Appointment newAppointment;

    // Start Date and Time fields
    private int startHour = -1;
    private int startMin = -1;

    // End Date and Time fields
    private int endHour = -1;
    private int endMin = -1;

    // Variables for checking if a value has been provided
    private boolean customerSelected;
    private boolean contactSelected;

    // Variable for tracking the user logged in
    private final int loggedUserID;
    private String loggedUsername;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        newAppointment = new Appointment();
        newAppointment.setUserID(loggedUserID);
        loggedUsername = getLoggedUsername();

        // Prepopulate the form options
        preSelectCurrentDate();
        generateAppointmentID();
        loadCustomers();
        loadContacts();
        loadTimeOptions();
    }

    public AddAppointmentController(int loggedUserID) {
        this.loggedUserID = loggedUserID;
    }

    // Default select the today's date for the start and end of the Appointment
    private void preSelectCurrentDate() {
        datePicker.setValue(LocalDate.now());
    }

    // Pre-populates the appointment ID for the new appointment being added
    private void generateAppointmentID() {
        try {
            int maxAppointmentID = 1;

            // DB Query
            String query = "SELECT MAX(Appointment_ID) FROM appointments";
            Statement st = JDBC.connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            while(rs.next()) {
                maxAppointmentID= rs.getInt(1);
            }

            newAppointment.setAppointmentID(++maxAppointmentID);
            appointmentIDTextField.setText("" + maxAppointmentID);
        }
        catch (SQLException e) {
            System.out.println("Error retrieving Appointment_IDs from the database");
        }
    }

    // Pre-populates the options within the drop-down menu for customers
    private void loadCustomers() {
        try {
            // DB Query
            String query = "SELECT * FROM customers ORDER BY Customer_Name ASC";
            Statement st = JDBC.connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            while(rs.next()) {
                // Create menu items for each customer
                int customerID = Integer.parseInt(rs.getString(1));
                MenuItem customerMenuItem = new MenuItem(rs.getString(2));

                // Set the onAction event for each Customer on the menu
                customerMenuItem.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        // Set the text of the menuButton to the selected item
                        customerMenuButton.setText(customerMenuItem.getText());

                        // Record the selected Contact for the appointment
                        newAppointment.setCustomerID(customerID);

                        // Flag that a customer has been selected
                        customerSelected = true;
                    }
                });
                customerMenuButton.getItems().add(customerMenuItem);
            }
        }
        catch(SQLException e) {
            System.out.println("Error fetching customers from the Database.");
        }
    }

    // Pre-populates the options within the drop-down menu for contacts
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
                        // Set the text of the menuButton to the selected item
                        contactMenuButton.setText(contactMenuItem.getText());

                        // Record the selected Contact for the appointment
                        newAppointment.setContactID(contactID);

                        // Flag that a contact has been selected
                        contactSelected = true;
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

    // Pre-populate the time options
    private void loadTimeOptions() {
        // Populate Time options
        String amPM = "AM";
        int hourCounted = 0;
        int hourDisplay = 12;

        while(hourCounted != 24) {
            // Logic for displaying the correct hour
            if(hourCounted == 1) {
                hourDisplay = 1;
            }
            else if(hourCounted == 13) {
                hourDisplay = 1;
            }

            if(hourCounted == 12) {
                amPM = "PM";
            }

            // Logic for recording the correct hour to the DB
            final int finalHourCounted = hourCounted;

            // StartTime Menu Options
            MenuItem hourSharpStart = new MenuItem(hourDisplay + ":00 " + amPM);
            hourSharpStart.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    startTimeMenuButton.setText(hourSharpStart.getText());
                    startHour = finalHourCounted;
                    startMin = 0;
                }
            });

            MenuItem hourThirtyStart = new MenuItem(hourDisplay + ":30 " + amPM);
            hourThirtyStart.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    startTimeMenuButton.setText(hourThirtyStart.getText());
                    startHour = finalHourCounted;
                    startMin = 30;
                }
            });

            startTimeMenuButton.getItems().add(hourSharpStart);
            startTimeMenuButton.getItems().add(hourThirtyStart);

            // EndTime Menu Options
            MenuItem hourSharpEnd = new MenuItem(hourDisplay + ":00 " + amPM);
            hourSharpEnd.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    endTimeMenuButton.setText(hourSharpEnd.getText());
                    endHour = finalHourCounted;
                    endMin = 0;
                }
            });

            MenuItem hourThirtyEnd = new MenuItem(hourDisplay + ":30 " + amPM);
            hourThirtyEnd.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    endTimeMenuButton.setText(hourThirtyEnd.getText());
                    endHour = finalHourCounted;
                    endMin = 30;
                }
            });

            endTimeMenuButton.getItems().add(hourSharpEnd);
            endTimeMenuButton.getItems().add(hourThirtyEnd);

            // Increment loop variables
            hourCounted++;
            hourDisplay++;
        }
    }

    // Adds the appointment to the Database
    @FXML
    private void addAppointment() {
        // Check to ensure that all fields on the form are filled
        if(allFieldsFilled()) {
            parseFormFields();
            setAuditTimestamps();

            // Checks to make sure that the startTime is not in the past
            if(newAppointment.startTimeInFuture()) {
                // Checks to make sure that the endTime is after the startTime
                if(newAppointment.endTimeAfterStartTime()) {
                    // Checks to make sure that the startTime and endTime are within business hours
                    if (newAppointment.isWithinBusinessHours()) {
                        if(!newAppointment.customerOverlappingAppt()) {
                            // Add Appointment to DB
                            try {
                                // DB Query for adding Appointment
                                String update = "INSERT INTO appointments VALUES (" + newAppointment.getAppointmentID() + ", '"
                                        + newAppointment.getTitle() + "', '"
                                        + newAppointment.getDescription() + "', '"
                                        + newAppointment.getLocation() + "', '"
                                        + newAppointment.getType() + "', '"
                                        + newAppointment.getUtcStartTimestamp() + "', '"
                                        + newAppointment.getUtcEndTimestamp() + "', '"
                                        + newAppointment.getCreateDate() + "', '"
                                        + newAppointment.getCreatedBy() + "', '"
                                        + newAppointment.getLastUpdate() + "', '"
                                        + newAppointment.getLastUpdatedBy() + "', '"
                                        + newAppointment.getCustomerID() + "', '"
                                        + newAppointment.getUserID() + "', "
                                        + newAppointment.getContactID() + ")";
                                Statement st = JDBC.connection.createStatement();
                                st.executeUpdate(update);

                                returnToMainController();
                            }
                            catch(SQLException e) {
                                System.out.println("Error adding Appointment to database.");
                            }
                        }
                        else {
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("Error");
                            alert.setContentText("The requested appointment time are conflicting with the customer's appointment schedule.\n\nPlease provide a new time.");
                            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                            stage.setAlwaysOnTop(true);
                            alert.show();
                        }
                    }
                    else {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Error");
                        alert.setContentText("The appointment start and end times must occur within the business hours of 8AM - 10PM EST within the same day.");
                        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                        stage.setAlwaysOnTop(true);
                        alert.show();
                    }
                }
                else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Error");
                    alert.setContentText("The end time of the appointment must occur after the start time.");
                    Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                    stage.setAlwaysOnTop(true);
                    alert.show();
                }
            }
            else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setContentText("The start time of the appointment must occur in the future.");
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.setAlwaysOnTop(true);
                alert.show();
            }
        }
        // Pop a dialogue error indicating that there is a field with missing values
        else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setContentText("All fields must have a value.");
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.setAlwaysOnTop(true);
            alert.show();
        }
    }

    // Checks to see if all the fields on the form has been filled
    private boolean allFieldsFilled() {
        if(!titleTextField.getText().isEmpty() && !descriptionTextArea.getText().isEmpty() &&
                !locationTextField.getText().isEmpty() && !typeTextField.getText().isEmpty() &&
                (customerSelected) && (contactSelected) && (datePicker.getValue().toString() != null) &&
                (startHour >= 0) && (startMin >= 0) && (endHour >= 0) && (endMin >= 0)) {
            return true;
        }
        else {
            return false;
        }
    }


    // Take values from the fields on the form and assign it to the Appointment object
    private void parseFormFields() {
        int year = datePicker.getValue().getYear();
        int month = datePicker.getValue().getMonth().getValue();
        int day = datePicker.getValue().getDayOfMonth();

        newAppointment.setTitle(titleTextField.getText());
        newAppointment.setDescription(descriptionTextArea.getText());
        newAppointment.setLocation(locationTextField.getText());
        newAppointment.setType(typeTextField.getText());
        newAppointment.setStartZDTs(year, month, day, startHour, startMin, 0);
        newAppointment.setEndZDTs(year, month, day, endHour, endMin, 0);
    }

    // Sets the 'created' and 'updated' fields in the Appointment object
    private void setAuditTimestamps() {
        SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        newAppointment.setCreateDate(utcFormat.format(new Date()));
        newAppointment.setCreatedBy(loggedUsername);
        newAppointment.setLastUpdate(utcFormat.format(new Date()));
        newAppointment.setLastUpdatedBy(loggedUsername);
    }

    // Retrieves the username of the currently logged-in user for record adding purposes
    private String getLoggedUsername() {
        String username = "";

        try {
            String query = "SELECT User_Name, USER_ID FROM users WHERE User_ID = " + loggedUserID;
            Statement st = JDBC.connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            while(rs.next()) {
                username = rs.getString(1);
            }
        }
        catch(SQLException e) {
            System.out.println("Error retrieving logged in user's information.");
        }

        return username;
    }

    // Closes current scene and switches back to the main controller
    @FXML
    private void returnToMainController() {
        try {
            // Load the FXML file.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Scheduler/View_Controller/MainController.fxml"));
            MainController controller = new MainController(loggedUserID, false);
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

    // Closes the current window
    private void closeCurrentWindow() {
        Stage currentStage = (Stage)addApptAnchorPane.getScene().getWindow();
        currentStage.close();
    }
}
