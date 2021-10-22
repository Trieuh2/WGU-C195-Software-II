package Model;

import helper.JDBC;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Appointment {
    // Time values are stored in UTC
    private int appointmentID;
    private String title;
    private String description;
    private String location;
    private String type;
    private String createDate;
    private String createdBy;
    private String lastUpdate;
    private String lastUpdatedBy;
    private int customerID;
    private String customerName;
    private int userID;
    private int contactID;
    private String contactName;

    // Start Time variables
    private ZonedDateTime localZonedDateTimeStart;
    private String localStartTimestamp;
    private ZonedDateTime utcZonedDateTimeStart;
    private String utcStartTimestamp;

    // End Time variables
    private ZonedDateTime localZonedDateTimeEnd;
    private String localEndTimestamp;
    private ZonedDateTime utcZonedDateTimeEnd;
    private String utcEndTimestamp;

    // Constructors
    public Appointment() {

    }

    public Appointment(int appointmentID, String title, String description, String location,
                       String type, String utcStartTimestamp, String utcEndTimestamp, int customerID, int userID, int contactID) {
        // Set object variables
        this.appointmentID = appointmentID;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.customerID = customerID;
        setCustomerName(customerID);
        this.userID = userID;
        this.contactID = contactID;
        setContactName(contactID);

        // Parse the timestamp value from the DB into ZonedDateTime objects in UTC and Local Time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
        this.utcZonedDateTimeStart = ZonedDateTime.parse(utcStartTimestamp + " " + ZoneId.of("UTC"), formatter);
        setUtcStartTimestamp();
        this.localZonedDateTimeStart = utcZonedDateTimeStart.withZoneSameInstant(ZoneId.systemDefault());
        setLocalStartTimestamp();

        this.utcZonedDateTimeEnd = ZonedDateTime.parse(utcEndTimestamp + " " + ZoneId.of("UTC"), formatter);
        setUtcEndTimestamp();
        this.localZonedDateTimeEnd = utcZonedDateTimeEnd.withZoneSameInstant(ZoneId.systemDefault());
        setLocalEndTimestamp();
    }

    // Accessor methods
    public int getAppointmentID() {
        return appointmentID;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getType() {
        return type;
    }

    public ZonedDateTime getUtcZonedDateTimeStart() {
        return utcZonedDateTimeStart;
    }

    public String getUtcStartTimestamp() {

        return this.utcStartTimestamp;
    }

    public ZonedDateTime getLocalZonedDateTimeStart() {
        return localZonedDateTimeStart;
    }

    public String getLocalStartTimestamp() {
        return localStartTimestamp;
    }

    public ZonedDateTime getUtcZonedDateTimeEnd() {
        return utcZonedDateTimeEnd;
    }

    public String getUtcEndTimestamp() {
        return utcEndTimestamp;
    }

    public ZonedDateTime getLocalZonedDateTimeEnd() {
        return localZonedDateTimeEnd;
    }

    public String getLocalEndTimestamp() {
        return localEndTimestamp;
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

    public int getCustomerID() {
        return customerID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public int getUserID() {
        return userID;
    }

    public int getContactID() {
        return contactID;
    }

    public String getContactName() {
        return contactName;
    }

    // Mutator methods
    public void setID(int appointmentID) {
        this.appointmentID = appointmentID;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUtcZonedDateTimeStart(int year, int month, int dayOfMonth, int hour, int minute, int second) {
        LocalDateTime localDateTime = LocalDateTime.of(year, month, dayOfMonth, hour, minute, second);
        this.localZonedDateTimeStart = localDateTime.atZone(ZoneId.systemDefault());
        this.utcZonedDateTimeStart = this.localZonedDateTimeStart.withZoneSameInstant(ZoneId.of("UTC"));
        setUtcStartTimestamp();
    }

    public void setUtcStartTimestamp() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.utcStartTimestamp = dateTimeFormatter.format(utcZonedDateTimeStart);
    }

    public void setLocalStartTimestamp() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm a");
        this.localStartTimestamp = dateTimeFormatter.format(localZonedDateTimeStart);
    }

    public void setUtcZonedDateTimeEnd(int year, int month, int dayOfMonth, int hour, int minute, int second) {
        LocalDateTime localDateTime = LocalDateTime.of(year, month, dayOfMonth, hour, minute, second);
        this.localZonedDateTimeEnd = localDateTime.atZone(ZoneId.systemDefault());
        this.utcZonedDateTimeEnd = localZonedDateTimeEnd.withZoneSameInstant(ZoneId.of("UTC"));
        setUtcEndTimestamp();
    }

    public void setLocalEndTimestamp() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm a");
        this.localEndTimestamp = dateTimeFormatter.format(localZonedDateTimeEnd);
    }

    public void setUtcEndTimestamp() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.utcEndTimestamp = dateTimeFormatter.format(utcZonedDateTimeEnd);
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

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public void setCustomerName(int customerID) {
        try {
            String query = "SELECT Customer_Name FROM customers WHERE Customer_ID = " + customerID;
            Statement st = JDBC.connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            while(rs.next()) {
                this.customerName = rs.getString(1);
            }
        }
        catch(SQLException e) {
            System.out.println("Error retrieving Customer information from the database.");
        }
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setContactID(int contactID) {
        this.contactID = contactID;
    }

    public void setContactName(int contactID) {
        try {
            String query = "SELECT Contact_Name FROM contacts WHERE Contact_ID = " + contactID;
            Statement st = JDBC.connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            while(rs.next()) {
                this.contactName = rs.getString(1);
            }
        }
        catch(SQLException e) {
            System.out.println("Error retrieving Contact information from the database.");
        }
    }

    // Checks if the start time of the Appointment is taking place in the future
    public boolean startTimeInFuture() {
        boolean startsInFuture = false;

        LocalDateTime now = LocalDateTime.now();
        ZonedDateTime nowZonedDateTime = now.atZone(ZoneId.systemDefault());
        ZonedDateTime nowUtcDateTime = nowZonedDateTime.withZoneSameInstant(ZoneId.of("UTC"));

        if(utcZonedDateTimeStart.isAfter(nowUtcDateTime)) {
            startsInFuture = true;
        }
        return startsInFuture;
    }

    // Checks if the end time of the Appointment is taking place after the startTime
    public boolean endTimeAfterStartTime() {
        boolean validEndTime = false;

        if(utcZonedDateTimeEnd.isAfter(utcZonedDateTimeStart)) {
            validEndTime = true;
        }

        return validEndTime;
    }

    // Checks to ensure that the start and end time of the Appointment is within the business hours of
    // 8am - 10pm EST
    // 5am - 7pm PST
    // 12pm - 2am (next day) UTC
    public boolean isWithinBusinessHours() {
        boolean validHours = false;

        // Business Start Time variables
        LocalDateTime businessStart = LocalDateTime.of(localZonedDateTimeStart.getYear(), localZonedDateTimeStart.getMonth(), localZonedDateTimeStart.getDayOfMonth(), 8, 0 , 0);
        ZonedDateTime zonedBusinessStart = businessStart.atZone(ZoneId.of("America/New_York"));
        ZonedDateTime utcBusinessStart = zonedBusinessStart.withZoneSameInstant(ZoneId.of("UTC"));

        // Business End Time variables
        LocalDateTime businessEnd = LocalDateTime.of(localZonedDateTimeStart.getYear(), localZonedDateTimeStart.getMonth(), localZonedDateTimeStart.getDayOfMonth(), 22, 0, 0);
        ZonedDateTime zonedBusinessEnd = businessEnd.atZone(ZoneId.of("America/New_York"));
        ZonedDateTime utcBusinessEnd = zonedBusinessEnd.withZoneSameInstant(ZoneId.of("UTC"));

        // Check if the start time is on or after the business start time but not at the end of the business time
        if( (utcZonedDateTimeStart.isEqual(utcBusinessStart) || (utcZonedDateTimeStart.isAfter(utcBusinessStart) && utcZonedDateTimeStart.isBefore(utcBusinessEnd)))) {
            // Check if end time is on or before the business end time but not on or before the start of the business time
            if ( (utcZonedDateTimeEnd.isEqual(utcBusinessEnd) || (utcZonedDateTimeEnd.isBefore(utcBusinessEnd) && utcZonedDateTimeEnd.isAfter(utcBusinessStart))) ) {
                validHours = true;
            }
        }

        return validHours;
    }

    // Checks to see if the start time and end time is overlapping with an appointment associated with the customer selected
    public boolean overlapsCustomer() {
        boolean overlap = false;

        try {
            // Query all of the Appointments associated with the Customer selected
            String query = "SELECT Start, End FROM appointments WHERE Customer_ID = " + this.customerID;
            Statement st = JDBC.connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            // Check to see if the selected appointment time is conflicting with existing appointments with the Customer
            while(rs.next()) {
                // Retrieve the timestamps from the DB
                String utcCustApptStartTimestamp = rs.getString("Start");
                String utcCustApptEndTimestamp = rs.getString("End");

                // Parse the timestamp value from the DB into ZonedDateTime objects in UTC and Local Time
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
                ZonedDateTime utcCustApptStart = ZonedDateTime.parse(utcCustApptStartTimestamp + " " + ZoneId.of("UTC"), formatter);
                ZonedDateTime utcCustApptEnd = ZonedDateTime.parse(utcCustApptEndTimestamp + " " + ZoneId.of("UTC"), formatter);

                // Only perform the comparison if the Appointment date is the same
                if(utcZonedDateTimeStart.getDayOfYear() == utcCustApptStart.getDayOfYear()) {
                    // Compare the selected Appointment times to see if they overlap {
                    if(utcZonedDateTimeStart.isBefore(utcCustApptEnd)) {
                        overlap = true;
                    }
                }
            }
        }
        catch(SQLException e) {
            System.out.println("Error retrieving Appointment information from the database.");
        }

        return overlap;
    }
}
