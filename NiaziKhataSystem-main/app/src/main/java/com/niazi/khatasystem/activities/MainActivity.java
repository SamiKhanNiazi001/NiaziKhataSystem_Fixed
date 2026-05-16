package com.niazi.khatasystem.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.niazi.khatasystem.R;
import com.niazi.khatasystem.adapters.KhataAdapter;
import com.niazi.khatasystem.database.DatabaseHelper;
import com.niazi.khatasystem.models.KhataRecord;

import java.util.List;

/**
 * MainActivity — Dashboard Screen
 *
 * This is the main screen of the Niazi Khata System.
 * It shows:
 *   - Summary cards (Total Records, Paid Amount, Remaining Amount)
 *   - Search bar for filtering records
 *   - RecyclerView list of all khata records
 *   - Floating Action Button to add new records
 *
 * All CRUD operations trigger a refresh of this screen.
 */
public class MainActivity extends AppCompatActivity implements KhataAdapter.OnItemClickListener {

    // ─── Constants for Activity communication ─────────────────────────────────
    public static final int REQUEST_ADD    = 1;  // Request code for Add
    public static final int REQUEST_EDIT   = 2;  // Request code for Edit
    public static final String EXTRA_RECORD_ID = "record_id"; // Intent key for record ID

    // ─── UI Components ─────────────────────────────────────────────────────────
    private RecyclerView recyclerView;
    private KhataAdapter adapter;
    private FloatingActionButton fabAdd;
    private EditText etSearch;
    private TextView tvTotalRecords;
    private TextView tvPaidAmount;
    private TextView tvRemainingAmount;
    private LinearLayout layoutEmpty;  // Empty state when no records exist

    // ─── Data & Database ───────────────────────────────────────────────────────
    private DatabaseHelper dbHelper;
    private List<KhataRecord> recordList;

    // ─── Activity Lifecycle ────────────────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize database helper (singleton)
        dbHelper = DatabaseHelper.getInstance(this);

        // Initialize all UI components
        initViews();

        // Set up search bar listener
        setupSearch();

        // Set up FAB click to open Add Record screen
        fabAdd.setOnClickListener(v -> openAddRecord());

        // Load data and refresh the screen
        loadAndDisplayData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data whenever we return to this screen (after add/edit)
        loadAndDisplayData();
    }

    // ─── Initialization ────────────────────────────────────────────────────────

    /**
     * Find all views by their IDs from the layout file.
     */
    private void initViews() {
        recyclerView       = findViewById(R.id.recyclerView);
        fabAdd             = findViewById(R.id.fabAdd);
        etSearch           = findViewById(R.id.etSearch);
        tvTotalRecords     = findViewById(R.id.tvTotalRecords);
        tvPaidAmount       = findViewById(R.id.tvPaidAmount);
        tvRemainingAmount  = findViewById(R.id.tvRemainingAmount);
        layoutEmpty        = findViewById(R.id.layoutEmpty);

        // Set up RecyclerView with a vertical LinearLayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
    }

    /**
     * Set up real-time search as the user types in the search bar.
     */
    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filter records as user types
                filterRecords(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // ─── Data Loading ──────────────────────────────────────────────────────────

    /**
     * Load all records from database and refresh the UI.
     * This is called on startup and after any CRUD operation.
     */
    private void loadAndDisplayData() {
        // Fetch all records from database
        recordList = dbHelper.getAllRecords();

        // Update summary dashboard cards
        updateDashboardStats();

        // Show or hide the empty state
        if (recordList.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            // Create or update the adapter
            if (adapter == null) {
                adapter = new KhataAdapter(this, recordList, this);
                recyclerView.setAdapter(adapter);
            } else {
                adapter.updateList(recordList);
            }
        }
    }

    /**
     * Update the three summary cards at the top of the dashboard.
     */
    private void updateDashboardStats() {
        int totalCount       = dbHelper.getTotalRecordCount();
        double paidAmount    = dbHelper.getTotalPaidAmount();
        double remainingAmt  = dbHelper.getTotalUnpaidAmount();

        tvTotalRecords.setText(String.valueOf(totalCount));
        tvPaidAmount.setText("Rs. " + String.format("%.0f", paidAmount));
        tvRemainingAmount.setText("Rs. " + String.format("%.0f", remainingAmt));
    }

    /**
     * Filter the displayed records based on search query.
     */
    private void filterRecords(String query) {
        List<KhataRecord> filtered;

        if (query.isEmpty()) {
            // No search text — show all records
            filtered = recordList;
        } else {
            // Search database for matching records
            filtered = dbHelper.searchByName(query);
        }

        // Show/hide empty state for search results
        if (filtered.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        // Update adapter with filtered results
        if (adapter != null) {
            adapter.updateList(filtered);
        } else if (!filtered.isEmpty()) {
            adapter = new KhataAdapter(this, filtered, this);
            recyclerView.setAdapter(adapter);
        }
    }

    // ─── Navigation ────────────────────────────────────────────────────────────

    /**
     * Open the AddEditKhataActivity to add a new record.
     */
    private void openAddRecord() {
        Intent intent = new Intent(this, AddEditKhataActivity.class);
        startActivityForResult(intent, REQUEST_ADD);
    }

    /**
     * Open the AddEditKhataActivity to edit an existing record.
     */
    private void openEditRecord(KhataRecord record) {
        Intent intent = new Intent(this, AddEditKhataActivity.class);
        intent.putExtra(EXTRA_RECORD_ID, record.getId()); // Pass record ID
        startActivityForResult(intent, REQUEST_EDIT);
    }

    /**
     * Open the RecordDetailActivity to view full record details.
     */
    private void openRecordDetail(KhataRecord record) {
        Intent intent = new Intent(this, RecordDetailActivity.class);
        intent.putExtra(EXTRA_RECORD_ID, record.getId());
        startActivity(intent);
    }

    // ─── KhataAdapter.OnItemClickListener callbacks ───────────────────────────

    @Override
    public void onItemClick(KhataRecord record) {
        // Card click → View full detail
        openRecordDetail(record);
    }

    @Override
    public void onEditClick(KhataRecord record) {
        // Edit button → Open edit screen
        openEditRecord(record);
    }

    @Override
    public void onDeleteClick(KhataRecord record) {
        // Delete button → Show confirmation dialog
        showDeleteConfirmation(record);
    }

    @Override
    public void onStatusToggle(KhataRecord record) {
        // Status badge click → Toggle Paid/Unpaid
        toggleStatus(record);
    }

    // ─── Delete Confirmation Dialog ────────────────────────────────────────────

    /**
     * Show a confirmation dialog before deleting a record.
     * This prevents accidental deletion.
     */
    private void showDeleteConfirmation(KhataRecord record) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Record")
                .setMessage("Are you sure you want to delete khata record for\n\"" +
                            record.getCustomerName() + "\"?\n\nThis action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // User confirmed — delete the record
                    int result = dbHelper.deleteRecord(record.getId());
                    if (result > 0) {
                        Toast.makeText(this, "Record deleted successfully", Toast.LENGTH_SHORT).show();
                        loadAndDisplayData(); // Refresh list
                    } else {
                        Toast.makeText(this, "Failed to delete record", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null) // Cancel does nothing
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    // ─── Status Toggle ─────────────────────────────────────────────────────────

    /**
     * Toggle a record's status between Paid and Unpaid.
     */
    private void toggleStatus(KhataRecord record) {
        // Determine new status (opposite of current)
        String newStatus = record.isPaid()
                ? KhataRecord.STATUS_UNPAID
                : KhataRecord.STATUS_PAID;

        // Update in database
        int result = dbHelper.updateStatus(record.getId(), newStatus);

        if (result > 0) {
            Toast.makeText(this,
                    "Marked as " + newStatus,
                    Toast.LENGTH_SHORT).show();
            loadAndDisplayData(); // Refresh the list and stats
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Refresh data when returning from Add or Edit screen
        if (resultCode == RESULT_OK) {
            loadAndDisplayData();
        }
    }
}
