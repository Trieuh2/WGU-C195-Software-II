package Scheduler.View_Controller;
import helper.JDBC;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class TypeMonthReport implements Initializable {
    // FXML variables
    @FXML GridPane typeReportGridPane;
    @FXML GridPane monthReportGridPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadTypeReport();
        loadMonthReport();
    }

    public TypeMonthReport() {

    }

    // Loads the report filtered by type
    private void loadTypeReport() {
        ArrayList<String> uniqueAppointmentTypes = new ArrayList<String>();
        ArrayList<Integer> appointmentTypeQuantity = new ArrayList<Integer>();

        // Query all appointments and add each unique "Type" to an ArrayList
        try{
            String query = "SELECT * FROM appointments ORDER BY Type ASC";
            Statement st = JDBC.connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            while(rs.next()) {
                if(!uniqueAppointmentTypes.isEmpty()) {
                    if(!uniqueAppointmentTypes.contains(rs.getString("Type"))) {
                        uniqueAppointmentTypes.add(rs.getString("Type"));
                    }
                }
                else{
                    uniqueAppointmentTypes.add(rs.getString("Type"));
                }
            }
        }
        catch(SQLException e) {
            System.out.println("Error retrieving Appointment information from the database.");
        }

        if(!uniqueAppointmentTypes.isEmpty()) {
            // Iterate through the ArrayList and count the number of occurrences by Type
            for(int i = 0; i < uniqueAppointmentTypes.size(); i++) {
                try{
                    String query = "SELECT COUNT(Type) AS quantity FROM appointments WHERE Type = '" + uniqueAppointmentTypes.get(i) + "';";
                    Statement st = JDBC.connection.createStatement();
                    ResultSet rs = st.executeQuery(query);

                    while(rs.next()) {
                        int quantity = rs.getInt("Quantity");
                        appointmentTypeQuantity.add(quantity);
                    }
                }
                catch(SQLException e) {
                    System.out.println("Error retrieving Appointment information from the database.");
                }
            }

            // Iterate through both ArrayLists and add the Type and Quantity to the TableView
            for(int i = 0; i < uniqueAppointmentTypes.size(); i++) {
                Label type = new Label(uniqueAppointmentTypes.get(i));
                type.setPadding(new Insets(5,5,5,5));
                Label quantity = new Label("" + appointmentTypeQuantity.get(i));
                quantity.setPadding(new Insets(5,5,5,5));

                typeReportGridPane.add(type,0,i);
                typeReportGridPane.add(quantity, 1, i);
            }
        }
    }

    // Loads the report filtered by month
    private void loadMonthReport() {
        ArrayList<Integer> quantityByMonth = new ArrayList<Integer>();

        // Initialize the ArrayList
        for(int i = 0; i < 12; i++) {
            quantityByMonth.add(0);
        }

        // Iterate through all the appointments and record the number of appointments occurring in the month
        try {
            String query = "SELECT * FROM appointments ORDER BY Start ASC";
            Statement st = JDBC.connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            while(rs.next()) {
                String utcTimestamp = rs.getString("Start");

                // Get month value of the appointment
                int month = Integer.parseInt(utcTimestamp.substring(5,7));

                // Increment the count by 1 on the month an appointment was found
                quantityByMonth.set(month-1, (quantityByMonth.get(month-1) + 1));
            }
        }
        catch(SQLException e) {
            System.out.println("Error retrieving Appointment information from the database.");
        }

        // Iterate through the ArrayList and display the quantity by month
        for(int i = 0; i < quantityByMonth.size(); i++) {
            String month = "";

            switch (i) {
                case 0: month = "January";
                        break;
                case 1: month = "February";
                    break;
                case 2: month = "March";
                    break;
                case 3: month = "April";
                    break;
                case 4: month = "May";
                    break;
                case 5: month = "June";
                    break;
                case 6: month = "July";
                    break;
                case 7: month = "August";
                    break;
                case 8: month = "September";
                    break;
                case 9: month = "October";
                    break;
                case 10: month = "November";
                    break;
                case 11: month = "December";
                    break;
            }
            // Create the labels that will be added to the GridPane used to display the statistics on the report
            int quantity = quantityByMonth.get(i);
            Label monthLabel = new Label(month);
            monthLabel.setPadding(new Insets(5,5,5,5));
            Label quantityLabel = new Label("" + quantity);
            quantityLabel.setPadding(new Insets(5,5,5,5));

            // Add the labels to the GridPane
            monthReportGridPane.add(monthLabel, 0, i);
            monthReportGridPane.add(quantityLabel, 1, i);
        }
    }
}
