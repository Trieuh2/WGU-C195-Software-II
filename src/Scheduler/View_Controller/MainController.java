package Scheduler.View_Controller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    // Primary AnchorPane on the Main Controller
    @FXML private AnchorPane mainAnchorPane;

    // Buttons
    @FXML private Button addCustomerButton;
    @FXML private Button addAppointmentButton;

    // Labels
    @FXML private Label monthWeekLabel;

    // TableView
    @FXML private GridPane monthViewGridPane;

    // Variable for tracking the user logged in
    private int loggedUserID;

    // Variables related to Appointment TableView
    int selectedYear;
    int selectedMonth;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Calendar cal = Calendar.getInstance();
        selectedMonth = cal.get(Calendar.MONTH);
        selectedYear = cal.get(Calendar.YEAR);

        loadMonthlyAppointmentView();
    }

    public MainController(int loggedUserID) {
        this.loggedUserID = loggedUserID;
    }

    // Loads the TableView of appointments in the month view
    private void loadMonthlyAppointmentView() {
        setMonthLabel();
        loadMonthGrid();
    }

    private void setMonthLabel() {
        switch (selectedMonth) {
            case 0: monthWeekLabel.setText("January " + selectedYear);
                    break;
            case 1: monthWeekLabel.setText("February " + selectedYear);
                break;
            case 2: monthWeekLabel.setText("March " + selectedYear);
                break;
            case 3: monthWeekLabel.setText("April " + selectedYear);
                break;
            case 4: monthWeekLabel.setText("May " + selectedYear);
                break;
            case 5: monthWeekLabel.setText("June " + selectedYear);
                break;
            case 6: monthWeekLabel.setText("July " + selectedYear);
                break;
            case 7: monthWeekLabel.setText("August " + selectedYear);
                break;
            case 8: monthWeekLabel.setText("September " + selectedYear);
                break;
            case 9: monthWeekLabel.setText("October " + selectedYear);
                break;
            case 10: monthWeekLabel.setText("November " + selectedYear);
                break;
            case 11: monthWeekLabel.setText("December " + selectedYear);
                break;
        }
    }

    private void loadMonthGrid() {
        // Find which day does the 1st of the month land on to know where to start
        Calendar cal = Calendar.getInstance();
        cal.set(selectedYear, selectedMonth, 1);
        Date firstDayOfMonth = cal.getTime();
        DateFormat sdf = new SimpleDateFormat("EEEEEE");
        String startDay = sdf.format(firstDayOfMonth);

        // Find the maximum number of days in this month
        int maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Display the calendar grid week by week
        int currentLabelDay = 1;
        int weekRow = 1;
        int dayCol = 0;

        // Using the parse first Day of the month, set the column index of the month grid view that we will start
        //      labeling the calendar.
        switch (startDay) {
            case "Sunday": dayCol = 0;
                break;
            case "Monday": dayCol = 1;
                break;
            case "Tuesday": dayCol = 2;
                break;
            case "Wednesday": dayCol = 3;
                break;
            case "Thursday": dayCol = 4;
                break;
            case "Friday": dayCol = 5;
                break;
            case "Saturday": dayCol = 6;
                break;
        }

        // Label the calendar with dates associated to the current month selected
        while(currentLabelDay <= maxDays) {
            // Move to next week
            if(dayCol == 7) {
                weekRow++;
                dayCol = 0;
            }

            // VBox for date
            Label dateLabel = new Label();
            dateLabel.setFont(new Font("System", 18));
            dateLabel.setText("" + currentLabelDay);
            dateLabel.setPadding(new Insets(2,2,2,2));
            VBox vbox = new VBox(dateLabel);
            vbox.setAlignment(Pos.TOP_LEFT);

            monthViewGridPane.add(vbox, dayCol, weekRow); // date, col, row

            currentLabelDay++;
            dayCol++;
        }
    }

    private void loadAppointmentsMonthView() {

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
