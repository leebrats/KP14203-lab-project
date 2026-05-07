import javafx.application.Application;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.time.LocalDate;
import java.util.List;

/**
 * TransactionHistoryApp.java
 * This is the MAIN file that launches the JavaFX GUI application.
 * It builds the window, table, filters, and search features.
 *
 * To run:
 *   1. Make sure JavaFX SDK is installed and linked in your IDE
 *   2. Run this class as the main entry point
 */
public class TransactionHistoryApp extends Application {

    // ─── Data manager (our "database") ────────────────────────────────────────
    private TransactionManager manager = new TransactionManager();

    // ─── The table that shows transactions ────────────────────────────────────
    private TableView<TransactionRow> tableView = new TableView<>();

    // ─── The list connected to the table (updates table automatically) ─────────
    private ObservableList<TransactionRow> tableData = FXCollections.observableArrayList();

    // ─── Summary labels at the top ────────────────────────────────────────────
    private Label lblTotalTopUp    = new Label();
    private Label lblTotalTransfer = new Label();
    private Label lblTotalBill     = new Label();
    private Label lblTotalCount    = new Label();

    // ─────────────────────────────────────────────────────────────────────────
    // start() is called automatically by JavaFX — this is where we build the UI
    // ─────────────────────────────────────────────────────────────────────────
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("💳 E-Wallet — Transaction History");

        // --- Root layout (top-to-bottom stacking) ---
        VBox root = new VBox(16);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: #0f0f1a;");

        // --- Title ---
        Label title = new Label("Transaction History");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #e0e0ff; -fx-font-family: 'Segoe UI';");

        Label subtitle = new Label("View, filter and search your e-wallet activity");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #8888aa; -fx-font-family: 'Segoe UI';");

        // --- Summary Cards Row ---
        HBox summaryRow = buildSummaryCards();

        // --- Filter & Search Bar ---
        HBox filterBar = buildFilterBar();

        // --- Transaction Table ---
        buildTable();

        // --- Add everything to root layout ---
        root.getChildren().addAll(title, subtitle, summaryRow, filterBar, tableView);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        // --- Load all transactions into table on startup ---
        loadTableData(manager.getAllTransactions());

        // --- Create and show the window ---
        Scene scene = new Scene(root, 980, 620);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Build the 4 summary cards at the top of the screen
    // ─────────────────────────────────────────────────────────────────────────
    private HBox buildSummaryCards() {
        HBox row = new HBox(12);

        // Format summary values
        lblTotalTopUp.setText("RM " + String.format("%.2f", manager.getTotalByType(Transaction.TYPE_TOPUP)));
        lblTotalTransfer.setText("RM " + String.format("%.2f", manager.getTotalByType(Transaction.TYPE_TRANSFER)));
        lblTotalBill.setText("RM " + String.format("%.2f", manager.getTotalByType(Transaction.TYPE_BILL)));
        lblTotalCount.setText(manager.getTotalCount() + " Records");

        // Create each card
        VBox c1 = card("💰 Total Top-Up",    lblTotalTopUp,    "#1a3a2a", "#4ade80");
        VBox c2 = card("📤 Total Transfers", lblTotalTransfer, "#1a2a3a", "#60a5fa");
        VBox c3 = card("🏷️ Total Bills",     lblTotalBill,     "#3a1a2a", "#f472b6");
        VBox c4 = card("📋 Total Records",   lblTotalCount,    "#2a2a1a", "#fbbf24");

        // Make all cards equal width
        HBox.setHgrow(c1, Priority.ALWAYS);
        HBox.setHgrow(c2, Priority.ALWAYS);
        HBox.setHgrow(c3, Priority.ALWAYS);
        HBox.setHgrow(c4, Priority.ALWAYS);

        row.getChildren().addAll(c1, c2, c3, c4);
        return row;
    }

    // Helper: create a single summary card
    private VBox card(String titleText, Label valueLabel, String bgColor, String accentColor) {
        VBox box = new VBox(6);
        box.setPadding(new Insets(16));
        box.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 12;");

        Label lbl = new Label(titleText);
        lbl.setStyle("-fx-text-fill: #aaaacc; -fx-font-size: 12px; -fx-font-family: 'Segoe UI';");

        valueLabel.setStyle("-fx-text-fill: " + accentColor + "; -fx-font-size: 20px; -fx-font-weight: bold; -fx-font-family: 'Segoe UI';");

        box.getChildren().addAll(lbl, valueLabel);
        return box;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Build the filter bar (type dropdown + date pickers + buttons)
    // ─────────────────────────────────────────────────────────────────────────
    private HBox buildFilterBar() {
        HBox bar = new HBox(10);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(12));
        bar.setStyle("-fx-background-color: #1a1a2e; -fx-background-radius: 10;");

        // --- Type filter dropdown ---
        Label lblType = styledLabel("Filter by Type:");
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("All", Transaction.TYPE_TOPUP, Transaction.TYPE_TRANSFER, Transaction.TYPE_BILL);
        typeCombo.setValue("All");
        styleCombo(typeCombo);

        // --- Date range pickers ---
        Label lblFrom = styledLabel("From:");
        DatePicker fromDate = new DatePicker();
        fromDate.setPromptText("Start date");
        styleDatePicker(fromDate);

        Label lblTo = styledLabel("To:");
        DatePicker toDate = new DatePicker();
        toDate.setPromptText("End date");
        styleDatePicker(toDate);

        // --- Search button ---
        Button btnSearch = new Button("🔍 Search");
        btnSearch.setStyle("-fx-background-color: #4f46e5; -fx-text-fill: white; -fx-font-size: 13px; "
                         + "-fx-background-radius: 8; -fx-padding: 8 16; -fx-cursor: hand;");

        // --- Reset button ---
        Button btnReset = new Button("↺ Reset");
        btnReset.setStyle("-fx-background-color: #374151; -fx-text-fill: #ccccdd; -fx-font-size: 13px; "
                        + "-fx-background-radius: 8; -fx-padding: 8 16; -fx-cursor: hand;");

        // ── Button Actions ──────────────────────────────────────────────────

        // When Search is clicked: apply type filter AND/OR date range filter
        btnSearch.setOnAction(e -> {
            String selectedType = typeCombo.getValue();
            LocalDate from = fromDate.getValue();
            LocalDate to   = toDate.getValue();

            List<Transaction> result;

            // Case 1: Both type filter and date range selected
            if (!selectedType.equals("All") && from != null && to != null) {
                result = manager.filterByType(selectedType);
                result.retainAll(manager.searchByDateRange(from, to));
            }
            // Case 2: Only type filter
            else if (!selectedType.equals("All")) {
                result = manager.filterByType(selectedType);
            }
            // Case 3: Only date range
            else if (from != null && to != null) {
                result = manager.searchByDateRange(from, to);
            }
            // Case 4: No filter - show all
            else {
                result = manager.getAllTransactions();
            }

            loadTableData(result);
        });

        // When Reset is clicked: clear filters and show all
        btnReset.setOnAction(e -> {
            typeCombo.setValue("All");
            fromDate.setValue(null);
            toDate.setValue(null);
            loadTableData(manager.getAllTransactions());
        });

        bar.getChildren().addAll(lblType, typeCombo, lblFrom, fromDate, lblTo, toDate, btnSearch, btnReset);
        return bar;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Build the table with 6 columns
    // ─────────────────────────────────────────────────────────────────────────
    private void buildTable() {
        tableView.setStyle("-fx-background-color: #1a1a2e; -fx-text-fill: #e0e0ff; "
                         + "-fx-font-family: 'Segoe UI'; -fx-font-size: 13px;");
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setItems(tableData);
        tableView.setPlaceholder(new Label("No transactions found."));

        // Column: ID
        TableColumn<TransactionRow, String> colId = new TableColumn<>("Transaction ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setMinWidth(110);

        // Column: Type
        TableColumn<TransactionRow, String> colType = new TableColumn<>("Type");
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colType.setMinWidth(120);

        // Column: Amount
        TableColumn<TransactionRow, String> colAmount = new TableColumn<>("Amount (RM)");
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colAmount.setMinWidth(110);

        // Column: Description
        TableColumn<TransactionRow, String> colDesc = new TableColumn<>("Description");
        colDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        colDesc.setMinWidth(200);

        // Column: Date
        TableColumn<TransactionRow, String> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colDate.setMinWidth(110);

        // Column: Status (with color coding)
        TableColumn<TransactionRow, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setMinWidth(90);
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    // Green for Success, Red for Failed
                    if (item.equals("Success")) {
                        setStyle("-fx-text-fill: #4ade80; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #f87171; -fx-font-weight: bold;");
                    }
                }
            }
        });

        tableView.getColumns().addAll(colId, colType, colAmount, colDesc, colDate, colStatus);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Load a list of Transaction objects into the table
    // ─────────────────────────────────────────────────────────────────────────
    private void loadTableData(List<Transaction> transactions) {
        tableData.clear(); // Clear old data first
        for (Transaction t : transactions) {
            tableData.add(new TransactionRow(
                t.getId(),
                t.getType(),
                String.format("%.2f", t.getAmount()),
                t.getDescription(),
                t.getDate().toString(),
                t.getStatus()
            ));
        }
    }

    // ─── Helper: styled label ──────────────────────────────────────────────────
    private Label styledLabel(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-text-fill: #aaaacc; -fx-font-size: 12px; -fx-font-family: 'Segoe UI';");
        return lbl;
    }

    // ─── Helper: style a ComboBox ──────────────────────────────────────────────
    private void styleCombo(ComboBox<String> combo) {
        combo.setStyle("-fx-background-color: #2d2d44; -fx-text-fill: white; "
                     + "-fx-font-size: 12px; -fx-background-radius: 6;");
    }

    // ─── Helper: style a DatePicker ───────────────────────────────────────────
    private void styleDatePicker(DatePicker dp) {
        dp.setStyle("-fx-background-color: #2d2d44; -fx-text-fill: white; "
                  + "-fx-font-size: 12px; -fx-background-radius: 6;");
        dp.setPrefWidth(130);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // MAIN ENTRY POINT — Run this to launch the app
    // ─────────────────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        launch(args);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Inner class: TransactionRow
    // JavaFX TableView needs SimpleStringProperty fields to display data
    // ─────────────────────────────────────────────────────────────────────────
    public static class TransactionRow {
        private final SimpleStringProperty id;
        private final SimpleStringProperty type;
        private final SimpleStringProperty amount;
        private final SimpleStringProperty description;
        private final SimpleStringProperty date;
        private final SimpleStringProperty status;

        public TransactionRow(String id, String type, String amount, String description, String date, String status) {
            this.id          = new SimpleStringProperty(id);
            this.type        = new SimpleStringProperty(type);
            this.amount      = new SimpleStringProperty(amount);
            this.description = new SimpleStringProperty(description);
            this.date        = new SimpleStringProperty(date);
            this.status      = new SimpleStringProperty(status);
        }

        // Getters required by PropertyValueFactory
        public String getId()          { return id.get(); }
        public String getType()        { return type.get(); }
        public String getAmount()      { return amount.get(); }
        public String getDescription() { return description.get(); }
        public String getDate()        { return date.get(); }
        public String getStatus()      { return status.get(); }
    }
}
