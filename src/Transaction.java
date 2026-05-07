import java.time.LocalDate;

/**
 * Transaction.java
 * This class represents a single transaction record in the e-wallet system.
 * Think of it as a "blueprint" for every transaction that happens.
 */
public class Transaction {

    // ─── Transaction Types ─────────────────────────────────────────────────────
    // These are the allowed transaction types (like constants/labels)
    public static final String TYPE_TOPUP    = "Top-Up";
    public static final String TYPE_TRANSFER = "Transfer";
    public static final String TYPE_BILL     = "Bill Payment";

    // ─── Fields (data stored in each transaction) ──────────────────────────────
    private String      id;          // Unique ID, e.g. "TXN001"
    private String      type;        // "Top-Up", "Transfer", or "Bill Payment"
    private double      amount;      // How much money
    private String      description; // Short note, e.g. "Sent to Alice"
    private LocalDate   date;        // The date of the transaction
    private String      status;      // "Success" or "Failed"

    // ─── Constructor (used to create a new Transaction object) ─────────────────
    public Transaction(String id, String type, double amount, String description, LocalDate date, String status) {
        this.id          = id;
        this.type        = type;
        this.amount      = amount;
        this.description = description;
        this.date        = date;
        this.status      = status;
    }

    // ─── Getters (used to read each field from outside this class) ─────────────
    public String    getId()          { return id; }
    public String    getType()        { return type; }
    public double    getAmount()      { return amount; }
    public String    getDescription() { return description; }
    public LocalDate getDate()        { return date; }
    public String    getStatus()      { return status; }

    // ─── toString (for quick debugging in console) ─────────────────────────────
    @Override
    public String toString() {
        return "[" + id + "] " + type + " | RM" + String.format("%.2f", amount)
             + " | " + description + " | " + date + " | " + status;
    }
}
