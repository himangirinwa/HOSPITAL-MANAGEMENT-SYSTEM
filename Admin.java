import java.sql.*;
import java.util.*;


//Admin
public class Admin {
    private String firstName, lastName, city, specialization, bloodGrp, contact, query, password;
    HashMap<Integer,String> slotMap; //for slot and timings
    private int id;
    boolean is_present;
    Connection conn;
    Scanner sc;
    Statement smt;
    PreparedStatement ps;
    ResultSet rs;

    //constructor
    Admin(){
        try{
            sc = new Scanner(System.in);
            //connecting to database
            conn = ConnectionProvider.getConn(); 
            smt = conn.createStatement();
        }
        catch(Exception e){
            System.err.println(e);
        }
        // System.out.println("Database Connection established!");

        //initializing hashtable for slot allotment
        //Creating HashMap
        slotMap = new HashMap<Integer,String>();

        slotMap.put(1, "09:45 - 10:05");    slotMap.put(2, "10:06 - 10:25");    slotMap.put(3, "10:26 - 10:45");
        slotMap.put(4, "10:46 - 11:05");    slotMap.put(5, "11:06 - 11:25");    slotMap.put(6, "11:26 - 11:45");
        slotMap.put(7, "12:06 - 12:25");    slotMap.put(8, "01:15 - 01:35");    slotMap.put(9, "01:36 - 02:00");
        slotMap.put(10, "02:00 - 02:20");    slotMap.put(11, "02:21 - 02:40");    slotMap.put(12, "02:41 - 03:00");   
        slotMap.put(13, "03:01 - 03:20");    slotMap.put(14, "03:21 - 03:40");    slotMap.put(15, "03:41 - 04:00");
        slotMap.put(16, "06:00 - 06:20");    slotMap.put(17, "06:21 - 06:40");    slotMap.put(18, "06:41 - 07:00");
        slotMap.put(19, "07:01 - 07:20");    slotMap.put(20, "07:21 - 07:40");    slotMap.put(21, "07:41 - 08:00");
        slotMap.put(22, "08:01 - 08:20");    slotMap.put(23, "08:21 - 08:40");    slotMap.put(24, "08:41 - 09:00");

    }

    //Finalize method is called by Garbage Collector just before the deletion/destroying the object
    @Override
    protected void finalize() throws Throwable {
        //closing connections and scanner
        if(ps!=null)
            ps.close();
        if(conn!=null)
            conn.close();
        if(smt!=null)
            smt.close();
        if(rs!=null)
            rs.close();
        sc.close();
        
    }

    //menu
    public void menu(){

        int choice;
    
        do{
        
            System.out.println("\n\n\n\t----==================== WELCOME ADMIN ====================----\n\n\n");
            System.out.println("1. ADD DOCTOR");
            System.out.println("2. VIEW RECORD OF ALL DOCTORS");
            System.out.println("3. MODIFY A DOCTOR'S RECORD");
            System.out.println("4. DELETE A DOCTOR'S RECORD");
            System.out.println("5. VIEW ALL PATIENTS");
            System.out.println("6. VIEW APPOINTMENTS");
            System.out.println("7. EXIT");
            System.out.print("\nENTER CHOICE : ");
            choice = sc.nextInt();
            
            switch(choice){
                case 1 :
                    this.addDoctor();
                    break;

                case 2 :
                    this.displayDoctors();
                    break;

                case 3 :
                    this.modifyDoctor();
                    break;

                case 4 :
                    this.deleteDoctor();
                    break;

                case 5 :
                    this.viewPatients();
                    break;

                case 6 :
                    this.viewAppointments();
                    break;
                
                case 7 :
                    break;

                default :
                    System.out.println("INVALID CHOICE !");
            }
        }while(choice!=7);

    }

    //Adding doctor to the database
    void addDoctor(){
        System.out.println("\n\n---------------------------------------------------------------------------------\n");
        System.out.println("\n\t\t- ENTER CREDENTIALS -\n");
        System.out.print("\nId NO : ");
        id = sc.nextInt();
        System.out.print("\nFIRST NAME : ");
        firstName = sc.next();
        System.out.print("\nLAST NAME : ");
        lastName = sc.next();
        System.out.print("\nCONTACT NUMBER : ");
        contact = sc.next();
        System.out.print("\nCITY : ");
        city = sc.next();
        System.out.print("\nBLOOD GROUP : ");
        bloodGrp = sc.next();
        System.out.print("\nSPECIALIZATION : ");
        specialization = sc.next();
        System.out.print("\nSET PASSWORD : ");
        password = sc.next();
        

        try{
           //creating doctors table if doesn't exist
            // smt.execute("create table if not exists doctor (id int primary key, firstName varchar(30), lastName varchar(30), city varchar(40), specialization varchar(30), bloodGroup varchar(3), contact varchar(12))");
            
            String queryString = "insert into doctor (id, firstName, lastNAme, city, specialization, bloodGroup, contact) values(?,?,?,?,?,?,?)";
            ps = conn.prepareStatement(queryString);
            //setting values
            ps.setInt(1, id);
            ps.setString(2, firstName);
            ps.setString(3, lastName);
            ps.setString(4, city);
            ps.setString(5, specialization);
            ps.setString(6, bloodGrp);
            ps.setString(7, contact);
            
            //executing our prepared statement
            //commiting all details to doctor's 
            int result = ps.executeUpdate();

            if(result == 1){
                //doctorCredentials Table
                // smt.execute("CREATE TABLE if not exists doctorCredentials (id int NOT NULL, password Varchar(30), PRIMARY KEY (id), FOREIGN KEY (id) REFERENCES doctor(id));");
                queryString = "insert into doctorCredentials (id, password) values(?,?)";
                ps = conn.prepareStatement(queryString);
                //setting values
                ps.setInt(1, id);
                ps.setString(2, password);
                ps.executeUpdate();
                
                System.out.println("\nNEW RECORD INSERTED SUCCESSFULLY!");
            }

        }
        catch(Exception e){
            if(e.getMessage().startsWith("Duplicate")){
                System.out.println("\tCOULDN'T ADD RECORD!");
                System.out.println("\tID IS ALREADY OCCUPIED !!!");
            }
            System.out.println(e);
        }
        
    }

    void displayDoctors(){
        try{
            smt.execute("select * from doctor");
            rs = smt.getResultSet();

            System.out.println("\n\n\n---------------------------------------------------------------------------------\n\n\n");
            
            // System.out.println("ID NO \t FIRST NAME \t\t LAST NAME \t\t CITY \t\t SPECIALIZATION \t\t  bloodGrp \t CONTACT");

            //getting values from the resultset
            while(rs.next()){

                id = rs.getInt("id");
                firstName = rs.getString("firstName");
                city = rs.getString("city");
                specialization = rs.getString("specialization");
                bloodGrp = rs.getString("bloodGroup");
                contact = rs.getString("contact");
                lastName = rs.getString("lastName");

                System.out.println(id + "\t"+ firstName.toUpperCase() + "\t\t" + lastName.toUpperCase()+ "\t\t" + city.toUpperCase() + "\t\t" + specialization.toUpperCase() + "\t\t" + bloodGrp.toUpperCase() + "\t" + contact);
            }
        }
        catch(SQLException e){
            System.out.println("SQL Exception : " + e.getMessage());
            System.out.println("SQL State : " + e.getSQLState());
            System.out.println("Vendor Error : " + e.getErrorCode());
        }
        catch(Exception e){
            System.err.println("Cannot connect to database Server");
        }
    }

    void modifyDoctor() {
        System.out.println("\n\n---------------------------------------------------------------------------------\n");
        System.out.println("\n\t\t- ENTER CREDENTIALS -\n");
        System.out.print("\nENTER ID WHOSE DETAILS ARE TO BE MODIFIED : ");
        id = sc.nextInt();
        System.out.print("ENTER MODIFIED FIRST NAME : ");
        firstName = sc.next();
        System.out.print("ENTER MODIFIED LAST NAME : ");
        lastName = sc.next();
        System.out.print("ENTER MODIFIED CONTACT NUMBER : ");
        contact = sc.next();
        System.out.print("ENTER MODIFIED CITY OF RESIDENCE : ");
        city = sc.next();
        System.out.print("ENTER MODIFIED BLOOD GROUP : ");
        bloodGrp = sc.next();
        System.out.print("ENTER MODIFIED SPECIALIZATION : ");
        specialization = sc.next();

        try{
           
            query = "update doctor set firstName = ?, lastName = ?, contact = ?, city = ?, bloodGroup = ?, specialization = ? WHERE id = ?";

            ps = conn.prepareStatement(query);
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, contact);
            ps.setString(4, city);
            ps.setString(5, bloodGrp);
            ps.setString(6, specialization);
            ps.setInt(7, id);
            //executing update
            ps.executeUpdate();
            
            System.out.println("\n\n\tRECORD MODIFIED SUCCESSFULLY!");

        }
        catch(Exception e){
            System.out.println("Couldn't add the record");
        }
    }


    void deleteDoctor(){
        System.out.println("\n\n---------------------------------------------------------------------------------\n");
        System.out.println("\n\t\t- ENTER CREDENTIALS OF THE RECORD TO BE DELETED -\n");
        System.out.print("ENTER DOCTOR'S ID NO : ");
        id = sc.nextInt();

        try{
            query = "DELETE FROM doctor WHERE id = ?";
            String query2 = "DELETE FROM doctorCredentials WHERE id = ?";
            
            //deleting crdentials from doctorCredentials
            ps = conn.prepareStatement(query2);
            //setting values
            ps.setInt(1, id);
            //deleting data firstly from doctor's credentials table
            ps.executeUpdate();

            //deleting details from doctorCredentials
            ps = conn.prepareStatement(query);
            //setting values
            ps.setInt(1, id);
            //deleting data  from doctor's credentials table
            ps.executeUpdate();

            System.out.println("\n\n\tRECORD DELETED SUCCESSFULLY!");
        }
        catch(Exception e){
            System.out.println("\n\n\tUNABLE TO DELETE RECORD");
        }
    }


    void viewPatients(){
        try{
            smt.execute("select * from patient");
            rs = smt.getResultSet();

            System.out.println("\n\n\n---------------------------------------------------------------------------------\n\n\n");
            while(rs.next()){
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                String contact = rs.getString("contact");
                String problem = rs.getString("problem");
                String city = rs.getString("city");
                String bloodGrp = rs.getString("bloodGrp");
                
                System.out.println(firstName.toUpperCase() + "\t\t" + lastName.toUpperCase() +"\t\t" + contact + "\t\t" + city.toUpperCase() + "\t\t" + bloodGrp.toUpperCase() + "\t" + problem.toUpperCase());
            }
        }
        catch(SQLException e){
            System.out.println("SQL Exception : " + e.getMessage());
            System.out.println("SQL State : " + e.getSQLState());
            System.out.println("Vendor Error : " + e.getErrorCode());
        }
        catch(Exception e){
            System.err.println("Cannot connect to database Server");
        }
    }


    //view Appointments
    void viewAppointments(){
        boolean isEmpty=true;
        System.out.println("\n\n---------------------------------------------------------------------------------\n");
        System.out.print("\n\nENTER DATE FOR WHICH YOUR APPOINTMENTS ARE TO BE VIEWED (IN DDMMYY FORMAT) : ");
        String date = sc.next();

        try{
            //joining 3 tables
            //doctor, appointments, patient
            ps = conn.prepareStatement("SELECT appointments.slot, doctor.firstName, doctor.lastName, patient.firstName, patient.lastName from appointments, patient, doctor where appointments.patientId = patient.contact and appointments.doctorId = doctor.id and appointments.date=?");
            ps.setString(1, date);
            rs = ps.executeQuery();

            //printing result
            System.out.println("\n\n\n\tTIME   \t\tDOCTORS'S NAME \tPATIENT'S NAME\n");
        
            while(rs.next()){
                isEmpty = false;
                System.out.println("\t" + slotMap.get(rs.getInt(1)) + "\t" + rs.getString(2).toUpperCase() + " " + rs.getString(3).toUpperCase() + "\t" + rs.getString(4).toUpperCase() + " " + rs.getString(5).toUpperCase());
            }

            if(isEmpty)
                System.out.println("\n\tNO APPOINTMENTS FOR TODAY!");
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

}
