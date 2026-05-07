import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TransactionManager.java
 * This class manages all transaction data.
 * It stores transactions in a list and provides methods to:
 *   - Add a new transaction
 *   - View all transactions
 *   - Filter by type (Top-Up, Transfer, Bill Payment)
 *   - Search by date
 */
public class TransactionManager {

    // A list that holds all Transaction objects (like a database in memory)
    private ArrayList<Transaction> transactionList;

    // Counter used to auto-generate unique IDs like TXN001, TXN002, etc.
    private int idCounter;

    // ─── Constructor ───────────────────────────────────────────────────────────
    public TransactionManager() {
        transactionList = new ArrayList<>();
        idCounter = 1;
        loadSampleData(); // Pre-load some dummy data so the app isn't empty
    }

    // ─── Add a new transaction ─────────────────────────────────────────────────
    public void addTransaction(String type, double amount, String description, LocalDate date, String status) {
        // Auto-generate an ID like "TXN001"
        String id = String.format("TXN%03d", idCounter++);
        Transaction t = new Transaction(id, type, amount, description, date, status);
        transactionList.add(t);
    }

    // ─── Get ALL transactions ──────────────────────────────────────────────────
    public List<Transaction> getAllTransactions() {
        return transactionList;
    }

    // ─── Filter by type (e.g., only "Top-Up" transactions) ────────────────────
    public List<Transaction> filterByType(String type) {
        // Loop through all transactions and keep only those matching the type
        List<Transaction> result = new ArrayList<>();
        for (Transaction t : transactionList) {
            if (t.getType().equalsIgnoreCase(type)) {
                result.add(t);
            }
        }
        return result;
    }

    // ─── Search by date ────────────────────────────────────────────────────────
    public List<Transaction> searchByDate(LocalDate date) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction t : transactionList) {
            if (t.getDate().equals(date)) {
                result.add(t);
            }
        }
        return result;
    }

    // ─── Search by date RANGE ──────────────────────────────────────────────────
    public List<Transaction> searchByDateRange(LocalDate from, LocalDate to) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction t : transactionList) {
            // Check if transaction date is within the from–to range
            boolean afterOrOnFrom = !t.getDate().isBefore(from);
            boolean beforeOrOnTo  = !t.getDate().isAfter(to);
            if (afterOrOnFrom && beforeOrOnTo) {
                result.add(t);
            }
        }
        return result;
    }

    // ─── Get total count ───────────────────────────────────────────────────────
    public int getTotalCount() {
        return transactionList.size();
    }

    // ─── Calculate total amount by type ───────────────────────────────────────
    public double getTotalByType(String type) {
        double total = 0;
        for (Transaction t : transactionList) {
            if (t.getType().equalsIgnoreCase(type) && t.getStatus().equals("Success")) {
                total += t.getAmount();
            }
        }
        return total;
    }

    // ─── Pre-load sample/dummy transactions for demo ───────────────────────────
    private void loadSampleData() {
        addTransaction(Transaction.TYPE_TOPUP,    100.00, "Bank Transfer Top-Up",     LocalDate.of(2025, 5, 1),  "Success");
        addTransaction(Transaction.TYPE_TRANSFER,  45.50, "Sent to Ahmad",            LocalDate.of(2025, 5, 3),  "Success");
        addTransaction(Transaction.TYPE_BILL,      30.00, "TNB Electricity Bill",     LocalDate.of(2025, 5, 5),  "Success");
        addTransaction(Transaction.TYPE_TOPUP,    200.00, "Online Banking Top-Up",    LocalDate.of(2025, 5, 7),  "Success");
        addTransaction(Transaction.TYPE_TRANSFER,  75.00, "Sent to Siti",             LocalDate.of(2025, 5, 10), "Success");
        addTransaction(Transaction.TYPE_BILL,      55.20, "Unifi Internet Bill",      LocalDate.of(2025, 5, 12), "Success");
        addTransaction(Transaction.TYPE_TRANSFER,  20.00, "Sent to Raj",              LocalDate.of(2025, 5, 14), "Failed");
        addTransaction(Transaction.TYPE_TOPUP,     50.00, "Cash Top-Up at ATM",       LocalDate.of(2025, 5, 15), "Success");
        addTransaction(Transaction.TYPE_BILL,      18.90, "Air Selangor Water Bill",  LocalDate.of(2025, 5, 17), "Success");
        addTransaction(Transaction.TYPE_TRANSFER, 150.00, "Sent to Mei Ling",         LocalDate.of(2025, 5, 19), "Success");
    }
}
