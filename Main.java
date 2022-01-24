import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException {
        String url = "jdbc:sqlite:" + args[1]; // for Jet Brains Academy automated testing

        //String url = "jdbc:sqlite:D:\\variousTests\\sqliteTest1\\edu.db"; // for local testing
        new Account(url);
        BankingSystem bankingSystem = new BankingSystem();
        while (!bankingSystem.isExit()) {
            bankingSystem.getTask(bankingSystem.startSession());

        }
    }
}
