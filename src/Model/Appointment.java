package Model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Appointment {
    // Required fields to add an Appointment record in the DB

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
    private int userID;
    private int contactID;

    // Start Time variables
    private ZonedDateTime localZonedDateTimeStart;
    private ZonedDateTime utcZonedDateTimeStart;

    // End Time variables
    private ZonedDateTime localZonedDateTimeEnd;
    private ZonedDateTime utcZonedDateTimeEnd;

    // Default Constructor
    public Appointment() {

    }

    // Accessor methods
    public int getID() {
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

    public String getStartTimestamp() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String utcStartTimestamp = dateTimeFormatter.format(utcZonedDateTimeStart);

        return utcStartTimestamp;
    }

    public String getEndTimestamp() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String utcEndTimestamp = dateTimeFormatter.format(utcZonedDateTimeEnd);

        return utcEndTimestamp;
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

    public int getUserID() {
        return userID;
    }

    public int getContactID() {
        return contactID;
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
    }

    public void setUtcZonedDateTimeEnd(int year, int month, int dayOfMonth, int hour, int minute, int second) {
        LocalDateTime localDateTime = LocalDateTime.of(year, month, dayOfMonth, hour, minute, second);
        this.localZonedDateTimeEnd = localDateTime.atZone(ZoneId.systemDefault());
        this.utcZonedDateTimeEnd = localZonedDateTimeEnd.withZoneSameInstant(ZoneId.of("UTC"));
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

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setContactID(int contactID) {
        this.contactID = contactID;
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
    // TODO:
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
}
