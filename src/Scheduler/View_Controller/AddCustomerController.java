package Scheduler.View_Controller;
import helper.JDBC;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AddCustomerController implements Initializable {

    // FXML Labels
    @FXML Label customerIDLabel;

    // FXML TextFields
    @FXML TextField customerIDTextField;

    // FXML Menus
    @FXML MenuButton countryMenu;
    @FXML MenuButton stateRegionMenu;

    // Required information for adding a customer
    private int customerID;
    private String customerName;
    private String address;
    private String postalCode;
    private String phoneNumber;
    private String selectedDivisionID;

    private String selectedCountry;
    private int selectedCountryID;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        prepopulateCustomerID();
        prepopulateCountryOptions();
    }

    // Gets the max customerID and adds a new
    private void prepopulateCustomerID() {
        try {
            int newCustomerID = -1;

            // DB Query
            String query = "SELECT MAX(Customer_ID) FROM customers";
            Statement st = JDBC.connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            while(rs.next()) {
                newCustomerID= rs.getInt(1);
            }

            newCustomerID++;
            customerIDTextField.setText("" + newCustomerID);
        }
        catch (SQLException e) {
            System.out.println("Error attempting to fetch customer ID from the database");
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

                        // Get the country ID that will be used for pre-populating the region selection
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

                        prepopulateFirstLevelDivisions(selectedCountryID);
                    }
                });
                countryMenu.getItems().add(menuItem);
            }
        }
        catch (SQLException e){
            System.out.println("Error attempting to fetch countries from the database.");
        }
    }

    // Pre-populates State and Division options based off the country selection
    private void prepopulateFirstLevelDivisions(int id) {
        stateRegionMenu.getItems().clear();

        // Query all the first level divisions from the DB
        try {
            // DB Query
            String query = "SELECT Division FROM first_level_divisions WHERE Country_ID = '" + selectedCountryID + "'";
            Statement st = JDBC.connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            // Add the region options to the menu
            while(rs.next()) {
                MenuItem menuItem = new MenuItem(rs.getString(1));

                // TODO: initialize the selectedDivisionID
                // Set the onAction event
                menuItem.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        try {
                            // Update GUI and set values
                            String divisionName = menuItem.getText();
                            stateRegionMenu.setText(divisionName);

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

                // Add the menuItem to the region menu
                stateRegionMenu.getItems().add(menuItem);
            }
        }
        catch(SQLException e) {
            System.out.println("Error retrieving first level divisions from the database.");
        }
    }

    // TODO:
    private void addCustomer(){

    }
}
