/**
 * This class represents a report that displays an audited trace of when Appointments were created, updated, or deleted.
 * The timestamps on the report are generated on the local time based on the end-user's machine.
 *
 * @author Henry Trieu
 */

package Scheduler.View_Controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.io.*;
import java.net.URL;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class AppointmentActivityController implements Initializable {
    @FXML GridPane logGridPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadAppointmentLog();
    }
    /**
     * Reads the audit log associated with the Appointments within the file "appointment_activity.txt" and parses the file
     *  into Labels that are displayed on the report table.
     */
    private void loadAppointmentLog() {
        File file = new File("appointment_activity.txt");

        // Creating an object of BuffferedReader class
        try {
            BufferedReader br= new BufferedReader(new FileReader(file));

            String line;
            int counter = 0;

            while((line = br.readLine()) != null) {
                String[] delimited = line.split(",");

                // Labels containing the information regarding the Appointment Activity log being displayed
                Label timestampLabel = new Label(convertUtcTimestampToLocal(delimited[0]));
                timestampLabel.setPadding(new Insets(5,5,5,5));

                Label appointmentIdLabel = new Label(delimited[1]);
                appointmentIdLabel.setPadding(new Insets(5,5,5,5));

                Label titleLabel = new Label(delimited[2]);
                titleLabel.setPadding(new Insets(5,5,5,5));

                Label actionLabel = new Label(delimited[3]);
                actionLabel.setPadding(new Insets(5,5,5,5));

                // Add the labels to the GridPane
                logGridPane.add(timestampLabel, 0, counter);
                logGridPane.add(appointmentIdLabel, 1, counter);
                logGridPane.add(titleLabel, 2, counter);
                logGridPane.add(actionLabel, 3, counter);

                // Increment the counter
                counter++;
            }
        }
        catch(FileNotFoundException e) {
            System.out.println("Error, couldn't find file \"appointment_activity.txt\"");
        }
        catch(IOException e) {
            System.out.println("Error processing appointment_activity.txt.");
        }
    }


    /**
     * This method takes in a String in UTC format and returns the timestamp converted into local timezone in a 12h format.
     *
     * @param utcTimestamp is the 24h timestamp in UTC regarding the associated Appointment action (create/update/delete)
     * @return a timestamp in the end-user's current timezone in a 12h format
     */
    private String convertUtcTimestampToLocal(String utcTimestamp) {
        DateTimeFormatter utcFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
        DateTimeFormatter localFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm a");

        ZonedDateTime utcZonedDateTime = ZonedDateTime.parse(utcTimestamp + " " + ZoneId.of("UTC"), utcFormatter);
        ZonedDateTime localZonedDateTime = utcZonedDateTime.withZoneSameInstant(ZoneId.systemDefault());

        String localTimestamp = localFormatter.format(localZonedDateTime);

        return localTimestamp;
    }
}
