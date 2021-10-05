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
    @FXML Button editButton;
    @FXML Button deleteButton;
    @FXML Button exitButton;

    private Customer selectedCustomer;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setButtonOnActions();
        setCellFactoryValues();
        loadTable();

        // Edit button is only visible when a customer is selected within the view
        editButton.setVisible(false);

        // Add a listener to the tableView
        customerTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if(newSelection != null) {
                editButton.setVisible(true);
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

    private void loadTable() {
        // Fetch all of the customers
        try {
            // DB Query
            String query = "SELECT * FROM customers";
            Statement st = JDBC.connection.createStatement();
            ResultSet rs = st.executeQuery(query);

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

    // Closes the current window
    private void closeCurrentWindow() {
        Stage currentStage = (Stage)viewCustomerAnchorPane.getScene().getWindow();
        currentStage.close();
    }

    // Closes the screen and switches to MainController where the appointment calendar is displayed
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

    // TODO: Switch to modifying customer record
    // Closes the screen and switches to UpdateCustomerController where the customer's records can be modified
    private void switchToUpdateCustomer() throws IOException {
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

    private void setButtonOnActions() {
        // TODO: IMPLEMENT RECORD MODIFICATION
        editButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

            }
        });

        // TODO: IMPLEMENT RECORD DELETION
        // TODO: Pop dialogue if error deleting due to dependencies
        deleteButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    // DB Query
                    System.out.println("Customer ID = " + selectedCustomer.getID());
                    String query = "DELETE FROM customers WHERE Customer_ID = '" + selectedCustomer.getID() + "';";
                    Statement st = JDBC.connection.createStatement();
                    ResultSet rs = st.executeQuery(query);
                }
                catch(SQLException e) {
                    System.out.println("Error deleting record from database");
                }
            }
        });

        exitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    switchToMainController();
                }
                catch(IOException e) {
                    System.out.println("Error switching to Main Controller");
                }
            }
        });
    }
}
