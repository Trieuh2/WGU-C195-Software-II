/**
 * Represents an appointment with all the respective attributes that will be/has already been added to the database.
 *
 * @author Henry Trieu
 */

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

    /**
     * Default constructor. This does not initialize any values by default.
     */
    public Appointment() {

    }

    /**
     * This is the overloaded constructor used to represent Appointments as they exist in the database. This constructor
     * is particularly used in containing retrieved Appointment information from the database and then used to display
     * them in a table later.
     *
     * @param appointmentID is the unique ID associated with the Appointment.
     * @param title is the name of the Appointment.
     * @param description provides a brief written representation of the event.
     * @param location is where the Appointment is taking place.
     * @param type categorizes the Appointment based on characteristics of the event.
     * @param utcStartTimestamp is the timestamp of the start of the event in UTC timezone.
     * @param utcEndTimestamp is the timestamp of the start of the event in UTC timezone.
     * @param customerID is the unique ID associated with the customer that is attending the Appointment.
     * @param userID is the unique ID associated with the user that is attending the Appointment.
     * @param contactID is the unique associated with the internal organization contact that is attending the Appointment.
     */
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
        this.localZonedDateTimeStart = utcZonedDateTimeStart.withZoneSameInstant(ZoneId.systemDefault());
        setStartTimestamps();

        this.utcZonedDateTimeEnd = ZonedDateTime.parse(utcEndTimestamp + " " + ZoneId.of("UTC"), formatter);
        this.localZonedDateTimeEnd = utcZonedDateTimeEnd.withZoneSameInstant(ZoneId.systemDefault());
        setEndTimestamps();
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

    public ZonedDateTime getUtcStartZDT() {
        return utcZonedDateTimeStart;
    }

    public String getUtcStartTimestamp() {

        return this.utcStartTimestamp;
    }

    public ZonedDateTime getLocalStartZDT() {
        return localZonedDateTimeStart;
    }

    public String getLocalStartTimestamp() {
        return localStartTimestamp;
    }

    public ZonedDateTime getUtcEndZDT() {
        return utcZonedDateTimeEnd;
    }

    public String getUtcEndTimestamp() {
        return utcEndTimestamp;
    }

    public ZonedDateTime getLocalEndZDT() {
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
    public void setAppointmentID(int appointmentID) {
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

    /**
     * Sets the start of the Appointment in the form of a ZonedDateTime in both local time associated with the end-user's machine
     * (used for displaying information for the end-user) and also in UTC (used for storing information on the database).
     * This method also calls the setStartTimestamps() method which defines the Appointment start time timestamp in local and UTC time.
     *
     * @param year is the year that the Appointment starts on.
     * @param month is the month that the Appointment starts on.
     * @param dayOfMonth is the day that the Appointment starts on.
     * @param hour is the hour that the Appointment starts on.
     * @param minute is the minute that the Appointment starts on.
     * @param second is the that the Appointment starts on.
     */
    public void setStartZDTs(int year, int month, int dayOfMonth, int hour, int minute, int second) {
        LocalDateTime localDateTime = LocalDateTime.of(year, month, dayOfMonth, hour, minute, second);
        this.localZonedDateTimeStart = localDateTime.atZone(ZoneId.systemDefault());
        this.utcZonedDateTimeStart = this.localZonedDateTimeStart.withZoneSameInstant(ZoneId.of("UTC"));
        setStartTimestamps();
    }

    /**
     * Sets the local and UTC timestamp of the start of the Appointment in the format accepted by the database.
     */
    public void setStartTimestamps() {
        DateTimeFormatter utcDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter localDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm a");
        this.utcStartTimestamp = utcDateTimeFormatter.format(utcZonedDateTimeStart);
        this.localStartTimestamp = localDateTimeFormatter.format(localZonedDateTimeStart);
    }

    /**
     * Sets the end of the Appointment in the form of a ZonedDateTime in both local time associated with the end-user's machine
     * (used for displaying information for the end-user) and also in UTC (used for storing information on the database).
     * This method also calls the setEndTimestamps() method which defines the Appointment start time timestamp in local and UTC time.
     *
     * @param year is the year that the Appointment ends on.
     * @param month is the month that the Appointment ends on.
     * @param dayOfMonth is the day that the Appointment ends on.
     * @param hour is the hour that the Appointment ends on.
     * @param minute is the minute that the Appointment ends on.
     * @param second is the second that the Appointment ends on.
     */
    public void setEndZDTs(int year, int month, int dayOfMonth, int hour, int minute, int second) {
        LocalDateTime localDateTime = LocalDateTime.of(year, month, dayOfMonth, hour, minute, second);
        this.localZonedDateTimeEnd = localDateTime.atZone(ZoneId.systemDefault());
        this.utcZonedDateTimeEnd = localZonedDateTimeEnd.withZoneSameInstant(ZoneId.of("UTC"));
        setEndTimestamps();
    }

    /**
     * Sets the local and UTC timestamp of the end of the Appointment in the format accepted by the database.
     */
    public void setEndTimestamps() {
        DateTimeFormatter utcDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter localDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm a");
        this.utcEndTimestamp = utcDateTimeFormatter.format(utcZonedDateTimeEnd);
        this.localEndTimestamp = localDateTimeFormatter.format(localZonedDateTimeEnd);
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

    /**
     * Sets the name of the Customer that is attending the Appointment, identified by the customerID.
     *
     * @param customerID is the unique ID associated with the Customer that is attending the Appointment.
     */
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

    /**
     * Sets the name of the Contact that is attending the Appointment, identified by the contactID.
     *
     * @param contactID is the unique ID associated with the Contact that is attending the Appointment.
     */
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

    /**
     * Compares if the selected start time of the Appointment occurs after the current time on the end-user's machine.
     *
     * @return true if the start time occurs after the current time and false if the start time occurs before the current time.
     */
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

    /**
     * Compares if the selected end time of the Appointment occurs after the requested appointment start time.
     *
     * @return true if the end time occurs after the appointment start time and false if the end time occurs before the start time.
     */
    // Checks if the end time of the Appointment is taking place after the startTime
    public boolean endTimeAfterStartTime() {
        boolean validEndTime = false;

        if(utcZonedDateTimeEnd.isAfter(utcZonedDateTimeStart)) {
            validEndTime = true;
        }

        return validEndTime;
    }

    /**
     * Checks to see if the requested start time and end times of the Appointment occur within the business hours defined as:
     * 8AM - 10PM EST. Business hours are stored in a ZonedDateTime using the EST zone and then converted to UTC to perform
     * comparison operations against the requested appointment start and end times.
     *
     * @return true if both the start and end times are within business hours OR false if both the start and end times
     * are NOT within the defined business hours.
     */
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

    /**
     * Checks to see if the requested appointment start and end times overlap with an appointment associated with the customer selected.
     *
     * @return true if the requested appointment start and end times cause a conflict in schedule for the selected customer OR
     * false if the requested times are valid.
     */
    public boolean customerOverlappingAppt() {
        boolean overlap = false;

        try {
            // Query all of the Appointments associated with the Customer selected
            String query = "SELECT Start, End FROM appointments WHERE Customer_ID = " + this.customerID + " AND Appointment_ID != " + this.appointmentID;
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
