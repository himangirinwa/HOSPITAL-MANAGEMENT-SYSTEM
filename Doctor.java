import java.sql.*;
import java.util.*;

public class Doctor {

    private int id;
    Connection conn;
    Scanner sc;
    Statement smt;
    PreparedStatement ps;
    ResultSet rs;
    PasswordEncrypter pass;

    Doctor(int id) {
        this.id = id;
        try {
            // establishing connection with the database
            sc = new Scanner(System.in);
            conn = ConnectionProvider.getConn();
            smt = conn.createStatement();
            pass = new PasswordEncrypter();
        } catch (Exception e) {
            System.err.println(e);
        }
        // System.out.println("Database Connection established!");

    }

    @Override
    protected void finalize() throws Throwable {
        if (ps != null)
            ps.close();
        if (conn != null)
            conn.close();
        if (smt != null)
            smt.close();
        if (rs != null)
            rs.close();
        sc.close();

    }

    // menu
    public void menu() {
        int choice = 0;
        System.out.println("\n\t\t\t----==================== WELCOME  ====================----");
        while (choice != 3) {
            System.out.println("\n\n\t--- MENU ---\n");
            System.out.println("1. VIEW APPOINTMENTS");
            System.out.println("2. CHANGE PASSWORD");
            System.out.println("3. EXIT");
            System.out.print("\nENTER CHOICE : ");

            choice = sc.nextInt();
            switch (choice) {
            case 1:
                this.viewAppointments();
                break;
            case 2:
                this.changePassword();
                break;
            case 3:
                break;
            default:
                System.out.println("INVALID CHOICE!");
            }
        }

    }

    // view Appointments
    void viewAppointments() {

        System.out.println("\n\n---------------------------------------------------------------------------------\n");
        System.out.print("\n\nENTER DATE FOR WHICH YOUR APPOINTMENTS ARE TO BE VIEWED (IN YYYY-MM-DD FORMAT) : ");
        String date_of_appointments = sc.next();
        try {
            ps = conn.prepareStatement(
                    "select appointments.slot_id, patient_details.firstname, patient_details.lastname, appointments.concern FROM appointments LEFT JOIN patient_details ON appointments.patientId = patient_details.contact WHERE appointments.doctorId=? AND  appointments.date =?");
            ps.setInt(1, this.id);
            ps.setString(2, date_of_appointments);
            rs = ps.executeQuery();

            // checking if the resultset is empty
            if (!rs.isBeforeFirst()) {
                // if the result set is empty
                System.out.println("\n\n\tNO APPOINTEMENTS BOOKED FOR " + date_of_appointments);
                return;
            }

            // viewing appoinments
            System.out.println("\n\n\n-- -- -- - APPOINTMENTS SCHEDULED ON " + date_of_appointments + " - -- -- --");
            System.out.println("\n\tSLOT-ID \tPATIENT'S NAME \t \tCONCERN\n");
            // printing result
            while (rs.next()) {
                System.out.println("\t " + rs.getInt("slot_id") + "\t\t  "
                        + rs.getString("patient_details.firstname").toUpperCase() + " "
                        + rs.getString("patient_details.lastname").toUpperCase() + " \t\t"
                        + rs.getString("appointments.concern").toUpperCase());
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    void changePassword() {
        System.out.println("\n\n---------------------------------------------------------------------------------\n");
        System.out.println("\n\t\t- RESET PASSWORD -\n");
        System.out.print("\nENTER YOUR CURRENT PASSWORD TO CONFIRM : ");
        String current_password = sc.next();
        try {
            String hashCode = pass.returnHash(current_password);

            smt.execute("SELECT password FROM doctor_login WHERE doctor_id=" + this.id);
            rs = smt.getResultSet();
            if (rs.next()) {
                String actualHash = rs.getString("password");
                if (hashCode.equals(actualHash)) {
                    // allowing to change password
                    System.out.print("\nNEW PASSWORD : ");
                    String new_password = sc.next();
                    System.out.print("\nCONFIRM PASSWORD : ");
                    String confirmed_password = sc.next();
                    if (!new_password.equals(current_password)) {

                        if (new_password.equals(confirmed_password)) {

                            // updating password into the database
                            String new_hash = pass.returnHash(new_password);
                            String query2 = "UPDATE doctor_login SET password=\"" + new_hash + "\" WHERE doctor_id="
                                    + this.id;
                            ps = conn.prepareStatement(query2);
                            int result = ps.executeUpdate();
                            if (result == 1)
                                System.out.println("\n\tPASSWORD CHANGED SUCCESSFULLY!!");
                        } else {
                            System.out.println("\n\tPASSWORDS DO NOT MATCH!");
                        }
                    } else {
                        System.out.println("\n\tSAME PASSWORD");
                    }
                } else
                    System.out.println("\tINVALID PASSWORD!");
            }

        } catch (Exception e) {
            System.out.println("\n\tTHERE WAS ERROR IN CHANING YOUR PASSWORD!");
            System.out.println(e);
        }
    }

}
