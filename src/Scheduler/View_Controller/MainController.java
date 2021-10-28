/**
 * This class represents the main page that displays the Appointments on a monthly/weekly basis. This page is also used as
 * a hub to move to different pages associated with the actions of adding a customer, adding an appointment, viewing customers,
 * and running reports.
 *
 * @author Henry Trieu
 */

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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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
    @FXML private Button updateAppointmentButton;
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
    private boolean accessedViaLogin;
    private Appointment selectedAppointment;

    // Variables related to Appointment TableView
    ZonedDateTime startRange;
    ZonedDateTime endRange;

    /**
     * Initializes the table properties used to display the Appointments and also performs a check to alert the logged-user
     * if there is an upcoming Appointment within 15 minutes of logging in. By default, the application will view the
     * appointments on a monthly view but radio buttons are provided to switch between the weekly/monthly view.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeTableColumns();
        initializeTableView();
        initializeRadioButtons();
        welcomeAlertDialog();
        monthlyWeeklyToggleGroup.selectToggle(monthlyRadioButton);
    }

    /**
     * Constructor for this MainController class.
     *
     * @param loggedUserID is used to track the user logged into the Scheduler application. This is later used to pass into
     *                     other classes that audit the creation/update of a Customer/Appointment object in the database.
     * @param accessedViaLogin is a flag used to check whether this Main Controller page was accessed via the login page. If
     *                         this Main Controller was accessed via the Login page, then the alert check would be run.
     */
    public MainController(int loggedUserID, boolean accessedViaLogin) {
        this.loggedUserID = loggedUserID;
        this.accessedViaLogin = accessedViaLogin;
    }

    /**
     * Sets the text of the label to represent the month and year of Appointments being viewed. By default, the user should
     * land on the page viewing the current month and year.
     */
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

    /**
     * Sets the text of the Appointment viewing range to be within a week.
     */
    private void loadWeekRangeLabel() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yy");

        String startWeek = formatter.format(startRange);
        String endWeek = formatter.format(endRange);

        monthWeekLabel.setText("Week " + startWeek + " to " + endWeek);
    }

    /**
     * Loads the Appointments into the table based on the start range and end range values which define the period of time
     * to display the Appointments.
     */
    private void loadAppointments() {
        // Clear the table and selection before loading/reloading Appointments into the table
        appointmentTableView.getItems().clear();
        appointmentTableView.getSelectionModel().clearSelection();
        updateAppointmentButton.setVisible(false);
        deleteAppointmentButton.setVisible(false);

        // Load the appointments
        try{
            String query = "SELECT * FROM appointments ORDER BY Start ASC";
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
                if( (tempAppointment.getUtcStartZDT().isEqual(startRange) || tempAppointment.getUtcStartZDT().isAfter(startRange)) &&
                        (tempAppointment.getUtcEndZDT().isEqual(endRange) || tempAppointment.getUtcEndZDT().isBefore(endRange))) {
                    appointmentTableView.getItems().add(tempAppointment);
                }
            }
        }
        catch(SQLException e) {
            System.out.println("Error retrieving Appointment information from the database.");
        }
    }

    /**
     * Initializes properties of the TableView to handle the hiding/displaying of the update Appointment and delete Appointment buttons
     * based on whether an Appointment has been selected from the table.
     */
    private void initializeTableView() {
        // Add a listener to the Appointment TableView
        appointmentTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if(newSelection != null) {
                selectedAppointment = (Appointment) appointmentTableView.getSelectionModel().getSelectedItem();
                updateAppointmentButton.setVisible(true);
                deleteAppointmentButton.setVisible(true);
            }
        });

        initializeTableViewRange();
    }

    /**
     * Initializes the start range and end range of Appointments being displayed based on the end-user's current time.
     */
    private void initializeTableViewRange() {
        LocalDateTime startOfCurrentMonth = LocalDateTime.of(Year.now().getValue(), YearMonth.now().getMonthValue(), 1, 0, 0);
        this.startRange = startOfCurrentMonth.atZone(ZoneId.systemDefault());

        LocalDateTime endOfCurrentMonth = LocalDateTime.of(Year.now().getValue(), YearMonth.now().getMonthValue(), YearMonth.now().lengthOfMonth(), 0, 0);
        this.endRange = endOfCurrentMonth.atZone(ZoneId.systemDefault());
    }

    /**
     * Groups the monthly and weekly radio buttons to work together in a toggle group and also defines the behavior of the
     * radio buttons when they are selected.
     */
    private void initializeRadioButtons() {
        // Group the RadioButtons so only one can be selected at a time
        monthlyWeeklyToggleGroup = new ToggleGroup();
        monthlyRadioButton.setToggleGroup(monthlyWeeklyToggleGroup);
        weeklyRadioButton.setToggleGroup(monthlyWeeklyToggleGroup);

        // listen to changes in selected toggle
        monthlyWeeklyToggleGroup.selectedToggleProperty().addListener((observable, oldVal, newVal) -> {
            if(monthlyWeeklyToggleGroup.getSelectedToggle() == monthlyRadioButton) {
                // Re-define the range of the TableView
                int lengthOfStartMonth = YearMonth.of(startRange.getYear(), startRange.getMonth()).lengthOfMonth();
                LocalDateTime newStartRange = LocalDateTime.of(startRange.getYear(), startRange.getMonthValue(), 1, 0, 0);
                LocalDateTime newEndRange = LocalDateTime.of(startRange.getYear(), startRange.getMonthValue(), lengthOfStartMonth, 0, 0);

                this.startRange = newStartRange.atZone(ZoneId.systemDefault());
                this.endRange = newEndRange.atZone(ZoneId.systemDefault());

                // Update the text label and load the appointments
                loadMonthYearLabel();
                loadAppointments();
            }
            else if(monthlyWeeklyToggleGroup.getSelectedToggle() == weeklyRadioButton) {
                // Re-define the range of the TableView
                LocalDateTime newStartRange = LocalDateTime.of(startRange.getYear(), startRange.getMonthValue(), 1, 0, 0);
                LocalDateTime newEndRange = LocalDateTime.of(startRange.getYear(), startRange.getMonthValue(), 7, 0, 0);

                this.startRange = newStartRange.atZone(ZoneId.systemDefault());
                this.endRange = newEndRange.atZone(ZoneId.systemDefault());

                // Update the text label and load the appointments
                loadWeekRangeLabel();
                loadAppointments();
            }
        });
    }

    /**
     * Configures the accepted attributes associated with the table columns used to display Appointment information
     */
    private void initializeTableColumns() {
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

    /**
     * Increments the start range and end range of Appointment viewing, respectively dependent on the radio button selection (+1 week
     *  or +1 month).
     */
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

    /**
     * Decrements the start range and end range of Appointment viewing, respectively dependent on the radio button selection (-1 week
     *  or -1 month).
     */
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

    /**
     * Provides an alert when there is an appointment within 15 minutes of the user's log-in.
     * If there are no appointments within 15 minutes of logging in, a message will indicate there are no upcoming appointments.
     */
    private void welcomeAlertDialog() {
        if(accessedViaLogin) {
            LocalDateTime now = LocalDateTime.now();
            ZonedDateTime nowZonedDateTime = now.atZone(ZoneId.systemDefault());
            ZonedDateTime fifteenMinsLaterFromNow = nowZonedDateTime.plusMinutes(15);
            boolean upcomingAppointment = false;

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

                    ZonedDateTime utcApptStart = tempAppointment.getUtcStartZDT();

                    // Generate an alert if there is an upcoming appointment within 15 minutes from logging in
                    if( (utcApptStart.equals(nowZonedDateTime)) ||
                            (utcApptStart.isAfter(nowZonedDateTime) && (utcApptStart.isBefore(fifteenMinsLaterFromNow))) ||
                            (utcApptStart.isEqual(fifteenMinsLaterFromNow))) {
                        // Convert the appointment time from UTC to local time and parse the information
                        ZonedDateTime localApptStart = utcApptStart.withZoneSameInstant(ZoneId.systemDefault());
                        String date = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(localApptStart);
                        String time = DateTimeFormatter.ofPattern("h:mm a").format(localApptStart);

                        // Display the alert
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Upcoming Appointment");
                        alert.setContentText("(Appointment ID: " + tempAppointment.getAppointmentID() + ") There is an upcoming appointment on " + date + " at " + time + ".");
                        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                        stage.setAlwaysOnTop(true);
                        alert.show();

                        // Flag that the alert was generated
                        upcomingAppointment = true;
                    }
                }
            }
            catch(SQLException e) {
                System.out.println("Error retrieving Appointment information from the database.");
            }
            //
            if(!upcomingAppointment) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("No Upcoming Appointments");
                alert.setContentText("There are no upcoming appointments within 15 minutes from now.");
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.setAlwaysOnTop(true);
                alert.show();
            }
        }
    }

    /**
     * Switches to the page for updating the selected Appointment. All original values associated with the selected Appointment
     * will appear on the update form.
     */
    @FXML
    private void updateAppointment() throws IOException{
        // Load the FXML file.
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Scheduler/View_Controller/UpdateAppointmentController.fxml"));
        UpdateAppointmentController controller = new UpdateAppointmentController(loggedUserID, selectedAppointment);
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
     * Deletes the selected Appointment from the database.
     */
    @FXML
    private void deleteAppointment() {
        try {
            // DB Query
            String update = "DELETE FROM appointments WHERE Appointment_ID = " + selectedAppointment.getAppointmentID();
            Statement st = JDBC.connection.createStatement();
            st.executeUpdate(update);

            // Reload the table after deleting the Appointment from the DB and hide the edit/delete buttons since there are no customers selected
            loadAppointments();
            updateAppointmentButton.setVisible(false);
            deleteAppointmentButton.setVisible(false);

            // Generate a custom alert indicating that the Appointment was canceled
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Appointment Canceled");
            alert.setContentText("(Appointment ID: " + selectedAppointment.getAppointmentID() + ") The " + selectedAppointment.getType() + " appointment has been canceled successfully.");
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.setAlwaysOnTop(true);
            alert.show();

            // Record this deletion activity within the appointment_activity.txt file
            recordAppointmentActivity();
        }
        catch(SQLException e) {
            System.out.println("There was an error deleting the appointment from the database.");
        }
    }

    /**
     * Records the delete action of the Appointment within the appointment_activity.txt file which is used for displaying a
     * report from within the main controller.
     */
    private void recordAppointmentActivity() {
        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(new File("appointment_activity.txt"), true);

            // Pieces to the activity log
            String timestamp;
            int appointmentId = selectedAppointment.getAppointmentID();
            String appointmentTitle = selectedAppointment.getTitle();

            // Retrieve the current timestamp in UTC
            LocalDateTime now = LocalDateTime.now();
            ZonedDateTime utcTime = now.atZone(ZoneId.of("UTC"));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'['yyyy-MM-dd HH:mm:ss']'");
            timestamp = formatter.format(utcTime);

            // Concatenate the pieces of the appointment activity that will be recorded in the text file
            String record = timestamp + " (Appointment ID: " + appointmentId + ") - Title:'" + appointmentTitle + "', DELETED.\n";

            // Write to the text file
            fileOutputStream.write(record.getBytes(StandardCharsets.UTF_8));
        }
        catch(FileNotFoundException e) {
            System.out.println("appointment_activity.txt not found" + e);
        }
        catch(IOException e) {
            System.out.println("Exception writing to appointment_activity.txt" + e);
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
     * Closes the current window. This method is called within returnToMainController() as a helper function.
     */
    private void closeCurrentWindow() {
        Stage currentStage = (Stage)mainAnchorPane.getScene().getWindow();
        currentStage.close();
    }

    /**
     * Closes the main page and switches to the controller where the user is prompted to fill out information to
     * add a new user.
     */
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



    /**
     * Closes the main page and switches to the controller where the user can view all the customers
     */
    @FXML
    private void switchToViewCustomerController() throws IOException {
        // Load the FXML file.
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Scheduler/View_Controller/ViewCustomerController.fxml"));
        ViewCustomerController controller = new ViewCustomerController(loggedUserID);
        loader.setController(controller);
        Parent root = loader.load();

        // Close the current window and display the View Customer
        closeCurrentWindow();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
    }



    /**
     * Closes the main page and switches to the controller where the user can add a new appointment
     */
    @FXML
    private void switchToAddAppointmentController() throws IOException {
        // Load the FXML file.
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Scheduler/View_Controller/AddAppointmentController.fxml"));
        AddAppointmentController controller = new AddAppointmentController(loggedUserID);
        loader.setController(controller);
        Parent root = loader.load();

        // Close the current window and display the scene to add a new Appointment
        closeCurrentWindow();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Opens a Selected Contact-based report on top of the current page that displays a schedule associated with a selected
     *  Contact on the report page.
     */
    @FXML
    private void switchToContactReportController() throws IOException {
        // Load the FXML file.
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Scheduler/View_Controller/ContactScheduleReport.fxml"));
        ContactScheduleReport controller = new ContactScheduleReport();
        loader.setController(controller);
        Parent root = loader.load();

        // Build the report scene and display it
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);
        stage.show();
    }

    /**
     * Opens a Type/Month-based report on top of the current page that provides statistical analysis of Appointment frequencies
     * sorted by unique 'Type's and number of Appointments within a month.
     */
    @FXML
    private void switchToTypeMonthReportController() throws IOException {
        // Load the FXML file.
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Scheduler/View_Controller/TypeMonthReport.fxml"));
        TypeMonthReport controller = new TypeMonthReport();
        loader.setController(controller);
        Parent root = loader.load();

        // Build the report scene and display it
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);
        stage.show();
    }

    /**
     * Opens a Type/Month-based report on top of the current page that provides statistical analysis of Appointment frequencies
     * sorted by unique 'Type's and number of Appointments within a month.
     */
    @FXML
    private void switchToAppointmentActivityController() throws IOException {
        // Load the FXML file.
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Scheduler/View_Controller/AppointmentActivityController.fxml"));
        AppointmentActivityController controller = new AppointmentActivityController();
        loader.setController(controller);
        Parent root = loader.load();

        // Build the report scene and display it
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);
        stage.show();
    }
}
