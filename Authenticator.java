import java.sql.*;

public class Authenticator {

    Statement stmt;
    Connection con;
    ResultSet result;

    //constructor
    Authenticator(){
        try{ 
            //establishing connection with the database   
            con = ConnectionProvider.getConn(); 
            stmt = con.createStatement();      
        }
        catch(Exception e){ System.out.println(e);}
    }

    @Override
    protected void finalize() throws Throwable {
        if(con!=null)
            con.close();
        if(stmt!=null)
            stmt.close();
        if(result!=null)
            result.close();
    }

    //doctor login
    void doctorLogin(int id, String password) {
        try{
            result = stmt.executeQuery("select id from doctorCredentials where id='"+id+"' and password='"+password+"'");
            
            //if credentials are valid, the logging in into the account
            if(result.next()){   
                
                Doctor doctor = new Doctor(id, password);
                doctor.menu();
            }
            else{
                System.out.println("INVALID LOGIN ID OR PASSWORD !");
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
    }


    //patient login
    void patientLogin(String contact, String password, String problem) {
        try {
            if(!problem.equalsIgnoreCase("same")){
                stmt.executeUpdate("update patient set problem='"+problem+"'");
            }                
            
            //check patient table , if exists and retrieve all data
            result = stmt.executeQuery("select patientId, password from patientcrdentials where patientId='"+contact+"' and password='"+password+"'"); 
            
            
            if(result.next()){   
                //patient exists 
                // result.next();
                String sql = "select * from patient where contact='"+result.getString(1)+"'";
                // System.out.println(sql);
                result = stmt.executeQuery(sql);
                result.next();
    
                // fetch all data of patient
                Patient patient = new Patient(result.getString("firstName"),  result.getString("lastName"),  result.getString("contact"), result.getString("problem"), result.getString("city"), password,  result.getString("bloodGrp"));
                //provinding patatient functionalities after successful login
                patient.menu();
            }
            else{
                System.out.println("INVALID LOGIN ID OR PASSWORD !");
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
}
