import java.sql.*;
import java.util.*;

public class Doctor {
    
    private String password, query;
    //for slot and timings
    HashMap<Integer,String> slotMap;
    int id;
    Connection conn;
    Scanner sc;
    Statement smt;
    PreparedStatement ps;
    ResultSet rs;

    Doctor(int id, String password){
        try{
            sc = new Scanner(System.in);  
            conn = ConnectionProvider.getConn();
            smt = conn.createStatement();
        }
        catch(Exception e){
            System.err.println(e);
        }
        // System.out.println("Database Connection established!");

        this.id = id;
        this.password = password;

        //HashMap for slot allotment
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

    @Override
    protected void finalize() throws Throwable {
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
        int choice=0;
        System.out.println("\n\t\t\t----==================== WELCOME  ====================----");
        while(choice!=3){
            System.out.println("\n\n\t--- MENU ---\n");
            System.out.println("1. VIEW APPOINTMENTS");
            System.out.println("2. CHANGE PASSWORD");
            System.out.println("3. EXIT");
            System.out.print("\nENTER CHOICE : ");
            
            choice = sc.nextInt();
            switch(choice){
                case 1 :
                    this.viewAppointments();
                    break;
                case 2 :
                    this.changePassword();
                    break;
                case 3 :
                    break;
                default :
                    System.out.println("INVALID CHOICE!");
            }
        }

    }


    //view Appointments
    void viewAppointments(){
        boolean isEmpty=true;
        System.out.println("\n\n---------------------------------------------------------------------------------\n");
        System.out.print("\n\nENTER DATE FOR WHICH YOUR APPOINTMENTS ARE TO BE VIEWED (IN DDMMYY FORMAT) : ");
        String date = sc.next();
        try{
            ps = conn.prepareStatement("SELECT appointments.slot, patient.firstName, patient.lastName, patient.problem from appointments, patient where appointments.patientId = patient.contact and appointments.doctorId =? and appointments.date=?");
            ps.setInt(1, this.id);
            ps.setString(2, date);
            rs = ps.executeQuery();

            System.out.println("\n\n\n\tTIME   \t\tPATIENT'S NAME \tCONCERN\n");
            //printing result
            while(rs.next()){
                isEmpty = false;
                System.out.println("\t" + slotMap.get(rs.getInt("slot")) + "\t" + rs.getString("firstName").toUpperCase() + " " + rs.getString("lastName").toUpperCase() + "\t" + rs.getString("problem").toUpperCase());
            }

            if(isEmpty)
                System.out.println("\n\tNO APPOINTMENTS FOR TODAY!");
        }
        catch(Exception e){
            System.out.println(e);
        }
    }


    void changePassword() {
        System.out.println("\n\n---------------------------------------------------------------------------------\n");
        System.out.println("\n\t\t- VERIFY CREDENTIALS -\n");
        

        System.out.print("\nENTER CURRENT PASSWORD : ");
        String pass = sc.next();

        if(this.password.equals(pass)){
            System.out.print("\nENTER NEW PASSWORD : ");
            pass = sc.next();
            
            try{
                query = "update doctorCredentials set password = ? where id = ?";
                ps = conn.prepareStatement(query);
                ps.setString(1, pass);
                ps.setInt(2, this.id);

                this.password = pass;
                System.out.println("\n\tPASSWORD CHANGED SUCCESSFULLY!");
            }
            catch(Exception e){
                System.out.println(e);
            }
        }
        else{
            System.out.println("WRONG PASSWORD!");
        }
    }

}
