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

    public int getDivisionID() {
        return divisionID;
    }

    public String getDivisionName() {
        return divisionName;
    }

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
}
