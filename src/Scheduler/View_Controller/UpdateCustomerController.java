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

    // Customer fields
    private Customer selectedCustomer;

    // Updatable customer fields
    private String name;
    private String address;
    private String postalCode;
    private String phoneNumber;
    private String selectedDivisionID;
    private String selectedCountryName;
    private int selectedCountryID;

    public UpdateCustomerController(Customer selectedCustomer) {
        // Assign values to local instance variables based on provided customer
        this.selectedCustomer = selectedCustomer;
        this.name = selectedCustomer.getName();
        this.address = selectedCustomer.getAddress();
        this.postalCode = selectedCustomer.getPostalCode();
        this.phoneNumber = selectedCustomer.getPhoneNumber();
        this.selectedDivisionID = "" + selectedCustomer.getDivisionID();
        this.selectedCountryName = selectedCustomer.getCountryName();
        this.selectedCountryID = selectedCustomer.getCountryID();
    }

    // DONE
    // Calls all the methods to prepopulate fields and menu options
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        prepopulateCountryOptions();
        prepopulateDivisionOptions(selectedCountryID);
        prepopulateCustomerInfoFields();
    }

    // DONE
    // Preloads the TextFields with the customer's information
    private void prepopulateCustomerInfoFields() {
        // Set the String in the TextFields
        custIDTextField.setText("" + selectedCustomer.getID());
        custNameTextField.setText(selectedCustomer.getName());
        custAddressTextField.setText(selectedCustomer.getAddress());
        custPostalCodeTextField.setText(selectedCustomer.getPostalCode());
        custPhoneNumberTextField.setText(selectedCustomer.getPhoneNumber());
        countryMenu.setText(selectedCustomer.getCountryName());
        divisionMenu.setText(selectedCustomer.getDivisionName());
    }

    // DONE
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
                MenuItem menuItem = new MenuItem(rs.getString(1));

                // Create the different onAction events based off country selection
                menuItem.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        selectedCountryName = menuItem.getText();
                        countryMenu.setText(menuItem.getText());

                        // Reset the division selection when a new country is selected
                        divisionMenu.setDisable(false);
                        divisionMenu.setText("Select");
                        divisionMenu.getItems().clear();
                        selectedDivisionID = null;

                        // Get the country ID that will be used for pre-populating the division selection
                        try {
                            // DB Query
                            String query = "SELECT Country_ID FROM countries WHERE Country = '" + selectedCountryName + "'";
                            Statement st = JDBC.connection.createStatement();
                            ResultSet rs = st.executeQuery(query);

                            while(rs.next()) {
                                selectedCountryID = rs.getInt(1);
                            }
                        }
                        catch(SQLException e) {
                            System.out.println("Error fetching countries from the database.");
                        }

                        prepopulateDivisionOptions(selectedCountryID);
                    }
                });
                countryMenu.getItems().add(menuItem);
            }
        }
        catch (SQLException e){
            System.out.println("Error retrieving countries from the database.");
        }
    }

    // DONE
    // Pre-populates State and Division options based off the country selection
    private void prepopulateDivisionOptions(int id) {
        // Query all the first level divisions from the DB
        try {
            // DB Query
            String query = "SELECT Division FROM first_level_divisions WHERE Country_ID = '" + selectedCountryID + "'";
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
                                selectedDivisionID = rs.getString(1);
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

    // DONE
    // Closes the current window
    private void closeCurrentWindow() {
        Stage currentStage = (Stage)updateCustAnchorPane.getScene().getWindow();
        currentStage.close();
    }

    // DONE
    // Updates the customer's records with the values provided/selected
    @FXML
    private void updateCustomer() {
        // Get values of all updatable fields
        name = custNameTextField.getText();
        address = custAddressTextField.getText();
        postalCode = custPostalCodeTextField.getText();
        phoneNumber = custPhoneNumberTextField.getText();

        // Ensure that none fo the updatable fields are null
        if(!name.isEmpty() && !address.isEmpty() && (selectedCountryName != null) && (selectedDivisionID != null) && !postalCode.isEmpty() && !phoneNumber.isEmpty()) {
            // Update the customer in the DB
            try {
                SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

                String lastUpdate = utcFormat.format(new Date());
                String lastUpdatedBy = "script";

                // Retrieves current logged in, used for auditing the user that created and last updated the customer record
                try {
                    lastUpdatedBy = JDBC.connection.getMetaData().getUserName();
                }
                catch (SQLException e) {
                    System.out.println("Error retrieving metadata information to retrieve current username.");
                }

                // DB Query to update the existing customer record
                String update = "UPDATE customers SET " +
                        "Customer_Name = " + "'" + name + "', " +
                        "Address = " + "'" + address + "', " +
                        "Postal_Code = " + "'" + postalCode + "', " +
                        "Phone = " + "'" + phoneNumber + "', " +
                        "Last_Update = " + "'" + lastUpdate + "', " +
                        "Last_Updated_By = " + "'" + lastUpdatedBy + "', " +
                        "Division_ID = " + "'" + selectedDivisionID + "'" +
                        "WHERE Customer_ID = " + selectedCustomer.getID();
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
            alert.show();
        }
    }

    // DONE
    // Closes the main screen and switches to the controller where the user can view all the customers
    @FXML
    private void switchToViewCustomerController() {
        try {
            // Load the FXML file.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Scheduler/View_Controller/ViewCustomerController.fxml"));
            ViewCustomerController controller = new ViewCustomerController();
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
}
