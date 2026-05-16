package com.niazi.khatasystem.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.niazi.khatasystem.R;
import com.niazi.khatasystem.database.DatabaseHelper;
import com.niazi.khatasystem.models.KhataRecord;

/**
 * RecordDetailActivity — Full Detail View of a Khata Record
 *
 * This screen shows all the information about a single khata record.
 * From here the user can:
 *   - Edit the record
 *   - Delete the record
 *   - Toggle Paid/Unpaid status
 */
public class RecordDetailActivity extends AppCompatActivity {

    // ─── UI Components ─────────────────────────────────────────────────────────
    private TextView tvCustomerName;
    private TextView tvAmount;
    private TextView tvDate;
    private TextView tvStatus;
    private TextView tvNotes;
    private TextView tvNotesLabel;
    private Button btnEdit;
    private Button btnDelete;
    private Button btnToggleStatus;
    private CardView cardStatus;

    // ─── Data ──────────────────────────────────────────────────────────────────
    private DatabaseHelper dbHelper;
    private KhataRecord record;
    private int recordId;

    // ─── Lifecycle ─────────────────────────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_detail);

        dbHelper = DatabaseHelper.getInstance(this);

        // Get record ID from intent
        recordId = getIntent().getIntExtra(MainActivity.EXTRA_RECORD_ID, -1);

        if (recordId == -1) {
            Toast.makeText(this, "Record not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        loadRecord();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload data if we returned from editing
        if (recordId != -1) {
            loadRecord();
        }
    }

    // ─── Initialization ────────────────────────────────────────────────────────

    private void initViews() {
        tvCustomerName  = findViewById(R.id.tvDetailCustomerName);
        tvAmount        = findViewById(R.id.tvDetailAmount);
        tvDate          = findViewById(R.id.tvDetailDate);
        tvStatus        = findViewById(R.id.tvDetailStatus);
        tvNotes         = findViewById(R.id.tvDetailNotes);
        tvNotesLabel    = findViewById(R.id.tvDetailNotesLabel);
        btnEdit         = findViewById(R.id.btnDetailEdit);
        btnDelete       = findViewById(R.id.btnDetailDelete);
        btnToggleStatus = findViewById(R.id.btnToggleStatus);
        cardStatus      = findViewById(R.id.cardDetailStatus);
    }

    /**
     * Load and display the record from database.
     */
    private void loadRecord() {
        record = dbHelper.getRecordById(recordId);

        if (record == null) {
            Toast.makeText(this, "Record not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // ── Bind data to views ─────────────────────────────────────────────────
        tvCustomerName.setText(record.getCustomerName());
        tvAmount.setText("Rs. " + String.format("%.0f", record.getAmount()));
        tvDate.setText("📅 " + record.getDate());

        // Notes
        if (record.getNotes() != null && !record.getNotes().trim().isEmpty()) {
            tvNotes.setText(record.getNotes());
            tvNotes.setVisibility(View.VISIBLE);
            tvNotesLabel.setVisibility(View.VISIBLE);
        } else {
            tvNotes.setVisibility(View.GONE);
            tvNotesLabel.setVisibility(View.GONE);
        }

        // ── Status styling ─────────────────────────────────────────────────────
        if (record.isPaid()) {
            tvStatus.setText("✓  PAID");
            tvStatus.setTextColor(Color.parseColor("#4CAF50"));
            cardStatus.setCardBackgroundColor(Color.parseColor("#1A4CAF50"));
            btnToggleStatus.setText("Mark as Unpaid");
            btnToggleStatus.setBackgroundResource(R.drawable.btn_unpaid);
            tvAmount.setTextColor(Color.parseColor("#4CAF50"));
        } else {
            tvStatus.setText("⚠  UNPAID");
            tvStatus.setTextColor(Color.parseColor("#FF6B35"));
            cardStatus.setCardBackgroundColor(Color.parseColor("#1AFF6B35"));
            btnToggleStatus.setText("Mark as Paid");
            btnToggleStatus.setBackgroundResource(R.drawable.btn_primary);
            tvAmount.setTextColor(Color.parseColor("#FF6B35"));
        }

        // ── Button click listeners ─────────────────────────────────────────────

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEditKhataActivity.class);
            intent.putExtra(MainActivity.EXTRA_RECORD_ID, recordId);
            startActivity(intent);
        });

        btnDelete.setOnClickListener(v -> showDeleteDialog());

        btnToggleStatus.setOnClickListener(v -> toggleStatus());
    }

    /**
     * Toggle the status of this record between Paid and Unpaid.
     */
    private void toggleStatus() {
        String newStatus = record.isPaid()
                ? KhataRecord.STATUS_UNPAID
                : KhataRecord.STATUS_PAID;

        dbHelper.updateStatus(recordId, newStatus);
        Toast.makeText(this, "Marked as " + newStatus, Toast.LENGTH_SHORT).show();
        loadRecord(); // Reload to reflect changes
    }

    /**
     * Show confirmation dialog before deleting.
     */
    private void showDeleteDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Record")
                .setMessage("Delete khata record for \"" + record.getCustomerName() + "\"?\n\nThis cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deleteRecord(recordId);
                    Toast.makeText(this, "Record deleted", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
