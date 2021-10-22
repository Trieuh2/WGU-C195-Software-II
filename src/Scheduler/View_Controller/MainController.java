package Scheduler.View_Controller;

import Model.Appointment;
import helper.JDBC;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    // Primary AnchorPane on the Main Controller
    @FXML private AnchorPane mainAnchorPane;

    // Buttons
    @FXML private Button addCustomerButton;
    @FXML private Button addAppointmentButton;
    @FXML private Button editAppointmentButton;
    @FXML private Button deleteAppointmentButton;
    @FXML private RadioButton monthlyRadioButton;
    @FXML private RadioButton weeklyRadioButton;
    @FXML private ToggleGroup monthlyWeeklyToggleGroup;

    // Labels
    @FXML private Label monthWeekLabel;

    // TableView
    @FXML private GridPane monthViewGridPane;
    @FXML private TableView appointmentTableView;

    // TableColumns
    @FXML TableColumn appointmentIdColumn;
    @FXML TableColumn titleColumn;
    @FXML TableColumn descriptionColumn;
    @FXML TableColumn locationColumn;
    @FXML TableColumn contactColumn;
    @FXML TableColumn typeColumn;
    @FXML TableColumn startTimeColumn;
    @FXML TableColumn endTimeColumn;
    @FXML TableColumn customerIdColumn;
    @FXML TableColumn userIdColumn;

    // Tracking variables
    private final int loggedUserID;
    private Appointment selectedAppointment;

    // Variables related to Appointment TableView
    ZonedDateTime startRange;
    ZonedDateTime endRange;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setCellFactoryValues();
        initializeTableView();
        initializeRadioButtons();

        // Load the Appointments in the Monthly view by default
        monthlyWeeklyToggleGroup.selectToggle(monthlyRadioButton);
    }

    public MainController(int loggedUserID) {
        this.loggedUserID = loggedUserID;
    }

    private void loadMonthYearLabel() {
        switch (startRange.getMonth().getValue()) {
            case 1: monthWeekLabel.setText("January " + startRange.getYear());
                    break;
            case 2: monthWeekLabel.setText("February " + startRange.getYear());
                break;
            case 3: monthWeekLabel.setText("March " + startRange.getYear());
                break;
            case 4: monthWeekLabel.setText("April " + startRange.getYear());
                break;
            case 5: monthWeekLabel.setText("May " + startRange.getYear());
                break;
            case 6: monthWeekLabel.setText("June " + startRange.getYear());
                break;
            case 7: monthWeekLabel.setText("July " + startRange.getYear());
                break;
            case 8: monthWeekLabel.setText("August " + startRange.getYear());
                break;
            case 9: monthWeekLabel.setText("September " + startRange.getYear());
                break;
            case 10: monthWeekLabel.setText("October " + startRange.getYear());
                break;
            case 11: monthWeekLabel.setText("November " + startRange.getYear());
                break;
            case 12: monthWeekLabel.setText("December " + startRange.getYear());
                break;
        }
    }

    private void loadWeekRangeLabel() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yy");

        String startWeek = formatter.format(startRange);
        String endWeek = formatter.format(endRange);

        monthWeekLabel.setText("Week " + startWeek + " to " + endWeek);
    }

    private void loadAppointments() {
        // Clear the table and selection before loading/reloading Appointments into the table
        appointmentTableView.getItems().clear();
        appointmentTableView.getSelectionModel().clearSelection();
        editAppointmentButton.setVisible(false);
        deleteAppointmentButton.setVisible(false);

        // Load the appointments
        try{
            String query = "SELECT * FROM appointments";
            Statement st = JDBC.connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            // Parse the information from the query and load them into the TableView
            while(rs.next()) {
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


                // Only add the Appointment to the display if it is within the range set
                if( (tempAppointment.getUtcZonedDateTimeStart().isEqual(startRange) || tempAppointment.getUtcZonedDateTimeStart().isAfter(startRange)) &&
                        (tempAppointment.getUtcZonedDateTimeEnd().isEqual(endRange) || tempAppointment.getUtcZonedDateTimeEnd().isBefore(endRange))) {
                    appointmentTableView.getItems().add(tempAppointment);
                }
            }
        }
        catch(SQLException e) {
            System.out.println("Error retrieving Appointment information from the database.");
        }
    }

    private void initializeTableView() {
        // Add a listener to the Appointment TableView
        appointmentTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if(newSelection != null) {
                selectedAppointment = (Appointment) appointmentTableView.getSelectionModel().getSelectedItem();
                editAppointmentButton.setVisible(true);
                deleteAppointmentButton.setVisible(true);
            }
        });

        initializeTableViewRange();
    }

    private void initializeTableViewRange() {
        LocalDateTime startOfCurrentMonth = LocalDateTime.of(Year.now().getValue(), YearMonth.now().getMonthValue(), 1, 0, 0);
        this.startRange = startOfCurrentMonth.atZone(ZoneId.systemDefault());

        LocalDateTime endOfCurrentMonth = LocalDateTime.of(Year.now().getValue(), YearMonth.now().getMonthValue(), YearMonth.now().lengthOfMonth(), 0, 0);
        this.endRange = endOfCurrentMonth.atZone(ZoneId.systemDefault());
    }

    private void initializeRadioButtons() {
        monthlyWeeklyToggleGroup = new ToggleGroup();
        monthlyRadioButton.setToggleGroup(monthlyWeeklyToggleGroup);
        weeklyRadioButton.setToggleGroup(monthlyWeeklyToggleGroup);


        // listen to changes in selected toggle
        monthlyWeeklyToggleGroup.selectedToggleProperty().addListener((observable, oldVal, newVal) -> {
            if(monthlyWeeklyToggleGroup.getSelectedToggle() == monthlyRadioButton) {
                // Re-define the end of the range to be the end of the end of the month
                int lengthOfStartMonth = YearMonth.of(startRange.getYear(), startRange.getMonth()).lengthOfMonth();
                LocalDateTime newEndRange = LocalDateTime.of(startRange.getYear(), startRange.getMonthValue(), lengthOfStartMonth, 0, 0);
                this.endRange = newEndRange.atZone(ZoneId.systemDefault());

                // Update the text label and load the appointments
                loadMonthYearLabel();
                loadAppointments();
            }
            else if(monthlyWeeklyToggleGroup.getSelectedToggle() == weeklyRadioButton) {
                // Re-define the end of the range to be the end of the 1st week of the month
                LocalDateTime newEndRange = LocalDateTime.of(startRange.getYear(), startRange.getMonthValue(), 7, 0, 0);
                this.endRange = newEndRange.atZone(ZoneId.systemDefault());

                // Update the text label and load the appointments
                loadWeekRangeLabel();
                loadAppointments();
            }
        });
    }

    private void setCellFactoryValues() {
        appointmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contactName"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        startTimeColumn.setCellValueFactory(new PropertyValueFactory<>("localStartTimestamp"));
        endTimeColumn.setCellValueFactory(new PropertyValueFactory<>("localEndTimestamp"));
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userID"));
    }

    @FXML
    private void incrementViewRange() {
        if(monthlyWeeklyToggleGroup.getSelectedToggle() == monthlyRadioButton) {
            // Increment the range of appointments viewed by 1 month
            this.startRange = startRange.plusMonths(1);
            this.endRange = endRange.plusMonths(1);

            // Update the month label on the view and reload the appointments
            loadMonthYearLabel();
            loadAppointments();
        }
        else if(monthlyWeeklyToggleGroup.getSelectedToggle() == weeklyRadioButton) {
            // Increment the range of appointments viewed by 1 week
            this.startRange = startRange.plusWeeks(1);
            this.endRange = endRange.plusWeeks(1);

            // Update the week label on the view and reload the appointments
            loadWeekRangeLabel();
            loadAppointments();
        }
    }

    @FXML
    private void decrementViewRange() {
        if(monthlyWeeklyToggleGroup.getSelectedToggle() == monthlyRadioButton) {
            // Decrement the range of appointments viewed by 1 month
            this.startRange = startRange.minusMonths(1);
            this.endRange = endRange.minusMonths(1);

            // Update the month label on the view and reload the appointments
            loadMonthYearLabel();
            loadAppointments();
        }
        else if(monthlyWeeklyToggleGroup.getSelectedToggle() == weeklyRadioButton) {
            // Decrement the range of appointments viewed by 1 week
            this.startRange = startRange.minusWeeks(1);
            this.endRange = endRange.minusWeeks(1);

            // Update the week label on the view and reload the appointments
            loadWeekRangeLabel();
            loadAppointments();
        }
    }

    // Provides an alert when there is an appointment within 15 minutes of the user's log-in
    // If there are no appointments within 15 minutes of logging in, a message will indicate there are no upcoming appointments.
    private void welcomeAlertDialog() {
        LocalDateTime now = LocalDateTime.now();
        ZonedDateTime nowZonedDateTime = now.atZone(ZoneId.systemDefault());
        ZonedDateTime fifteenMinsLaterFromNow = nowZonedDateTime.plusMinutes(15);
        boolean alertGenerated = false;

        // Go through all appointments and generate an alert if there is an upcoming Appointment within 15 minutes of the user logging in.
        try{
            String query = "SELECT * FROM appointments";
            Statement st = JDBC.connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            while(rs.next()) {
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

                ZonedDateTime utcApptStart = tempAppointment.getUtcZonedDateTimeStart();

                // Generate an alert if there is an upcoming appointment within 15 minutes from logging in
                if( (utcApptStart.equals(nowZonedDateTime)) ||
                            (utcApptStart.isAfter(nowZonedDateTime) && (utcApptStart.isBefore(fifteenMinsLaterFromNow))) ||
                            (utcApptStart.isEqual(fifteenMinsLaterFromNow))) {
                    // Convert the appointment time from UTC to local time and parse the information
                    ZonedDateTime localApptStart = utcApptStart.withZoneSameInstant(ZoneId.systemDefault());
                    String date = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(utcApptStart);
                    String time = DateTimeFormatter.ofPattern("h:mm a").format(utcApptStart);

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Upcoming Appointment");
                    alert.setContentText("(Appointment ID: " + tempAppointment.getAppointmentID() + ") There is an upcoming appointment on " + date + " at " + time);

                    // Display the alert
                    alert.show();

                    // Flag that the alert was generated
                    alertGenerated = true;
                }
            }
        }
        catch(SQLException e) {
            System.out.println("Error retrieving Appointment information from the database.");
        }

        if(!alertGenerated) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Upcoming Appointments");
            alert.setContentText("There are no upcoming appointments within 15 minutes from now.");

            alert.show();
        }
    }

    @FXML
    private void editAppointment() {

    }

    @FXML
    private void deleteAppointment() {
        try {
            // DB Query
            String update = "DELETE FROM appointments WHERE Appointment_ID = " + selectedAppointment.getAppointmentID();
            Statement st = JDBC.connection.createStatement();
            st.executeUpdate(update);

            // Reload the table after deleting the Appointment from the DB and hide the edit/delete buttons since there are no customers selected
            loadAppointments();
            editAppointmentButton.setVisible(false);
            deleteAppointmentButton.setVisible(false);

            // Generate a custom alert indicating that the Appointment was canceled
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Appointment Canceled");
            alert.setContentText("(Appointment ID: " + selectedAppointment.getAppointmentID() + ") The " + selectedAppointment.getType() + " appointment has been canceled successfully.");

            alert.show();
        }
        catch(SQLException e) {
            System.out.println("There was an error deleting the appointment from the database.");
        }
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
