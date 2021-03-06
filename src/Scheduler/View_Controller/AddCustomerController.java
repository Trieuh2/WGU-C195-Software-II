/**
 * This class allows the user to add a new Customer to the connected database by filling out form fields on the
 * page.
 *
 * @author Henry Trieu
 */

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
import java.util.Date;
import java.util.ResourceBundle;
import java.util.TimeZone;

public class AddCustomerController implements Initializable {
    @FXML AnchorPane addCustAnchorPane;

    // FXML TextFields
    @FXML TextField custIDTextField;
    @FXML TextField custNameTextField;
    @FXML TextField custAddressTextField;
    @FXML TextField custPostalCodeTextField;
    @FXML TextField custPhoneNumberTextField;

    // FXML Menus
    @FXML MenuButton countryMenu;
    @FXML MenuButton divisionMenu;

    // New customer that is added to the DB
    private Customer newCustomer;

    // Variable for tracking the user logged in
    private final int loggedUserID;
    private String loggedUsername;

    /**
     * Retrieves the username of the currently logged user for auditing purposes.
     * Preloads and preselects the country options within the page for adding a new Customer to the database.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        loggedUsername = retrieveUsernameLoggedIn();
        newCustomer = new Customer();
        retrieveUsernameLoggedIn();
        prepopulateCustomerID();
        prepopulateCountryOptions();
        divisionMenu.setDisable(true);
    }

    /**
     * This is the constructor for this class.
     *
     * @param loggedUserID is used for tracking the current user logged in, which is used for auditing who added the Customer,
     *                     and who last updated the Customer.
     */
    public AddCustomerController(int loggedUserID) {
        this.loggedUserID = loggedUserID;
    }

    /**
     * Pre-populates an un-taken customer ID by finding the current highest value associated with the Customer ID in the database
     * and incrementing it by 1.
     */
    private void prepopulateCustomerID() {
        try {
            int maxCustomerID = 1;

            // DB Query
            String query = "SELECT MAX(Customer_ID) FROM customers";
            Statement st = JDBC.connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            while(rs.next()) {
                maxCustomerID= rs.getInt(1);
            }

            newCustomer.setID(++maxCustomerID);
            custIDTextField.setText("" + newCustomer.getID());
        }
        catch (SQLException e) {
            System.out.println("Error retrieving Customer_IDs from the database");
        }
    }

    /**
     * Pre-populates the country options into the menu box
     */
    private void prepopulateCountryOptions() {
        try {
            // DB Query
            String query = "SELECT Country FROM countries";
            Statement st = JDBC.connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            // Add each Country option found in the DB and add it to the MenuButton
            while(rs.next()) {
                // Create Menu Items for each Country found in the DB
                MenuItem menuItem = new MenuItem(rs.getString(1));

                // Create the different onAction events based off country selection
                menuItem.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        newCustomer.setCountryName(menuItem.getText());
                        countryMenu.setText(menuItem.getText());

                        // Reset the division selection when a country is selected
                        divisionMenu.setDisable(false);
                        divisionMenu.setText("Select");
                        divisionMenu.getItems().clear();
                        newCustomer.setDivisionID(-1);

                        // Get the country ID that will be used for pre-populating the division selection
                        try {
                            // DB Query
                            String query = "SELECT Country_ID FROM countries WHERE Country = '" + newCustomer.getCountryName() + "'";
                            Statement st = JDBC.connection.createStatement();
                            ResultSet rs = st.executeQuery(query);

                            while(rs.next()) {
                                newCustomer.setCountryID(rs.getInt(1));
                            }
                        }
                        catch(SQLException e) {
                            System.out.println("Error fetching countries from the database.");
                        }
                        // Populate the division options within the drop-down menu depending on the country selection
                        prepopulateDivisionOptions();
                    }
                });
                // Add the discovered country to the menu
                countryMenu.getItems().add(menuItem);
            }
        }
        catch (SQLException e){
            System.out.println("Error retrieving countries from the database.");
        }
    }

    /**
     * Pre-populates State and Division options based off the country selection
     */
    private void prepopulateDivisionOptions() {
        // Query all the first level divisions from the DB
        try {
            // DB Query
            String query = "SELECT Division FROM first_level_divisions WHERE Country_ID = '" + newCustomer.getCountryID() + "'";
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
                            newCustomer.setDivisionName(menuItem.getText());
                            divisionMenu.setText(menuItem.getText());

                            // DB Query
                            String query = "SELECT Division_ID FROM first_level_divisions WHERE Division = '" + menuItem.getText() + "'";
                            Statement st = JDBC.connection.createStatement();
                            ResultSet rs = st.executeQuery(query);

                            while(rs.next()) {
                                newCustomer.setDivisionID(rs.getInt(1));
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

    /**
     * Checks to ensure that all fields on the form have been field, assigns parsed values from the form to a temporary
     * Customer object, and then adds the customer information to the database.
     */
    @FXML
    private void addCustomer() {
        // Check to ensure that no values are unassigned
        if(allFieldsFilled()) {
            parseFormFields();
            setAuditTimestamps();

            // Add customer to DB
            try {
                // DB Query for adding Customer
                String update = "INSERT INTO customers VALUES (" + newCustomer.getID()
                                                                + ", '" + newCustomer.getName() + "', '"
                                                                + newCustomer.getAddress() + "', '"
                                                                + newCustomer.getPostalCode() + "', '"
                                                                + newCustomer.getPhoneNumber() + "', '"
                                                                + newCustomer.getCreateDate() + "', '"
                                                                + newCustomer.getCreatedBy() + "', '"
                                                                + newCustomer.getLastUpdate() + "', '"
                                                                + newCustomer.getLastUpdatedBy() + "', "
                                                                + newCustomer.getDivisionID() + ")";
                Statement st = JDBC.connection.createStatement();
                st.executeUpdate(update);

                // Return to the Main Controller after saving the new customer to the database
                returnToMainController();
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

    /**
     * Retrieves the username of the currently logged-in user for record adding purposes
     *
     * @return the username of the logged-in user based off the UserID passed through the constructor of this class.
     */
    private String retrieveUsernameLoggedIn() {
        String username = "";

        // Retrieves current logged in, used for auditing the user that added the customer
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

    /**
     * Checks to see if all the fields on the form has been filled. The forms checked only apply to form fields that the
     * end-user can provide a value to.
     *
     * @return if all forms on the field have been provided a value by the end-user.
     */
    private boolean allFieldsFilled() {
        if(!custNameTextField.getText().isEmpty() &&
                (!custAddressTextField.getText().isEmpty()) &&
                (newCustomer.getDivisionID() >= 1) &&
                (!custPostalCodeTextField.getText().isEmpty()) &&
                (!custPhoneNumberTextField.getText().isEmpty()) ) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Take values from the fields on the form and assign it to the Customer object associated with this class
     */
    private void parseFormFields() {
        newCustomer.setName(custNameTextField.getText());
        newCustomer.setAddress(custAddressTextField.getText());
        newCustomer.setPostalCode(custPostalCodeTextField.getText());
        newCustomer.setPhoneNumber(custPhoneNumberTextField.getText());
    }

    /**
     * Sets the 'created' and 'updated' fields in the Customer object
     */
    private void setAuditTimestamps() {
        SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        newCustomer.setCreateDate(utcFormat.format(new Date()));
        newCustomer.setCreatedBy(loggedUsername);
        newCustomer.setLastUpdate(utcFormat.format(new Date()));
        newCustomer.setLastUpdatedBy(loggedUsername);
    }

    /**
     * Closes current scene and switches back to the main controller
     */
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

    /**
     * Closes the current window. This method is called within returnToMainController() as a helper function.
     */
    private void closeCurrentWindow() {
        Stage currentStage = (Stage)addCustAnchorPane.getScene().getWindow();
        currentStage.close();
    }
}
