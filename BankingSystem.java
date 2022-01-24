import java.util.Scanner;

public class BankingSystem {
    boolean exit = false;
    static  boolean exitServices = false;
    Scanner scan = new Scanner(System.in);
    Account account;

    public boolean isExit() {
        return exit;
    }


    int startSession() {
        System.out.println(
                "\n1. Create an account\n"
                        + "2. Log into account\n"
                        + "0. Exit");
        return scan.nextInt();
    }

    void getTask(int option) {
        switch (option) {
            case 1:
                account = new Account();
                break;
            case 2:
                logIn();
                break;
            case 0:
                System.out.println("\nBye!");
                exit = true;
                break;
            default:
                break;
        }
    }

    void logIn() {
        System.out.println("Enter your card number:");
        String cardNumber = scan.next();
        System.out.println("Enter your PIN:");
        String pin = scan.next();
        account = new Account(cardNumber, pin);

        if (account.authentication(pin, cardNumber)) {
            System.out.println("You have successfully logged in!");
            while (!exitServices) {
                offerServices(cardNumber);
            }
        } else {
            System.out.println("Wrong card number or PIN!");
        }
    }

    void offerServices(String cardNumber) {  
        System.out.println("\n1. Balance\n"
                + "2. Add income\n"
                + "3. Do transfer\n"
                + "4. Close account\n"
                + "5. Log out\n"
                + "0. Exit");

        switch (scan.nextInt()) {
            case 1:
                System.out.println("Balance: " + account.balanceChecker(cardNumber));
                break;
            case 2:
                System.out.println("\nEnter income:");
                account.addIncome(cardNumber, scan.nextInt());
                System.out.println("Income was added!");
                break;
            case 3:
                System.out.println("\nTransfer\nEnter card number:");
                account.doTransfer(cardNumber,scan.next());
                break;
            case 4:
                account.closeAccount(cardNumber);
                break;
            case 5:
                System.out.println("You have successfully logged out!");
                exitServices = true;
                break;
            case 0:
                System.out.println("Bye!");
                exitServices = true;
                exit = true;
                break;
        }
    }


}
