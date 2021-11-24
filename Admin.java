import java.sql.*;
import java.util.*;

//Admin
public class Admin {
    // private String firstName, lastName, city, specialization, bloodGrp, contact,
    // query, password;
    private int id;
    boolean is_present;
    Connection conn;
    Scanner sc;
    Statement smt;
    PreparedStatement ps;
    ResultSet rs;
    PasswordEncrypter pass;

    // constructor
    Admin() {
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

    // menu
    public void menu() {

        int choice;

        do {

            System.out.println("\n\n\n\t----==================== WELCOME ADMIN ====================----\n\n\n");
            System.out.println("1. ADD DOCTOR");
            System.out.println("2. ADD SPECILIZATION FOR A DOCTOR");
            System.out.println("3. VIEW RECORD OF ALL DOCTORS");
            System.out.println("4. VIEW ALL DOCTORS SPECIALIZATION");
            System.out.println("5. MODIFY A DOCTOR'S RECORD");
            System.out.println("6. DELETE A DOCTOR'S RECORD");
            System.out.println("7. CHANGE PASSWORD");
            System.out.println("8. VIEW ALL PATIENTS");
            System.out.println("9. VIEW APPOINTMENTS");
            System.out.println("10. EXIT");
            System.out.print("\nENTER CHOICE : ");
            choice = sc.nextInt();

            switch (choice) {
            case 1:
                this.addDoctor();
                break;

            case 2:
                this.addSpecilization();
                break;

            case 3:
                this.displayDoctors();
                break;

            case 4:
                this.viewSpecilizations();
                break;

            case 5:
                this.modifyDoctor();
                break;

            case 6:
                this.deleteDoctor();
                break;

            case 7:
                this.changePassword();
                break;

            case 8:
                this.viewPatients();
                break;

            case 9:
                this.viewAppointments();
                break;

            case 10:
                break;

            default:
                System.out.println("INVALID CHOICE !");
            }
        } while (choice != 10);

    }

    // Adding doctor to the database
    void addDoctor() {
        System.out.println("\n\n---------------------------------------------------------------------------------\n");
        System.out.println("\n\t\t- ENTER CREDENTIALS -\n");
        System.out.print("\nId NO : ");
        int id = sc.nextInt();
        System.out.print("\nFIRST NAME : ");
        String firstName = sc.next();
        System.out.print("\nLAST NAME : ");
        String lastName = sc.next();
        System.out.print("\nCONTACT NUMBER : ");
        String contact = sc.next();
        System.out.print("\nMAIL ID : ");
        String mailid = sc.next();
        System.out.print("\nBLOOD GROUP : ");
        String bloodGrp = sc.next();
        System.out.print("\nCITY : ");
        String city = sc.next();
        System.out.print("\nSALARY : ");
        int salary = sc.nextInt();
        System.out.print("\nSPECIALIZATION : ");
        String specialization = sc.next();
        System.out.print("\nSET PASSWORD : ");
        String password = sc.next();

        try {

            String query = "INSERT INTO doctor_details (doctor_id, firstName, lastname, bloodgrp, contact, city, mail_id, salary) VALUES(?,?,?,?,?,?,?,?)";
            ps = conn.prepareStatement(query);
            // setting values
            ps.setInt(1, id);
            ps.setString(2, firstName);
            ps.setString(3, lastName);
            ps.setString(4, bloodGrp);
            ps.setString(5, contact);
            ps.setString(6, city);
            ps.setString(7, mailid);
            ps.setInt(8, salary);

            // executing our prepared statement
            // commiting all details to doctor_details table
            int result = ps.executeUpdate();

            // after successfull insertion in doctor details table
            // inserting further data into doctor_login and doctor specialization
            if (result == 1) {
                // doctorlogin
                query = "INSERT INTO doctor_login (doctor_id, password) values(?,?)";
                ps = conn.prepareStatement(query);
                // setting values
                ps.setInt(1, id);
                String password_hash = pass.returnHash(password);
                ps.setString(2, password_hash);
                int result1 = ps.executeUpdate();

                // adding specialization
                query = "INSERT INTO doctor_specialization (doctor_id, spcialized_in) values(?,?)";
                ps = conn.prepareStatement(query);
                ps.setInt(1, id);
                ps.setString(2, specialization);
                int result2 = ps.executeUpdate();

                // if insertion is succesfull in all the relations
                if (result1 == 1 && result2 == 1) {
                    System.out.println("\n\tNEW RECORD INSERTED SUCCESSFULLY!");
                }
            } else {
                System.out.println("\n\tCOULDN'T ADD RECORD!");
            }

        } catch (Exception e) {
            if (e.getMessage().startsWith("Duplicate")) {
                System.out.println("\tCOULDN'T ADD RECORD!");
                System.out.println("\tID IS ALREADY OCCUPIED !!!");
            }
            System.out.println(e);
        }

    }

    void addSpecilization() {
        System.out.println("\n\n---------------------------------------------------------------------------------\n");
        System.out.println("\n\t\t- ENTER CREDENTIALS -\n");
        System.out.print("\nDOCTOR'S ID NO : ");
        int id = sc.nextInt();
        System.out.print("\nSPECIALIZATION : ");
        String specilized_in = sc.next();
        try {
            String query = "INSERT INTO doctor_specialization (doctor_id, spcialized_in) VALUES(?,?)";
            ps = conn.prepareStatement(query);
            // setting values
            ps.setInt(1, id);
            ps.setString(2, specilized_in);

            // executing our prepared statement
            // commiting all details to doctor_details table
            int result = ps.executeUpdate();
            if (result == 1)
                System.out.println("\n\tRECORD ADDED SUCCESSFULLY!!");
        } catch (Exception e) {
            System.out.println("\n\tSOME ERROR OCCURED, COULDN'T ADD RECORD!");
        }
    }

    void displayDoctors() {
        try {
            smt.execute("select * from doctor_details");
            rs = smt.getResultSet();

            System.out.println(
                    "\n\n\n---------------------------------------------------------------------------------------------------------------------------------------------------\n\n\n");

            System.out.println(
                    "ID NO \t FIRST NAME \t LAST NAME\t CONTACT \t MAIL ID \t\t BLOOD GROUP \t\t CITY \t\t SALARY");

            // getting values from the resultset
            while (rs.next()) {

                int id = rs.getInt("doctor_id");
                String firstname = rs.getString("firstname");
                String lastname = rs.getString("lastname");
                String contact = rs.getString("contact");
                String mailid = rs.getString("mail_id");
                String bloodGrp = rs.getString("bloodgrp");
                String city = rs.getString("city");
                int salary = rs.getInt("salary");

                System.out.println(id + "\t" + firstname.toUpperCase() + "\t\t" + lastname.toUpperCase() + "\t\t"
                        + contact + "\t" + mailid + "\t\t" + bloodGrp.toUpperCase() + "\t\t" + city.toUpperCase()
                        + "\t\t" + salary);
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception : " + e.getMessage());
            System.out.println("SQL State : " + e.getSQLState());
            System.out.println("Vendor Error : " + e.getErrorCode());
        } catch (Exception e) {
            System.err.println("Cannot connect to database Server");
        }
    }

    void viewSpecilizations() {
        try {
            smt.execute(
                    "SELECT doctor_specialization.doctor_id, doctor_details.firstname,  doctor_details.lastname, doctor_specialization.spcialized_in, doctor_details.contact FROM doctor_details LEFT JOIN doctor_specialization ON doctor_details.doctor_id = doctor_specialization.doctor_id ORDER BY doctor_specialization.doctor_id;");
            rs = smt.getResultSet();

            System.out.println(
                    "\n\n\n---------------------------------------------------------------------------------------------------------------------------------------------------\n\n\n");

            System.out.println("ID NO \t FIRST NAME \t LAST NAME\t SPECILIZATION \t\t CONTACT");

            // getting values from the resultset
            while (rs.next()) {

                int id = rs.getInt("doctor_id");
                String firstname = rs.getString("firstname");
                String lastname = rs.getString("lastname");
                String specilization = rs.getString("spcialized_in");
                String contact = rs.getString("contact");

                System.out.println(id + "\t" + firstname.toUpperCase() + "\t\t" + lastname.toUpperCase() + "\t\t"
                        + specilization + "\t\t" + contact);
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception : " + e.getMessage());
            System.out.println("SQL State : " + e.getSQLState());
            System.out.println("Vendor Error : " + e.getErrorCode());
        } catch (Exception e) {
            System.err.println("Cannot connect to database Server");
        }
    }

    void modifyDoctor() {
        System.out.println("\n\n---------------------------------------------------------------------------------\n");
        System.out.println("\n\n\t---- CHOOSE FIELD TO BE UPDATED ----\n\n\n");
        System.out.println("1. FIRST NAME");
        System.out.println("2. LAST NAME");
        System.out.println("3. BLOOD GROUP");
        System.out.println("4. CONTACT NUMBER");
        System.out.println("5. MAIL ID");
        System.out.println("6. CITY");
        System.out.println("7. SALARY");
        System.out.println("8. DELETE SPECILIZATION");
        System.out.println("9. RETURN");
        System.out.print("\nENTER CHOICE : ");
        int choice = sc.nextInt();

        if (choice < 9 && choice > 0) {
            System.out.print("\n--------------------------------------------------\n\n");
            System.out.print("\n\nENTER DOCTOR'S ID NUMBER : ");
            int id = sc.nextInt();
            String input, query;
            int result = 0;
            try {
                switch (choice) {
                case 1:
                    System.out.print("\n\nENTER UPDATED FIRST NAME : ");
                    input = sc.next();
                    query = "UPDATE doctor_details SET firstname=\"" + input + "\" WHERE doctor_id=" + id;
                    result = smt.executeUpdate(query);
                    break;

                case 2:
                    System.out.print("\n\nENTER UPDATED LAST NAME : ");
                    input = sc.next();
                    query = "UPDATE doctor_details SET lastname=\"" + input + "\" WHERE doctor_id=" + id;
                    result = smt.executeUpdate(query);
                    break;

                case 3:
                    System.out.print("\n\nENTER UPDATED BLOOD GROUP : ");
                    input = sc.next();
                    query = "UPDATE doctor_details SET bloodgrp=\"" + input + "\" WHERE doctor_id=" + id;
                    result = smt.executeUpdate(query);
                    break;

                case 4:
                    System.out.print("\n\nENTER UPDATED CONTACT NUMBER : ");
                    input = sc.next();
                    query = "UPDATE doctor_details SET contact=\"" + input + "\" WHERE doctor_id=" + id;
                    result = smt.executeUpdate(query);
                    break;

                case 5:
                    System.out.print("\n\nENTER UPDATED MAIL ID : ");
                    input = sc.next();
                    query = "UPDATE doctor_details SET mail_id=\"" + input + "\" WHERE doctor_id=" + id;
                    result = smt.executeUpdate(query);
                    break;

                case 6:
                    System.out.print("\n\nENTER UPDATED CITY : ");
                    input = sc.next();
                    query = "UPDATE doctor_details SET city=\"" + input + "\" WHERE doctor_id=" + id;
                    result = smt.executeUpdate(query);
                    break;

                case 7:
                    System.out.print("\n\nENTER UPDATED SALARY : ");
                    int salary = sc.nextInt();
                    query = "UPDATE doctor_details SET salary=" + salary + " WHERE doctor_id=" + id;
                    result = smt.executeUpdate(query);
                    break;

                case 8:
                    System.out.print("\n\nENTER SPECILAIZATION TO BE DELETED : ");
                    input = sc.next();
                    query = "DELETE FROM doctor_specialization WHERE spcialized_in=\"" + input + "\" AND doctor_id="
                            + id;
                    result = smt.executeUpdate(query);
                    break;

                default:
                    System.out.print("\n\n\tINVALID CHOICE");
                }

                if (result == 1)
                    System.out.println("\n\tUPDATION SUCCESSFULL!!");

            } catch (Exception e) {
                System.out.println("COULDN'T UPDATE THE RECORD!");
                System.out.println(e);
            }
        }
    }

    void deleteDoctor() {
        System.out.println("\n\n---------------------------------------------------------------------------------\n");
        System.out.println("\n\t\t- ENTER CREDENTIALS OF THE RECORD TO BE DELETED -\n");
        System.out.print("ENTER DOCTOR'S ID NO : ");
        id = sc.nextInt();

        try {
            String query = "DELETE FROM doctor_details WHERE doctor_id = ?";

            // deleting crdentials from doctorCredentials
            ps = conn.prepareStatement(query);
            // setting values
            ps.setInt(1, id);
            // deleting data firstly from doctor's credentials table
            int result = ps.executeUpdate();

            if (result == 1)
                System.out.println("\n\tRECORD DELETED SUCCESSFULLY!");
            else
                System.out.println("\n\tUNABLE TO DELETE RECORD");
        } catch (Exception e) {
            System.out.println("\n\n\tUNABLE TO DELETE RECORD");
            System.out.println(e);
        }
    }

    void viewPatients() {
        try {
            System.out.println(
                    "\n\n\n---------------------------------------------------------------------------------\n\n\n");

            smt.execute("SELECT firstname, lastname, contact, bloodgrp, mail_id, city FROM patient_details");
            rs = smt.getResultSet();

            if (!rs.isBeforeFirst()) {
                // if the result set is empty
                System.out.println("\n\n\tNO PATIENT RECORD AVAILABLE!");
                return;
            }
            System.out.println("\t\t \t- -- -- - PATIENT DETAILS - --  -- -\n\n");
            System.out.println("\tPATIENT NAME\t CONTACT NO\tMAIL ID\t\t \t\tBLOOD GROUP   CITY \n");

            // viewing details if exist
            while (rs.next()) {
                System.out.println(
                        "\t" + rs.getString("firstname").toUpperCase() + " " + rs.getString("lastname").toUpperCase()
                                + "\t " + rs.getString("contact") + "\t" + rs.getString("mail_id") + "\t \t"
                                + rs.getString("bloodgrp") + "\t\t" + rs.getString("city").toUpperCase());
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception : " + e.getMessage());
            System.out.println("SQL State : " + e.getSQLState());
            System.out.println("Vendor Error : " + e.getErrorCode());
        } catch (Exception e) {
            System.err.println("Cannot connect to database Server");
        }
    }

    // view Appointments
    void viewAppointments() {
        System.out.println("\n\n---------------------------------------------------------------------------------\n");
        System.out.print("\n\nENTER DATE FOR WHICH APPOINTMENTS ARE TO BE VIEWED (IN YYYY-MM-DD FORMAT) : ");
        String date_of_appointments = sc.next();
        try {
            ps = conn.prepareStatement(
                    "SELECT  appointments.slot_id, doctor_details.firstName, doctor_details.lastName, patient_details.firstname, patient_details.lastname, appointments.concern FROM doctor_details JOIN appointments ON appointments.doctorId = doctor_details.doctor_id JOIN patient_details ON appointments.patientId = patient_details.contact WHERE appointments.date = ? ORDER BY appointments.doctorId, appointments.slot_id");
            ps.setString(1, date_of_appointments);
            rs = ps.executeQuery();

            // checking if the resultset is empty
            if (!rs.isBeforeFirst()) {
                // if the result set is empty
                System.out.println("\n\n\tNO APPOINTEMENTS BOOKED FOR " + date_of_appointments);
                return;
            }

            // viewing appoinments
            System.out.println(
                    "\n\n\n\t\t-- -- -- - APPOINTMENTS SCHEDULED ON " + date_of_appointments + " - -- -- --\n\n");
            System.out.println("\n\tSLOT-ID \t DOCTOR'S NAME \t\t PATIENT'S NAME \t \tCONCERN\n");
            // printing result
            while (rs.next()) {
                System.out.println("\t " + rs.getInt("appointments.slot_id") + "  \t\t "
                        + rs.getString("doctor_details.firstName") + " " + rs.getString("doctor_details.lastName")
                        + "\t\t" + rs.getString("patient_details.firstname").toUpperCase() + " "
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

            // rs = smt.executeQuery("SELECT password FROM admin_credentials");
            smt.execute("SELECT password FROM admin_credentials");
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
                            String query = "DELETE FROM admin_credentials";
                            ps = conn.prepareStatement(query);
                            ps.executeUpdate();

                            String new_hash = pass.returnHash(new_password);
                            String query2 = "INSERT INTO admin_credentials VALUES (\"" + new_hash + "\")";
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
