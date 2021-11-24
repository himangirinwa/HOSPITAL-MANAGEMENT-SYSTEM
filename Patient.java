import java.io.*;
import java.sql.*;
import java.util.*;

public class Patient {
    private String contact;
    String query;
    Scanner sc;
    Connection conn;
    Statement smt;
    PreparedStatement ps;
    ResultSet rs;
    PasswordEncrypter pass;

    Patient(String contact) {
        this.contact = contact;
        try {
            sc = new Scanner(System.in);
            // connecting to database
            conn = ConnectionProvider.getConn();
            smt = conn.createStatement();

            pass = new PasswordEncrypter();
        } catch (Exception e) {
            System.err.println(e);
        }
        // System.out.println("Database Connection established!");

    }

    // Finalize method is called by Garbage Collector just before the
    // deletion/destroying the object
    @Override
    protected void finalize() throws Throwable {
        // closing connections and scanner
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

    void patientSignup() {
        // patient signup
        System.out.println("\n\n---------------------- WELCOME ---------------------------------");
        System.out.println("\n- - ENTER YOUR DETAILS - -");
        System.out.print("\nENTER FIRST NAME : ");
        String firstName = sc.next();
        System.out.print("\nENTER LAST NAME : ");
        String lastName = sc.next();
        System.out.print("\nENTER CONTACT NUMBER : ");
        String contact = sc.next();
        System.out.print("\nENTER MAIL ID : ");
        String mailid = sc.next();
        System.out.print("\nENTER BLOOD GROUP : ");
        String bloodGrp = sc.next();
        System.out.print("\nENTER CITY : ");
        String city = sc.next();
        System.out.print("\nSET PASSWORD : ");
        String password = sc.next();

        try {
            // adding details to the database
            String query = "INSERT INTO patient_details (firstname, lastname, contact, bloodgrp, mail_id, city) VALUES(?,?,?,?,?,?)";
            ps = conn.prepareStatement(query);
            // setting values
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, contact);
            ps.setString(4, bloodGrp);
            ps.setString(5, mailid);
            ps.setString(6, city);

            // executing our prepared statement
            // commiting all details to doctor_details table
            int result = ps.executeUpdate();
            if (result == 1) {
                String password_hash = pass.returnHash(password);

                String query2 = "INSERT INTO patient_login (patientId, password) VALUES(?,?)";
                ps = conn.prepareStatement(query2);
                // setting values
                ps.setString(1, contact);
                ps.setString(2, password_hash);

                result = ps.executeUpdate();
                if (result == 1) {
                    System.out.println("\n\tREGISTRATION SUCCESSFULL!");
                    this.contact = contact;
                    this.menu();
                } else
                    System.out.println("SOME ERROR OCCURED, COULD ADD YOU TO THE SYSTEM. \nPLEASE TRY AGAIN LATER");
            } else
                System.out.println("SOME ERROR OCCURED, COULD ADD YOU TO THE SYSTEM. \nPLEASE TRY AGAIN LATER");
        } catch (Exception e) {

            if (e.getMessage().startsWith("Duplicate"))
                System.out.println("CONTACT ALREADY EXIST !");
            else {
                System.out.println("SOME ERROR OCCURED, COULD ADD YOU TO THE SYSTEM. \nPLEASE TRY AGAIN LATER");
                System.out.println(e);
            }

        }
    }

    // menu
    public void menu() throws IOException {
        int choice = 0;
        do {
            System.out.println("\n\n==================== WELCOME ====================");
            System.out.println("\n1. BOOK APPOINTMENT");
            System.out.println("2. MODIFY YOUR DETAILS");
            System.out.println("3. CHANGE PASSWORD");
            System.out.println("4. EXIT");
            System.out.print("ENTER CHOICE : ");
            choice = sc.nextInt();

            switch (choice) {
            case 1:
                this.bookAppointment();
                break;

            case 2:
                this.modifyPatientDetails();
                break;

            case 3:
                this.changePassword();
                break;

            case 4:
                break;

            default:
                System.out.println("INVALID CHOICE!");
            }
        } while (choice != 4);
    }

    // booking an appointment
    public void bookAppointment() {

        System.out.println("\n\n---------------------------------------------------------------------------------\n");

        try {
            query = "select doctor_details.doctor_id, doctor_details.firstname, doctor_details.lastname, doctor_specialization.spcialized_in from doctor_details LEFT JOIN doctor_specialization ON doctor_details.doctor_id = doctor_specialization.doctor_id ORDER BY doctor_specialization.spcialized_in";
            rs = smt.executeQuery(query);
            System.out.println("\n\t\t- - OUR DOCTORS - -");
            System.out.println("\nID \tFIRSTNAME \tLASTNAME \tSPECIALIZATION \n");

            while (rs.next()) {

                System.out.println(rs.getString("doctor_id") + "\t" + rs.getString("firstname").toUpperCase() + "\t\t"
                        + rs.getString("lastname").toUpperCase() + "\t\t"
                        + rs.getString("spcialized_in").toUpperCase());
            }

            System.out.print("\n\n\tENTER DOCTOR ID WITH WHOM YOU WANT TO BOOK YOUR APPOINTMENT : ");
            int doctor_id = sc.nextInt();
            System.out.print("\n\tDATE OF APPOINTMENT (YYYY-MM-DD FORMAT): ");
            String date_of_appointment = sc.next();

            // checking for empty slots
            ps = conn.prepareStatement(
                    "SELECT slots.slot_id, slots.start_time, slots.end_time FROM slots WHERE slots.slot_id NOT IN (SELECT slot_id from appointments where doctorId=? and date=?)");
            ps.setInt(1, doctor_id);
            ps.setString(2, date_of_appointment);
            rs = ps.executeQuery();

            // checking if the resultset is empty
            if (!rs.isBeforeFirst()) {
                // if the result set is empty
                System.out.println("NO SLOTS AVAILABLE ON " + date_of_appointment);
                return;
            }

            // returning empty slots if available
            System.out.println("\n\n\tAVAILABLE SLOTS :");
            System.out.println("\n\tSLOT.NO \t START TIME  \t END TIME");
            while (rs.next()) {

                int slot_id = rs.getInt("slot_id");
                String slot_start_time = rs.getString("start_time");
                String slot_end_time = rs.getString("end_time");

                System.out.println("\t" + slot_id + "\t\t" + slot_start_time + "\t" + slot_end_time);
            }

            System.out.print("\n\nENTER SLOT ID TO BE BOOKED : ");
            int slot_id = sc.nextInt();

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            System.out.print("\n\nENTER YOUR CONCERN IN SHORT (AT MAX 75 CHARACTERS) : ");
            String visit_concern = br.readLine();

            ps = conn.prepareStatement(
                    "INSERT INTO appointments (patientId, doctorId, date, slot_id, concern) values (?, ?, ?, ?, ?)");
            ps.setString(1, this.contact);
            ps.setInt(2, doctor_id);
            ps.setString(3, date_of_appointment);
            ps.setInt(4, slot_id);
            ps.setString(5, visit_concern);
            int result = ps.executeUpdate();

            if (result == 1) {
                System.out.println("\n\n\t\tSLOT BOOKED SUCCESSFULLY!");
            } else {
                System.out.println("\n\n\tCOULDN'T BOOK YOUR SLOT! TRY AGAIN LATER!");
            }

            br.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // change password
    public void changePassword() {
        System.out.println("\n\n---------------------------------------------------------------------------------\n");
        System.out.println("\n\t\t- RESET PASSWORD -\n");
        System.out.print("\nENTER YOUR CURRENT PASSWORD TO CONFIRM : ");
        String current_password = sc.next();
        try {
            String hashCode = pass.returnHash(current_password);

            smt.execute("SELECT password FROM patient_login WHERE patientId=\"" + this.contact + "\"");
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
                            String query2 = "UPDATE patient_login SET password=\"" + new_hash + "\" WHERE patientId=\""
                                    + this.contact + "\"";
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

    // update credentials
    public void modifyPatientDetails() {
        System.out.println("\n\n---------------------------------------------------------------------------------\n");
        System.out.println("\n\n\t---- CHOOSE FIELD TO BE UPDATED ----\n\n\n");
        System.out.println("1. FIRST NAME");
        System.out.println("2. LAST NAME");
        System.out.println("3. CONTACT NUMBER");
        System.out.println("4. MAIL ID");
        System.out.println("5. BLOOD GROUP");
        System.out.println("6. CITY");
        System.out.println("7. RETURN");
        System.out.print("\nENTER CHOICE : ");
        int choice = sc.nextInt();

        if (choice < 7 && choice > 0) {
            System.out.print("\n--------------------------------------------------\n");
            String input, query;
            int result = 0;
            try {
                switch (choice) {
                // Updating First Name
                case 1:
                    System.out.print("\nENTER NEW FIRST NAME : ");
                    input = sc.next();
                    query = "UPDATE patient_details SET firstname=\"" + input + "\" WHERE contact=\"" + this.contact
                            + "\"";
                    result = smt.executeUpdate(query);
                    break;

                // Updating Last Name
                case 2:
                    System.out.print("\n\nENTER NEW LAST NAME : ");
                    input = sc.next();
                    query = "UPDATE patient_details SET lastname=\"" + input + "\" WHERE contact=\"" + this.contact
                            + "\"";
                    result = smt.executeUpdate(query);
                    break;

                // Updating Contact Number
                case 3:
                    System.out.print("\n\nENTER NEW CONTACT NUMBER : ");
                    input = sc.next();
                    query = "UPDATE patient_details SET contact=\"" + input + "\" WHERE contact=\"" + this.contact
                            + "\"";
                    this.contact = input;
                    result = smt.executeUpdate(query);
                    break;

                // Updating Mail Id
                case 4:
                    System.out.print("\n\nENTER NEW MAIL ID : ");
                    input = sc.next();
                    query = "UPDATE patient_details SET mail_id=\"" + input + "\" WHERE contact=\"" + this.contact
                            + "\"";
                    result = smt.executeUpdate(query);
                    break;

                case 5:
                    System.out.print("\n\nENTER NEW BLOOD GROUP  : ");
                    input = sc.next();
                    query = "UPDATE patient_details SET bloodgrp=\"" + input + "\" WHERE contact=\"" + this.contact
                            + "\"";
                    result = smt.executeUpdate(query);
                    break;

                case 6:
                    System.out.print("\n\nENTER NEW CITY : ");
                    input = sc.next();
                    query = "UPDATE patient_details SET city=\"" + input + "\" WHERE contact=\"" + this.contact + "\"";
                    result = smt.executeUpdate(query);
                    break;

                default:
                    System.out.print("\n\n\tINVALID CHOICE");
                }

                if (result == 1)
                    System.out.println("\n\tUPDATION SUCCESSFULL!!");
                else {
                    System.out.println("COULDN'T UPDATE THE RECORD!");
                }

            } catch (Exception e) {
                System.out.println("COULDN'T UPDATE THE RECORD!");
                System.out.println(e);
            }
        }

    }

}
