package Scheduler.View_Controller;
import Model.Customer;
import helper.JDBC;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.TimeZone;

public class UpdateCustomerController implements Initializable {
    @FXML AnchorPane updateCustAnchorPane;

    // FXML TextFields
    @FXML TextField custIDTextField;
    @FXML TextField custNameTextField;
    @FXML TextField custAddressTextField;
    @FXML TextField custPostalCodeTextField;
    @FXML TextField custPhoneNumberTextField;

    // FXML Menus
    @FXML MenuButton countryMenu;
    @FXML MenuButton divisionMenu;

    // Temporary Customer object used to update the values for the Customer selected from the previous screen
    private Customer updatedCustomer;

    // Variable for tracking the user logged in
    private int loggedUserID;
    private String loggedUsername;

    // Calls all the methods to prepopulate fields and menu options
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loggedUsername = retrieveUsernameLoggedIn();
        prepopulateCountryOptions();
        prepopulateDivisionOptions(updatedCustomer.getCountryID());
        prepopulateCustomerInfoFields();
    }

    public UpdateCustomerController(Customer selectedCustomer, int loggedUserID) {
        this.loggedUserID = loggedUserID;
        this.updatedCustomer = selectedCustomer;
    }

    // Preloads the TextFields with the customer's information
    private void prepopulateCustomerInfoFields() {
        // Set the String in the TextFields
        custIDTextField.setText("" + updatedCustomer.getID());
        custNameTextField.setText(updatedCustomer.getName());
        custAddressTextField.setText(updatedCustomer.getAddress());
        custPostalCodeTextField.setText(updatedCustomer.getPostalCode());
        custPhoneNumberTextField.setText(updatedCustomer.getPhoneNumber());
        countryMenu.setText(updatedCustomer.getCountryName());
        divisionMenu.setText(updatedCustomer.getDivisionName());
    }

    // Pre-populates the country options into the menu box
    private void prepopulateCountryOptions() {
        try {
            ArrayList<String> countryNames = new ArrayList<String>();

            // DB Query
            String query = "SELECT Country FROM countries";
            Statement st = JDBC.connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            // Add the country options to the ArrayList
            while(rs.next()) {
                // Create Menu Items for each Country found in the DB
                MenuItem menuItem = new MenuItem(rs.getString("Country"));

                // Create the different onAction events based off country selection
                menuItem.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        updatedCustomer.setCountryName(menuItem.getText());
                        countryMenu.setText(menuItem.getText());

                        // Reset the division selection when a new country is selected
                        divisionMenu.setDisable(false);
                        divisionMenu.setText("Select");
                        divisionMenu.getItems().clear();
                        updatedCustomer.setDivisionID(0);

                        // Get the country ID that will be used for pre-populating the division selection
                        try {
                            // DB Query
                            String query = "SELECT Country_ID FROM countries WHERE Country = '" + updatedCustomer.getCountryName() + "'";
                            Statement st = JDBC.connection.createStatement();
                            ResultSet rs = st.executeQuery(query);

                            while(rs.next()) {
                                updatedCustomer.setCountryID(rs.getInt("Country_ID"));
                            }
                        }
                        catch(SQLException e) {
                            System.out.println("Error fetching countries from the database.");
                        }

                        prepopulateDivisionOptions(updatedCustomer.getCountryID());
                    }
                });
                countryMenu.getItems().add(menuItem);
            }
        }
        catch (SQLException e){
            System.out.println("Error retrieving countries from the database.");
        }
    }

    // Pre-populates State and Division options based off the country selection
    private void prepopulateDivisionOptions(int id) {
        // Query all the first level divisions from the DB
        try {
            // DB Query
            String query = "SELECT Division FROM first_level_divisions WHERE Country_ID = '" + updatedCustomer.getCountryID() + "'";
            Statement st = JDBC.connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            // Add the division options to the menu
            while(rs.next()) {
                MenuItem menuItem = new MenuItem(rs.getString(1));

                // Set the onAction event
                menuItem.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        try {
                            // Update GUI and set values
                            String divisionName = menuItem.getText();
                            divisionMenu.setText(divisionName);

                            // DB Query
                            String query = "SELECT Division_ID FROM first_level_divisions WHERE Division = '" + divisionName + "'";
                            Statement st = JDBC.connection.createStatement();
                            ResultSet rs = st.executeQuery(query);

                            while(rs.next()) {
                                updatedCustomer.setDivisionID(rs.getInt("Division_ID"));
                            }
                        }
                        catch(SQLException e) {
                            System.out.println("Error retrieving the Division_ID value from the database.");
                        }
                    }
                });

                // Add the menuItem to the division menu
                divisionMenu.getItems().add(menuItem);
            }
        }
        catch(SQLException e) {
            System.out.println("Error retrieving first level divisions from the database.");
        }
    }

    // Updates the customer's records with the values provided/selected
    @FXML
    private void updateCustomer() {
        // Ensure that all fields on the form have been provided a value
        if(allFieldsFilled()) {
            // Parse TextField values and store them into variables for updating the Customer
            parseFormFields();
            setAuditTimestamps();

            // Update the customer in the DB
            try {
                // DB Query to update the existing customer record
                String update = "UPDATE customers SET " +
                        "Customer_Name = " + "'" + updatedCustomer.getName() + "', " +
                        "Address = " + "'" + updatedCustomer.getAddress() + "', " +
                        "Postal_Code = " + "'" + updatedCustomer.getPostalCode() + "', " +
                        "Phone = " + "'" + updatedCustomer.getPhoneNumber() + "', " +
                        "Last_Update = " + "'" + updatedCustomer.getLastUpdate() + "', " +
                        "Last_Updated_By = " + "'" + updatedCustomer.getLastUpdatedBy() + "', " +
                        "Division_ID = " + "'" + updatedCustomer.getDivisionID() + "'" +
                        "WHERE Customer_ID = " + updatedCustomer.getID();
                Statement st = JDBC.connection.createStatement();
                st.executeUpdate(update);

                // Return to the ViewCustomerController after saving the new customer to the database
                switchToViewCustomerController();
            }
            catch(SQLException e) {
                System.out.println("Error adding customer to database.");
            }
        }
        else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setContentText("All fields must have a value.");
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.setAlwaysOnTop(true);
            alert.show();
        }
    }


    // Retrieves current logged in, used for auditing the user that added the customer
    private String retrieveUsernameLoggedIn() {
        String username = "";

        try {
            String query = "SELECT User_Name FROM users WHERE User_ID = " + loggedUserID;
            Statement st = JDBC.connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            while(rs.next()) {
                username = rs.getString("User_Name");
            }
        }
        catch (SQLException e) {
            System.out.println("Error retrieving logged in user's information.");
        }

        return username;
    }

    // Checks to ensure that all fields on the form have been provided a value
    private boolean allFieldsFilled() {
        if(!custNameTextField.getText().isEmpty() &&
                (!custAddressTextField.getText().isEmpty()) &&
                (updatedCustomer.getDivisionID() >= 1) &&
                (!custPostalCodeTextField.getText().isEmpty()) &&
                (!custPhoneNumberTextField.getText().isEmpty()) ) {
            return true;
        }
        else {
            return false;
        }
    }

    // Get values from the TextFields and store them in variables
    private void parseFormFields() {
        updatedCustomer.setName(custNameTextField.getText());
        updatedCustomer.setAddress(custAddressTextField.getText());
        updatedCustomer.setPostalCode(custPostalCodeTextField.getText());
        updatedCustomer.setPhoneNumber(custPhoneNumberTextField.getText());
    }

    // Sets the 'updated' fields in the Customer object
    private void setAuditTimestamps() {
        SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        updatedCustomer.setLastUpdate(utcFormat.format(new Date()));
        updatedCustomer.setLastUpdatedBy(loggedUsername);

    }

    // Closes the main screen and switches to the controller where the user can view all the customers
    @FXML
    private void switchToViewCustomerController() {
        try {
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
        catch(IOException e) {
            System.out.println("Error switching back to ViewCustomer Controller");
        }
    }

    // Closes the current window
    private void closeCurrentWindow() {
        Stage currentStage = (Stage)updateCustAnchorPane.getScene().getWindow();
        currentStage.close();
    }
}
