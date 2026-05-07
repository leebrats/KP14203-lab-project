/**
 * BalanceTrackerDemo
 * Demonstrates the Balance Tracking module of the E-Wallet / Simple Payment Management System.
 *
 * Module: Balance Tracking
 * Author: [Your Name]
 * Subject: Object-Oriented Programming / Java Application Development
 */
public class BalanceTrackerDemo {

    public static void main(String[] args) {

        System.out.println("============================================================");
        System.out.println("   E-WALLET / SIMPLE PAYMENT MANAGEMENT SYSTEM");
        System.out.println("   Module: Balance Tracking");
        System.out.println("============================================================\n");

        // --- Create two accounts ---
        BalanceManager alice = new BalanceManager("ACC-001", "Alice Tan", 500.00);
        BalanceManager bob   = new BalanceManager("ACC-002", "Bob Lim",  200.00);

        // --- Display initial balances ---
        System.out.println("\n[Initial Balances]");
        alice.displayBalance();
        bob.displayBalance();

        // --- Deposit demo ---
        System.out.println("\n[Deposit Demo]");
        alice.deposit(150.00);
        bob.deposit(50.00);

        // --- Withdrawal demo ---
        System.out.println("\n[Withdrawal Demo]");
        alice.withdraw(75.00);

        // --- Insufficient funds test ---
        System.out.println("\n[Insufficient Funds Test]");
        try {
            bob.withdraw(500.00); // Should throw exception
        } catch (IllegalStateException e) {
            System.out.println("[ERROR CAUGHT] " + e.getMessage());
        }

        // --- Simulated transfer (integration point with Transfer module) ---
        System.out.println("\n[Transfer Simulation]");
        double transferAmount = 100.00;
        try {
            alice.debitForTransfer(transferAmount, bob.getAccountId());
            bob.creditFromTransfer(transferAmount, alice.getAccountId());
            System.out.println("Transfer of RM " + String.format("%.2f", transferAmount) + " completed.");
        } catch (IllegalStateException e) {
            System.out.println("[TRANSFER FAILED] " + e.getMessage());
        }

        // --- Display final balances ---
        System.out.println("\n[Final Balances]");
        alice.displayBalance();
        bob.displayBalance();

        // --- Display audit logs ---
        alice.displayAuditLog();
        bob.displayAuditLog();

        System.out.println("============================================================");
        System.out.println("   End of Balance Tracking Module Demo");
        System.out.println("============================================================");
    }
}
