/**
 * This class is used to display all Customers that exist within the database. These customers can be selected to be then updated/deleted
 * while on the form.
 *
 * @author Henry Trieu
 */

package Scheduler.View_Controller;
import Model.Customer;
import helper.JDBC;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class ViewCustomerController implements Initializable {
    // AnchorPane
    @FXML AnchorPane viewCustomerAnchorPane;

    // TableView
    @FXML TableView<Customer> customerTableView;

    // TableColumns
    @FXML TableColumn idColumn;
    @FXML TableColumn nameColumn;
    @FXML TableColumn addressColumn;
    @FXML TableColumn postalCodeColumn;
    @FXML TableColumn phoneNumberColumn;
    @FXML TableColumn divisionColumn;

    // Buttons
    @FXML Button editButton;
    @FXML Button deleteButton;
    @FXML Button exitButton;

    private Customer selectedCustomer;

    // Variable for tracking the user logged in
    private final int loggedUserID;

    /**
     * Initializes the properties associated with the Table displaying the Customers and then loads the Customers into the table.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeTableColumns();
        loadCustomers();
        initializeTableViewListener();

        // Edit and Delete buttons are only visible when a change is made to a customer record
        editButton.setVisible(false);
        deleteButton.setVisible(false);
    }

    public ViewCustomerController(int loggedUserID) {
        this.loggedUserID = loggedUserID;
    }

    /**
     * Adds a listener to the table via a Lambda expression to handle hiding/displaying the edit customer and delete customer buttons.
     */
    private void initializeTableViewListener() {
        customerTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if(newSelection != null) {
                selectedCustomer = customerTableView.getSelectionModel().getSelectedItem();
                editButton.setVisible(true);
                deleteButton.setVisible(true);
            }
        });
    }

    /**
     * Sets the accepted attributes associated with the table columns used for displaying Customer information.
     */
    private void initializeTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        postalCodeColumn.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        divisionColumn.setCellValueFactory(new PropertyValueFactory<>("divisionName"));
    }

    /**
     * Fetches all the customers from the DB and loads it into the TableView
     */
    private void loadCustomers() {
        // Clear the table and selection before loading/reloading customers into the table
        customerTableView.getItems().clear();
        customerTableView.getSelectionModel().clearSelection();
        deleteButton.setVisible(false);

        // Fetch all the customers
        try {
            // DB Query
            String query = "SELECT * FROM customers";
            Statement st = JDBC.connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            // Fetch each customer and add them as an object to the TableView
            while (rs.next()) {
                int ID = rs.getInt("Customer_ID");
                String name = rs.getString("Customer_Name");
                String address = rs.getString("Address");
                String postalCode = rs.getString("Postal_Code");
                String phoneNumber = rs.getString("Phone");
                int divisionID = rs.getInt("Division_ID");

                Customer tempCustomer = new Customer(ID, name, address, postalCode, phoneNumber, divisionID);

                customerTableView.getItems().add(tempCustomer);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching customers from database.");
        }
    }

    /**
     * Deletes the selected Customer if the Customer does not have any Appointments associated.
     */
    @FXML
    private void deleteCustomer() {
        // Only delete the customer if the customer does not have an appointment
        if(!selectedCustomer.hasAppointments()) {
            try {
                // DB Query
                String update = "DELETE FROM customers WHERE Customer_ID = " + selectedCustomer.getID();
                Statement st = JDBC.connection.createStatement();
                st.executeUpdate(update);

                // Re-load the table after deleting the customer from the DB and hide the edit button since there is no
                // customer selected
                loadCustomers();
                editButton.setVisible(false);

                // Display a custom message to confirm successful deletion
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Customer Deleted");
                alert.setContentText(selectedCustomer.getName() + " has been deleted successfully.");
                alert.show();
            }
            catch(SQLException e) {
                System.out.println("Error deleting record from database");
            }
        }
        // Display an error letting the user know that the associated customer has existing appointments
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Deleting Customer");
            alert.setContentText(selectedCustomer.getName() + " could not be deleted because the customer has existing appointments.\n\n" +
                    "Please remove all existing appointments and try again.");

            alert.show();
        }
    }

    /**
     * Closes the page and switches to the UpdateCustomerController screen where the customer's information can be updated
     */
    @FXML
    private void switchToUpdateCustomerController() throws IOException {
        // Load the FXML file.
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Scheduler/View_Controller/UpdateCustomerController.fxml"));
        UpdateCustomerController controller = new UpdateCustomerController(selectedCustomer, loggedUserID);
        loader.setController(controller);
        Parent root = loader.load();

        // Close the current window and build the UpdateCustomerController scene
        closeCurrentWindow();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
    }


    /**
     * Closes current scene and switches back to the main controller
     */
    @FXML
    private void switchToMainController() throws IOException {
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

    /**
     * Closes the current window. This method is called within returnToMainController() as a helper function.
     */
    private void closeCurrentWindow() {
        Stage currentStage = (Stage)viewCustomerAnchorPane.getScene().getWindow();
        currentStage.close();
    }
}
