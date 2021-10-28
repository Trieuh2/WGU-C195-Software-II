/**
 * Represents a Customer with all the respective attributes that will be/has already been added to the database.
 *
 * @author Henry Trieu
 */

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

    private String createDate;
    private String createdBy;
    private String lastUpdate;
    private String lastUpdatedBy;

    /**
     * Default constructor
     */
    public Customer() {

    }

    /**
     * Constructor used for displaying Customers in the ViewCustomerController Class. Within this constructor, the division
     * name is also defined by identifying the name associated with the selected division ID.
     *
     * @param ID is the unique ID of the Customer, used for identification.
     * @param name is the name of the Customer.
     * @param address is the address of the Customer.
     * @param postalCode is the postal code of the Customer.
     * @param phoneNumber is the phone number of the Customer.
     * @param divisionID is the division ID of the Customer, used to identify where the Customer lives.
     */
    public Customer(int ID, String name, String address, String postalCode, String phoneNumber, int divisionID) {
        this.ID = ID;
        this.name = name;
        this.address = address;
        this.postalCode = postalCode;
        this.phoneNumber = phoneNumber;
        this.divisionID = divisionID;

        // Use the divisionID to set the divisionName and CountryID
        try {
            // DB Query
            String query = "SELECT Division, Country_ID FROM first_level_divisions WHERE Division_ID = '" + this.divisionID + "'";
            Statement st = JDBC.connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            while(rs.next()) {
                // Pull the divisionName and countryID from DB
                this.divisionName = rs.getString(1);
                this.countryID = rs.getInt(2);
            }
        }
        catch(SQLException e) {
            System.out.println("Error fetching divisions from the database.");
        }

        // Use the CountryID to set the CountryName
        try {
            // DB Query
            String query = "SELECT Country FROM countries WHERE Country_ID = '" + this.countryID + "'";
            Statement st = JDBC.connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            while(rs.next()) {
                // Pull the divisionName and countryID from DB
                this.countryName = rs.getString(1);
            }
        }
        catch(SQLException e) {
            System.out.println("Error retrieving country names from the database.");
        }
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

    public String getCreateDate() {
        return createDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    /**
     * Checks if the customer has any appointments associated within the database.
     *
     * @return true if there are appointments associated with this Customer, false if there are no appointments associated
     * with this Customer.
     */
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

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    // Sets the Country ID based off the Division ID used to create the Customer
    public void setCountryID(int countryID) {
        this.countryID = countryID;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public void setDivisionID(int divisionID) {
        this.divisionID = divisionID;
    }

    // Sets the Division Name based off of the Division ID used to create the Customer
    public void setDivisionName(String divisionName) {
        this.divisionName = divisionName;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }
}
