import java.util.Scanner;

public class App {
    public static void menu(){
        System.out.println("\n\n==================== WELCOME ====================\n");
        System.out.println("\n1. ADMIN LOGIN");
        System.out.println("2. DOCTOR LOGIN");
        System.out.println("3. PATIENT SIGNUP");
        System.out.println("4. PATIENT LOGIN");
        System.out.println("5. EXIT");
        System.out.print("ENTER CHOICE : ");
    }
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        int choice;
        
        //menu
        App.menu();
        choice = sc.nextInt();

        switch(choice){
            //admin login
            case 1:
                System.out.println("\n\n----- ADMIN LOGIN -----");
                System.out.print("\nENTER PASSWORD : ");
                String pass = sc.next();
                final String passCode = "L1mhdy54kyqI";
                //12-character password for admin

                //verifying password
                if(pass.equals(passCode)){
                    //providing admin funtionalities after correct login
                    Admin a = new Admin();
                    a.menu();
                } 
                else
                    System.out.println("INCORRECT PASSWORD!");
                break;

            case 2:
                //doctor login
                System.out.println("\n\n------------------------------------------------------------\n");
                System.out.println("\n- - ENTER YOUR DETAILS - -");  
                System.out.print("\nENTER ID NO : ");
                int id = sc.nextInt();
                System.out.print("\nENTER PASSWORD : ");
                String passcode = sc.next();

                Authenticator doctorAuth = new Authenticator();
                doctorAuth.doctorLogin(id, passcode);
                break;
                
            case 3:
                //patient signup
                System.out.println("\n\n------------------------------------------------------------");
                System.out.println("\n- - ENTER YOUR DETAILS - -");
                System.out.print("\nENTER YOUR FIRST NAME : ");
                String firstName = sc.next();
                System.out.print("\nENTER YOUR LAST NAME : ");
                String lastName = sc.next();
                System.out.print("\nENTER CONTACT NUMBER : ");
                String contact = sc.next();
                System.out.print("\nENTER CITY : ");
                String city = sc.next();
                System.out.print("\nENTER BLOOD GROUP : ");
                String bloodGrp = sc.next();
                System.out.print("\nSET PASSWORD : ");
                String password = sc.next();
                System.out.print("\nMENTION YOU CONCERN OR SYMPTOMS : ");
                String problem = sc.next();

                Patient patient = new Patient(firstName, lastName, contact, problem, city, password, bloodGrp);
                //after successfull signup, patient will be directed to patient menu or funtionalities
                if(patient.signup())
                    patient.menu();
                break;

            case 4:  
                //patient login 
                System.out.print("\n\n\n---------------------------------------------------------------------------------\n\n\n");              
                System.out.print("ENTER CONTACT NUMBER : ");
                contact = sc.next();
                System.out.print("\nENTER PASSWORD : ");
                password = sc.next();
                System.out.print("\nDESCRIBE YOUR PROBLEM (ENTER 'same' IF PROBLEM AS LAST LOGIN) : ");
                problem = sc.next();

                Authenticator patientAuth = new Authenticator();
                patientAuth.patientLogin(contact, password, problem);
                break;

            case 5: 
                System.exit(0);
            
            default:
                System.out.println("\nINVALID CHOICE!");
        }
    
        //closing scanner
        sc.close();
    }
}
    