import java.util.Scanner;

public class App {
    public static void menu() {
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
        String passcode;

        // menu
        App.menu();
        choice = sc.nextInt();

        switch (choice) {
        // admin login
        case 1:
            System.out.println("\n\n----- ADMIN LOGIN -----");
            System.out.print("\nENTER PASSWORD : ");
            passcode = sc.next();

            // verifying password
            Authenticator adminAuth = new Authenticator();
            adminAuth.adminLogin(passcode);
            break;

        case 2:
            // doctor login
            System.out.println("\n\n------------------------------------------------------------\n");
            System.out.println("\n- - ENTER YOUR DETAILS - -");
            System.out.print("\nENTER ID NO : ");
            int id = sc.nextInt();
            System.out.print("\nENTER PASSWORD : ");
            passcode = sc.next();

            Authenticator doctorAuth = new Authenticator();
            doctorAuth.doctorLogin(id, passcode);
            break;

        case 3:

            Patient pat = new Patient("");
            pat.patientSignup();
            break;

        case 4:
            System.out.println("\n\n------------------------------------------------------------\n");
            System.out.println("\n- - ENTER YOUR DETAILS - -");
            System.out.print("\nENTER CONTACT NO : ");
            String contact = sc.next();
            System.out.print("\nENTER PASSWORD : ");
            passcode = sc.next();
            Authenticator patientAuth = new Authenticator();
            patientAuth.patientLogin(contact, passcode);
            break;

        case 5:
            System.exit(0);

        default:
            System.out.println("\nINVALID CHOICE!");
        }

        // closing scanner
        sc.close();
    }
}
