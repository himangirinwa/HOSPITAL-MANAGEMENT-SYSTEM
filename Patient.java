import java.io.IOException;
import java.sql.*;
import java.util.*;


public class Patient {
    private String firstName, lastName, city, password, problem, bloodGrp, contact;
    Set<Integer> slot;
    HashMap<Integer,String> slotMap; //for slot and timings
    String query;
    Scanner sc;
    Connection con;
    Statement statement;
    PreparedStatement ps;
    ResultSet rs;

    Patient(){
        sc = new Scanner(System.in);
        try{  
            //connecting to database
            con = ConnectionProvider.getConn();
            statement = con.createStatement();
        }
        catch(Exception e){ System.out.println(e);}

        //after connection is established successfully with the database
        slot = new HashSet<>();
        slot.add(1);    slot.add(2);    slot.add(3);    slot.add(4);    slot.add(5);    slot.add(6);
        slot.add(7);    slot.add(8);    slot.add(9);    slot.add(10);   slot.add(11);   slot.add(12);
        slot.add(13);   slot.add(14);   slot.add(15);   slot.add(16);   slot.add(17);   slot.add(18);
        slot.add(19);   slot.add(20);   slot.add(21);   slot.add(22);   slot.add(23);   slot.add(24);

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

    @Override
    protected void finalize() throws Throwable {
        sc.close();
        con.close();
    }

    Patient(String firstName, String lastName, String contact, String problem, String city, String password, String bloodGrp){
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.contact = contact;
        this.problem = problem;
        this.city = city;
        this.bloodGrp = bloodGrp;
        this.password = password;
    }

    //menu
    public void menu() throws IOException{
        int choice = 0;
        while(choice!=4){
         
            System.out.println("\n\n==================== WELCOME ====================");
            System.out.println("\n1. BOOK APPOINTMENT");
            System.out.println("2. MODIFY YOUR DETAILS");
            System.out.println("3. CHANGE PASSWORD");
            System.out.println("4. EXIT");
            System.out.print("ENTER CHOICE : ");
            choice = sc.nextInt();

            switch(choice){
                case 1 :
                    this.bookAppointment();
                    break;

                case 2 :
                    this.modifyPatientDetails();
                    break;

                case 3 :
                    this.changePassword();
                    break;

                case 4 :
                    break;
                    
                default :
                    System.out.println("INVALID CHOICE!");
            }
        }
    }


    //booking an appointment
    public void bookAppointment(){

        Set<Integer> occupiedSlot = new HashSet<>();
        System.out.println("\n\n---------------------------------------------------------------------------------\n");
        
        try{
            query = "select id, firstName, lastName, specialization from doctor";
            rs = statement.executeQuery(query);
            System.out.println("\n\t\t- - OUR DOCTORS - -");
            System.out.println("\nID \t FIRSTNAME \t LASTNAME \t SPECIALIZATION");
            
            while(rs.next()){

                System.out.println(rs.getString("id") + "\t" + rs.getString("firstName").toUpperCase() + "\t\t" + rs.getString("lastName").toUpperCase() + "\t\t" + rs.getString("specialization").toUpperCase());
            }

            System.out.print("\n\n\tENTER DOCTOR ID WITH WHOM YOU WANT TO BOOK YOU APPOINTMENT : ");
            int id = sc.nextInt();
            System.out.print("\n\tDATE OF APPOINTMENT (DDMMYY): ");
            String date = sc.next();

            //checking for empty slots
            ps = con.prepareStatement("select slot from appointments where doctorId= ? and date=?");
            ps.setInt(1, id);
            ps.setString(2, date);
            rs = ps.executeQuery();

            //getting occupied slots
            while(rs.next()){
                occupiedSlot.add(rs.getInt("slot"));
            }

            //getting empty slots
            slot.removeAll(occupiedSlot);
            if(slot.isEmpty()){
                System.out.println("NO EMPTY SLOTS TODAY!");
                slot.addAll(occupiedSlot);
                return;
            }

            System.out.println("\n\n\tAVAILABLE SLOTS :");
            System.out.println("\n\tSLOT.NO \t TIME");
            //provding empty slots to choose
            for (int slotNo : slot)
                System.out.println("\t" + slotNo + "\t\t" + slotMap.get(slotNo));

            System.out.print("\n\n\tENTER SLOT NO TO BE BOOKED : ");
            int slotNumber = sc.nextInt();

            if(!slot.contains(slotNumber)){
                System.out.println("\nTHIS SLOT IS ALREADY OCCUPIED!");
                slot.addAll(occupiedSlot);
                return;
            }
            
            ps = con.prepareStatement("insert into appointments (patientId, doctorId, date, slot) values (?, ?, ?, ?)");
            ps.setString(1, this.contact);
            ps.setInt(2, id);
            ps.setString(3, date);
            ps.setInt(4, slotNumber);
            int result = ps.executeUpdate();

            if(result ==1){
                System.out.println("\n\n\t\tSLOT BOOKED SUCCESSFULLY!");
            }
            else{
                System.out.println("\n\n\tCOULDN'T BOOK YOUR SLOT! TRY AGAIN LATER!");
            }
            slot.addAll(occupiedSlot);
        }
        catch(Exception e){
            System.out.println(e);
        }
    }


    //change password
    public void changePassword(){
        String newPassword;
        System.out.println("\n\n---------------------------------------------------------------------------------\n");
        System.out.println("\nENTER YOUR CURRENT PASSWORD TO CONFIRM : ");
        newPassword = sc.next();
        if(this.password.equals(newPassword)){
            System.out.println("ENTER NEW PASSWORD : ");
            newPassword = sc.next();
            this.password = newPassword;
            try{
                statement = con.createStatement();
                query = "update patientCrdentials set password='"+newPassword+"' where patientId='"+this.contact+"'";
    
                int result = statement.executeUpdate(query);
                if(result!=0){
                    System.out.println("Password changed successfully !!!");
                }
                else{
                    System.out.println("Something went wrong! Please try again.");
                }
                sc.nextLine();
            }
            catch(Exception e){
                System.out.println(e);;
            }
        }
        else{
            System.out.println("INCORRECT PASSWORD !");
        }
    }

    //update credentials
    public void modifyPatientDetails(){
        System.out.print("\n\n\n---------------------------------------------------------------------------------\n\n\n");
        System.out.println("\n - ENTER YOUR NEW DETAILS -");
        System.out.print("\nFIRST NAME : ");
        this.firstName = sc.next();
        System.out.print("LAST NAME : ");
        this.lastName = sc.next();
        System.out.print("CITY : ");
        this.city = sc.next();
        System.out.print("BLOOD GROUP : ");
        this.bloodGrp = sc.next();
        System.out.print("CONCERN OR SYMPTOMS : ");
        this.problem = sc.next();

        //comiting changes to database
        try{
            Statement statement = con.createStatement();
            String sql = "update patient set firstName='"+this.firstName+"', lastName='"+this.lastName+"', city='"+this.city+"', bloodGrp='"+this.bloodGrp+"', problem='"+this.problem+"' where contact='"+this.contact+"'";

            int result = statement.executeUpdate(sql);
            if(result!=0){
                System.out.println("DETAILS UPDATED SUCCESSFULLY !!!");
            }
            else{
                System.out.println("SOMETHING WENT WRONG PLEASE TRY AGAIN LATER!");
            }
        }
        catch(Exception e){
            System.out.println(e);;
        }
    }

    //signup for new patients
    public boolean signup(){
        try{
            Statement statement = con.createStatement();
                
            // insert record in patient table
            String sql = "insert into patient values ('"+this.firstName+"', '"+ this.contact +"', '"+this.problem+"', '"+ this.city +"', '"+this.bloodGrp+"', '"+ this.lastName+ "')";
            int result = statement.executeUpdate(sql);
            
            if(result==1){

                // create patientCredential table if not exists
                sql = "create table if not exists patientCrdentials (patientId varchar(10), password varchar(30), primary key(patientId), foreign key (patientId) references patient(contact));";
                result = statement.executeUpdate(sql);
                if(result==0){

                    // insert credentials
                    sql = "insert into patientcrdentials values ('"+this.contact+"','"+this.password+"')";
                    result = statement.executeUpdate(sql);
                    if(result==1){
                        System.out.println("\n\tREGISTRATION SUCCESSFULL !!!");
                        return true;
                    }
                }
            }
        }
        catch(Exception e){
            if(e.getMessage().startsWith("Duplicate")){
                System.out.println("CONTACT ALREADY EXIST !");
                return false;
            }
            System.out.println(e.getMessage());            
        }
        return false;
    }
}
