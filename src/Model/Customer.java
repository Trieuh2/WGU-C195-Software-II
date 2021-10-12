package Model;

import helper.JDBC;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Customer {
    private int ID;
    private String name;
    private String address;
    private String postalCode;
    private String phoneNumber;
    private int countryID;
    private String countryName;
    private int divisionID;
    private String divisionName;

    // Constructors
    public Customer() {

    }

    public Customer(int ID, String name, String address, String postalCode, String phoneNumber, int divisionID) {
        this.ID = ID;
        this.name = name;
        this.address = address;
        this.postalCode = postalCode;
        this.phoneNumber = phoneNumber;
        this.divisionID = divisionID;
        setDivisionName();
        setCountryID();
        setCountryName();
    }

    // Accessor methods
    public int getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getCountryID() {
        return countryID;
    }

    public String getCountryName() {
        return countryName;
    }

    public int getDivisionID() {
        return divisionID;
    }

    public String getDivisionName() {
        return divisionName;
    }

    // Checks if the customer has any appointments associated
    public boolean hasAppointments() {
        boolean hasAppointments = false;
        try {
            // DB Query
            String query = "SELECT * FROM appointments WHERE Customer_ID = " + this.ID;
            Statement st = JDBC.connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            while(rs.next()) {
                hasAppointments = true;
            }
        }
        catch(SQLException e) {
            System.out.println("Error retrieving appointments for selected user.");
        }

        return hasAppointments;
    }

    // Sets the Division Name based off of the Division ID used to create the Customer
    private void setDivisionName() {
        try {
            // DB Query
            String query = "SELECT Division FROM first_level_divisions WHERE Division_ID = '" + this.divisionID + "'";
            Statement st = JDBC.connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            while(rs.next()) {
                divisionName = rs.getString(1);
            }
        }
        catch(SQLException e) {
            System.out.println("Error fetching division name from database.");
        }
    }

    // Sets the Country ID based off the Division ID used to create the Customer
    private void setCountryID() {
        try {
            // DB Query
            String query = "SELECT COUNTRY_ID FROM first_level_divisions WHERE Division_ID = '" + this.divisionID + "'";
            Statement st = JDBC.connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            while(rs.next()) {
                countryID = rs.getInt(1);
            }
        }
        catch(SQLException e) {
            System.out.println("Error fetching Country_ID for selected customer from database.");
        }
    }

    private void setCountryName() {
        if(countryID == 1) {
            countryName = "U.S";
        }
        else if(countryID == 2) {
            countryName = "UK";
        }
        else {
            countryName = "Canada";
        }
    }
}
