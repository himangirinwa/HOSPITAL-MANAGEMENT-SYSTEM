import java.sql.*;

public class Authenticator {

    Statement stmt;
    Connection con;
    ResultSet result;
    PasswordEncrypter pass;

    // constructor
    Authenticator() {
        try {
            // establishing connection with the database
            con = ConnectionProvider.getConn();
            stmt = con.createStatement();

            try {
                pass = new PasswordEncrypter();
            } catch (Exception e) {
                System.out.println(e);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        if (con != null)
            con.close();
        if (stmt != null)
            stmt.close();
        if (result != null)
            result.close();
    }

    // admin login
    void adminLogin(String password) {
        String hashCode = pass.returnHash(password);

        try {
            result = stmt.executeQuery("SELECT password FROM admin_credentials");

            if (result.next()) {
                String actualHash = result.getString("password");
                if (hashCode.equals(actualHash)) {
                    // providing admin funtionalities after correct login
                    Admin a = new Admin();
                    a.menu();
                } else
                    System.out.println("INVALID PASSWORD !");
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // doctor login
    void doctorLogin(int id, String password) {
        String hashCode = pass.returnHash(password);

        try {
            result = stmt.executeQuery("SELECT password FROM doctor_login WHERE doctor_id=\"" + id + "\"");

            if (result.next()) {
                String actualHash = result.getString("password");
                if (hashCode.equals(actualHash)) {
                    // providing admin funtionalities after correct login
                    Doctor doct = new Doctor(id);
                    doct.menu();
                } else
                    System.out.println("INVALID PASSWORD!");
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // patient login
    void patientLogin(String contact, String password) {
        String hashCode = pass.returnHash(password);

        try {
            result = stmt.executeQuery("SELECT password FROM patient_login WHERE patientId=\"" + contact + "\"");

            if (result.next()) {
                String actualHash = result.getString("password");
                if (hashCode.equals(actualHash)) {
                    // providing admin funtionalities after correct login
                    Patient pat = new Patient(contact);
                    pat.menu();
                } else
                    System.out.println("INVALID PASSWORD!");
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
