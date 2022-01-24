import org.sqlite.SQLiteDataSource;

import java.sql.*;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class Account {
    private int balance = 0;
    private String pin;
    private String cardNumber;
    static SQLiteDataSource sds;
    static Scanner scan = new Scanner(System.in);

    public Account() {
        this.cardNumber = accountCreator();
        this.pin = pinCreator();
        dbInsertValues(pin, cardNumber);
    }

    public Account(String cardNumber, String pin) {
        this.cardNumber = cardNumber;
        this.pin = pin;
    }

    public Account(String url) {
        sds = new SQLiteDataSource();
        sds.setUrl(url);
        createDB();
    }

    static String accountCreator() {
        Random random = new Random();
        int bin = 400000;
        String accountID = String.format("%09d", ThreadLocalRandom.current().nextInt(1000000000));
        int checkSum = checkSumCreator(bin + accountID);  //
        String cardNumber = bin + accountID + checkSum;
        System.out.println("Your card has been created\n" +
                "Your card number:");
        System.out.print(cardNumber);
        return cardNumber;
    }

    static int checkSumCreator(String binPlusaccountID) { 

        int[] digits = new int[binPlusaccountID.length()]; 
        for (int i = 0; i < binPlusaccountID.length(); i++) {   
            digits[i] = Character.getNumericValue(binPlusaccountID.charAt(i));   
        }

        for (int i = 0; i < digits.length; i += 2) {
            digits[i] *= 2;
            if (digits[i] > 9) {
                digits[i] -= 9;
            }
        }

        int sum = 0;
        for (int i = 0; i < digits.length; i++) {
            sum += digits[i];
        }
        return (10 - sum % 10) % 10;
    }

    static String pinCreator() {
        String pin = String.format("%04d", ThreadLocalRandom.current().nextInt(10000));
        System.out.println("\nYour card PIN:\n" + pin);
        return pin;
    }

    void createDB() {

        try (Connection cn = sds.getConnection()) {
            try (Statement st = cn.createStatement()) {
                st.executeUpdate("CREATE TABLE IF NOT EXISTS card (" +
                        "id INTEGER PRIMARY KEY, " +
                        "number TEXT NOT NULL, " +
                        "pin TEXT NOT NULL, " +
                        "balance INTEGER DEFAULT 0)");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void dbInsertValues(String pin, String cardNumber) {

        try (Connection cn = sds.getConnection()) {
            try (Statement st = cn.createStatement()) {
                st.executeUpdate("INSERT INTO card (number, pin) VALUES (" + "'" + cardNumber + "', '" + pin + "')");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean authentication(String pin, String cardNumber) {  // long cardNumber

        try (Connection cn = sds.getConnection()) {
            try (Statement st = cn.createStatement()) {
                try (ResultSet set = st.executeQuery("SELECT * FROM card")) {
                    while (set.next()) {
                        // Retrieve column values
                        String registeredCardNumber = set.getString("number");
                        String registeredPin = set.getString("pin");

                        if (registeredCardNumber.equals(cardNumber) && //  String.valueOf(cardNumber)
                                registeredPin.equals(pin)) {
                            return true;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    int balanceChecker(String cardNumber) {  
        try (Connection cn = sds.getConnection()) {
            try (Statement st = cn.createStatement()) {
                try (ResultSet set = st.executeQuery("SELECT * FROM card")) {
                    while (set.next()) {
                        String registeredCardNumber = set.getString("number");
                        if (registeredCardNumber.equals(cardNumber)) { 
                            return set.getInt("balance");
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    void addIncome(String cardNumber, int amount) {
        try (Connection cn = sds.getConnection()) {
            try (Statement st = cn.createStatement()) {
                st.executeUpdate(
                        "UPDATE card SET balance = balance + "
                                + amount + " WHERE number = '"
                                + cardNumber + "' ");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void doTransfer(String senderCardNumber, String receiverCardNumber) {
        boolean receiverCardNumberExists = false;
        int desiredTransferAmount;
        String withDrawnMoneySql = "UPDATE card SET balance = ? WHERE number = ?";
        String addMoneySql = "UPDATE card SET balance = ? WHERE number = ?";
        if (senderCardNumber.equals(receiverCardNumber)) {
            System.out.println("You can't transfer money to the same account!");
            return;
        }
        if (!passLuhnAlgorithmTest(receiverCardNumber)) {
            System.out.println("Probably you made a mistake in the card number. " +
                    "Please try again!");
            return;
        }
        try (Connection con = sds.getConnection()) {
            try (Statement st = con.createStatement()) {         
                try (ResultSet set = st.executeQuery("SELECT * FROM card")) {
                    while (set.next()) {
                        String accountNumber = set.getString("number");
                        if (accountNumber.equals(receiverCardNumber)) {
                            receiverCardNumberExists = true;
                        }
                    }
                }

                con.setAutoCommit(false);

                if (!receiverCardNumberExists) {
                    System.out.println("Such a card does not exist.");
                    return;
                } else {
                    System.out.println("Enter how much money you want to transfer:");
                    desiredTransferAmount = scan.nextInt();
                    if (desiredTransferAmount > balanceChecker(senderCardNumber)) {
                        System.out.println("Not enough money!");
                        return;
                    } else {
                        try (PreparedStatement withDrawnMoneyPS = con.prepareStatement(withDrawnMoneySql);
                             PreparedStatement addMoneyPS = con.prepareStatement(addMoneySql)) {

                            withDrawnMoneyPS.setInt(1, balanceChecker(senderCardNumber) - desiredTransferAmount);
                            withDrawnMoneyPS.setString(2, senderCardNumber);
                            withDrawnMoneyPS.executeUpdate();

                            addMoneyPS.setInt(1, balanceChecker(receiverCardNumber) + desiredTransferAmount);
                            addMoneyPS.setString(2, receiverCardNumber);
                            addMoneyPS.executeUpdate();

                            con.commit();
                        }
                        System.out.println("Success!");
                        return;
                    }
                }

            } catch (Exception e) {
                if (con != null) {
                    try {
                        System.err.print("Transaction is being rolled back");
                        con.rollback();
                    } catch (SQLException excep) {
                        excep.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!receiverCardNumberExists) {
            System.out.println("Such a card does not exist.");
            return;
        }
        System.out.println("");
    }

    boolean passLuhnAlgorithmTest(String toCardNumber) {
        if (checkSumCreator(toCardNumber.substring(0, 15)) == Integer.valueOf(toCardNumber.substring(15))) {
            return true;
        }
        return false;
    }

    void closeAccount(String cardNumber) {
        String insert = "DELETE FROM card WHERE number = ?";
        try (Connection con = sds.getConnection()) {
            try (PreparedStatement preparedStatement = con.prepareStatement(insert)) {
                preparedStatement.setString(1, cardNumber);
                preparedStatement.executeUpdate();

                System.out.println("The account has been closed!");
                BankingSystem.exitServices = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
