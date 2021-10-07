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

    // Cell Columns
    @FXML TableColumn idColumn;
    @FXML TableColumn nameColumn;
    @FXML TableColumn addressColumn;
    @FXML TableColumn postalCodeColumn;
    @FXML TableColumn phoneNumberColumn;
    @FXML TableColumn divisionColumn;

    // Buttons
    @FXML Button saveButton;
    @FXML Button deleteButton;
    @FXML Button exitButton;

    private Customer selectedCustomer;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setCellFactoryValues();
        loadTable();

        // Save button is only visible when a change is made to a customer record
        saveButton.setVisible(false);

        // Delete button is only visible when a customer selection is made
        deleteButton.setVisible(false);

        // Add a listener to the tableView
        customerTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if(newSelection != null) {
                deleteButton.setVisible(true);
                selectedCustomer = customerTableView.getSelectionModel().getSelectedItem();
            }
        });
    }

    private void setCellFactoryValues() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        postalCodeColumn.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        divisionColumn.setCellValueFactory(new PropertyValueFactory<>("divisionName"));
    }

    // DONE
    // Fetches all the customers from the DB and loads it into the TableView
    private void loadTable() {
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

    @FXML
    private void deleteCustomer() {
        // Only delete the customer if the customer does not have an appointment
        if(!selectedCustomer.hasAppointments()) {
            try {
                // DB Query
                String update = "DELETE FROM customers WHERE Customer_ID = " + selectedCustomer.getID();
                Statement st = JDBC.connection.createStatement();
                st.executeUpdate(update);

                // Re-load the table after deleting the customer from the DB
                loadTable();

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

    // DONE
    // Closes the current window
    private void closeCurrentWindow() {
        Stage currentStage = (Stage)viewCustomerAnchorPane.getScene().getWindow();
        currentStage.close();
    }

    // DONE
    // Closes the screen and switches to MainController where the appointment calendar is displayed
    @FXML
    private void switchToMainController() throws IOException {
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
}
