import java.util.ArrayList;
import java.util.List;

/**
 * BalanceManager - Core class for the Balance Tracking module.
 * Manages user account balances including deposit, withdrawal, and balance inquiry.
 */
public class BalanceManager {

    private String accountId;
    private String accountHolder;
    private double balance;
    private List<String> auditLog;

    // Constructor
    public BalanceManager(String accountId, String accountHolder, double initialBalance) {
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative.");
        }
        this.accountId = accountId;
        this.accountHolder = accountHolder;
        this.balance = initialBalance;
        this.auditLog = new ArrayList<>();
        auditLog.add(String.format("[INIT] Account '%s' created for '%s' with initial balance: RM %.2f",
                accountId, accountHolder, initialBalance));
    }

    /**
     * Deposits money into the account.
     * @param amount Amount to deposit (must be > 0)
     */
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than zero.");
        }
        balance += amount;
        String log = String.format("[DEPOSIT] +RM %.2f | New Balance: RM %.2f", amount, balance);
        auditLog.add(log);
        System.out.println(log);
    }

    /**
     * Withdraws money from the account.
     * @param amount Amount to withdraw (must be > 0 and <= balance)
     */
    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than zero.");
        }
        if (amount > balance) {
            throw new IllegalStateException(
                String.format("Insufficient balance. Available: RM %.2f, Requested: RM %.2f", balance, amount));
        }
        balance -= amount;
        String log = String.format("[WITHDRAW] -RM %.2f | New Balance: RM %.2f", amount, balance);
        auditLog.add(log);
        System.out.println(log);
    }

    /**
     * Credits an amount to this account (used by transfer simulation module).
     * @param amount Amount to credit
     * @param fromAccount Sender account ID
     */
    public void creditFromTransfer(double amount, String fromAccount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer credit amount must be greater than zero.");
        }
        balance += amount;
        String log = String.format("[TRANSFER IN] +RM %.2f from '%s' | New Balance: RM %.2f",
                amount, fromAccount, balance);
        auditLog.add(log);
        System.out.println(log);
    }

    /**
     * Debits an amount from this account (used by transfer simulation module).
     * @param amount Amount to debit
     * @param toAccount Receiver account ID
     */
    public void debitForTransfer(double amount, String toAccount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer debit amount must be greater than zero.");
        }
        if (amount > balance) {
            throw new IllegalStateException(
                String.format("Insufficient balance for transfer. Available: RM %.2f, Requested: RM %.2f", balance, amount));
        }
        balance -= amount;
        String log = String.format("[TRANSFER OUT] -RM %.2f to '%s' | New Balance: RM %.2f",
                amount, toAccount, balance);
        auditLog.add(log);
        System.out.println(log);
    }

    /**
     * Returns the current balance.
     * @return current balance as double
     */
    public double getBalance() {
        return balance;
    }

    /**
     * Displays formatted balance information.
     */
    public void displayBalance() {
        System.out.println("========================================");
        System.out.printf("  Account ID    : %s%n", accountId);
        System.out.printf("  Account Holder: %s%n", accountHolder);
        System.out.printf("  Current Balance: RM %.2f%n", balance);
        System.out.println("========================================");
    }

    /**
     * Displays the full audit log for this account.
     */
    public void displayAuditLog() {
        System.out.println("\n--- Audit Log for Account: " + accountId + " ---");
        for (String entry : auditLog) {
            System.out.println(entry);
        }
        System.out.println("--- End of Log ---\n");
    }

    // Getters
    public String getAccountId()     { return accountId; }
    public String getAccountHolder() { return accountHolder; }
    public List<String> getAuditLog() { return new ArrayList<>(auditLog); }

    // Add external log entry (used by transaction/transfer modules)
    public void addLogEntry(String entry) {
        auditLog.add(entry);
    }
}
