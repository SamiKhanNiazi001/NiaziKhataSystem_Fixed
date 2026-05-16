package com.niazi.khatasystem.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.niazi.khatasystem.R;
import com.niazi.khatasystem.database.DatabaseHelper;
import com.niazi.khatasystem.models.KhataRecord;

import java.util.Calendar;

/**
 * AddEditKhataActivity — Add or Edit a Khata Record
 *
 * This activity handles both adding new records and editing existing ones.
 * The mode is determined by whether a record ID is passed via Intent:
 *   - No ID → Add Mode
 *   - With ID → Edit Mode (pre-fills the form with existing data)
 *
 * Fields: Customer Name, Amount, Date, Status (Paid/Unpaid), Notes
 */
public class AddEditKhataActivity extends AppCompatActivity {

    // ─── UI Components ─────────────────────────────────────────────────────────
    private TextView tvTitle;
    private EditText etCustomerName;
    private EditText etAmount;
    private EditText etDate;
    private EditText etNotes;
    private RadioGroup rgStatus;
    private RadioButton rbPaid;
    private RadioButton rbUnpaid;
    private Button btnSave;
    private Button btnCancel;

    // ─── Data ──────────────────────────────────────────────────────────────────
    private DatabaseHelper dbHelper;
    private KhataRecord existingRecord; // null if adding new, populated if editing
    private int recordId = -1;          // -1 means no existing record (Add mode)

    // ─── Activity Lifecycle ────────────────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_khata);

        // Initialize database
        dbHelper = DatabaseHelper.getInstance(this);

        // Initialize views
        initViews();

        // Check if we're in Edit mode (an ID was passed)
        recordId = getIntent().getIntExtra(MainActivity.EXTRA_RECORD_ID, -1);

        if (recordId != -1) {
            // ── EDIT MODE ──────────────────────────────────────────────────────
            tvTitle.setText("Edit Record");
            btnSave.setText("Update Record");
            loadExistingRecord(recordId);
        } else {
            // ── ADD MODE ───────────────────────────────────────────────────────
            tvTitle.setText("Add New Record");
            btnSave.setText("Save Record");
            // Set today's date as default
            setTodayDate();
            // Default status: Unpaid
            rbUnpaid.setChecked(true);
        }

        // Set up date picker when date field is clicked
        etDate.setOnClickListener(v -> showDatePicker());

        // Save button
        btnSave.setOnClickListener(v -> saveRecord());

        // Cancel button → go back
        btnCancel.setOnClickListener(v -> finish());
    }

    // ─── Initialization ────────────────────────────────────────────────────────

    private void initViews() {
        tvTitle         = findViewById(R.id.tvFormTitle);
        etCustomerName  = findViewById(R.id.etCustomerName);
        etAmount        = findViewById(R.id.etAmount);
        etDate          = findViewById(R.id.etDate);
        etNotes         = findViewById(R.id.etNotes);
        rgStatus        = findViewById(R.id.rgStatus);
        rbPaid          = findViewById(R.id.rbPaid);
        rbUnpaid        = findViewById(R.id.rbUnpaid);
        btnSave         = findViewById(R.id.btnSave);
        btnCancel       = findViewById(R.id.btnCancel);
    }

    /**
     * Load an existing record's data into the form fields (Edit mode).
     */
    private void loadExistingRecord(int id) {
        existingRecord = dbHelper.getRecordById(id);

        if (existingRecord != null) {
            // Pre-fill all form fields with existing data
            etCustomerName.setText(existingRecord.getCustomerName());
            etAmount.setText(String.valueOf((int) existingRecord.getAmount()));
            etDate.setText(existingRecord.getDate());
            etNotes.setText(existingRecord.getNotes());

            // Pre-select the current status radio button
            if (existingRecord.isPaid()) {
                rbPaid.setChecked(true);
            } else {
                rbUnpaid.setChecked(true);
            }
        } else {
            Toast.makeText(this, "Record not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Set today's date in the date field as default.
     */
    private void setTodayDate() {
        Calendar calendar = Calendar.getInstance();
        int year  = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Month is 0-indexed
        int day   = calendar.get(Calendar.DAY_OF_MONTH);

        etDate.setText(String.format("%02d/%02d/%d", day, month, year));
    }

    // ─── Date Picker ───────────────────────────────────────────────────────────

    /**
     * Show the Android DatePickerDialog when the user taps the date field.
     */
    private void showDatePicker() {
        // Get current date to pre-select in picker
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    // month is 0-indexed, so +1
                    String date = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year);
                    etDate.setText(date);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    // ─── Validation & Save ─────────────────────────────────────────────────────

    /**
     * Validate the form and save or update the record.
     */
    private void saveRecord() {
        // ── Get input values ───────────────────────────────────────────────────
        String name   = etCustomerName.getText().toString().trim();
        String amtStr = etAmount.getText().toString().trim();
        String date   = etDate.getText().toString().trim();
        String notes  = etNotes.getText().toString().trim();

        // Get selected status from radio group
        String status = rbPaid.isChecked()
                ? KhataRecord.STATUS_PAID
                : KhataRecord.STATUS_UNPAID;

        // ── Validation ─────────────────────────────────────────────────────────

        // Customer name is required
        if (TextUtils.isEmpty(name)) {
            etCustomerName.setError("Customer name is required");
            etCustomerName.requestFocus();
            return;
        }

        // Amount is required and must be a valid number
        if (TextUtils.isEmpty(amtStr)) {
            etAmount.setError("Amount is required");
            etAmount.requestFocus();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amtStr);
            if (amount <= 0) {
                etAmount.setError("Amount must be greater than 0");
                etAmount.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            etAmount.setError("Please enter a valid amount");
            etAmount.requestFocus();
            return;
        }

        // Date is required
        if (TextUtils.isEmpty(date)) {
            etDate.setError("Date is required");
            etDate.requestFocus();
            return;
        }

        // ── All valid — Save or Update ─────────────────────────────────────────

        if (recordId == -1) {
            // ADD MODE — Insert new record
            KhataRecord newRecord = new KhataRecord(name, amount, date, status, notes);
            long result = dbHelper.insertRecord(newRecord);

            if (result != -1) {
                Toast.makeText(this, "✓ Record added successfully!", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK); // Tell MainActivity to refresh
                finish();
            } else {
                Toast.makeText(this, "Failed to save record. Try again.", Toast.LENGTH_SHORT).show();
            }

        } else {
            // EDIT MODE — Update existing record
            existingRecord.setCustomerName(name);
            existingRecord.setAmount(amount);
            existingRecord.setDate(date);
            existingRecord.setStatus(status);
            existingRecord.setNotes(notes);

            int result = dbHelper.updateRecord(existingRecord);

            if (result > 0) {
                Toast.makeText(this, "✓ Record updated successfully!", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK); // Tell MainActivity to refresh
                finish();
            } else {
                Toast.makeText(this, "Failed to update record. Try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
