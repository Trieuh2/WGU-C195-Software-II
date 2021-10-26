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
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.TimeZone;

public class UpdateAppointmentController implements Initializable {

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


    // Appointment object that is created using data pulled from the Database
    private Appointment selectedAppointment;

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
        loggedUsername = getLoggedUsername();

        // Prepopulate the form with the values associated with the selected Appointment and load the updatable options
        preSelectTimeOptions();
        loadTextFields();
        loadAppointmentID();
        loadCustomers();
        loadContacts();
        loadTimeOptions();
    }

    public UpdateAppointmentController(int loggedUserID, Appointment selectedAppointment) {
        this.loggedUserID = loggedUserID;
        this.selectedAppointment = selectedAppointment;
    }

    // Fills out the TextFields/TextAreas with the previously provided values
    private void loadTextFields() {
        titleTextField.setText(selectedAppointment.getTitle());
        descriptionTextArea.setText(selectedAppointment.getDescription());
        locationTextField.setText(selectedAppointment.getLocation());
        typeTextField.setText(selectedAppointment.getType());
    }

    // Default select the previously provided values for the start and end of the appointment
    private void preSelectTimeOptions() {
        // Preselect the date of the Appointment
        ZonedDateTime localZonedDateTimeStart = selectedAppointment.getLocalZonedDateTimeStart();
        int year = localZonedDateTimeStart.getYear();
        int month = localZonedDateTimeStart.getMonthValue();
        int day = localZonedDateTimeStart.getDayOfMonth();
        datePicker.setValue(LocalDate.of(year, month, day));

        // Preselect the original appointment time start and end values
        startHour = selectedAppointment.getLocalZonedDateTimeStart().getHour();
        startMin = selectedAppointment.getLocalZonedDateTimeStart().getMinute();
        startTimeMenuButton.setText(DateTimeFormatter.ofPattern("hh:mm a").format(selectedAppointment.getLocalZonedDateTimeStart()));

        endHour = selectedAppointment.getLocalZonedDateTimeEnd().getHour();
        endMin = selectedAppointment.getLocalZonedDateTimeEnd().getMinute();
        endTimeMenuButton.setText(DateTimeFormatter.ofPattern("hh:mm a").format(selectedAppointment.getLocalZonedDateTimeEnd()));
    }

    // Pre-populates the appointment ID for the new appointment being added
    private void loadAppointmentID() {
        appointmentIDTextField.setText("" + selectedAppointment.getAppointmentID());
    }

    // Pre-populates the options within the drop-down menu for customers
    private void loadCustomers() {
        // Pre-select the Customer associated with the Appointment
        customerMenuButton.setText(selectedAppointment.getCustomerName());
        customerSelected = true;

        // Populate the options
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
                        selectedAppointment.setCustomerID(customerID);

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
        // Pre-select the Customer associated with the Appointment
        contactMenuButton.setText(selectedAppointment.getContactName());
        contactSelected = true;

        // Populate the options
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
                        selectedAppointment.setContactID(contactID);

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
    private void updateAppointment() {
        // Check to ensure that all fields on the form are filled
        if(allFieldsFilled()) {
            parseFormFields();
            setAuditTimestamps();

            // Checks to make sure that the startTime is not in the past
            if(selectedAppointment.startTimeInFuture()) {
                // Checks to make sure that the endTime is after the startTime
                if(selectedAppointment.endTimeAfterStartTime()) {
                    // Checks to make sure that the startTime and endTime are within business hours
                    if (selectedAppointment.isWithinBusinessHours()) {
                        if(!selectedAppointment.overlapsCustomer()) {
                            // Add Appointment to DB
                            try {
                                // DB Query for adding Appointment
                                String update = "UPDATE appointments SET " +
                                        "Title = '" + selectedAppointment.getTitle() + "', " +
                                        "Description = '" + selectedAppointment.getDescription() + "', " +
                                        "Location = '" + selectedAppointment.getLocation() + "', " +
                                        "Type = '" + selectedAppointment.getType() + "', " +
                                        "Start = '" + selectedAppointment.getUtcStartTimestamp() + "', " +
                                        "End = '" + selectedAppointment.getUtcEndTimestamp() + "', " +
                                        "Last_Update = '" + selectedAppointment.getLastUpdate() + "', " +
                                        "Last_Updated_By = '" + selectedAppointment.getLastUpdatedBy() + "', " +
                                        "Customer_ID = '" + selectedAppointment.getCustomerID() + "', " +
                                        "User_ID = '" + loggedUserID + "', " +
                                        "Contact_ID = '" + selectedAppointment.getContactID() + "'"
                                        + "WHERE APPOINTMENT_ID = " + selectedAppointment.getAppointmentID();
                                Statement st = JDBC.connection.createStatement();
                                st.executeUpdate(update);

                                returnToMainController();
                            }
                            catch(SQLException e) {
                                System.out.println("Error updating Appointment in database.");
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

    // Take values from TextFields/TextAreas and assign it to the Appointment being added
    private void parseFormFields() {
        int year = datePicker.getValue().getYear();
        int month = datePicker.getValue().getMonth().getValue();
        int day = datePicker.getValue().getDayOfMonth();

        selectedAppointment.setTitle(titleTextField.getText());
        selectedAppointment.setDescription(descriptionTextArea.getText());
        selectedAppointment.setLocation(locationTextField.getText());
        selectedAppointment.setType(typeTextField.getText());
        selectedAppointment.setZonedDateTimeStarts(year, month, day, startHour, startMin, 0);
        selectedAppointment.setZonedDateTimeEnds(year, month, day, endHour, endMin, 0);
    }

    // Sets the 'created' and 'updated' fields in the Appointment object
    private void setAuditTimestamps() {
        SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        selectedAppointment.setLastUpdate(utcFormat.format(new Date()));
        selectedAppointment.setLastUpdatedBy(loggedUsername);
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
