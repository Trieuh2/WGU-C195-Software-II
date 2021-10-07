package Scheduler.View_Controller;
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
import java.util.ArrayList;
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

    // Required information for adding a customer
    private int customerID;
    private String name;
    private String address;
    private String postalCode;
    private String phoneNumber;
    private String selectedDivisionID;

    private String selectedCountry;
    private int selectedCountryID;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        name = "";
        address = "";
        postalCode = "";
        phoneNumber = "";
        selectedDivisionID = "";

        prepopulateCustomerID();
        prepopulateCountryOptions();
        divisionMenu.setDisable(true);
    }

    // Pre-populates an untaken customer ID
    private void prepopulateCustomerID() {
        try {
            customerID = 1;

            // DB Query
            String query = "SELECT MAX(Customer_ID) FROM customers";
            Statement st = JDBC.connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            while(rs.next()) {
                customerID= rs.getInt(1);
            }

            customerID++;
            custIDTextField.setText("" + customerID);
        }
        catch (SQLException e) {
            System.out.println("Error retrieving Customer_IDs from the database");
        }
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
                MenuItem menuItem = new MenuItem(rs.getString(1));

                // Create the different onAction events based off country selection
                menuItem.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        selectedCountry = menuItem.getText();
                        countryMenu.setText(menuItem.getText());

                        // Reset the division selection when a country is selected
                        divisionMenu.setDisable(false);
                        divisionMenu.setText("Select");
                        divisionMenu.getItems().clear();
                        selectedDivisionID = null;

                        // Get the country ID that will be used for pre-populating the division selection
                        try {
                            // DB Query
                            String query = "SELECT Country_ID FROM countries WHERE Country = '" + selectedCountry + "'";
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

    @FXML
    private void addCustomer() {
        name = custNameTextField.getText();
        address = custAddressTextField.getText();
        postalCode = custPostalCodeTextField.getText();
        phoneNumber = custPhoneNumberTextField.getText();

        if(!name.isEmpty() && !address.isEmpty() && (selectedCountry != null) && (selectedDivisionID != null) && !postalCode.isEmpty() && !phoneNumber.isEmpty()) {
            // Add customer to DB
            try {
                SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

                String createDate = utcFormat.format(new Date());
                String lastUpdate = utcFormat.format(new Date());

                String createdBy = "script";
                String lastUpdatedBy = "script";

                // Retrieves current logged in, used for auditing the user that created and last updated the customer record
                try {
                    createdBy = JDBC.connection.getMetaData().getUserName();
                    lastUpdatedBy = JDBC.connection.getMetaData().getUserName();
                }
                catch (SQLException e) {
                    System.out.println("Error retrieving metadata information to retrieve current username.");
                }

                // DB Query
                String update = "INSERT INTO customers VALUES (" + customerID
                                                                + ", '" + name + "', '"
                                                                + address + "', '"
                                                                + postalCode + "', '"
                                                                + phoneNumber + "', '"
                                                                + createDate + "', '"
                                                                + createdBy + "', '"
                                                                + lastUpdate + "', '"
                                                                + lastUpdatedBy + "', "
                                                                + selectedDivisionID + ")";
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
            alert.show();
        }
    }

    // Closes current scene and switches back to the main controller
    @FXML
    private void returnToMainController() {
        try {
            // Load the FXML file.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Scheduler/View_Controller/MainController.fxml"));
            MainController controller = new MainController();
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
        Stage currentStage = (Stage)addCustAnchorPane.getScene().getWindow();
        currentStage.close();
    }
}
